package org.maxgamer.rs.assets;

import org.maxgamer.rs.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Manages a single file table. That is, data inside of a "main_file_cache.dat2" file, with the table of contents
 * information inside a "main_file_cache.idx(number)" file. This doesn't modify the master table of contents file
 *
 * @author netherfoam
 */
public class DataTable {
    /**
     * The length of an index block. These are two tri-byte integers - the file size and the starting block number
     */
    public static final int INDEX_BLOCK_LEN = 6;

    /**
     * The header for each block. Blocks are prefixed with 8 bytes: fileId (short), chunk number (short, consecutive),
     * next block number (tri-byte) and index id (byte).
     */
    public static final int BLOCK_HEADER_LEN = 8;

    /**
     * The length of each block in the cache, excluding the header
     */
    public static final int BLOCK_LEN = 512;

    /**
     * The total length of each block in the cache, including the header
     */
    public static final int TOTAL_BLOCK_LEN = BLOCK_HEADER_LEN + BLOCK_LEN;

    private static int readTriByte(ByteBuffer buffer) {
        return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
    }

    private static void writeTriByte(ByteBuffer buffer, int value) {
        buffer.put((byte) ((value >> 16) & 0xFF));
        buffer.put((byte) ((value >> 8) & 0xFF));
        buffer.put((byte) (value & 0xFF));
    }

    /**
     * The index id (0-255)
     */
    private byte indexId;
    private FileChannel index;
    private FileChannel data;
    private long lastModified = System.currentTimeMillis();

    /**
     * Constructs a new data table. If the data table doesn't exist yet, this will initialise it.
     * @param indexId the index id (0-255)
     * @param index the index
     * @param data the data channel to read/write to
     * @throws IOException if the channel can't be read or written to
     */
    public DataTable(int indexId, FileChannel index, FileChannel data) throws IOException {
        Assert.isTrue(indexId >= 0 && indexId <= 255, "Index must be 0-255 inclusive");

        this.indexId = (byte) indexId;
        this.index = index;
        this.data = data;

        long currentBlock = (data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN;
        if(currentBlock <= 0) {
            // Initialize the first block in the table. This is a special block, which indicates
            // a missing file. It should never be read from.
            data.write(ByteBuffer.wrap("DO NOT READ".getBytes()), currentBlock * TOTAL_BLOCK_LEN);
            lastModified = System.currentTimeMillis();
        }
    }

    /**
     * Writes the given file to disk.
     *
     * NB: If a file is overwritten, the previous contents are not removed, but no longer have a pointer in the index. Hence
     * they can't be read from.
     *
     * @param fileId the file to write to
     * @param contents the contents of the file
     * @throws IOException if the data file or index file can't be written to
     */
    public void write(int fileId, ByteBuffer contents) throws IOException {
        Assert.isPositive(fileId, "File Id must be positive");
        Assert.isTrue(fileId <= 0xFFFF, "File Id is 2 bytes at most");
        Assert.isTrue(contents.remaining() <= 0xFFFFFF, "Files must be < 16 megabytes");

        // Store in the index where the file starts, and how big it is
        long currentBlock = (data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN;
        Assert.isTrue(currentBlock + (contents.remaining() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN < 0xFFFFFF, "Cache size would reach 16 million blocks (8.125gb)");

        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        writeTriByte(entry, contents.remaining());
        writeTriByte(entry, (int) currentBlock);

        entry.flip();
        index.write(entry, fileId * INDEX_BLOCK_LEN);

        // Begin writing the payload
        short chunkNumber = 0;
        ByteBuffer chunk = ByteBuffer.allocate(TOTAL_BLOCK_LEN);

        while(contents.hasRemaining()) {
            long nextBlock = currentBlock + 1;

            chunk.putShort((short) fileId);
            chunk.putShort(chunkNumber);
            writeTriByte(chunk, (int) nextBlock);
            chunk.put(indexId);

            // We write as much of the contents to the chunk buffer as possible
            int limit = contents.limit();
            contents.limit(Math.min(limit, contents.position() + chunk.remaining()));
            chunk.put(contents);
            contents.limit(limit);

            chunk.flip();
            data.write(chunk, currentBlock * TOTAL_BLOCK_LEN);

            // Reset the chunk buffer to position 0
            chunk.flip();

            chunkNumber++;
            currentBlock = nextBlock;
        }

        lastModified = System.currentTimeMillis();
    }

    /**
     * Reads a file from disk. This file is still compressed and encrypted.
     * @param fileId the file to read
     * @return the contents of the file
     * @throws FileNotFoundException if the file has been deleted or doesn't exist
     * @throws IOException if the channel can't be read from
     */
    public ByteBuffer read(int fileId) throws IOException {
        Assert.isPositive(fileId, "File Id must be positive");
        Assert.isTrue(fileId <= 0xFFFFFF, "File Id is 3 bytes at most");

        if(fileId * INDEX_BLOCK_LEN >= index.size()) {
            // We avoid slipping off the edge of the file and trying to access data that doesn't exist.
            // This isn't the only possible reason a file doesn't exist though!
            throw new FileNotFoundException("no such file (" + fileId + ")");
        }

        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        index.read(entry, fileId * INDEX_BLOCK_LEN);
        entry.flip();

        int size = readTriByte(entry);
        int nextBlock = readTriByte(entry);

        if (nextBlock == 0) {
            throw new FileNotFoundException("no such file (" + fileId + ") - it may have been erased or not initialised");
        }

        ByteBuffer contents = ByteBuffer.allocate(size);
        ByteBuffer chunk = ByteBuffer.allocate(TOTAL_BLOCK_LEN);

        int chunkNumber = 0;
        while(contents.remaining() > 0) {
            data.read(chunk, nextBlock * TOTAL_BLOCK_LEN);
            chunk.flip();

            Assert.equal(fileId, chunk.getShort() & 0xFFFF);
            Assert.equal(chunkNumber, chunk.getShort() & 0xFFFF);

            nextBlock = readTriByte(chunk);
            Assert.equal(indexId, chunk.get());

            // We read as much of the chunk as we can, until the buffer is full. Once the buffer
            // is full, the rest of the chunk is garbage (and there should be no more chunks)
            int limit = chunk.limit();
            if(chunk.remaining() > contents.remaining()) {
                chunk.limit(chunk.position() + contents.remaining());
            }

            contents.put(chunk);
            chunk.limit(limit);

            chunk.flip();
            chunkNumber++;
        }

        contents.flip();

        return contents;
    }

    /**
     * The size of the data table. This doesn't indicate the number of files available (due to erasing files), but it does indicate
     * the maximum file number available in this table
     *
     * @return the maximum potentially available file in this table
     * @throws IOException if the channel can't be read
     */
    public int size() throws IOException {
        return (int) (index.size() / INDEX_BLOCK_LEN);
    }

    /**
     * Erase the pointer to the given file. The file is not overwritten (nor will it be overwritten), but the pointer is erased.
     *
     * @param fileId the file id
     * @throws IOException if the channel can't be read
     */
    public void erase(int fileId) throws IOException {
        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        writeTriByte(entry, 0);
        writeTriByte(entry, 0);

        entry.flip();
        index.write(entry, fileId * INDEX_BLOCK_LEN);

        lastModified = System.currentTimeMillis();
    }

    /**
     * The last time, in milliseconds, that this DataTable was modified (write/erase/create operations)
     * @return the last modification timestamp in milliseconds
     */
    public long modified() {
        return lastModified;
    }
}
