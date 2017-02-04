package org.maxgamer.rs.model.entity.mob.persona;

import org.maxgamer.rs.model.entity.mob.MobModel;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Equipment;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.util.io.OutputStreamWrapper;

import java.io.IOException;

/**
 * @author netherfoam
 */
public class PersonaModel extends MobModel implements YMLSerializable {

    /**
     * If this flag is set, then the graphics to render has no equipment to
     * overlay
     */
    private static final short FLAG_MODEL_ONLY = 0x100;
    private static final short FLAG_EQUIP_ONLY = (short) 0x8000; // 0x8000 is -1 as a short
    private byte[] look = new byte[]{3, // Hair
            14, // Beard
            18, // Torso
            26, // Arms
            34, // Bracelets
            38, // Legs
            42,// Shoes
    };
    private Persona p;

    public PersonaModel(Persona p) {
        if (p == null) throw new NullPointerException("Persona may not be null");
        this.p = p;
        this.setCombatColoured(true);
        this.setSkulled(false);
        this.setName(p.getName());
    }

    public byte[] getLook() {
        return look;
    }

    @Override
    protected void appendUpdate(OutputStreamWrapper out) throws IOException {
        Equipment e = p.getEquipment();
        // SLOT_HAT = 0, SLOT_CAPE = 1, SLOT_AMULET = 2, SLOT_WEAPON = 3
        for (int i = 0; i < 4; i++) {
            ItemStack item = e.get(i);
            if (item == null || item.getWeapon() == null) {
                out.writeByte(0);
            } else {
                out.writeShort(0x8000 | item.getWeapon().getModel());
            }
        }

        ItemStack chest = e.get(WieldType.BODY);
        if (chest == null || chest.getWeapon() == null) {
            out.writeShort(FLAG_MODEL_ONLY | look[2]); // Torso/Chest
        } else {
            out.writeShort(FLAG_EQUIP_ONLY | chest.getWeapon().getModel());
        }

        ItemStack shield = e.get(WieldType.SHIELD);
        if (shield == null || shield.getWeapon() == null) {
            out.writeByte(0); // Shield
        } else {
            out.writeShort(FLAG_EQUIP_ONLY | shield.getWeapon().getModel());
        }

        /*
         * If we're wearing a full body that hides arms, we notify the client
         */
        if (chest == null || chest.getWeapon() == null || !chest.getWeapon().isFull()) {
            out.writeShort(FLAG_MODEL_ONLY | look[3]); // Arms
        } else {
            out.writeByte(0);
        }

        ItemStack legs = e.get(WieldType.LEGS);
        if (legs == null || legs.getWeapon() == null) {
            out.writeShort(FLAG_MODEL_ONLY | look[5]); // Legs
        } else {
            out.writeShort(FLAG_EQUIP_ONLY | legs.getWeapon().getModel());
        }

        ItemStack hat = e.get(WieldType.HAT);
        if (hat == null || hat.getWeapon() == null || !hat.getWeapon().isFull()) { // TODO: Zamarok/Saradomin hats (4042, 4041) do
            // weird
            // things
            out.writeShort(FLAG_MODEL_ONLY | look[0]); // Hat
        } else {
            out.writeByte(0);
        }

        ItemStack hands = e.get(WieldType.GLOVES);
        if (hands == null || hands.getWeapon() == null) {
            out.writeShort(FLAG_MODEL_ONLY | look[4]); // Hands
        } else {
            out.writeShort(FLAG_EQUIP_ONLY | hands.getWeapon().getModel());
        }

        ItemStack feet = e.get(WieldType.BOOTS);
        if (feet == null || feet.getWeapon() == null) {
            out.writeShort(FLAG_MODEL_ONLY | look[6]); // Feet
        } else {
            out.writeShort(FLAG_EQUIP_ONLY | feet.getWeapon().getModel());
        }

        if (hat == null || hat.getWeapon() == null || !hat.getWeapon().isFull()) {
            out.writeShort(FLAG_MODEL_ONLY | look[1]); // Beard
        } else {
            out.writeByte(0);
        }

        out.writeShort(0);//Overrides TODO:
    }

    @Override
    public String getName() {
        return p.getName();
    }

    @Override
    public byte getPrayerIcon() {
        return (byte) p.getPrayer().getPrayerHeadIcon();
    }

    @Override
    public ConfigSection serialize() {
        ConfigSection map = super.serialize();

        map.set("look", this.look);

        return map;
    }

    @Override
    public void deserialize(ConfigSection map) {
        super.deserialize(map);

        this.look = map.getByteArray("look", this.look).clone();
    }
}