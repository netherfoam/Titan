package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.assets.DataTable;
import org.maxgamer.rs.cache.CacheFile;
import org.maxgamer.rs.cache.FileTable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author netherfoam
 */
public class LowParityTest {
    private File cache = new File("cache");

    @Test
    public void masterTableTest() throws IOException {
        RandomAccessFile rafIdx = new RandomAccessFile(new File(cache, "main_file_cache.idx" + 255), "r");
        RandomAccessFile data = new RandomAccessFile(new File(cache, "main_file_cache.dat2"), "r");

        FileTable original = FileTable.decode(255, rafIdx, data);
        DataTable assets = new DataTable(255, rafIdx.getChannel(), data.getChannel());

        Assert.assertEquals(original.size(), assets.size());
        CacheFile.getRaw(255, rafIdx.getChannel(), data.getChannel(), 0);
    }

    @Test
    public void fileTableTest() throws IOException {
        RandomAccessFile rafIdx = new RandomAccessFile(new File(cache, "main_file_cache.idx" + 0), "r");
        RandomAccessFile data = new RandomAccessFile(new File(cache, "main_file_cache.dat2"), "r");

        FileTable original = FileTable.decode(0, rafIdx, data);
        DataTable assets = new DataTable(0, rafIdx.getChannel(), data.getChannel());

        Assert.assertEquals(original.size(), assets.size());
        CacheFile.getRaw(0, rafIdx.getChannel(), data.getChannel(), 0);
    }
}
