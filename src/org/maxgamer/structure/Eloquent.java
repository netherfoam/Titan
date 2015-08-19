package org.maxgamer.structure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Represents a data model class which stores a particular result set from a database,
 * with convenient insert, update and delete functions.
 * @author Dirk
 */
public class Eloquent{
	/** The name of the tabel this object is being loaded from */
	private String table;
	/** The current values for the row */
	private HashMap<String, Object> fields = new HashMap<String, Object>();
	/** The old values for the row, which have been overwritten in the fields variable */
	private HashMap<String, Object> oldFields = new HashMap<String, Object>();
	
	/**
	 * Creates a new Eloquent object
	 * @param table The table name
	 */
	public Eloquent(String table){
		this.table = table;
	}
	
	/**
	 * Removes this object from the database.
	 * @param con The connection to the database.
	 * @throws SQLException If there was an error performing the query
	 */
	public void delete(Connection con) throws SQLException{
		StringBuilder sb = new StringBuilder("DELETE FROM " + this.table + " WHERE ");
		Iterator<Entry<String, Object>> keys = fields.entrySet().iterator();
		Object[] values = new Object[fields.size()];
		int i = 0;
		
		Entry<String, Object> e = keys.next();
		sb.append(e.getKey() + " = ?");
		values[i++] = e.getValue();
		
		while(keys.hasNext()){
			e = keys.next();
			sb.append(" AND " + e.getKey() + " = ?");
			values[i++] = e.getValue();
		}
		sb.append(" LIMIT 1");
		
		PreparedStatement ps = con.prepareStatement(sb.toString());
		for(i = 0; i < values.length; i++){
			ps.setString(i + 1, String.valueOf(values[i]));
		}
		
		//Log.debug(ps.toString());
		ps.execute();
	}
	
	/**
	 * Inserts this object into the database.
	 * @param con The connection
	 * @throws SQLException If there was an error performing the query
	 */
	public void insert(Connection con) throws SQLException{
		StringBuilder sb = new StringBuilder("INSERT INTO " + this.table + " ( ");
		Iterator<Entry<String, Object>> keys = fields.entrySet().iterator();
		Object[] values = new Object[fields.size()];
		int i = 0;
		
		Entry<String, Object> e = keys.next();
		sb.append(e.getKey());
		values[i++] = e.getValue();
		
		while(keys.hasNext()){
			e = keys.next();
			sb.append(", " + e.getKey());
			values[i++] = e.getValue();
		}
		sb.append(") VALUES (?");
		
		for(i = 1; i < values.length; i++){
			sb.append(", ?");
		}
		sb.append(")");
		
		PreparedStatement ps = con.prepareStatement(sb.toString());
		for(i = 0; i < values.length; i++){
			ps.setString(i + 1, String.valueOf(values[i]));
		}
		
		//Log.debug(ps.toString());
		ps.execute();
	}
	
	/**
	 * Updates this object in the database. This method modifies a previously
	 * saved object.
	 * @param con The connection
	 * @throws SQLException If there was an error performing the query
	 */
	public void update(Connection con) throws SQLException{
		StringBuilder sb = new StringBuilder("UPDATE " + this.table + " SET ");
		Iterator<Entry<String, Object>> entry = fields.entrySet().iterator();
		Object[] values = new Object[fields.size()];
		String[] keys = new String[fields.size()];
		int i = 0;
		
		Entry<String, Object> e = entry.next();
		sb.append(e.getKey() + " = ?");
		keys[i] = e.getKey();
		values[i++] = e.getValue();
		
		while(entry.hasNext()){
			e = entry.next();
			keys[i] = e.getKey();
			values[i++] = e.getValue();
			
			sb.append(", " + e.getKey() + " = ?");
			
		}
		sb.append(" WHERE ");
		
		i = 0;
		
		sb.append(keys[i++] + " = ?");
		
		while(i < keys.length){
			//"Nothing compares to null!"
			// - Not even null itself. This is a best of both worlds workaround.
			if((oldFields.containsKey(keys[i]) && oldFields.get(keys[i]) == null) || (values[i] == null)){
				sb.append(" AND (" + keys[i] + " IS NULL OR " + keys[i] + " = ?)");
			}
			else{
				sb.append(" AND " + keys[i] + " = ?");
			}
			
			i++;
		}
		
		PreparedStatement ps = con.prepareStatement(sb.toString());
		for(i = 0; i < values.length; i++){
			ps.setString(i + 1, String.valueOf(values[i]));
			
			if(oldFields.containsKey(keys[i])){
				//If we have an old field, use it, that's what is in the database currently
				ps.setObject((i + values.length + 1), oldFields.get(keys[i]));
			}
			else{
				//This field hasn't changed from what is in the database.
				ps.setObject((i + values.length + 1), values[i]);
			}
		}
		
		ps.execute();
		
		oldFields.clear(); //We've updated the database.
	}
	
