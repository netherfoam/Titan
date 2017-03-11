package org.maxgamer.rs.structure.dbmodel;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * A class used by the @Mapping annotation that will help deserialize
 * a field
 *
 * @author netherfoam
 */
public interface Serializer {
    /**
     * Serializes the given object, and places it in the map under an appropriate name,
     * usually the name of the field. The simplest form of this is just map.put(field.getName(), o)
     * but the operation may be quite extensive.
     *
     * @param map   the map of column name to column value to modify
     * @param field the field that is being serialized
     * @param o     the object which is having its field serialized
     */
    void serialize(Map<String, Object> map, Field field, Object o);

    /**
     * Deserializes this object from teh given result set and returns it. The simplest
     * for will be return rs.getXXXX(field.getName()), but this operation may be quite
     * extensive.
     *
     * @param field the field that is being deserialized
     * @param rs    the result set, positioned at the row to parse. Contains the field data
     * @return the object. The result should be castable to field.getType(). May be null for non-primitive values
     * @throws SQLException
     */
    Object deserialize(Field field, ResultSet rs) throws SQLException;
}