package org.maxgamer.rs.assets.codec.asset;

import org.maxgamer.rs.assets.codec.Codec;
import org.maxgamer.rs.cache.RSCompression;
import org.maxgamer.rs.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author netherfoam
 */
public final class IndexTable extends Codec {
    /**
     * A flag which indicates this {@link IndexTable} contains
     * {@link Djb2} hashed identifiers.
     */
    public static final int FLAG_IDENTIFIERS = 0x01;

    /**
     * A flag which indicates this {@link IndexTable} contains
     * whirlpool digests for its entries.
     */
    public static final int FLAG_WHIRLPOOL = 0x02;

    public static int djb2(String djb2) {
        int count = 0;
        String s = djb2.toLowerCase(); // The client forces all names to be lowercase.
        byte[] characters = s.getBytes();
        for (int i = 0; i < s.length(); i++) {
            count = (characters[i] & 0xff) + ((count << 5) - count);
        }

        return count;
    }

    private int idx;
    private int format = 6;
    private int version = 1;
    private int flags = 0;

    private TreeMap<Integer, AssetReference> references;

    private RSCompression compression = RSCompression.NONE;

    public IndexTable(int idx, int version) {
        super();

        Assert.isPositive(idx, "Index must be positive");
        Assert.isPositive(version, "Version must be positive");
        Assert.isTrue(idx < 255, "Idx must be 0-254");

        this.idx = idx;
        this.references = new TreeMap<>();
        this.version = version;
    }

    public IndexTable(int idx, ByteBuffer content) {
        this.idx = idx;

        decode(content);
    }

    public int getFile(String djb2) throws FileNotFoundException {
        if((flags & FLAG_IDENTIFIERS) == 0) throw new FileNotFoundException("IndexTable doesn't have identifiers");

        int hash = djb2(djb2);

        for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            if(entry.getValue().getIdentifier() == hash) return entry.getKey();
        }

        throw new FileNotFoundException("No such file by hash: " + djb2);
    }

    public int getIdx() {
        return idx;
    }

    public TreeMap<Integer, AssetReference> getReferences() {
        return references;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void decode(ByteBuffer bb) {
        references = new TreeMap<>();

        if(!bb.hasRemaining()) {
            format = 6;
            version = 1;
            flags = 0;
            return;
        }

        format = bb.get() & 0xFF;

        if (format >= 6) {
            version = bb.getInt();
        }

        flags = bb.get() & 0xFF;

        /* the number of references */
        int size = bb.getShort() & 0xFFFF;

        /* read the ids */
        int[] ids = new int[size];
        int accumulator = 0;
        size = -1;
        for (int i = 0; i < ids.length; i++) {
            int delta = bb.getShort() & 0xFFFF;
            ids[i] = accumulator + delta;
            accumulator = ids[i];

            if (ids[i] > size) {
                size = ids[i];
            }
        }
        size++;

        /* and allocate specific entries within that array */
        for (int id : ids) {
            references.put(id, new AssetReference());
        }

        if ((flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                references.get(id).identifier = bb.getInt();
            }
        }

        /* read the CRC32 checksums */
        for (int id : ids) {
            references.get(id).crc = bb.getInt();
        }

        /* read the whirlpool digests if present */
        if ((flags & FLAG_WHIRLPOOL) != 0) {
            for (int id : ids) {
                bb.get(references.get(id).whirlpool);
            }
        }

        /* read the version numbers */
        for (int id : ids) {
            references.get(id).version = bb.getInt();
        }

        /* read the child sizes */
        int[][] members = new int[size][];
        for (int id : ids) {
            int length = bb.getShort() & 0xFFFF;
            members[id] = new int[length];
            references.get(id).children = new SubAssetReference[length];
        }

        /* read the child ids */
        for (int id : ids) {
            /* reset the accumulator and size */
            accumulator = 0;

            /* loop through the array of ids */
            for (int i = 0; i < members[id].length; i++) {
                int delta = bb.getShort() & 0xFFFF;
                members[id][i] = accumulator += delta;
            }

            /* and allocate specific entries within the array */
            for (int i = 0; i < members[id].length; i++) {
                int childId = members[id][i];
                SubAssetReference c = new SubAssetReference();
                c.id = childId;
                references.get(id).children[i] = c;
            }
        }

        /* read the child identifiers if present */
        if ((flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                for (int i = 0; i < members[id].length; i++) {
                    SubAssetReference c = references.get(id).children[i];
                    c.identifier = bb.getInt();
                }
            }
        }
    }

    public ByteBuffer encode() {
        // Length:
        // format: +1
        // if format > 6: +4
        // flags: +1
        // size: +2
        // ids: + (size * 2)
        // if flags & FLAG_IDENTIFIERS: + (4 * size)
        // crc: + (4 * size)
        // if flags & FLAG_WHIRLPOOL: + (64 * size)
        // version: + (4 * size)
        // children sizes: + (2 * size)
        // children ids: + (2 * size * (sum child count))
        // if flags & FLAG_IDENTIFIERS: + (4 * size * (sum child count))

        // format, flags, size
        int length = 4;

        // table version
        if(format >= 6) length += 4;

        // file ids
        length += references.size() * 2;

        // identifiers (hash) of each file
        if((flags & FLAG_IDENTIFIERS) != 0) length += 4 * references.size();

        // CRC of each file
        length += 4 * references.size(); // length should now be 14 at the end of this

        // Whirlpool hash of each file
        if((flags & FLAG_WHIRLPOOL) != 0) length += 64 * references.size();

        // Version of each file
        length += 4 * references.size();

        // Number of children in each file
        length += 2 * references.size();

        int totalChildren = 0;
        for(AssetReference ref : references.values()) {
            totalChildren += ref.getChildCount();
        }

        // Child id's inside each file
        length += 2 * totalChildren;

        if((flags & FLAG_IDENTIFIERS) != 0) length += 4 * totalChildren;

        ByteBuffer bb = ByteBuffer.allocate(length);

        bb.put((byte) format);

        if(format >= 6) {
            bb.putInt(version);
        }

        bb.put((byte) flags);
        bb.putShort((short) references.size());

        int accumulator = 0;
        for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            int delta = entry.getKey() - accumulator;
            accumulator = entry.getKey();

            bb.putShort((short) delta);
        }

        if((flags & FLAG_IDENTIFIERS) != 0) {
            for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
                bb.putInt(entry.getValue().getIdentifier());
            }
        }

        for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            bb.putInt(entry.getValue().getCRC());
        }

        if((flags & FLAG_WHIRLPOOL) != 0) {
            for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
                bb.put(entry.getValue().getWhirlpool());
            }
        }

        for(Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            bb.putInt(entry.getValue().getVersion());
        }

        for (Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            bb.putShort((short) entry.getValue().getChildCount());
        }

        for (Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
            accumulator = 0;
            AssetReference asset = entry.getValue();

            for(int i = 0; i < asset.getChildCount(); i++) {
                int delta = i - accumulator;
                accumulator = i;

                bb.putShort((short) delta);
            }
        }

        if ((flags & FLAG_IDENTIFIERS) != 0) {
            for (Map.Entry<Integer, AssetReference> entry : references.entrySet()) {
                AssetReference asset = entry.getValue();

                for (SubAssetReference subAsset : asset.getChildren()) {
                    bb.putInt(subAsset.getIdentifier());
                }
            }
        }

        Assert.isTrue(!bb.hasRemaining(), "Expect to have calculated exact length");

        bb.flip();

        return bb;
    }

    public Asset toAsset() throws IOException {
        return Asset.create(null, compression, -1, this.encode());
    }
}
