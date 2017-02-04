package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.cache.Archive;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.util.BufferUtils;

import javax.persistence.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.maxgamer.rs.model.entity.mob.Bonuses.*;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "NPC")
public class NPCType {
    //Values stored in the database. These are not always guaranteed
    //to be correct or sane since they are not official data.
    @Id
    private int id;
    @Column
    private String name = "null";
    @ManyToOne
    private NPCGroup group;
    @Column
    private boolean poison_immune;
    @Column
    private boolean is_magic;
    @Column
    private boolean is_range;
    @Column
    private boolean is_melee;
    @Column
    private boolean aggressive;
    @Column
    private boolean walk;
    @Column
    private int projectile;
    @Column
    private int end_graphics;
    @Column
    private int start_graphics;
    @Column
    private int attack_animation;
    @Column
    private int defence_animation;
    @Column
    private int death_animation;
    @Column
    private int respawn_delay;
    @Column
    private int health;
    @Column
    private int attack;
    @Column
    private int strength;
    @Column
    private int defence;
    @Column(name = "`range`")
    private int range;
    @Column
    private int magic;
    @Column
    private int attack_delay;
    @Column
    private String examine;
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
    /*
     * int anInt3164 = -1; private short[] aShortArray3166; int anInt3167;
     * boolean aBoolean3169; int configFileId; boolean aBoolean3172; int
     * anInt3173; int anInt3178 = -1; int anInt3179;
     */
    private transient String[] options = new String[5];
    /*
     * int anInt3181; int anInt3182; private short[] aShortArray3183; int
     * anInt3184; boolean aBoolean3187; boolean aBoolean3190; boolean
     * isVisibleOnMap; private int[] anIntArray3192; byte aByte3193; Object
     * aClass183_3195; boolean aBoolean3196; int anInt3200; private short[]
     * aShortArray3201; int[] childrenIds; int anInt3203; private short[]
     * aShortArray3204; private byte[] aByteArray3205; public int renderEmote;
     * byte aByte3207; int configId; int anInt3209; boolean aBoolean3210; int
     * anInt3212; short aShort3213; int anInt3214; byte aByte3215; int
     * anInt3216;
     */
    private transient int combatLevel = -1;
    /*
     * int[] anIntArray3219; boolean isClickable; int headIcons; int anInt3223;
     * public byte direction; int anInt3226; int anInt3227; private int[]
     * anIntArray3230; byte aByte3233; int anInt3235;
     */
    private transient int size = 1;

    protected NPCType() {
        /*
         * aBoolean3172 = true; aBoolean3169 = true; aBoolean3196 = false;
         * anInt3200 = -1; anInt3173 = -1; anInt3167 = -1; anInt3179 = -1;
         * renderEmote = -1; aByte3193 = (byte) -16; anInt3184 = 0; anInt3214 =
         * -1; aBoolean3210 = false; aByte3207 = (byte) 0; aByte3215 = (byte)
         * -96; anInt3181 = -1; configId = -1; anInt3216 = 255; isVisibleOnMap =
         * true; anInt3212 = -1; anInt3226 = -1; anInt3203 = -1; aBoolean3187 =
         * false; anInt3227 = -1; anInt3223 = -1; direction = (byte) 4;
         * configFileId = -1; isClickable = true; headIcons = -1; anInt3235 =
         * 32; aShort3213 = (short) 0; aByte3233 = (byte) -1; anInt3182 = -1;
         * aShort3237 = (short) 0;
         */
    }

