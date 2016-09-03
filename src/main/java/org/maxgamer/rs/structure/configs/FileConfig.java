package org.maxgamer.rs.structure.configs;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A ConfigSection which is based on a particular file.
 * This class contains methods for reloading and saving
 * a ConfigSection.
 *
 * @author netherfoam
 */
public class FileConfig extends ConfigSection {
    /**
     * The file we write to
     */
    private final File file;
    /**
     * The YML parser we use as an interface to write to the file
     * This uses the SnakeYAML library.
     */
    private final Yaml parser;

    /**
     * Creates a new FileConfig based on the given file.
     * This method does not load the data from the file.
     * Instead you should call FileConfig.reload() if you
     * wish to view previously stored values.
     *
     * @param file the file to base this config on.
     */
    public FileConfig(File file) {
        super(new HashMap<String, Object>());
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        parser = new Yaml(options);
        this.file = file;
    }

    /**
     * Loads additional data from the given file.
     *
     * @param from
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void load(File from) throws IOException {
        try {
            InputStream in = new FileInputStream(from);
            Map<String, Object> data = (Map<String, Object>) parser.load(in);

            if (data != null) {
                this.map.putAll(data);
            }

            in.close();
        } catch (FileNotFoundException e) {
            //File does not exist, therefore it has no data to load.
        }
    }

    /**
     * Dumps all previous values and loads the config from
     * the file supplied in the constructor.
     *
     * @throws IOException If there was an error loading the file (Eg, not found)
     */
    public void reload() throws IOException {
        this.map.clear(); //Drop all previous info
        load(this.file); //Load from disk again

    }

    /**
     * Writes this FileConfig to disk. This creates
     * the file if it does not exist, including parent
     * folders.
     *
     * @throws IOException if an IO error occured.
     */
    public void save() throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            file.createNewFile();
        }
        PrintStream ps = new PrintStream(file);
        String out = parser.dump(map);
        ps.print(out);
        ps.close();
    }

    /**
     * Returns what would be the contents of this FileConfig
     * if it were written to disk, in string form.
     */
    @Override
    public String toString() {
        return parser.dump(map);
    }
}