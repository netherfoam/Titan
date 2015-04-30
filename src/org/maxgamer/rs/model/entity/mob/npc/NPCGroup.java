package org.maxgamer.rs.model.entity.mob.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.npc.loot.CommonLootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.Loot;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class NPCGroup {
	private static HashMap<Integer, NPCGroup> groups = new HashMap<Integer, NPCGroup>(512);;
	
	public static void reload() throws SQLException {
		groups = new HashMap<Integer, NPCGroup>(512);
		Connection con = Core.getWorldDatabase().getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM npc_group_loot");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			NPCGroup g = groups.get(rs.getInt("groupId"));
			if (g == null) {
				g = new NPCGroup(rs.getInt("groupId"), new Loot());
				groups.put(rs.getInt("groupId"), g);
			}
			
			try {
				ItemStack.create(rs.getInt("itemId"));
			}
			catch (RuntimeException e) {
				Log.warning("Loot " + rs.getInt("itemId") + " for " + rs.getInt("groupId") + " is not a valid item.");
				PreparedStatement del = con.prepareStatement("DELETE FROM npc_group_loot WHERE itemId = ?");
				del.setInt(1, rs.getInt("itemId"));
				del.execute();
				continue;
			}
			
			CommonLootItem loot = new CommonLootItem(rs.getInt("itemId"), rs.getDouble("chance"), rs.getInt("min"), rs.getInt("max"));
			g.loot.add(loot, false);
		}
		rs.close();
		ps.close();
		
		ps = con.prepareStatement("SELECT * FROM npc_group_loot_guarantee");
		rs = ps.executeQuery();
		while (rs.next()) {
			NPCGroup g = groups.get(rs.getInt("groupId"));
			if (g == null) {
				g = new NPCGroup(rs.getInt("groupId"), new Loot());
				groups.put(rs.getInt("groupId"), g);
			}
			
			try {
				ItemStack.create(rs.getInt("itemId"));
			}
			catch (RuntimeException e) {
				Log.warning("Loot " + rs.getInt("itemId") + " for " + rs.getInt("groupId") + " is not a valid item.");
				PreparedStatement del = con.prepareStatement("DELETE FROM npc_group_loot_guarantee WHERE itemId = ?");
				del.setInt(1, rs.getInt("itemId"));
				del.execute();
				continue;
			}
			
			CommonLootItem loot = new CommonLootItem(rs.getInt("itemId"), 100, rs.getInt("amount"), rs.getInt("amount"));
			g.loot.add(loot, true);
		}
		rs.close();
		ps.close();
	}
	
	public static NPCGroup get(int id) {
		NPCGroup g = groups.get(id);
		if (g == null) {
			try {
				Loot loots = new Loot();
				Connection con = Core.getWorldDatabase().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM npc_group_loot WHERE groupId = ?");
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					CommonLootItem loot = new CommonLootItem(rs.getInt("itemId"), rs.getInt("chance"), rs.getInt("min"), rs.getInt("max"));
					loots.add(loot, false);
				}
				rs.close();
				ps.close();
				
				g = new NPCGroup(id, loots);
				groups.put(id, g);
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return g;
	}
	
	private int id;
	
	/**
	 * A class to store NPC's which look different, but are the same underneath.
	 * This means Loot, Attacks and ?? are the same for the NPCs in this group.
	 */
	private Loot loot;
	
	private NPCGroup(int id, Loot loot) {
		this.id = id;
		this.loot = loot;
	}
	
	public int getId() {
		return id;
	}
	
	public Loot getLoot() {
		return loot;
	}
}