package org.maxgamer.rs.model.item.weapon;

import org.maxgamer.rs.definition.Definition;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.structure.dbmodel.FixedArraySerializer;
import org.maxgamer.rs.structure.dbmodel.Mapping;

/**
 * @author netherfoam
 */
public class Weapon extends Definition {
	@Mapping
	private int id;
	
	@Mapping
	private boolean fullBody;
	@Mapping
	private boolean fullHat;
	@Mapping
	private boolean fullMask;
	@Mapping
	private int equipmentType;
	@Mapping
	private int wornModel;
	
	@Mapping(serializer = FixedArraySerializer.class)
	private int[] bonus;
	
	public Weapon(int id) {
		super("item_weapons", "id", id);
	}
	
	public int getId() {
		return id;
	}
	
	public int getBonus(int type) {
		return bonus[type];
	}
	
	public int[] getBonuses() {
		return bonus.clone();
	}
	
	public boolean isFullBody() {
		return fullBody;
	}
	
	public boolean isFullHat() {
		return fullHat;
	}
	
	public boolean isFullMask() {
		return fullMask;
	}
	
	public WieldType getSlot() {
		if (equipmentType < 0) return null;
		
		return WieldType.forSlot(equipmentType);
	}
	
	public int getWornModel() {
		return wornModel;
	}
	
}