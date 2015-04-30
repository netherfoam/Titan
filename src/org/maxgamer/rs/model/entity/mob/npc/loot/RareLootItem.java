package org.maxgamer.rs.model.entity.mob.npc.loot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class RareLootItem extends LootItem {
	/**
	 * The loot weight for rare items catagory to be selected E.g. LootItems are
	 * constructed with a weight. If this item is picked (chances are based on
	 * <b>RARE_CHANCE_WEIGHT</b> vs the other chances of items that can be
	 * dropped by the given monster) then, this returns an ItemStack based on
	 * its OWN list of pickable items.<br/>
	 * <br/>
	 * <b>RARE_CHANCE_WEIGHT</b> is the global weight of the RareLootItem being
	 * picked.
	 * */
	public static final int RARE_CHANCE_WEIGHT = 1;
	/** The single rare loot item */
	private static RareLootItem instance;
	static {
		LinkedList<LootItem> loot = new LinkedList<LootItem>();
		
		try {
			//-1 is the rares table NPC id.
			Connection con = Core.getWorldDatabase().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT min,max,chance,itemId,groupId FROM npc_group_loot WHERE groupId < 0");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int itemId = rs.getInt("itemId");
				double weight = rs.getDouble("weight");
				int min = rs.getInt("min");
				int max = rs.getInt("max");
				LootItem l = new CommonLootItem(itemId, weight, min, max);
				loot.add(l);
			}
			con.close();
			System.out.println("Rare loot item generated. Options: " + loot.size());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		instance = new RareLootItem(RARE_CHANCE_WEIGHT, loot);
	}
	
	public static RareLootItem getInstance() {
		return instance;
	}
	
	/** The WeightedPicker which selects the sub item to fetch */
	private WeightedPicker<LootItem> picker;
	
	/**
	 * Creates a new rare item.
	 * @param weight The weighted chance (RARE_CHANCE_WEIGHT)
	 * @param options The items which can be selected.
	 */
	private RareLootItem(int weight, Collection<LootItem> options) {
		super(weight);
		this.picker = new WeightedPicker<LootItem>();
		for (LootItem l : options) {
			this.picker.add(l);
		}
	}
	
	/**
	 * Returns a random item stack from this RareLootItem's pool
	 */
	@Override
	public ItemStack getItemStack() {
		System.out.println("Generating RareLootItem!");
		return picker.next().getItemStack();
	}
}