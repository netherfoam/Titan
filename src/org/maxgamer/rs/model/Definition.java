package org.maxgamer.rs.model;

import org.maxgamer.rs.structure.dbmodel.Transparent;

/**
 * @author netherfoam
 */
//public abstract class Definition extends Eloquent {
public abstract class Definition extends Transparent{
	public Definition(String table, String[] keys, Object[] values) {
		super(table, keys, values);
	}
	public Definition(String table, String[] keys){
		super(table, keys, null);
	}
	public Definition(String table, String key, Object value){
		this(table, new String[]{key}, new Object[]{value});
	}
	public Definition(String table, String key){
		this(table, new String[]{key});
	}
}