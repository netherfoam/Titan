package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.npc.loot.CommonLootItem;
import org.maxgamer.rs.model.entity.mob.npc.loot.Loot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "NPCGroup")
public class NPCGroup {
    /*public static void reload() throws SQLException {
        groups = new HashMap<Integer, NPCGroup>(512);
		Connection con = Core.getDatabase().getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM NPCGroupLoot");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			NPCGroup g = groups.get(rs.getInt("group_id"));
			if (g == null) {
				g = new NPCGroup(rs.getInt("group_id"), new Loot());
				groups.put(rs.getInt("group_id"), g);
			}
			
			try {
				ItemStack.create(rs.getInt("item_id"));
			}
			catch (RuntimeException e) {
				Log.warning("Loot " + rs.getInt("item_id") + " for " + rs.getInt("group_id") + " is not a valid item.");
				PreparedStatement del = con.prepareStatement("DELETE FROM NPCGroupLoot WHERE item_id = ?");
				del.setInt(1, rs.getInt("item_id"));
				del.execute();
				continue;
			}
			
			CommonLootItem loot = new CommonLootItem(rs.getInt("item_id"), rs.getDouble("chance"), rs.getInt("min"), rs.getInt("max"));
			g.loot.add(loot, false);
		}
		rs.close();
		ps.close();
		
		ps = con.prepareStatement("SELECT * FROM NPCGroupLootGuarantee");
		rs = ps.executeQuery();
		while (rs.next()) {
			NPCGroup g = groups.get(rs.getInt("group_id"));
			if (g == null) {
				g = new NPCGroup(rs.getInt("group_id"), new Loot());
				groups.put(rs.getInt("group_id"), g);
			}
			
			try {
				ItemStack.create(rs.getInt("item_id"));
			}
			catch (RuntimeException e) {
				Log.warning("Loot " + rs.getInt("item_id") + " for " + rs.getInt("group_id") + " is not a valid item.");
				PreparedStatement del = con.prepareStatement("DELETE FROM NPCGroupLootGuarantee WHERE itemId = ?");
				del.setInt(1, rs.getInt("item_id"));
				del.execute();
				continue;
			}
			
			CommonLootItem loot = new CommonLootItem(rs.getInt("item_id"), 100, rs.getInt("amount"), rs.getInt("amount"));
			g.loot.add(loot, true);
		}
		rs.close();
		ps.close();
	}*/
	
	/*public static NPCGroup get(int id) {
		NPCGroup g = groups.get(id);
		if (g == null) {
			try {
				Loot loots = new Loot();
				Connection con = Core.getDatabase().getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM NPCGroupLoot WHERE group_id = ?");
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					CommonLootItem loot = new CommonLootItem(rs.getInt("item_id"), rs.getInt("chance"), rs.getInt("min"), rs.getInt("max"));
					loots.add(loot, false);
				}
				rs.close();
				ps.close();
				
				g = new NPCGroup(id, loots);
				groups.put(id, g);
				
				ps = con.prepareStatement("SELECT * FROM NPCGroupLootGuarantee WHERE group_id = ?");
				ps.setInt(1, g.id);
				rs = ps.executeQuery();
				while (rs.next()) {
					ItemStack.create(rs.getInt("item_id"));
					CommonLootItem loot = new CommonLootItem(rs.getInt("item_id"), 100, rs.getInt("amount"), rs.getInt("amount"));
					g.loot.add(loot, true);
				}
				rs.close();
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return g;
	}*/

    @Id
    private int id;

    @OneToMany(mappedBy = "group")
    private List<NPCGroupLoot> lootChances;

    @OneToMany(mappedBy = "group")
    private List<NPCGroupLootGuarantee> lootGuarantee;

    /**
     * A class to store NPC's which look different, but are the same underneath.
     * This means Loot, Attacks and ?? are the same for the NPCs in this group.
     */
    private transient Loot loot;

    public NPCGroup() {

    }

    public int getId() {
        return id;
    }

    public Loot getLoot() {
        if (loot == null) {
            loot = new Loot();
            for (NPCGroupLoot npcGroupLoot : this.lootChances) {
                loot.add(npcGroupLoot.toLoot(), false);
            }

            for (NPCGroupLootGuarantee guaranteed : this.lootGuarantee) {
                loot.add(new CommonLootItem(guaranteed.toItem(), 100), true);
            }
        }
        return loot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final NPCGroup other = (NPCGroup) obj;

        return Objects.equals(this.id, other.id);
    }
}