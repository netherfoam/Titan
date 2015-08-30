package org.maxgamer.structure.dbmodel;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class FixedArraySerializer implements Serializer{

	@Override
	public void serialize(Map<String, Object> map, Field field, Object o) {
		for(int i = Array.getLength(o) - 1; i >= 0; i--){
			map.put(field.getName() + i, Array.get(o, i));
		}
	}

	@Override
	public Object deserialize(Field field, ResultSet rs) throws SQLException {
		int max = 0;
		
		for(int i = rs.getMetaData().getColumnCount(); i > 0; i--){
			String name = rs.getMetaData().getColumnName(i);
			if(name.startsWith(field.getName()) == false) continue;
				
			max = Math.max(max, Integer.parseInt(name.substring(field.getName().length())));
		}
		
		Class<?> type = field.getType().getComponentType();
		
		if(type.isPrimitive()){
			if(type == int.class){
				int[] values = new int[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getInt(field.getName() + i);
				}
				return values;
			}
			if(type == byte.class){
				byte[] values = new byte[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getByte(field.getName() + i);
				}
				return values;
			}
			if(type == short.class){
				short[] values = new short[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getShort(field.getName() + i);
				}
				return values;
			}
			if(type == long.class){
				long[] values = new long[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getLong(field.getName() + i);
				}
				return values;
			}
			if(type == double.class){
				double[] values = new double[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getDouble(field.getName() + i);
				}
				return values;
			}
			if(type == float.class){
				float[] values = new float[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getFloat(field.getName() + i);
				}
				return values;
			}
			if(type == char.class){
				char[] values = new char[max+1];
				for(int i = 0; i <= max; i++){
					String s = rs.getString(field.getName() + i);
					if(s.isEmpty()) continue;
					values[i] = s.charAt(0);
				}
				return values;
			}
			if(type == boolean.class){
				boolean[] values = new boolean[max+1];
				for(int i = 0; i <= max; i++){
					values[i] = rs.getBoolean(field.getName() + i);
				}
				return values;
			}
		}
		if(type == String.class){
			String[] values = new String[max+1];
			for(int i = 0; i < max; i++){
				values[i] = rs.getString(field.getName() + i);
			}
			return values;
		}
		
		throw new RuntimeException(field.getName() + " is not an array of primitive types! Its type is " + type.getCanonicalName() + "[]");
	}
	
}