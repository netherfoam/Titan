package org.maxgamer.rs.importers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Scanner;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;

/**
 * @author netherfoam
 */
public class LootImport {
	public static void main(String[] args) throws Exception {
		Connection con = Core.getWorldDatabase().getConnection();
		Scanner sc = new Scanner(new File("data", "drop_tables_up.txt"));
		PreparedStatement addAlways = con.prepareStatement("INSERT INTO npc_group_loot_guarantee (groupId, itemId, amount) VALUES (?, ?, ?)");
		PreparedStatement addWeighted = con.prepareStatement("INSERT INTO npc_group_loot (groupId, itemId, min, max, chance) VALUES (?, ?, ?, ?, ?)");
		HashSet<Integer> groups = new HashSet<>(8192);
		
		PreparedStatement ps1 = con.prepareStatement("SELECT * FROM npc_definitions");
		ResultSet rs = ps1.executeQuery();
		while (rs.next()) {
			groups.add(rs.getInt("groupId"));
		}
		rs.close();
		ps1.close();
		
		HashSet<String> permaKeys = new HashSet<>(8192);
		
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.startsWith("//")) continue;
			
			String[] parts = line.split(",? ");
			int npcId = Integer.parseInt(parts[0].substring(1, parts[0].length() - 2));
			int itemId = Integer.parseInt(parts[1]);
			double weight = Double.parseDouble(parts[2]);
			int min = Integer.parseInt(parts[3]);
			int max = Integer.parseInt(parts[4]);
			boolean rare = parts[5].equalsIgnoreCase("true");
			
			if (rare) continue; //Skipped
			
			if (groups.contains(npcId)) {
				//We have a group match!
				if (weight == 100) {
					if (permaKeys.add("perma-" + npcId + "-" + itemId)) {
						//Insert into perma loot
						addAlways.setInt(1, npcId);
						addAlways.setInt(2, itemId);
						addAlways.setInt(3, (min + max) / 2);
						addAlways.addBatch();
					}
				}
				else {
					if (permaKeys.add("loot-" + npcId + "-" + itemId)) {
						//Insert into weighted loot
						addWeighted.setInt(1, npcId);
						addWeighted.setInt(2, itemId);
						addWeighted.setInt(3, min);
						addWeighted.setInt(4, max);
						addWeighted.setDouble(5, weight);
						addWeighted.addBatch();
					}
				}
			}
		}
		sc.close();
		
		Log.info("Executing perma-loot query...");
		addAlways.executeBatch();
		Log.info("Executing random-loot query...");
		addWeighted.executeBatch();
		Log.info("Generated loot tables!");
	}
}