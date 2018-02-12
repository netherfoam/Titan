package org.maxgamer.rs.structure.configs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * A ConfigSection which is based on a particular file.
 * This class contains methods for reloading and saving
 * a ConfigSection.
 *
 * @author netherfoam
 */
public abstract class FileConfig extends MutableConfig {
    /**
     * The file we write to
     */
    protected final File file;

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

        this.file = file;
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

    public abstract void load(File from) throws IOException;
    public abstract void save() throws IOException;
}