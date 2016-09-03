package org.maxgamer.rs.module;

import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;

import java.io.File;
import java.net.URLClassLoader;

/**
 * @author netherfoam
 */
public class ModuleMeta {
    private FileConfig config;
    private ConfigSection jarCfg;
    private File file;
    private URLClassLoader loader;

    protected ModuleMeta(File file, ConfigSection jarCfg, URLClassLoader loader) {
        this.file = file;
        this.jarCfg = jarCfg;
        this.loader = loader;
    }

    public URLClassLoader getLoader() {
        return loader;
    }

    protected FileConfig getConfig() {
        return config;
    }

    protected void setConfig(FileConfig config) {
        this.config = config;
    }

    protected File getJar() {
        return file;
    }

    protected String getName() {
        return jarCfg.getString("name", file.getName().substring(0, file.getName().lastIndexOf('.')));
    }
}