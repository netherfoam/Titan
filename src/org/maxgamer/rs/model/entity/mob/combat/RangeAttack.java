package org.maxgamer.rs.model.entity.mob.combat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.lib.Erratic;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.entity.mob.EquipmentHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;

/**
 * @author netherfoam
 */
public class RangeAttack extends Attack {
	public static class RangeWeapon {
		//private int weaponId;
		//Map of RangeAmmo -> Quantity
		private RangeWeapon() {
		} //Private constructor 
		
		private HashMap<RangeAmmo, Integer> ammos = new HashMap<RangeAmmo, Integer>(4);
		
		public HashMap<RangeAmmo, Integer> getAmmo() {
			return new HashMap<RangeAmmo, Integer>(ammos);
		}
		
		public boolean isAmmo(ItemStack item) {
			if (item == null) throw new NullPointerException("Item may not be null");
			for (RangeAmmo a : ammos.keySet()) {
				if (a.itemId == item.getId()) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class RangeAmmo {
		public final int itemId;
		public final int graphicsId; //TODO: Use this
		public final int projectileId;
		
		public RangeAmmo(int item, int gfx, int projectile) {
			this.itemId = item;
			this.graphicsId = gfx;
			this.projectileId = projectile;
		}
	}
	
	private static HashMap<Integer, RangeWeapon> weapons;
	private static HashMap<Integer, RangeAmmo> ammos;
	
	public static void init() throws SQLException {
		weapons = new HashMap<Integer, RangeWeapon>(256);
		ammos = new HashMap<Integer, RangeAmmo>(3000);
		Connection con = Core.getWorldDatabase().getConnection();
		
		PreparedStatement ps = con.prepareStatement("SELECT * FROM item_ammo");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			RangeAmmo a = new RangeAmmo(rs.getInt("itemId"), rs.getInt("graphicsId"), rs.getInt("projectileId"));
			ammos.put(a.itemId, a);
		}
		
		ps = con.prepareStatement("SELECT * FROM item_range_weapons");
		rs = ps.executeQuery();
		while (rs.next()) {
			Integer weapon = rs.getInt("weaponId");
			
			RangeAmmo a = ammos.get(rs.getInt("ammoId"));
			if (a == null) {
				Log.debug("WeaponId " + rs.getInt("weaponId") + " uses ammoId " + rs.getInt("ammoId") + " as ammo, but that ammo is not defined in the database. It is being defined now.");
				PreparedStatement insert = con.prepareStatement("INSERT INTO item_ammo (itemId, graphicsId, graphicsHeight, projectileId) VALUES (?, ?, ?, ?)");
				insert.setInt(1, rs.getInt("ammoId"));
				insert.setInt(2, -1);
				insert.setInt(3, 0);
				insert.setInt(4, -1);
				insert.execute();
				a = new RangeAmmo(rs.getInt("ammoId"), -1, -1);
				ammos.put(a.itemId, a);
			}
			
			RangeWeapon wep = weapons.get(weapon);
			if (wep == null) {
				wep = new RangeWeapon();
				weapons.put(weapon, wep);
			}
			
			wep.ammos.put(a, rs.getInt("quantity"));
		}
		
		rs.close();
		ps.close();
	}
	
	public static Damage roll(Mob attacker, Mob target) {
		CombatStats srcStats = attacker.getCombatStats();
		CombatStats vicStats = target.getCombatStats();
		
		double accuracy = Erratic.getGaussian(0.5, srcStats.getRangeHitRating());
		double defence = Erratic.getGaussian(0.5, vicStats.getRangeDefenceRating());
		int max = srcStats.getRangePower();
		
		if (accuracy > defence) {
			int hit = (int) Erratic.getGaussian(accuracy / (accuracy + defence), max);
			Damage d = new Damage(hit, DamageType.RANGE, target);
			if (hit * 20 > max * 19) {
				//top 5% of hits are 'max' for us
				d.setMax(true);
			}
			return d;
		}
		
		return new Damage(0, DamageType.MISS, target);
	}
	
	public static RangeWeapon getData(ItemStack wep) {
		return weapons.get(wep.getId());
	}
	
	//TODO: Is this necessary?
	private RangeAmmo projectile;
	
	public RangeAttack(Mob attacker) {
		super(attacker, attacker.getCombatStats().getAttackAnimation(), -1);
	}
	
	@Override
	public boolean prepare(Mob target, AttackResult damage) {
		Damage d = RangeAttack.roll(attacker, target);
		damage.add(d);
		
		return true;
	}
	
	@Override
	public void perform(final Mob target, final AttackResult damage) {
		if (projectile != null) {
			Projectile.create(projectile.projectileId, attacker.getLocation(), target).launch();
		}
		
		//Core.getServer().getTicker().submit(1, new Tickable(){;
		new Tickable() {
			@Override
			public void tick() {
				RangeAttack.super.perform(target, damage);
			}
		}.queue(1);
	}
	
	@Override
	public boolean takeConsumables() {
		//TODO: NPC's which use bows need to be given a bow or something here!
		if (attacker instanceof EquipmentHolder) {
			Container equip = ((EquipmentHolder) attacker).getEquipment();
			ItemStack wep = equip.get(WieldType.WEAPON.getSlot());
			
			if (wep == null) {
				return false; //No weapon, can't range.
			}
			
			RangeWeapon rwep = weapons.get(wep.getId());
			
			if (rwep != null && rwep.ammos.isEmpty() == false) {
				for (RangeAmmo item : rwep.ammos.keySet()) {
					if (equip.contains(ItemStack.create(item.itemId))) {
						projectile = item;
					}
				}
				
				if (projectile == null) {
					//Attack failed, no ammo.
					return false;
				}
				
				try {
					equip.remove(ItemStack.create(projectile.itemId));
				}
				catch (ContainerException e) {
					//Attack failed, no ammo?
					//Ammo was in there earlier. Possible threading issue?
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public int getMaxDistance() {
		return 15; //TODO: Magic number, varies based on bow (longbow? Crossbow? darts?) + attack style (long range? accurate? rapid?)
	}
	
	@Override
	public int getWarmupTicks() {
		return 4; //TODO: This varies from weapon to weapon
	}
	
}