    @PostLoad
    public void cache() throws IOException {
        //We don't have a previous version. We shall now load it and cache it.
        Archive a = Core.getCache().getArchive(IDX.NPCS, id >> 7);
        ByteBuffer bb = a.get(id & 0x7F); //Last 7 bits are our ID within the archive
        this.decode(bb);
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

    /* short aShort3237; */

    //Serverside values

    public int getPowStrength() {
        return powStrength;
    }

    public NPCGroup getGroup() {
        return group;
    }

    public boolean isPoisonable() {
        return !poison_immune;
    }

    public boolean isMagic() {
        return is_magic;
    }

    public boolean isRange() {
        return is_range;
    }

    public boolean isMelee() {
        return is_melee;
    }

    public boolean isAggressive() {
        return aggressive;
    }

    public boolean canWalk() {
        return walk;
    }

    public int getProjectileId() {
        return projectile;
    }

    public int getEndGraphics() {
        return end_graphics;
    }

    public int getStartGraphics() {
        return start_graphics;
    }

    public int getAttackAnimation() {
        return attack_animation;
    }

    public int getDefenceAnimation() {
        return defence_animation;
    }

    public int getDeathAnimation() {
        return death_animation;
    }

    public int getRespawnDelayTicks() {
        return respawn_delay;
    }

    public int getMaxHealth() {
        return health;
    }

    public int getRangeLevel() {
        return range;
    }

    public int getMagicLevel() {
        return magic;
    }

    public int getDefenceLevel() {
        return defence;
    }

    public int getStrengthLevel() {
        return strength;
    }

    public int getAttackLevel() {
        return attack;
    }

    public String getExamine() {
        return examine;
    }

    /**
     * Gets the NPC itemDefinition.
     *
     * @return The NPC itemDefinition.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the NPC.
     *
     * @return The name of the NPC.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the NPC's size, in tiles.
     *
     * @return The size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets an interaction option.
     *
     * @param slot The slot of the option.
     * @return The option, or {@code null} if there isn't any at the specified
     * slot.
     * @throws IndexOutOfBoundsException if the slot is out of bounds.
     */
    public String getOption(int slot) {
        if (slot < 0 || slot >= options.length) {
            throw new IndexOutOfBoundsException();
        }
        return options[slot];
    }

    public String[] getInteractions() {
        return options.clone();
    }

    public boolean hasInteraction(String name) {
        for (String s : options) {
            if (s == null) continue;
            if (s.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    /**
     * Gets the NPC's combat level.
     *
     * @return The combat level, or -1 if it doesn't have one.
     */
    public int getCombatLevel() {
        return combatLevel;
    }

    /**
     * Checks if there is an interaction option present.
     *
     * @param slot The slot to check.
     * @return {@code true} if so, {@code false} if not.
     * @throws IndexOutOfBoundsException if the slot is out of bounds.
     */
    public boolean hasInteraction(int slot) {
        if (slot < 0 || slot >= options.length) {
            throw new IndexOutOfBoundsException();
        }
        return options[slot] != null;
    }

    /**
     * Checks if the NPC has a combat level.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasCombatLevel() {
        return combatLevel != -1;
    }

    public int getAttackDelay() {
        return this.attack_delay;
    }

    private void decode(ByteBuffer bb) throws IOException {
        NPCType d = this;

        int opcode = -1;

        while ((opcode = (bb.get() & 0xFF)) != 0) {
            switch (opcode) {
                case 163:
                    bb.get();
                    break;
                case 164:
                    bb.getShort();
                    bb.getShort();
                    break;
                case 165:
                    bb.get();
                    break;
                case 168:
                    bb.get();
                    break;
            }
            if (opcode != 1) {
                if (opcode == 2) {
                    d.name = BufferUtils.readRS2String(bb);
                } else if (opcode != 12) {
                    if (opcode < 30 || opcode >= 35) {
                        if (opcode == 40) {
                            int i_1_ = (bb.get() & 0xFF);
                            short[] aShortArray3166 = new short[i_1_];
                            short[] aShortArray3201 = new short[i_1_];
                            for (int i_2_ = 0; i_2_ < i_1_; i_2_++) {
                                aShortArray3201[i_2_] = (short) (bb.getShort() & 0xFFFF);
                                aShortArray3166[i_2_] = (short) (bb.getShort() & 0xFFFF);
                            }
                        } else if (opcode != 41) {
                            if (opcode != 42) {
                                if (opcode != 60) {
                                    if (opcode == 93) {
                                        /* d.isVisibleOnMap = false; */
                                    } else if (opcode == 95) {
                                        d.combatLevel = (bb.getShort() & 0xFFFF);
                                    } else if (opcode != 97) {
                                        if (opcode != 98) {
                                            if (opcode == 99) {
                                                /* d.aBoolean3210 = true; */
                                            } else if (opcode == 100) {
                                                bb.get();
                                            } else if (opcode == 101) {
                                                bb.get();
                                            } else if (opcode == 102) {
                                                /* d.headIcons = ( */
                                                bb.getShort()/*
                                                                                 * &
                                                                                 * 0xFFFF
                                                                                 * )
                                                                                 */;
                                            } else if (opcode == 103) {
                                                /* d.anInt3235 = ( */
                                                bb.getShort()/*
                                                                                 * &
                                                                                 * 0xFFFF
                                                                                 * )
                                                                                 */;
                                            } else if (opcode != 106 && opcode != 118) {
                                                if (opcode != 107) {
                                                    if (opcode == 109) {
                                                        /*
                                                         * d.aBoolean3169 =
                                                         * false;
                                                         */
                                                    } else if (opcode != 111) {
                                                        if (opcode == 113) {
                                                            /*
                                                             * d.aShort3213 =
                                                             * (short) (
                                                             */
                                                            bb.getShort()/*
                                                                             * &
                                                                             * 0xFFFF
                                                                             * )
                                                                             */;
                                                            /*
                                                             * d.aShort3237 =
                                                             * (short) (
                                                             */
                                                            bb.getShort()/*
                                                                             * &
                                                                             * 0xFFFF
                                                                             * )
                                                                             */;
                                                        } else if (opcode == 114) {
                                                            /* d.aByte3215 = */
                                                            bb.get();
                                                            /* d.aByte3193 = */
                                                            bb.get();
                                                        } else if (opcode != 119) {
                                                            if (opcode != 121) {
                                                                if (opcode != 122) {
                                                                    if (opcode == 123) {
                                                                        /*
                                                                         * d.
                                                                         * anInt3203
                                                                         * = (
                                                                         */
                                                                        bb.getShort()/*
                                                                                         * &
                                                                                         * 0xFFFF
                                                                                         * )
                                                                                         */;
                                                                    } else if (opcode != 125) {
                                                                        if (opcode == 127) {
                                                                            /*
                                                                             * d.
                                                                             * renderEmote
                                                                             * =
                                                                             * (
                                                                             */
                                                                            bb.getShort()/*
                                                                                             * &
                                                                                             * 0xFFFF
                                                                                             * )
                                                                                             */;
                                                                        } else if (opcode != 128) {
                                                                            if (opcode == 134) {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3173
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3212
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 */
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3226
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                                /*
                                                                                 * if
                                                                                 * (
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * ==
                                                                                 * 65535
                                                                                 * )
                                                                                 * {
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3179
                                                                                 * =
                                                                                 * -
                                                                                 * 1
                                                                                 * ;
                                                                                 * }
                                                                                 */
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3184
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                            } else if (opcode == 135) {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3214
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3178
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort()/*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                            } else if (opcode != 136) {
                                                                                if (opcode == 137) {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3223
                                                                                     * =
                                                                                     * (
                                                                                     */
                                                                                    bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     * )
                                                                                                     */;
                                                                                } else if (opcode == 138) {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3167
                                                                                     * =
                                                                                     */
                                                                                    bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     */;
                                                                                } else if (opcode != 139) {
                                                                                    if (opcode == 140) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * anInt3216
                                                                                         * =
                                                                                         */
                                                                                        bb.get()/*
                                                                                                     * &
                                                                                                     * 0xFF
                                                                                                     */;
                                                                                    } else if (opcode == 141) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * aBoolean3187
                                                                                         * =
                                                                                         * true
                                                                                         * ;
                                                                                         */
                                                                                    } else if (opcode == 142) {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * anInt3200
                                                                                         * =
                                                                                         */
                                                                                        bb.getShort()/*
                                                                                                         * &
                                                                                                         * 0xFFFF
                                                                                                         */;
                                                                                    } else if (opcode != 143) {
                                                                                        if (opcode < 150 || opcode >= 155) {
                                                                                            if (opcode == 155) {
                                                                                                bb.get();
                                                                                                bb.get();
                                                                                                bb.get();
                                                                                                bb.get();
                                                                                            } else if (opcode != 158) {
                                                                                                if (opcode == 159) {
                                                                                                    /*
                                                                                                     * d
                                                                                                     * .
                                                                                                     * aByte3233
                                                                                                     * =
                                                                                                     * (
                                                                                                     * byte
                                                                                                     * )
                                                                                                     * 0
                                                                                                     * ;
                                                                                                     */
                                                                                                } else if (opcode != 160) {
                                                                                                    if (opcode != 161) {
                                                                                                        if (opcode == 249) {
                                                                                                            int i_3_ = (bb.get() & 0xFF);
                                                                                                            for (int i_5_ = 0; i_3_ > i_5_; i_5_++) {
                                                                                                                boolean bool = (bb.get() & 0xFF) == 1;
                                                                                                                //bb.read24BitInt();
                                                                                                                BufferUtils.getTriByte(bb);
                                                                                                                //System.out.println("ScriptID " + i_6_);
                                                                                                                if (!bool) {
                                                                                                                    bb.getInt();
                                                                                                                } else {
                                                                                                                    BufferUtils.readRS2String(bb);
                                                                                                                }
                                                                                                                //System.out.println("Script NPC STRING: " + bb.readRS2String());
                                                                                                            }
                                                                                                        }
                                                                                                    } else {
                                                                                                        /*
                                                                                                         * d
                                                                                                         * .
                                                                                                         * aBoolean3190
                                                                                                         * =
                                                                                                         * true
                                                                                                         * ;
                                                                                                         */
                                                                                                    }
                                                                                                } else {
                                                                                                    int i_7_ = (bb.get() & 0xFF);
                                                                                                    int[] anIntArray3219 = new int[i_7_];
                                                                                                    for (int i_8_ = 0; i_7_ > i_8_; i_8_++) {
                                                                                                        anIntArray3219[i_8_] = (bb.getShort() & 0xFFFF);
                                                                                                    }
                                                                                                }
                                                                                            } else {
                                                                                                /*
                                                                                                 * d
                                                                                                 * .
                                                                                                 * aByte3233
                                                                                                 * =
                                                                                                 * (
                                                                                                 * byte
                                                                                                 * )
                                                                                                 * 1
                                                                                                 * ;
                                                                                                 */
                                                                                            }
                                                                                        } else {
                                                                                            d.options[opcode - 150] = BufferUtils.readRS2String(bb); //bb.readRS2String();
                                                                                            /*
                                                                                             * if
                                                                                             * (
                                                                                             * !
                                                                                             * (
                                                                                             * (
                                                                                             * Class183
                                                                                             * )
                                                                                             * aClass183_3195
                                                                                             * )
                                                                                             * .
                                                                                             * aBoolean2484
                                                                                             * )
                                                                                             * options
                                                                                             * [
                                                                                             * opcode
                                                                                             * +
                                                                                             * -
                                                                                             * 150
                                                                                             * ]
                                                                                             * =
                                                                                             * null
                                                                                             * ;
                                                                                             */
                                                                                        }
                                                                                    } else {
                                                                                        /*
                                                                                         * d
                                                                                         * .
                                                                                         * aBoolean3196
                                                                                         * =
                                                                                         * true
                                                                                         * ;
                                                                                         */
                                                                                    }
                                                                                } else {
                                                                                    /*
                                                                                     * d
                                                                                     * .
                                                                                     * anInt3164
                                                                                     * =
                                                                                     * (
                                                                                     */
                                                                                    bb.getShort()/*
                                                                                                     * &
                                                                                                     * 0xFFFF
                                                                                                     * )
                                                                                                     */;
                                                                                }
                                                                            } else {
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3181
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.get()/*
                                                                                             * &
                                                                                             * 0xFF
                                                                                             * )
                                                                                             */;
                                                                                /*
                                                                                 * d
                                                                                 * .
                                                                                 * anInt3227
                                                                                 * =
                                                                                 * (
                                                                                 */
                                                                                bb.getShort() /*
                                                                                                 * &
                                                                                                 * 0xFFFF
                                                                                                 * )
                                                                                                 */;
                                                                            }
                                                                        } else {
                                                                            bb.get();
                                                                        }
                                                                    } else {
                                                                        /*
                                                                         * d.
                                                                         * direction
                                                                         * =
                                                                         */
                                                                        bb.get();
                                                                    }
                                                                } else {
                                                                    /*
                                                                     * d.anInt3182
                                                                     * = (
                                                                     */
                                                                    bb.getShort() /*
                                                                                     * &
                                                                                     * 0xFFFF
                                                                                     * )
                                                                                     */;
                                                                }
                                                            } else {
                                                                int i_9_ = (bb.get() & 0xFF);
                                                                for (int i_10_ = 0; (i_9_ > i_10_); i_10_++) {
                                                                    //(bb.get() & 0xFF);
                                                                    bb.get();
                                                                    int[] is = new int[3];
                                                                    is[0] = bb.get();
                                                                    is[1] = bb.get();
                                                                    is[2] = bb.get();
                                                                }
                                                            }
                                                        } else {
                                                            /* d.aByte3207 = */
                                                            bb.get();
                                                        }
                                                    } else {
                                                        /*
                                                         * d.aBoolean3172 =
                                                         * false;
                                                         */
                                                    }
                                                } else {
                                                    /* d.isClickable = false; */
                                                }
                                            } else {
                                                /* d.configFileId = ( */
                                                bb.getShort()/*
                                                                                     * &
                                                                                     * 0xFFFF
                                                                                     * )
                                                                                     */;
                                                /*
                                                 * if (d.configFileId == 65535)
                                                 * { d.configFileId = -1; }
                                                 * d.configId = (
                                                 */
                                                bb.getShort()/* & 0xFFFF) */;
                                                /*
                                                 * if (d.configId == 65535) {
                                                 * d.configId = -1; }
                                                 */
                                                int i_12_ = -1;
                                                if (opcode == 118) {
                                                    i_12_ = (bb.getShort() & 0xFFFF);
                                                    if (i_12_ == 65535) {
                                                        i_12_ = -1;
                                                    }
                                                }
                                                int i_13_ = (bb.get() & 0xFF);
                                                int[] childrenIds = new int[2 + i_13_];
                                                for (int i_14_ = 0; i_14_ <= i_13_; i_14_++) {
                                                    childrenIds[i_14_] = (bb.getShort() & 0xFFFF);
                                                    if ((childrenIds[i_14_]) == 65535) {
                                                        childrenIds[i_14_] = -1;
                                                    }
                                                }
                                                childrenIds[1 + i_13_] = i_12_;
                                            }
                                        } else {
                                            bb.getShort()/* & 0xFFFF */;
                                        }
                                    } else {
                                        bb.getShort()/* & 0xFFFF */;
                                    }
                                } else {
                                    int i_15_ = (bb.get() & 0xFF);
                                    int[] anIntArray3192 = new int[i_15_];
                                    for (int i_16_ = 0; i_16_ < i_15_; i_16_++) {
                                        anIntArray3192[i_16_] = (bb.getShort() & 0xFFFF);
                                    }
                                }
                            } else {
                                int i_17_ = (bb.get() & 0xFF);
                                byte[] aByteArray3205 = new byte[i_17_];
                                for (int i_18_ = 0; i_18_ < i_17_; i_18_++) {
                                    aByteArray3205[i_18_] = bb.get();
                                }
                            }
                        } else {
                            int i_19_ = (bb.get() & 0xFF);
                            short[] aShortArray3183 = new short[i_19_];
                            short[] aShortArray3204 = new short[i_19_];
                            for (int i_20_ = 0; i_20_ < i_19_; i_20_++) {
                                aShortArray3183[i_20_] = (short) (bb.getShort() & 0xFFFF);
                                aShortArray3204[i_20_] = (short) (bb.getShort() & 0xFFFF);
                            }
                        }
                    } else {
                        d.options[opcode - 30] = BufferUtils.readRS2String(bb);
                    }
                } else {
                    d.size = (bb.get() & 0xFF);
                }
            } else {
                int i_21_ = (bb.get() & 0xFF);
                int[] anIntArray3230 = new int[i_21_];
                for (int i_22_ = 0; i_21_ > i_22_; i_22_++) {
                    anIntArray3230[i_22_] = (bb.getShort() & 0xFFFF);
                    if (anIntArray3230[i_22_] == 65535) {
                        anIntArray3230[i_22_] = -1;
                    }
                }
            }
        }

        if (bb.remaining() > 0) {
            //We still have remaining data. The file should end in a NULL terminator.
            //But we read a NULL opcode terminator and we didn't reach the end of the
            //file. This is bad! We don't know where in the file corruption started,
            //so any amount of this data could be rubbish.
            throw new IOException("NPC Format is bad!");
        }
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

        final NPCType other = (NPCType) obj;

        return Objects.equals(this.id, other.id);
    }
}