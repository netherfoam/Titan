package org.maxgamer.structure.dbmodel;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PrimitiveSerializer implements Serializer{

	@Override
	public void serialize(Map<String, Object> map, Field field, Object o) {
		map.put(field.getName(), o);
	}

	@Override
	public Object deserialize(Field field, ResultSet rs) throws SQLException {
		Class<?> type = field.getType();
		if(field.getType().isPrimitive()){
			if(type == int.class){
				return rs.getInt(field.getName());
			}
			if(type == byte.class){
				return rs.getByte(field.getName());
			}
			if(type == short.class){
				return rs.getShort(field.getName());
			}
			if(type == long.class){
				return rs.getLong(field.getName());
			}
			if(type == double.class){
				return rs.getDouble(field.getName());
			}
			if(type == float.class){
				return rs.getFloat(field.getName());
			}
			if(type == char.class){
				String s = rs.getString(field.getName());
				if(s == null || s.length() <= 0) return null;
				return s.charAt(0);
			}
			if(type == boolean.class){
				return rs.getBoolean(field.getName());
			}
		}
		if(type == String.class){
			return rs.getString(field.getName());
		}
		
		throw new RuntimeException(field.getName() + " is not a primitive field! Its type is " + type.getCanonicalName());
	}
}