package org.maxgamer.rs.model.item.weapon;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.maxgamer.rs.definition.Definition;
import org.maxgamer.rs.model.entity.mob.Bonuses;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.structure.dbmodel.Mapping;

/**
 * @author netherfoam
 */
public class Equipment extends Definition {
	@Mapping
	private int id;
	
	@Mapping
	private boolean full;
	@Mapping
	private int type;
	@Mapping
	private int model;
	private Bonuses bonuses;
	
	public Equipment(int id) {
		super("item_weapons", "id", id);
	}
	
	@Override
	public void reload(ResultSet rs) throws SQLException{
		super.reload(rs);
		this.bonuses = new Bonuses();
		this.bonuses.reload(rs);
	}
	
	public int getId() {
		return id;
	}
	
	public int getBonus(int type) {
		return bonuses.getBonus(type);
	}
	
	public Bonuses getBonuses(){
		return bonuses;
	}
	
	public boolean isFull(){
		return full;
	}
	
	public WieldType getSlot() {
		if (type < 0) return null;
		
		return WieldType.forSlot(type);
	}
	
	public int getModel() {
		return model;
	}
	
}