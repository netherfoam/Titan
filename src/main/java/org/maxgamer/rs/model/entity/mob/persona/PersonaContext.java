package org.maxgamer.rs.model.entity.mob.persona;

import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.assets.formats.BitVarConfigFormat;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.MobContext;
import org.maxgamer.rs.util.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class PersonaContext extends MobContext {
    public void set(int id, int value) {
        configs.put(id, value);
    }

    public void setBit(int bConfigId, int value) {
        try {
            ByteBuffer content = Core.getCache().archive(IDX.CONFIGS, bConfigId >> 10).get(bConfigId & 0x3FF);
            BitVarConfigFormat config = new BitVarConfigFormat(content);

            int previous = get(config.getId());
            int start = config.getStart();
            int end = config.getEnd();
            int length = end - start;

            // If our value we're setting is outside the range, then that's no good!
            Assert.equal((value >> start) & BIT_MASKS[length], value);

            // Now we clear all the bits we're setting.
            int mask = BIT_MASKS[length];
            int current = previous & ~(mask << start);
            current |= value << start;

            set(config.getId(), current);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
