package org.maxgamer.rs.assets.codec.asset;

import net.openrs.util.crypto.Whirlpool;
import org.maxgamer.rs.assets.DataTable;
import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * @author netherfoam
 */
public class AssetWriter {
    /**
     * Performs a whirlpool function on the remaining content of the given {@link ByteBuffer}. This doesn't
     * consume the rest of the buffer, since it takes a read only copy and works off that. It avoids needlessly
     * duplicating the entire buffer as a byte array.
     *
     * @param bb the byte buffer
     * @return the hash
     */
    public static byte[] whirlpool(ByteBuffer bb) {
        bb = bb.asReadOnlyBuffer();

        Whirlpool whirlpool = new Whirlpool();
        whirlpool.NESSIEinit();

        byte[] source = new byte[1024];
        while(bb.remaining() > 0) {
            int length = Math.min(bb.remaining(), 1024);
            bb.get(source, 0, length);

            whirlpool.NESSIEadd(source, length * 8);
        }

        byte digest[] = new byte[64];
        whirlpool.NESSIEfinalize(digest);

        return digest;
    }

    /**
     * Performs a CRC32 hash on the remaining content of the given {@link ByteBuffer}. This doesn't consume
     * the rest of the buffer, since it takes a read only copy and works off that. It avoids needlessly
     * duplicating the entire buffer as a byte array
     *
     * @param bb the buffer to checksum
     * @return the crc as a 32 bit integer
     */
    public static int crc32(ByteBuffer bb) {
        bb = bb.asReadOnlyBuffer();

        CRC32 crc = new CRC32();

        byte[] source = new byte[1024];
        while(bb.remaining() > 0) {
            int length = Math.min(bb.remaining(), 1024);
            bb.get(source, 0, length);

            crc.update(source, 0, length);
        }

        return (int) crc.getValue();
    }

    private static final class WriteRequest {
        private final AssetReference reference;
        private final ByteBuffer content;
        private final XTEAKey key;

        public WriteRequest(AssetReference reference, ByteBuffer content, XTEAKey key) {
            this.reference = reference;
            this.content = content;
            this.key = key;
        }
    }

    private IndexTable indexTable;
    private DataTable masterTable;
    private DataTable dataTable;
    private XTEAStore xteas;

    private Map<Integer, WriteRequest> writes = new HashMap<>();

    /**
     * Constructs a new AssetWriter. This is a wrapper class that helps write files into
     * the cache.
     *
     * @param indexTable the decoded list of files in the index
     * @param masterTable the index table to write changes to file meta into
     * @param dataTable the data table to write changes to file contents into
     */
    public AssetWriter(IndexTable indexTable, DataTable masterTable, DataTable dataTable, XTEAStore xteas) {
        Assert.notNull(indexTable, "IndexTable may not be null");
        Assert.notNull(masterTable, "MasterTable may not be null");
        Assert.notNull(dataTable, "DataTable may not be null");
        Assert.notNull(xteas, "XTEAStore may not be null");

        this.indexTable = indexTable;
        this.masterTable = masterTable;
        this.dataTable = dataTable;
        this.xteas = xteas;
    }

    /**
     * Appends the given write to the list of writes to perform when {@link #commit()} is called. This does not
     * modify the given {@link ByteBuffer}, as it takes a read-only copy for later.
     *
     * @param id the id of the file to write to
     * @param reference the existing reference to the file
     * @param asset the asset to write for the file
     * @return this
     */
    public AssetWriter write(int id, AssetReference reference, Asset asset) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.notNull(reference, "Reference may not be null");
        Assert.notNull(asset, "Asset may not be null");

        // Take a read only copy for writing later
        ByteBuffer content = asset.encode();

        if(asset.getVersion() != -1) {
            // We don't checksum or hash the version
            content.limit(content.limit() - 2);
        }

        // We don't want to modify the original, in case it was pulled from the cache (until we commit)
        reference = reference.copy();
        reference.whirlpool = whirlpool(content);
        reference.crc = crc32(content);

        if(asset.getVersion() != -1) {
            // Reset limit
            content.limit(content.limit() + 2);
        }

        // We assert that asset.getVersion() == reference.getVersion()
        Assert.isTrue(asset.getVersion() == -1 || reference.getVersion() == asset.getVersion(), "Asset version must equal reference version, if set");

        writes.put(id, new WriteRequest(reference, content, asset.getKey()));

