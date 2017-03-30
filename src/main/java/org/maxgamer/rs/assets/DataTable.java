package org.maxgamer.rs.assets;

import org.maxgamer.rs.Assert;

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
    private static final int INDEX_BLOCK_LEN = 6;
    private static final int BLOCK_HEADER_LEN = 8;
    private static final int BLOCK_LEN = 512;
    private static final int TOTAL_BLOCK_LEN = BLOCK_HEADER_LEN + BLOCK_LEN;

    private static int readTriByte(ByteBuffer buffer) {
        return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
    }

    private static void writeTriByte(ByteBuffer buffer, int value) {
        buffer.put((byte) ((value >> 16) & 0xFF));
        buffer.put((byte) ((value >> 8) & 0xFF));
        buffer.put((byte) (value & 0xFF));
    }

    private byte indexId;
    private FileChannel index;
    private FileChannel data;

    public DataTable(int indexId, FileChannel index, FileChannel data) throws IOException {
        this.indexId = (byte) indexId;
        this.index = index;
        this.data = data;

        long currentBlock = (data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN;
        if(currentBlock <= 0) {
            // Initialize the first block in the table. This is a special block, which indicates
            // a missing file. It should never be read from.
            data.write(ByteBuffer.wrap("DO NOT READ".getBytes()), currentBlock * TOTAL_BLOCK_LEN);
        }
    }

    public void write(int fileId, ByteBuffer contents) throws IOException {
        long currentBlock = (data.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN;
        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        writeTriByte(entry, contents.remaining());
        writeTriByte(entry, (int) currentBlock);

        entry.flip();
        index.write(entry, fileId * INDEX_BLOCK_LEN);

        short chunkNumber = 0;
        ByteBuffer chunk = ByteBuffer.allocate(TOTAL_BLOCK_LEN);

        while(contents.hasRemaining()) {
            long nextBlock = currentBlock + 1;

            chunk.putShort((short) fileId);
            chunk.putShort(chunkNumber);
            writeTriByte(chunk, (int) nextBlock);
            chunk.put(indexId);

            // TODO: Could be done faster
            while (contents.remaining() > 0 && chunk.remaining() > 0) {
                chunk.put(contents.get());
            }

            chunk.flip();
            data.write(chunk, currentBlock * TOTAL_BLOCK_LEN);

            // Reset the chunk buffer to position 0
            chunk.flip();

            chunkNumber++;
            currentBlock = nextBlock;
        }
    }

    public ByteBuffer read(int fileId) throws IOException {
        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        index.read(entry, fileId * INDEX_BLOCK_LEN);
        entry.flip();

        int size = readTriByte(entry);
        int nextBlock = readTriByte(entry);

        if (nextBlock == 0) {
            throw new FileNotFoundException("no such file (" + fileId + ")");
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

            while(chunk.remaining() > 0 && contents.remaining() > 0) {
                contents.put(chunk.get());
            }

            chunk.flip();
            chunkNumber++;
        }

        contents.flip();

        return contents;
    }

    public int size() throws IOException {
        return (int) (index.size() / INDEX_BLOCK_LEN);
    }

    public void erase(int fileId) throws IOException {
        ByteBuffer entry = ByteBuffer.allocate(INDEX_BLOCK_LEN);
        writeTriByte(entry, 0);
        writeTriByte(entry, 0);

        entry.flip();
        index.write(entry, fileId * INDEX_BLOCK_LEN);
    }
}
