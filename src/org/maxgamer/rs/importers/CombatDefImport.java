package org.maxgamer.rs.importers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

import org.maxgamer.rs.core.Core;

/**
 * @author netherfoam
 */
public class CombatDefImport {
	public static void main(String[] args) throws Exception {
		//Core.init(4, args);
		Core.getWorldDatabase();
		Scanner sc = new Scanner(new File("data", "unpackedCombatDefinitionsList.txt"));
		Connection con = Core.getWorldDatabase().getConnection();
		PreparedStatement ps = con.prepareStatement("UPDATE npc_definitions SET maxHealth = ?, attackAnimation = ?, defenceAnimation = ?, deathAnimation = ?, attackDelay = ?, respawnDelay = ?, isAggressive = ?, projectileId = ?, startGraphics = ?, isMelee = ?, isRange = ?, isMagic = ? WHERE id = ?");
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.contains("//")) continue;
			String[] parts = line.split(" ");
			int npcId = Integer.parseInt(parts[0]);
			int hp = Integer.parseInt(parts[2]);
			int atkAnim = Integer.parseInt(parts[3]);
			int defAnim = Integer.parseInt(parts[4]);
			int deathAnim = Integer.parseInt(parts[5]);
			int atkDelay = Integer.parseInt(parts[6]);
			//int deathDelay = Integer.parseInt(parts[7]);
			int respawnDelay = Integer.parseInt(parts[8]);
			int gfx = Integer.parseInt(parts[11]);
			int projectileId = Integer.parseInt(parts[12]);
			boolean aggressive = parts[13].equalsIgnoreCase("AGRESSIVE");
			
			boolean mage = parts[10].equalsIgnoreCase("MAGE");
			boolean range = parts[10].equalsIgnoreCase("RANGE");
			boolean melee = (range || mage ? false : true); //Only melee if not magic or range
			
			ps.setInt(1, hp);
			ps.setInt(2, atkAnim);
			ps.setInt(3, defAnim);
			ps.setInt(4, deathAnim);
			ps.setInt(5, atkDelay);
			ps.setInt(6, respawnDelay);
			ps.setInt(7, aggressive ? 1 : 0);
			
			ps.setInt(8, projectileId);
			ps.setInt(9, gfx);
			ps.setInt(10, melee ? 1 : 0);
			ps.setInt(11, range ? 1 : 0);
			ps.setInt(12, mage ? 1 : 0);
			
			//projectileId = ?, startGraphics = ?, isMelee = ?, isRange = ?, isMagic = ?
			ps.setInt(13, npcId);
			
			ps.addBatch();
		}
		ps.executeBatch();
		sc.close();
	}
}