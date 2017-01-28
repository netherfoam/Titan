package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.entity.mob.EquipmentHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemAmmoType;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.util.Erratic;

import java.util.List;

/**
 * @author netherfoam
 */
public class RangeAttack extends Attack {
    private ItemAmmoType ammo;

    public RangeAttack(Mob attacker) {
        super(attacker, attacker.getCombatStats().getAttackAnimation(), -1);
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

    @Override
    public boolean prepare(Mob target, AttackResult damage) {
        Damage d = RangeAttack.roll(attacker, target);
        damage.add(d);

        return true;
    }

    @Override
    public void perform(final Mob target, final AttackResult damage) {
        if (ammo != null) {
            Projectile.create(ammo.getProjectile().getProjectile().getProjectile(), attacker.getLocation(), target).launch();
        }

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

            if (wep.getDefinition().getAmmo().isEmpty() == false) {
                List<ItemAmmoType> types = wep.getDefinition().getAmmo();

                for (ItemAmmoType type : types) {
                    if (equip.contains(type.toItem())) {
                        ammo = type;
                        break;
                    }
                }

                if (ammo == null) {
                    //Attack failed, no ammo.
                    return false;
                }

                try {
                    equip.remove(ammo.toItem());
                } catch (ContainerException e) {
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