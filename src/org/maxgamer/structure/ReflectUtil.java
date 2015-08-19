package org.maxgamer.structure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectUtil{
	public static String describe(Object o){
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(o.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = o.getClass().getDeclaredFields();

		//print field names paired with their values
		for (Field field : fields) {
			if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
				continue; //Field is static
			}
			boolean access = field.isAccessible();
			if(!access) field.setAccessible(true);
			
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				//requires access to private field: 
				Object v = field.get(o);
				
				if(v instanceof Object[]){
					result.append(Arrays.toString((Object[]) v));
				}
				else if(v instanceof boolean[]){
					result.append(Arrays.toString((boolean[]) v));
				}
				else if(v instanceof char[]){
					result.append(Arrays.toString((char[]) v));
				}
				else if(v instanceof byte[]){
					result.append(Arrays.toString((byte[]) v));
				}
				else if(v instanceof short[]){
					result.append(Arrays.toString((short[]) v));
				}
				else if(v instanceof int[]){
					result.append(Arrays.toString((int[]) v));
				}
				else if(v instanceof long[]){
					result.append(Arrays.toString((long[]) v));
				}
				else if(v instanceof double[]){
					result.append(Arrays.toString((double[]) v));
				}
				else if(v instanceof float[]){
					result.append(Arrays.toString((float[]) v));
				}
				else{
					result.append(field.get(o));
				}
			}
			catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
			if(!access) field.setAccessible(false);
		}
		result.append("}");

		return result.toString();
	}
}