        return this;
    }

    /**
     * Appends the given write to the list of writes to perform when {@link #commit()} is called. This does not
     * modify the given {@link ByteBuffer}, as it takes a read-only copy for later. This increments the version
     * on the asset, or takes some sane defaults if the asset doesn't exist.
     *
     * @param id the id of the file to write to
     * @param asset the asset to write for the file
     * @return this
     */
    public AssetWriter write(int id, Asset asset) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.notNull(asset, "Asset may not be null");

        AssetReference reference = this.indexTable.getReferences().get(id);

        if(reference == null) {
            int version = asset.getVersion();
            if(version == -1) {
                reference = AssetReference.create(1);
            } else {
                reference = AssetReference.create(version);
            }
        } else {
            reference = new AssetReference(reference.getIdentifier(), 0, null, reference.getVersion() + 1, reference.getChildren());
            asset.setVersion(reference.getVersion());
        }

        return write(id, reference, asset);
    }

    /**
     * Appends the given write to the list of writes to perform when {@link #commit()} is called. This does not
     * modify the given {@link ByteBuffer}, as it takes a read-only copy for later. This increments the version
     * on the asset, or takes some sane defaults if it doesn't exist.
     *
     * @param id the id of the file to write to
     * @param multi the assets to write for the file
     * @return this
     */
    public AssetWriter write(int id, MultiAsset multi) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.notNull(multi, "MultiAsset may not be null");

        Asset asset;
        XTEAKey key = xteas.getKey(indexTable.getIdx(), id);
        try {
            asset = new Asset(key, dataTable.read(id));
        } catch (FileNotFoundException e) {
            // Asset doesn't exist yet
            asset = new Asset(key);
        }

        AssetReference reference = this.indexTable.getReferences().get(id);
        if(reference == null) {
            SubAssetReference[] children = new SubAssetReference[multi.size()];
            int index = 0;
            Iterator<Map.Entry<Integer, ByteBuffer>> mit = multi.iterator();

            while(mit.hasNext()) {
                Map.Entry<Integer, ByteBuffer> entry = mit.next();
                children[index++] = new SubAssetReference(entry.getKey(), 0);
            }

            if(asset.getVersion() == -1) {
                reference = AssetReference.create(1, children);
            } else {
                reference = AssetReference.create(asset.getVersion() + 1, children);
            }
        }

        multi = multi.copy(reference);
        Assert.isTrue(multi.isComplete(), "Expect MultiAsset to be complete");
        asset.setPayload(multi.encode());
        asset.setVersion(reference.getVersion());

        return write(id, reference, asset);
    }

    /**
     * Appends the given write to the list of writes to perform when {@link #commit()} is called. This does not
     * modify the given {@link ByteBuffer}, as it takes a read-only copy for later. This increments the version
     * on the asset, or takes some sane defaults if it doesn't exist.
     *
     * @param id the id of the file to write to
     * @param subAsset the id (not index) of the sub asset to write to
     * @param content the contents of the sub asset
     * @return this
     */
    public AssetWriter write(int id, int subAsset, ByteBuffer content) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.isPositive(subAsset, "Sub Asset Id must be positive");
        Assert.notNull(content, "Content may not be null");

        AssetReference reference;
        Asset asset;
        MultiAsset multi;

        if(writes.containsKey(id)) {
            // We've got an existing write request. We need to operate off of it, instead
            // of the data currently written to the cache
            WriteRequest request = this.writes.get(id);

            if(request == null) {
                // The existing write request is a delete request. We make our previous reference
                // a dummy one at version 0 with no children
                reference = AssetReference.create(0);
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id));
                multi = new MultiAsset(reference);
            } else {
                // The existing write request can be modified
                reference = request.reference;
                asset = new Asset(request.key, request.content);
                multi = new MultiAsset(reference, asset.getPayload());
            }
        } else {
            // We've got no existing write request (or it's a delete request). It's okay
            // to read from the cache
            reference = indexTable.getReferences().get(id);
            if(reference == null) {
                reference = AssetReference.create(0);
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id));
                multi = new MultiAsset(reference);
            } else {
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id), dataTable.read(id));
                multi = new MultiAsset(reference, asset.getPayload());
            }
        }

        // Append the new child to the existing multi asset, increment the versions, and save.
        SubAssetReference[] children;
        int index = reference.indexOf(subAsset);
        if(index == -1) {
            // We're adding a new child
            children = new SubAssetReference[reference.getChildCount() + 1];
        } else {
            // We're replacing an existing child
            children = new SubAssetReference[reference.getChildCount()];
        }

        for(int i = 0; i < reference.getChildCount(); i++) {
            children[i] = reference.getChild(i);
        }

        if(index == -1) index = children.length - 1;
        children[index] = new SubAssetReference(subAsset, 0);
        reference = new AssetReference(reference.getIdentifier(), 0, null, reference.getVersion() + 1, children);

        multi.put(subAsset, content.asReadOnlyBuffer());
        multi = multi.copy(reference);
        Assert.isTrue(multi.isComplete(), "Expect multi asset to be complete");
        asset.setPayload(multi.encode());
        asset.setVersion(reference.getVersion());

        return write(id, reference, asset);
    }

    /**
     * Delete an existing sub-asset. This doesn't delete the parent file.
     * @param id the id of the parent
     * @param subAsset the id of the child
     * @return this
     * @throws IOException if the file can't be deleted
     */
    public AssetWriter delete(int id, int subAsset) throws IOException {
        Assert.isPositive(id, "File ID must be positive");
        Assert.isPositive(subAsset, "Sub Asset ID must be positive");

        AssetReference reference;
        Asset asset;
        MultiAsset multi;

        if(writes.containsKey(id)) {
            // We've got an existing write request. We need to operate off of it, instead
            // of the data currently written to the cache
            WriteRequest request = this.writes.get(id);

            if(request == null) {
                // The existing write request is a delete request. We make our previous reference
                // a dummy one at version 0 with no children
                reference = AssetReference.create(0);
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id));
                multi = new MultiAsset(reference);
            } else {
                // The existing write request can be modified
                reference = request.reference;
                asset = new Asset(request.key, request.content);
                multi = new MultiAsset(reference, asset.getPayload());
            }
        } else {
            // We've got no existing write request (or it's a delete request). It's okay
            // to read from the cache
            reference = indexTable.getReferences().get(id);
            if(reference == null) {
                reference = AssetReference.create(0);
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id));
                multi = new MultiAsset(reference);
            } else {
                asset = new Asset(xteas.getKey(indexTable.getIdx(), id), dataTable.read(id));
                multi = new MultiAsset(reference, asset.getPayload());
            }
        }

        // Append the new child to the existing multi asset, increment the versions, and save.
        SubAssetReference[] children;
        int index = reference.indexOf(subAsset);
        if(index == -1) {
            throw new FileNotFoundException("Subfile at " + id + ", " + subAsset + ") doesn't exist");
        } else {
            // We're deleting an existing child
            children = new SubAssetReference[reference.getChildCount() - 1];
        }

        // Copy all of the previous children, skipping the deleted one
        for(int i = 0; i < index; i++) {
            children[i] = reference.getChild(i);
        }

        for(int i = index; i < reference.getChildCount() - 1; i++) {
            children[i] = reference.getChild(i + 1);
        }

        reference = new AssetReference(reference.getIdentifier(), 0, null, reference.getVersion() + 1, children);

        // Delete the data from the asset
        multi.put(subAsset, null);
        multi = multi.copy(reference);

        Assert.isTrue(multi.isComplete(), "Expect multi asset to be complete");
        asset.setPayload(multi.encode());
        asset.setVersion(reference.getVersion());

        return write(id, reference, asset);
    }

    /**
     * Deletes the given asset
     * @param id the asset to delete
     * @return this
     * @throws IOException if the asset can't be deleted
     */
    public AssetWriter delete(int id) throws IOException {
        Assert.isPositive(id, "File ID must be positive");

        writes.put(id, null);

        return this;
    }

    /**
     * Writes all of the contents to the data table and the meta object, then writes the encoded meta
     * object to the data table as well
     *
     * @throws IOException if the data can't be written
     */
    public void commit() throws IOException {
        if(writes.isEmpty()) {
            // No assets to be written!
            return;
        }

        // First, we write all of updated contents of files to disk
        for(Map.Entry<Integer, WriteRequest> entry : writes.entrySet()) {
            int id = entry.getKey();
            WriteRequest writeRequest = entry.getValue();

            if(writeRequest == null) {
                indexTable.getReferences().remove(id);
                dataTable.erase(id);
                xteas.setKey(indexTable.getIdx(), id, null);
            } else {
                AssetReference ref = writeRequest.reference;
                ByteBuffer bb = writeRequest.content;

                indexTable.getReferences().put(id, ref);
                dataTable.write(id, bb);

                // Update the encryption key in the XTEA store
                xteas.setKey(indexTable.getIdx(), id, writeRequest.key);
            }
        }

        // Now we write the new meta information about the assets. First, we must compress the
        // encoded information though, using an Asset wrapper, which supports compression.
        Asset wrapper = indexTable.toAsset();

        // Finally, save to disk
        masterTable.write(indexTable.getIdx(), wrapper.encode());
    }
}
