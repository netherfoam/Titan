package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.assets.formats.BitVarConfigFormat;
import org.maxgamer.rs.core.Core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class MobContext {
    /**
     * An array of masks, with each value containing bits 0 to (index + 1) set. Eg, index 0 has the first bit set, index 1
     * has the first and second bits set.  This is useful for extracting only a subset of values out of a bit config.
     */
    protected static final int[] BIT_MASKS = new int[32];

    static {
        int i = 2;
        for (int index = 0; index < BIT_MASKS.length; index++) {
            BIT_MASKS[index] = i - 1;
            i += i;
        }
    }

    protected Map<Integer, Integer> configs = new HashMap<>();

    public int get(int id) {
        Integer v = configs.get(id);
        if(v == null) return 0;

        return v.intValue();
    }

    public int getBit(int bConfigId) {
        try {
            ByteBuffer content = Core.getCache().archive(IDX.CONFIGS, bConfigId >> 10).get(bConfigId & 0x3FF);
            BitVarConfigFormat config = new BitVarConfigFormat(content);
            int v = get(config.getId());

            int start = config.getStart();
            int end = config.getEnd();
            int length = end - start;

            return (v >> start) & BIT_MASKS[length];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
