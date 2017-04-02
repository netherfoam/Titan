package org.maxgamer.rs.fs;

import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.assets.codec.asset.AssetWriter;
import org.maxgamer.rs.assets.codec.asset.IndexTable;
import org.maxgamer.rs.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class AssetStorageRealityTest {
    private AssetStorage storage;

    @Before
    public void init() throws IOException {
        storage = new AssetStorage(new File("cache")) {
            @Override
            public AssetWriter writer(int idx) throws IOException {
                // We don't want to accidentally modify the real cache when performing these tests
                throw new IllegalStateException("Can't touch this");
            }
        };
    }

    @Test
    public void rewriteMasterIndex() throws IOException {
        for(int i = 0; i < storage.size(); i++) {
            IndexTable index;
            try {
                index = storage.getIndex(i);
            } catch (FileNotFoundException e) {
                continue;
            }

            ByteBuffer rawOriginal = storage.getMasterTable().read(i);

            // Parity check block
            ByteBuffer generated = index.encode();
            ByteBuffer rawGenerated = Asset.create(null, index.getCompression(), -1, generated).encode();

            // Both buffers are now encoded using the same compression, however different systems
            // may encode the same files differently, I guess. So we should decompress them first
            // before comparing them.

            Asset finalOriginal = new Asset(null, rawOriginal);
            Asset finalGenerated = new Asset(null, rawGenerated);

            ByteBuffer abb = finalOriginal.getPayload();
            ByteBuffer bbb = finalGenerated.getPayload();

            while(abb.hasRemaining() || bbb.hasRemaining()) {
                if(abb.get() == bbb.get()) continue;

                Assert.fail("Differing at position " + abb.position() + ".." + bbb.position());
            }
        }
    }

    @Test
    public void rewriteMasterIndexAsset() throws IOException {
        for(int i = 0; i < storage.size(); i++) {
            IndexTable index;
            try {
                index = storage.getIndex(i);
            } catch (FileNotFoundException e) {
                continue;
            }

            ByteBuffer raw = new Asset(null, storage.getMasterTable().read(i)).getPayload();

            // Parity check block
            ByteBuffer expected = index.encode();

            Assert.equal(raw.remaining(), expected.remaining());

            while (raw.hasRemaining() || expected.hasRemaining()) {
                if (raw.get() == expected.get()) continue;

                Assert.fail("Differing at position " + raw.position() + ".." + expected.position());
            }
        }
    }
}
