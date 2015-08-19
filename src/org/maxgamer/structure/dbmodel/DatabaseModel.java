package org.maxgamer.structure.dbmodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class DatabaseModel{
	private HashMap<String, Object> dbValues = new HashMap<>();
	private HashMap<String, Object> newValues = new HashMap<>();
	private HashSet<String> keys = new HashSet<>(1);
	private String table;
	
	public DatabaseModel(String table, String... keys){
		this.table = table;
		if(keys.length <= 0){
			throw new IllegalArgumentException("You must specify the keys to use for the table, given none.");
		}
		for(String key : keys){
			this.keys.add(key);
		}
	}
	
	protected Object setField(String field, Object value){
		Object v = newValues.put(field, value);
		if(v == null){
			v = dbValues.get(field);
		}
		return v;
	}
	
	protected Object getField(String field){
		Object o = newValues.get(field);
		if(o == null){
			o = dbValues.get(field);
		}
		return o;
	}
	
	public void load(ResultSet rs) throws SQLException{
		int cols = rs.getMetaData().getColumnCount();
		for(int i = 1; i <= cols; i++){
			String name = rs.getMetaData().getColumnName(i);
			
			if(rs.getObject(i) == null){
				dbValues.put(name, null);
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
					dbValues.put(name, rs.getBoolean(i));
					break;
					
				case Types.CHAR: //Fixed length
				case Types.VARCHAR: //Variabel length
				case Types.LONGNVARCHAR:
				case Types.LONGVARBINARY:
				case Types.LONGVARCHAR:
				case Types.NVARCHAR:
					dbValues.put(name, rs.getString(i));
					break;
					
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.DECIMAL:
					dbValues.put(name, rs.getDouble(i));
					break;
					
				case Types.FLOAT:
					dbValues.put(name, rs.getFloat(i));
					break;
					
				case Types.BIGINT:
				case Types.TIME:
				case Types.TIMESTAMP:
				case Types.INTEGER: //SQLite reports integer type when a long is required.
					dbValues.put(name, rs.getLong(i));
					break;
					
				case Types.SMALLINT:
					dbValues.put(name, rs.getShort(i));
					break;
					
				case Types.TINYINT:
					dbValues.put(name, rs.getByte(i));
					break;
			}
		}
	}
	
	public void insert(Connection con) throws SQLException{
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO " + table + " (");
		
		Iterator<String> sit = newValues.keySet().iterator();
		if(sit.hasNext()){
			query.append(sit.next());
			
			while(sit.hasNext()){
				query.append(", " + sit.next());
			}
		}
		
		query.append(") VALUES(");
		sit = newValues.keySet().iterator();
		if(sit.hasNext()){
			sit.next();
			query.append("?");
			
			while(sit.hasNext()){
				sit.next();
				query.append(", ?");
			}
		}
		query.append(")");
		
		PreparedStatement ps = con.prepareStatement(query.toString());
		int pos = 1;
		sit = newValues.keySet().iterator();
		while(sit.hasNext()){
			Object v = getField(sit.next());
			ps.setString(pos++, v == null ? null : v.toString());
		}
		try{
			ps.execute();
		}
		catch(SQLException e){
			throw new SQLException("Query=" + query.toString(), e);
		}
		ps.close();
		
		for(Entry<String, Object> entry : this.newValues.entrySet()){
			this.dbValues.put(entry.getKey(), entry.getValue());
		}
		this.newValues.clear();
	}
	
	public void update(Connection con) throws SQLException{
		StringBuilder query = new StringBuilder();
		query.append("UPDATE " + table + " SET ");
		
		Iterator<String> sit = dbValues.keySet().iterator();
		if(sit.hasNext()){
			query.append(sit.next() + " = ?");
			
			while(sit.hasNext()){
				query.append(", " + sit.next() + " = ?");
			}
		}
		
		query.append(" WHERE ");
		Iterator<String> keys = this.keys.iterator();
		if(keys.hasNext()){
			query.append(keys.next() + " = ?");
			
			while(keys.hasNext()){
				query.append(" AND " + keys.next() + " = ?");
			}
		}
		
		PreparedStatement ps = con.prepareStatement(query.toString());
		
		int pos = 1;
		sit = dbValues.keySet().iterator();
		if(sit.hasNext()){
			Object v = getField(sit.next());
			ps.setString(pos++, v == null ? null : v.toString());
			
			while(sit.hasNext()){
				v = getField(sit.next());
				ps.setString(pos++, v == null ? null : v.toString());
			}
		}
		
		keys = this.keys.iterator();
		if(keys.hasNext()){
			ps.setString(pos++, this.dbValues.get(keys.next()).toString());
			
			while(keys.hasNext()){
				ps.setString(pos++, this.dbValues.get(keys.next()).toString());
			}
		}
		try{
			ps.execute();
		}
		catch(SQLException e){
			throw new SQLException("Query=" + query.toString(), e);
		}
		ps.close();
		
		for(Entry<String, Object> entry : this.newValues.entrySet()){
			this.dbValues.put(entry.getKey(), entry.getValue());
		}
		this.newValues.clear();
	}
	
	public void delete(Connection con) throws SQLException{
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM " + table + " WHERE ");
		
		Iterator<String> keys = this.keys.iterator();
		if(keys.hasNext()){
			query.append(keys.next() + " = ?");
			
			while(keys.hasNext()){
				query.append(" AND " + keys.next() + " = ?");
			}
		}
		
		PreparedStatement ps = con.prepareStatement(query.toString());
		
		int pos = 1;
		keys = this.keys.iterator();
		if(keys.hasNext()){
			ps.setString(pos++, this.dbValues.get(keys.next()).toString());
			
			while(keys.hasNext()){
				ps.setString(pos++, this.dbValues.get(keys.next()).toString());
			}
		}
		try{
			ps.execute();
		}
		catch(SQLException e){
			throw new SQLException("Query=" + query.toString(), e);
		}
		ps.close();
	}
	
	/**
	 * Fetches the String in the given field
	 * @param field the field
	 * @return the String
	 * @throws ClassCastException if the field does not represent a String.
	 */
	public String getString(String field){
		return (String) getField(field);
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
		return  ((Boolean) getField(field)).booleanValue();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Keys: " + keys.toString());
		sb.append("\nNew Columns & Rows: " + this.newValues.toString());
		sb.append("\nOld Columns & Rows: " + this.dbValues.toString());
		
		return sb.toString();
	}
}