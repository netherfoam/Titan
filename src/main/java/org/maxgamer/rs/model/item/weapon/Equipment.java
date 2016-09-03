package org.maxgamer.rs.model.item.weapon;

import org.maxgamer.rs.model.item.ItemType;
import org.maxgamer.rs.model.item.WieldType;

import javax.persistence.*;
import java.io.Serializable;

import static org.maxgamer.rs.model.entity.mob.Bonuses.*;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "Equipment")
public class Equipment implements Serializable {
    @Id
    @MapsId
    @OneToOne
    protected ItemType item;

    @Column
    private boolean full;

    @Column
    private int type;

    @Column
    private int model;

    @Column(name = "atk_stab")
    private int atkStab;

    @Column(name = "atk_slash")
    private int atkSlash;

    @Column(name = "atk_crush")
    private int atkCrush;

    @Column(name = "atk_magic")
    private int atkMagic;

    @Column(name = "atk_range")
    private int atkRange;

    @Column(name = "def_stab")
    private int defStab;

    @Column(name = "def_slash")
    private int defSlash;

    @Column(name = "def_crush")
    private int defCrush;

    @Column(name = "def_magic")
    private int defMagic;

    @Column(name = "def_range")
    private int defRange;

    @Column(name = "def_summon")
    private int defSummon;

    @Column(name = "pow_magic")
    private int powMagic;

    @Column(name = "pow_range")
    private int powRange;

    @Column(name = "pow_prayer")
    private int powPrayer;

    @Column(name = "pow_strength")
    private int powStrength;

    public boolean isFull() {
        return full;
    }

    public WieldType getSlot() {
        if (type < 0) return null;

        return WieldType.forSlot(type);
    }

    public int getModel() {
        return model;
    }

    public int getBonus(int type) {
        switch (type) {
            case ATK_STAB:
                return atkStab;
            case ATK_SLASH:
                return atkSlash;
            case ATK_CRUSH:
                return atkCrush;
            case ATK_MAGIC:
                return atkMagic;
            case ATK_RANGE:
                return atkRange;
            case DEF_STAB:
                return defStab;
            case DEF_SLASH:
                return defSlash;
            case DEF_CRUSH:
                return defCrush;
            case DEF_MAGIC:
                return defMagic;
            case DEF_RANGE:
                return defRange;
            case DEF_SUMMON:
                return defSummon;
            case POW_MAGIC:
                return powMagic;
            case PRAYER:
                return powPrayer;
            case POW_STRENGTH:
                return powStrength;
            case POW_RANGE:
                return powRange;
        }
        throw new IllegalArgumentException("No such bonus type: " + type);
    }

    public int[] toBonusArray() {
        return new int[]{
                atkStab,
                atkSlash,
                atkCrush,
                atkMagic,
                atkRange,

                defStab,
                defSlash,
                defCrush,
                defMagic,
                defRange,
                defSummon,

                powMagic,
                powRange,
                powPrayer,
                powStrength
        };
    }

    public int getAtkStab() {
        return atkStab;
    }

    public int getAtkSlash() {
        return atkSlash;
    }

    public int getAtkCrush() {
        return atkCrush;
    }

    public int getAtkMagic() {
        return atkMagic;
    }

    public int getAtkRange() {
        return atkRange;
    }

    public int getDefStab() {
        return defStab;
    }

    public int getDefSlash() {
        return defSlash;
    }

    public int getDefCrush() {
        return defCrush;
    }

    public int getDefMagic() {
        return defMagic;
    }

    public int getDefRange() {
        return defRange;
    }

    public int getDefSummon() {
        return defSummon;
    }

    public int getPowRange() {
        return powRange;
    }

    public int getPowPrayer() {
        return powPrayer;
    }

    public int getPowMagic() {
        return powMagic;
    }

    public int getPowStrength() {
        return powStrength;
    }
}