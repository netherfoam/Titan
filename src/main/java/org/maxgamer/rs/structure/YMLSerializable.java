package org.maxgamer.rs.structure;

import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

/**
 * Represents an object which may be serialized and deserialized from a YML
 * config.
 *
 * @author netherfoam
 */
public interface YMLSerializable {
    /**
     * Serializes this object into a map.
     *
     * @return the map
     */
    MutableConfig serialize();

    /**
     * Deserializes this object from the given map.
     *
     * @param map the map.
     */
    void deserialize(MutableConfig map);
}