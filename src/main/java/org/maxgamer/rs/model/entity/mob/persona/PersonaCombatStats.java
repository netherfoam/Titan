package org.maxgamer.rs.model.entity.mob.persona;

import org.maxgamer.rs.model.entity.mob.CombatStats;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.skill.SkillType;

/**
 * @author netherfoam
 */
public class PersonaCombatStats extends CombatStats {
    public PersonaCombatStats(Persona owner) {
        super(owner);
    }

    @Override
    public Persona getOwner() {
        return (Persona) super.getOwner();
    }

    @Override
    public int getAttackAnimation() {
        ItemStack wep = getOwner().getEquipment().get(WieldType.WEAPON);
        if (wep == null) return 428;
        String name = wep.getName().toLowerCase();
        //TODO: 13055 is Maul attack
        //TODO: 13053/13052 is godsword attack/2H

        if(name.contains("greataxe")) {
            if(getOwner().getAttackStyle().isType(SkillType.ATTACK)) {
                return 2066;
            }
            return 2067;
        }

        if (name.contains("maul")) {
            return 13055;
        }
        if (name.contains("godsword")) {
            return 13053;
        }

        if (name.contains("c'bow") || name.contains("crossbow")) {
            return 4230; //May be incorrect
        }

        if (name.contains("dart")) {
            return 6600;
        }

        if (name.contains("thrownaxe") || name.contains("javelin") || name.contains("knife")) {
            return 929;
        }

        if (name.contains("bow") || name.equals("seercull") || name.equals("sling")) {
            return 426;
        }

        if (name.equals("hand cannon")) {
            return 12174;
        }

        switch (wep.getId()) {
            case 4151: //Abyssal Whip
                return 1658;
        }
        //Well we don't know.
        return 428;
    }

    @Override
    public int getDefenceAnimation() {
        ItemStack shield = getOwner().getEquipment().get(WieldType.SHIELD);
        if (shield == null) return 434;

        //TODO: 1156 is a sick shield animation.
        //TODO: 4177 seems to be a defender anim
        //TODO: 13054 is maul block
        //TODO: 13051 is godsword block

        return 1156;
    }

    @Override
    public int getDeathAnimation() {
        return 9055; //Bow down to your death animation
    }
}