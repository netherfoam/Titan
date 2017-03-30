package org.maxgamer.rs.cache;

import org.maxgamer.rs.util.io.InputStreamWrapper;
import org.maxgamer.rs.util.io.OutputStreamWrapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * A manage class which allows XTEA keys to be stored
 * for any file in a container from the cache.
 *
 * @author netherfoam
 */
public class XTEAStore {
    public static void loadFormatUnpacked(Cache c, File folder, XTEAStore convert) throws IOException {
        for (File f : folder.listFiles()) {
            if (!f.getName().endsWith(".txt")) continue; //Only pack .txt files

            int regionId = Integer.parseInt(f.getName().substring(0, f.getName().indexOf(".")));

            //This may be the other way around.
            int rx = regionId >> 8;
            int ry = regionId & 0xFF;
            int fileId;
            try {
                fileId = c.getFileId(5, "l" + rx + "_" + ry);
            } catch (FileNotFoundException e) {
                System.out.println("No file found for l" + rx + "_" + ry);
                continue;
            }
            Scanner sc = new Scanner(f);

            int[] keys = new int[4];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = sc.nextInt();
            }
            sc.close();

            convert.setKey(5, fileId, new XTEAKey(keys));
        }
    }

    public static void loadFormat0(Cache c, File f, XTEAStore convert) throws IOException {
        InputStreamWrapper in = new InputStreamWrapper(new FileInputStream(f));

        while (in.available() > 0) {
            int idx = in.readInt();
            String name = in.readString();
            int[] values = new int[4];
            for (int i = 0; i < values.length; i++) {
                values[i] = in.readInt();
            }
            int fileId = c.getFileId(idx, name);
            convert.setKey(idx, fileId, new XTEAKey(values));
        }

        in.close();
    }

    /**
     * The size of an XTEA int[] array. This may not vary.
     */
    public static final int XTEA_KEY_LENGTH = 4;

    /**
     * The file this was loaded from
     */
    private File file;

    /**
     * A hashmap of (IDX << 24) | (fileId) to XTEA Key for that file
     */
    private HashMap<Integer, XTEAKey> keys = new HashMap<>(1024);

    public XTEAStore(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    /**
     * Loads this XTEA Store from the XTEA file. This does
     * not overwrite existing values and will not throw
     * an exception if duplicate values are added, instead, it
     * is ignored.
     *
     * @throws IOException           if there was an IO error
     * @throws FileNotFoundException if the file could not be found
     */
    public void load() throws IOException {
        this.load(file);
    }

    /**
     * Loads this XTEA Store from the given file. This does
     * not overwrite existing values.
     * specified in the given file.
     *
     * @param f the file to load from
     * @throws IOException           if there was an IO error
     * @throws FileNotFoundException if the file could not be found
     */
    private void load(File f) throws IOException {
        // Throws FileNotFound
        InputStreamWrapper in = new InputStreamWrapper(new FileInputStream(f));

        while (in.available() > 0) {
            byte idx = in.readByte();
            int fileId = ((in.readByte() & 0xFF) << 16) | ((in.readByte() & 0xFF) << 8) | (in.readByte() & 0xFF);

            int[] v = new int[XTEA_KEY_LENGTH];
            for (int i = 0; i < v.length; i++) {
                v[i] = in.readInt();
            }

            this.setKey(idx, fileId, new XTEAKey(v));
        }
        in.close();
    }

    /**
     * Saves all loaded XTEAs in this store to the XTEA file.
     * If the file doesn't exist, this method creates any relevant
     * directories and then creates the file.
     *
     * @throws IOException if there was an IO error
     */
    public void save() throws IOException {
        save(getFile());
    }

    /**
     * Saves all loaded XTEAs in this store to the given file.
     * If the file doesn't exist, this method creates any relevant
     * directories and then creates the file.
     *
     * @param f the file to save to
     * @throws IOException if there was an IO error
     */
    private void save(File f) throws IOException {
        if (!f.exists()) {
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
        }

        OutputStreamWrapper out = new OutputStreamWrapper(new FileOutputStream(f));

        for (Entry<Integer, XTEAKey> e : keys.entrySet()) {
            out.writeByte(e.getKey() >> 24); //IDX
            out.writeByte(e.getKey() >> 16); //FileId
            out.writeByte(e.getKey() >> 8); //FileId
            out.writeByte(e.getKey()); //FileId

            XTEAKey value = e.getValue();
            int[] data = value.getKeys();

            //The next 4 integers are the key.
            for (int i = 0; i < XTEA_KEY_LENGTH; i++) {
                out.writeInt(data[i]);
            }
        }
        out.close();
    }

    /**
     * Returns the number of keys in this store
     *
     * @return the number of keys in this store
     */
    public int size() {
        return keys.size();
    }

    /**
     * Fetches the xtea value for the given hash value
     *
     * @param idx  the type
     * @param fileId the file number
     * @return the key.
     */
    public XTEAKey getKey(int idx, int fileId) {
        return keys.get((idx << 24) | fileId);
    }

    /**
     * Sets the key for the given idx and container to the given value.
     *
     * @param idx       the idx for the section
     * @param fileId    the file id
     * @param key       the keys to set, possibly null
     * @throws IllegalArgumentException if the keys length is not 4
     */
    public void setKey(int idx, int fileId, XTEAKey key) {
        if (fileId < 0 || idx < 0 || idx > 255) throw new IllegalArgumentException();

        if (key == null) {
            keys.remove((idx << 24) | fileId);
        } else {
            //No need to validate key, XTEAKey class does this for us.
            keys.put((idx << 24) | fileId, key);
        }
    }
}