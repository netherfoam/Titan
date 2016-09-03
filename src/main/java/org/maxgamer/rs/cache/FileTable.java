package org.maxgamer.rs.cache;

import org.maxgamer.rs.cache.reference.ReferenceTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class FileTable {
    protected int size;
    protected int idx;
    protected FileChannel index;
    protected FileChannel data;
    protected HashMap<Integer, CacheFile> files;
    public FileTable(int idx, int size) {
        this();

        this.idx = idx;
        this.files = new HashMap<Integer, CacheFile>(256);
        this.size = size;
    }

    protected FileTable() {

    }

    /**
     * Decodes the given file table.
     *
     * @param idx
     * @param index
     * @param data
     * @return
     * @throws IOException
     */
    public static FileTable decode(int idx, RandomAccessFile index, RandomAccessFile data) throws IOException {
        FileTable ft = new FileTable(idx, (int) (index.length() / ReferenceTable.IDX_BLOCK_LEN));

        ft.index = index.getChannel();
        ft.data = data.getChannel();

        return ft;
    }

	/*public void write(int fileId, CacheFile file) throws IOException{
		ByteBuffer bb = file.encode();
		
		if(files.get(fileId) == null){
			//New file
			
			
		}
		throw new RuntimeException("Not implemented");
		
	}*/

    /**
     * Fetches the file by the given ID. If the file has been fetched previously,
     * it is retrieved from a memory cache.
     *
     * @param fileId the fileID
     * @param key    the XTEA key for decrypting the file, or null if there is none
     * @return the file if it is found, not null
     * @throws IOException           if there was an IO error parsing the cache
     * @throws FileNotFoundException if the file doesn't exist
     */
    public CacheFile get(int fileId, XTEAKey key) throws IOException {
        CacheFile file = files.get(fileId);
        if (file == null) {
            file = CacheFile.decode(this.idx, index, data, fileId, key);
            files.put(fileId, file);
        }

        return file;
    }

    public void set(int fileId, CacheFile file) {
        files.put(fileId, file);
    }

    /**
     * The number of files that are listed by the index
     *
     * @return the number of files
     * @throws IOException if the index file could not be read
     */
    public int size() {
        return size;
    }

    /**
     * The IDX value of this table.
     *
     * @return The IDX value of this table.
     */
    public int getIDX() {
        return idx;
    }

    /**
     * Discards all files currently in the cache for this filetable. This saves on
     * memory if you know files aren't going to be accessed in a reasonable timeframe.
     */
    public void prune() {
        files.clear();
    }
}