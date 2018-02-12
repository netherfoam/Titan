package org.maxgamer.rs.structure.configs;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * TODO: Document this
 */
public class YamlConfig extends FileConfig {
    /**
     * The YML parser we use as an interface to write to the file
     * This uses the SnakeYAML library.
     */
    private final Yaml parser;

    public YamlConfig(File file) {
        super(file);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        parser = new Yaml(options);
    }

    /**
     * Loads additional data from the given file.
     *
     * @param from
     * @throws IOException
     */
    @Override
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
     * Writes this FileConfig to disk. This creates
     * the file if it does not exist, including parent
     * folders.
     *
     * @throws IOException if an IO error occured.
     */
    @Override
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
