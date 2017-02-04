package org.maxgamer.rs.module;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.dependency.Graph;
import org.maxgamer.rs.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author netherfoam
 */
public class ModuleLoader {
    public static final ModuleClassLoader CLASS_LOADER = new ModuleClassLoader(Core.CLASS_LOADER);

    /**
     * Currently loaded modules
     */
    private HashMap<String, Module> modules;

    /**
     * The folder where modules are loaded from
     */
    private File folder;

    private String field;

    /**
     * Constructs a new Module loader class. This class loads modules from the
     * modules/ folder. This does not call load(), which can be done later.
     */
    public ModuleLoader(File folder, String field) {
        if (folder == null) {
            throw new NullPointerException("Folder may not be null.");
        }
        this.modules = new HashMap<>();
        this.folder = folder;
        this.field = field;
    }

    /**
     * Loads all modules from the module folder.
     */
    public void load() {
        long startTime = System.currentTimeMillis();

        Runtime r = Runtime.getRuntime();

        long startMem = r.totalMemory() - r.freeMemory();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File parent, String name) {
                return name.endsWith(".jar");

            }
        });

        HashMap<String, File> names = new HashMap<>();
        HashMap<String, ConfigSection> moduleYMLs = new HashMap<>();
        for (File f : files) {
            try {
                ConfigSection info = readModuleYML(f);
                String name = info.getString("name");

                if (names.containsKey(name)) {
                    Log.warning("There are two modules called " + name + "! One is from " + f + ", the other is from " + names.get(name) + ". Skipping " + f);
                    continue;
                }

                names.put(name, f);
                moduleYMLs.put(name, info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.debug("Graphing dependencies...");
        Graph<String> dependencies = new Graph<>();
        for (String name : names.keySet()) {
            ConfigSection config = moduleYMLs.get(name);

            List<String> depends = config.getList("depends", String.class);
            if (depends != null) {
                for (String s : depends) {
                    File required = names.get(s);
                    if (required == null) {
                        Log.warning("Couldn't find a module called " + s + ". The module " + name + " depends on it. Can't load " + name);
                        continue;
                    }
                    dependencies.addDependency(s, name);
                }
            }
        }

        ArrayList<String> deps = dependencies.generateDependencies();
        Log.debug("Dependency tree generated!");

        for (String name : names.keySet()) {
            if (!deps.contains(name)) {
                deps.add(0, name);
            }
        }

        for (String s : deps) {
            File f = names.get(s);
            try {
                if (f == null) {
                    Log.warning("Couldn't find module " + s);
                    continue;
                }

                boolean load = true;

                ConfigSection info = moduleYMLs.get(s);
                for (String d : info.getList("depends", String.class, Collections.<String>emptyList())) {
                    if (getModule(d) == null) {
                        Log.warning("Missing dependency for " + s + " (depends " + d + ")");
                        load = false;
                        break;
                    }
                }

                if (load) {
                    load(f);
                }
            } catch (Throwable e) {
                Log.info("Exception loading module " + f.getName());
                e.printStackTrace();
            }
        }

        //This is just a total stab in the dark guess.
        long endMem = r.totalMemory() - r.freeMemory();

        long endTime = System.currentTimeMillis();
        Log.info("Modules Loaded! Utilising ~" + (endMem - startMem) / (1024 * 1024) + "MB. Took " + (endTime - startTime) + "ms.");
    }

    private ConfigSection readModuleYML(File f) throws IOException {
        JarFile jar = new JarFile(f);

        try {
            ModuleClassLoader cl = ModuleLoader.CLASS_LOADER;
            cl.addURL(f.toURI().toURL());

            ZipEntry e = jar.getEntry("module.yml");
            if (e == null) {
                throw new IOException(f.getName() + ": JAR file does not contain a module.yml file in the root!");
            }

            InputStream in = jar.getInputStream(e);
            ConfigSection cfg = new ConfigSection(in);

            if (cfg.getString("name", "").isEmpty()) {
                throw new IOException("module.yml is missing name field!");
            }

            return cfg;
        } finally {
            jar.close();
        }
    }

    /**
     * Loads the given file as a module and returns it. This method supresses
     * and logs any error messages and if it returns null, there was an error.
     * Otherwise it returns the loaded module.
     *
     * @param f the file to load from
     * @return the module.
     * @throws Throwable
     */
    public Module load(File f) throws Throwable {
        JarFile jar = null;
        try {
            jar = new JarFile(f);

            ModuleClassLoader cl = ModuleLoader.CLASS_LOADER;
            cl.addURL(f.toURI().toURL());

            ZipEntry e = jar.getEntry("module.yml");
            if (e == null) {
                throw new IOException(f.getName() + ": JAR file does not contain a module.yml file in the root!");
            }

            InputStream in = jar.getInputStream(e);
            ConfigSection cfg = new ConfigSection(in);

            String name = cfg.getString("name");
            if (name == null) {
                throw new IOException(f.getName() + ": JAR module.yml must contain a valid field 'name' to load..");
            }

            in.close();
            String str = cfg.getString(this.field);
            if (str == null) {
                //throw new IOException(f.getName() + ": JAR module.yml must contain a valid field '" + this.field + "' to load.");
                return null;
            }

            Class<?> clazz = cl.loadClass(str);

            Class<?> parent = clazz.getSuperclass();
            while (parent != null && parent != Module.class) {
                parent = parent.getSuperclass();
            }

            if (parent != Module.class) {
                throw new IOException(f.getName() + ": Class specified in module.yml in JAR file must extend Module class.");
            }

            Log.info("Loading " + f.getName());

            Module m;
            try {
                m = (Module) clazz.newInstance();
            } catch (Exception ex) {
                throw new IOException(f.getName() + ": Class specified in module.yml in JAR file must have a zero-args constructor.");
            }

            ModuleMeta meta = new ModuleMeta(f, cfg, cl);
            m.setMeta(meta);

            FileConfig modCfg = new FileConfig(new File(m.getFolder(), "config.yml"));
            modCfg.reload(); //Initial load
            meta.setConfig(modCfg);

            // We add it now, so that if loading calls unload() during load(), then we don't add the module to the set of loaded modules
            this.modules.put(m.getName().toLowerCase(), m);

            try {
                m.load();
                // TODO: Reimplement this
                /*ModuleLoadEvent event = new ModuleLoadEvent(m);
                event.call();*/
            } catch (Throwable t) {
                Log.severe("Error calling module load() on " + m.getName() + " (" + f.getName() + ").");
                try {
                    m.unload();
                    m.getMeta().getLoader().close();
                } catch (Throwable t2) {
                    //Ignored, failed to unload.
                }
                this.modules.remove(m.getName().toLowerCase());
                throw t; //Caught later
            }


            return m;
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe("Failed to load module " + f.getName() + ". Invalid/Corrupt JAR File?");
        } catch (Exception e) {
            e.printStackTrace();
            Log.severe("Failed to load module " + f.getName() + ".");
        } finally {
            try {
                jar.close();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Fetches a module by name. This is case insensitive and uses a hashmap
     * lookup. The name this is matched against is the result of the
     * module.getName() call.
     *
     * @param name the name of the module
     * @return the module or null if not found
     */
    public Module getModule(String name) {
        return modules.get(name.toLowerCase());
    }

    /**
     * Fetches the module by the given type. Note that this adds a hard
     * dependency in your module project.
     *
     * @param type the type
     * @return the module
     */
    @SuppressWarnings("unchecked")
    public <M extends Module> M getModule(Class<M> type) {
        for (Module m : this.modules.values()) {
            if (type.isInstance(m)) {
                return (M) m;
            }
        }
        return null;
    }

    /**
     * Fetches a module by file. This is a linear lookup, and returns the module
     * which was loaded from the given file (for example, getJar() == file).
     *
     * @param file The file a module was loaded from
     * @return the module or null if there is no module from that file
     */
    public Module getModule(File file) {
        for (Module m : modules.values()) {
            if (m.getJar().equals(file)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of modules which are currently loaded on
     * this server.
     *
     * @return the list of modules loaded
     */
    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    /**
     * Unloads the given module
     *
     * @param m The module
     * @return true if successful, false if the unload method throws an
     * exception.
     */
    public boolean unload(Module m) {
        try {
            // TODO: Reimplement this
            /*ModuleUnloadEvent event = new ModuleUnloadEvent(m);
            event.call();*/

            m.unload();
            m.getMeta().getLoader().close();
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        } finally {
            modules.remove(m.getName().toLowerCase());
        }
    }

    /**
     * Unloads all currently active modules.
     */
    public void unload() {
        Iterator<Module> mit = modules.values().iterator();
        while (mit.hasNext()) {
            Module m = mit.next();
            try {
                m.unload();
                m.getMeta().getLoader().close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            mit.remove();
        }
    }
}