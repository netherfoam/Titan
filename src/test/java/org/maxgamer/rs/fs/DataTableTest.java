package org.maxgamer.rs.fs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.DataTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author netherfoam
 */
public class DataTableTest {
    private DataTable dataTable;
    private File index = new File("index.idx0");
    private File data = new File("data.dat2");

    @Before
    public void init() throws IOException {
        if(index.exists()) index.delete();
        if(data.exists()) data.delete();

        index.createNewFile();
        data.createNewFile();

        index.deleteOnExit();
        data.deleteOnExit();

        RandomAccessFile rafIndex = new RandomAccessFile(index, "rw");
        RandomAccessFile rafData = new RandomAccessFile(data, "rw");

        dataTable = new DataTable(0, rafIndex.getChannel(), rafData.getChannel());
    }

    @After
    public void cleanup() {
        index.delete();
        data.delete();
    }

    @Test
    public void writeRead() throws IOException {
        byte[] data = "Hello World".getBytes();
        ByteBuffer contents = ByteBuffer.wrap(data);

        dataTable.write(0, contents);
        ByteBuffer result = dataTable.read(0);

        Assert.assertArrayEquals(data, result.array());
    }

    @Test
    public void writeDelete() throws IOException {
        byte[] data = "Hello World".getBytes();
        ByteBuffer contents = ByteBuffer.wrap(data);

        dataTable.write(0, contents);
        dataTable.erase(0);

        try {
            dataTable.read(0);
            Assert.fail("Expect to raise FileNotFoundException after delete");
        } catch (FileNotFoundException e) {
            // Success! File should be missing
        }
    }

    @Test
    public void writeDeleteWrite() throws IOException {
        byte[] data = "Hello World".getBytes();
        ByteBuffer contents = ByteBuffer.wrap(data);

        dataTable.write(0, contents.asReadOnlyBuffer());
        dataTable.erase(0);
        dataTable.write(1, contents.asReadOnlyBuffer());

        Assert.assertArrayEquals(data, dataTable.read(1).array());
    }

    @Test
    public void writeDeleteOverwrite() throws IOException {
        byte[] data = "Hello World".getBytes();
        ByteBuffer contents = ByteBuffer.wrap(data);

        dataTable.write(0, contents.asReadOnlyBuffer());
        dataTable.erase(0);
        dataTable.write(0, contents.asReadOnlyBuffer());

        Assert.assertArrayEquals(data, dataTable.read(0).array());
    }

    @Test
    public void multiChunkReadWrite() throws IOException {
        byte[] data = data(580);
        ByteBuffer contents = ByteBuffer.wrap(data);

        dataTable.write(0, contents);
        ByteBuffer result = dataTable.read(0);

        Assert.assertArrayEquals(data, result.array());
    }

    @Test
    public void multiReadWrite() throws IOException {
        byte[] first = "Hello World".getBytes();
        byte[] second = "Goodbye World".getBytes();

        dataTable.write(0, ByteBuffer.wrap(first));
        dataTable.write(1, ByteBuffer.wrap(second));;

        Assert.assertArrayEquals(first, dataTable.read(0).array());
        Assert.assertArrayEquals(second, dataTable.read(1).array());
    }

    @Test
    public void multiChunkMultiReadWrite() throws IOException {
        byte[] first = data(600);
        byte[] second = data(1050);

        dataTable.write(0, ByteBuffer.wrap(first));
        dataTable.write(1, ByteBuffer.wrap(second));;

        Assert.assertArrayEquals(first, dataTable.read(0).array());
        Assert.assertArrayEquals(second, dataTable.read(1).array());
    }

    private byte[] data(int size) {
        Random r = new Random(0);
        byte[] data = new byte[size];
        r.nextBytes(data);

        return data;
    }
}