	/**
	 * Returns the table this record is from
	 * @return the table this record is from
	 */
	public String getTable(){
		return table;
	}
	
	/**
	 * Fetches all fields names in the table. This array is a copy.
	 * @return The field names in the table.
	 */
	protected String[] getFields(){
		return fields.keySet().toArray(new String[fields.size()]);
	}
	
	/**
	 * Fetches the value of a field by name
	 * @param field the field to retrieve the value of
	 * @return the field.
	 */
	protected Object getField(String field){
		return fields.get(field);
	}
	
	/**
	 * Sets the value of a field by name.
	 * Caution should be taken here, that the table can store this type of data.
	 * @param field the field to set the value of
	 * @param value the value
	 * @return the previous value.
	 */
	protected Object setField(String field, Object value){
		Object old;
		if(fields.containsKey(field)){
			old = fields.put(field, value);
			if(oldFields.containsKey(field) == false){
				oldFields.put(field, old);
			}
		}
		else{
			old = fields.put(field, value);
		}
		
		return old;
	}
	
	/**
	 * Fetches the String in the given field
	 * @param field the field
	 * @return the String
	 * @throws ClassCastException if the field does not represent a String.
	 */
	public String getString(String field){
		return (String) fields.get(field);
	}
	
	/**
	 * Fetches the float in the given field
	 * @param field the field
	 * @return the float
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public float getFloat(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).floatValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the double in the given field
	 * @param field the field
	 * @return the double
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public double getDouble(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).doubleValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the long in the given field
	 * @param field the field
	 * @return the long
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public long getLong(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).longValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the int in the given field
	 * @param field the field
	 * @return the int
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public int getInt(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).intValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the short in the given field
	 * @param field the field
	 * @return the short
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public short getShort(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).shortValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the byte in the given field
	 * @param field the field
	 * @return the byte
	 * @throws ClassCastException if the field does not represent a number.
	 */
	public byte getByte(String field){
		Object v = getField(field);
		if(v instanceof Number) return ((Number) v).byteValue();
		else throw new ClassCastException(v.getClass() + " cannot be cast to " + Float.class.getSimpleName());
	}
	
	/**
	 * Fetches the boolean in the given field
	 * @param field the field
	 * @return the boolean
	 * @throws ClassCastException if the field does not represent a boolean or a number.
	 */
	public boolean getBoolean(String field){
		Object v = getField(field);
		if(v instanceof Number){
			return ((Number) v).longValue() != 0;
		}
		return  ((Boolean) fields.get(field)).booleanValue();
	}
	
	/**
	 * Loads the record from the given ResultSet.
	 * @param rs The result set
	 * @throws SQLException If something went wrong
	 */
	public void load(ResultSet rs) throws SQLException{
		int cols = rs.getMetaData().getColumnCount();
		for(int i = 1; i <= cols; i++){
			String name = rs.getMetaData().getColumnName(i);
			
			if(rs.getObject(i) == null){
				fields.put(name, null);
				continue;
			}
			
			switch(rs.getMetaData().getColumnType(i)){
				case Types.ARRAY:
				case Types.VARBINARY:
				case Types.BLOB:
				case Types.CLOB:
				case Types.DATALINK:
				case Types.DATE:
				case Types.DISTINCT:
				case Types.JAVA_OBJECT:
				case Types.NULL:
				case Types.NCHAR:
				case Types.NCLOB:

				case Types.OTHER:
				case Types.REAL:
				case Types.REF:
				case Types.ROWID:
				case Types.SQLXML:
				case Types.STRUCT:
				case Types.BINARY:
					throw new SQLException("Unsupported database data type for column " + rs.getMetaData().getColumnName(i) + " detected type is " + rs.getMetaData().getColumnTypeName(i) + "(" + rs.getMetaData().getColumnType(i) + ")");
				
				case Types.BIT:
				case Types.BOOLEAN:
					fields.put(name, rs.getBoolean(i));
					break;
					
				case Types.CHAR: //Fixed length
				case Types.VARCHAR: //Variabel length
				case Types.LONGNVARCHAR:
				case Types.LONGVARBINARY:
				case Types.LONGVARCHAR:
				case Types.NVARCHAR:
					fields.put(name, rs.getString(i));
					break;
					
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.DECIMAL:
					fields.put(name, rs.getDouble(i));
					break;
					
				case Types.FLOAT:
					fields.put(name, rs.getFloat(i));
					break;
					
				case Types.BIGINT:
				case Types.TIME:
				case Types.TIMESTAMP:
				case Types.INTEGER: //SQLite reports integer type when a long is required.
					fields.put(name, rs.getLong(i));
					break;
					
				case Types.SMALLINT:
					fields.put(name, rs.getShort(i));
					break;
					
				case Types.TINYINT:
					fields.put(name, rs.getByte(i));
					break;
			}
		}
	}
}