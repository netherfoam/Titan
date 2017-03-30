package org.maxgamer.rs.fs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.DataTable;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.cache.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class AssetRealityTest {
    private AssetStorage storage;

    @Before
    public void init() throws IOException {
        File root = new File("cache");

        storage = new AssetStorage(root);
    }

    @Test
    public void parity() throws IOException {
        Cache cache = new Cache(new File("cache"));

        for(int i = 0; i < cache.getIDXCount(); i++) {
            FileTable a = cache.getFileTable(i);
            DataTable b = null;

            try {
                b = storage.getTable(i);
            } catch (FileNotFoundException e) {
                // Ignored
            }

            if(a == null && b == null) continue;
            Assert.assertNotNull("found original, but not asset", a);
            Assert.assertNotNull("found asset, but not original", b);
            Assert.assertEquals("expect same number of files in each", a.size(), b.size());

            for(int f = 0; f < a.size(); f++) {
                ByteBuffer rawCache = null;
                ByteBuffer rawAsset = null;

                try {
                    rawCache = cache.getRaw(i, f);
                } catch (FileNotFoundException e) {
                    // Ignored
                }

                try {
                    rawAsset = b.read(f);
                } catch (FileNotFoundException e) {
                    // Ignored
                }

                if(rawCache == null && rawAsset == null) continue;
                Assert.assertNotNull("asset found, but original not", rawCache);
                Assert.assertNotNull("original found, but asset not", rawAsset);

                Assert.assertEquals("raw file size must be same", rawCache.remaining(), rawAsset.remaining());

                byte[] cacheBytes = new byte[rawCache.remaining()];
                byte[] assetBytes = new byte[rawAsset.remaining()];
                rawCache.get(cacheBytes);
                rawAsset.get(assetBytes);

                Assert.assertArrayEquals("expect files to match", cacheBytes, assetBytes);
            }
        }
    }

    @Test
    public void itemParity() throws IOException {
        Cache cache = new Cache(new File("cache"));
        FileTable originalTable = cache.getFileTable(IDX.ITEMS);

        for(int i = 0; i < originalTable.size(); i++) {
            CacheFile cacheFile;
            try {
                cacheFile = originalTable.get(i, null);
            } catch (FileNotFoundException e) {
                // Other tests prove the file exists with parity, no need to prove it here again
                continue;
            }
            Asset asset = storage.read(IDX.ITEMS, i);

            ByteBuffer cacheBuffer = cacheFile.getData();
            ByteBuffer assetBuffer = asset.getPayload();

            Assert.assertEquals(cacheBuffer.remaining(), assetBuffer.remaining());

            Archive cacheArchive = cache.getArchive(IDX.ITEMS, i);
            MultiAsset multiAsset = new MultiAsset(storage.properties(IDX.ITEMS, i), asset.getPayload());

            Assert.assertEquals(cacheArchive.size(), multiAsset.size());

            for(int j = 0; j < cacheArchive.size(); j++) {
                ByteBuffer cacheItem = cacheArchive.get(j);
                ByteBuffer assetItem = multiAsset.get(j);

                if(cacheItem == null && assetItem == null) continue;

                Assert.assertNotNull("cacheItem is null, but assetItem is not", cacheItem);
                Assert.assertNotNull("assetItem is null, but cacheItem is not", assetItem);

                Assert.assertEquals("raw file size must be same", cacheItem.remaining(), assetItem.remaining());

                byte[] cacheBytes = new byte[cacheItem.remaining()];
                byte[] assetBytes = new byte[assetItem.remaining()];
                cacheItem.get(cacheBytes);
                assetItem.get(assetBytes);

                Assert.assertArrayEquals("expect items to match", cacheBytes, assetBytes);
            }
        }
    }

    @Test
    public void itemRewriteParity() throws IOException {
        Cache cache = new Cache(new File("cache"));
        FileTable originalTable = cache.getFileTable(IDX.ITEMS);

        for(int i = 0; i < originalTable.size(); i++) {
            CacheFile cacheFile;
            try {
                cacheFile = originalTable.get(i, null);
            } catch (FileNotFoundException e) {
                // Other tests prove the file exists with parity, no need to prove it here again
                continue;
            }
            Asset asset = storage.read(IDX.ITEMS, i);

            ByteBuffer cacheBuffer = cacheFile.getData();
            ByteBuffer assetBuffer = asset.getPayload();

            Assert.assertEquals(cacheBuffer.remaining(), assetBuffer.remaining());

            Archive cacheArchive = cache.getArchive(IDX.ITEMS, i);
            MultiAsset multiAsset = new MultiAsset(storage.properties(IDX.ITEMS, i), asset.getPayload());
            multiAsset = new MultiAsset(storage.properties(IDX.ITEMS, i), multiAsset.encode());

            Assert.assertEquals(cacheArchive.size(), multiAsset.size());

            for(int j = 0; j < cacheArchive.size(); j++) {
                ByteBuffer cacheItem = cacheArchive.get(j);
                ByteBuffer assetItem = multiAsset.get(j);

                if(cacheItem == null && assetItem == null) continue;

                Assert.assertNotNull("cacheItem is null, but assetItem is not", cacheItem);
                Assert.assertNotNull("assetItem is null, but cacheItem is not", assetItem);

                Assert.assertEquals("raw file size must be same", cacheItem.remaining(), assetItem.remaining());

                byte[] cacheBytes = new byte[cacheItem.remaining()];
                byte[] assetBytes = new byte[assetItem.remaining()];
                cacheItem.get(cacheBytes);
                assetItem.get(assetBytes);

                Assert.assertArrayEquals("expect items to match", cacheBytes, assetBytes);
            }
        }
    }

    /*@Test
    public void apiRewriteTest() throws IOException {
        Cache cache = new Cache(new File("cache"));
        File out = new File("out");

        if(out.exists()) {
            for(File f : out.listFiles()) {
                f.delete();
            }
        } else {
            Assert.assertTrue("must create root folder", out.mkdir());
        }

        storage = AssetStorage.create(out);

        for(int idx = 0; idx < cache.getIDXCount(); idx++) {
            FileTable fileTable = cache.getFileTable(idx);
            ReferenceTable refTable = cache.getReferenceTable(idx);
            AssetWriter writer = storage.writer(idx);

            if(fileTable == null) continue;

            for(int file = 0; file < fileTable.size(); file++) {
                Reference ref = refTable.getReference(file);
                if (ref == null) continue;

                SubAssetReference[] children = new SubAssetReference[ref.getChildCount()];
                int childPos = 0;
                for (ChildReference childRef : ref.getChildren()) {
                    children[childPos++] = new SubAssetReference(childRef.getId(), childRef.getIdentifier());
                }

                AssetReference reference = new AssetReference(ref.getIdentifier(), ref.getCRC(), ref.getWhirlpool(), ref.getVersion(), children);
                XTEAKey key = cache.getXTEA().getKey(idx, file);
                CacheFile cacheFile;
                try {
                    cacheFile = fileTable.get(file, key);

                } catch (ZipException e) {
                    // XTEA key is missing - we can't rewrite these using the high level API
                    Asset asset = new Asset(key);
                    asset.setPayload(ByteBuffer.wrap("Contents were encrypted during translation".getBytes()));

                    writer.write(file, reference, asset);
                    continue;
                }

                Asset asset = new Asset(key);
                asset.setPayload(cacheFile.getData());
                asset.setCompression(cacheFile.getCompression());
                asset.setVersion(cacheFile.getVersion());

                writer.write(file, reference, asset);
            }

            writer.commit();
        }

        File src = new File("cache", "xteas.xstore2");
        File dst = new File(out, src.getName());
        dst.createNewFile();
        FileOutputStream fout = new FileOutputStream(dst);
        FileInputStream fin = new FileInputStream(src);
        byte[] buffer = new byte[1024];
        int n;
        while((n = fin.read(buffer)) > 0) {
            fout.write(buffer, 0, n);
        }
        fout.close();
        fin.close();

        Cache other = new Cache(out);

        for(int idx = 0; idx < cache.getIDXCount(); idx++) {
            // these keys are encoded, skip them
            if(idx == 5) continue;

            FileTable sourceFileTable = cache.getFileTable(idx);
            FileTable destFileTable = other.getFileTable(idx);
            ReferenceTable sourceRefTable = cache.getReferenceTable(idx);
            ReferenceTable destRefTable = cache.getReferenceTable(idx);

            if(sourceFileTable == null) continue;

            for(int file = 0; file < sourceFileTable.size(); file++) {
                Reference sourceRef = sourceRefTable.getReference(file);
                Reference destRef = destRefTable.getReference(file);

                if(sourceRef == null) continue;

                Assert.assertEquals(sourceRef.getChildren().size(), destRef.getChildren().size());

                XTEAKey key = cache.getXTEA().getKey(idx, file);
                CacheFile sourceCacheFile;
                try {
                    sourceCacheFile = sourceFileTable.get(file, key);
                } catch (ZipException e) {
                    // XTEA key is missing - we can't rewrite these using the high level API
                    continue;
                }

                CacheFile destCacheFile = destFileTable.get(file, key);

                ByteBuffer sourceBuffer = sourceCacheFile.getData();
                ByteBuffer destBuffer = destCacheFile.getData();

                byte[] sourceData = new byte[sourceBuffer.remaining()];
                byte[] destData = new byte[destBuffer.remaining()];

                sourceBuffer.get(sourceData);
                destBuffer.get(destData);

                Assert.assertArrayEquals("expect same data", sourceData, destData);
            }
        }
    }*/
}
