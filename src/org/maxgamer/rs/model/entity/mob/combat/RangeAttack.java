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
import org.maxgamer.rs.structure.dbmodel.Mapping;
import org.maxgamer.rs.structure.dbmodel.Transparent;

/**
 * @author netherfoam
 */
public class RangeAttack extends Attack {
	public static class RangeWeapon {
		//private int weaponId;
		//Map of RangeAmmo -> Quantity
		private RangeWeapon() {
		} //Private constructor 
		
		private HashMap<Ammo, Integer> ammos = new HashMap<Ammo, Integer>(4);
		
		public HashMap<Ammo, Integer> getAmmo() {
			return new HashMap<Ammo, Integer>(ammos);
		}
		
		public boolean isAmmo(ItemStack item) {
			if (item == null) throw new NullPointerException("Item may not be null");
			for (Ammo a : ammos.keySet()) {
				if (a.item_id == item.getId()) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class Ammo extends Transparent{
		@Mapping
		public int item_id;
		@Mapping
		public int graphics; //TODO: Use this
		@Mapping
		public int projectile;
		@Mapping
		public int height;
	}
	
	private static HashMap<Integer, RangeWeapon> weapons;
	private static HashMap<Integer, Ammo> ammos;
	
	public static void init() throws SQLException {
		weapons = new HashMap<Integer, RangeWeapon>(256);
		ammos = new HashMap<Integer, Ammo>(3000);
		Connection con = Core.getWorldDatabase().getConnection();
		
		PreparedStatement ps = con.prepareStatement("SELECT * FROM Ammo");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Ammo a = new Ammo();
			a.reload(rs);
			ammos.put(a.item_id, a);
		}
		
		ps = con.prepareStatement("SELECT * FROM ItemAmmo");
		rs = ps.executeQuery();
		while (rs.next()) {
			Integer weapon = rs.getInt("item_id");
			
			Ammo a = ammos.get(rs.getInt("ammo_id"));
			if (a == null) {
				Log.debug("WeaponId " + rs.getInt("weaponId") + " uses item ID #" + rs.getInt("ammoId") + " as ammo, but that ammo is not defined in the database. It is being defined now.");
				PreparedStatement insert = con.prepareStatement("INSERT INTO Ammo (item_id, graphics, height, projectile) VALUES (?, ?, ?, ?)");
				insert.setInt(1, rs.getInt("ammoId"));
				insert.setInt(2, -1);
				insert.setInt(3, 0);
				insert.setInt(4, -1);
				insert.execute();
				a = new Ammo();
				a.item_id = rs.getInt("item_id");
				ammos.put(a.item_id, a);
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
	private Ammo projectile;
	
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
			Projectile.create(projectile.projectile, attacker.getLocation(), target).launch();
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
				for (Ammo item : rwep.ammos.keySet()) {
					if (equip.contains(ItemStack.create(item.item_id))) {
						projectile = item;
					}
				}
				
				if (projectile == null) {
					//Attack failed, no ammo.
					return false;
				}
				
				try {
					equip.remove(ItemStack.create(projectile.item_id));
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