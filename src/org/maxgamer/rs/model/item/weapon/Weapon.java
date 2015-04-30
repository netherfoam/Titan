package org.maxgamer.rs.model.item.weapon;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.structure.Eloquent;

/**
 * @author netherfoam
 */
public class Weapon extends Eloquent {
	private int[] bonuses = new int[15]; //TODO: Magic number
	
	public Weapon() {
		super("item_weapons");
	}
	
	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);
		for (int i = 0; i < bonuses.length; i++) {
			bonuses[i] = this.getInt("bonus" + i);
		}
	}
	
	public int getId() {
		return getInt("id");
	}
	
	public int getBonus(int type) {
		return bonuses[type];
	}
	
	public int[] getBonuses() {
		return bonuses;
	}
	
	public boolean isFullBody() {
		return getBoolean("fullBody");
	}
	
	public boolean isFullHat() {
		return getBoolean("fullHat");
	}
	
	public boolean isFullMask() {
		return getBoolean("fullMask");
	}
	
	public WieldType getSlot() {
		int type = getInt("equipmentType");
		if (type < 0) return null;
		
		return WieldType.forSlot(type);
	}
	
	public int getWornModel() {
		return getInt("wornModel");
	}
	
}