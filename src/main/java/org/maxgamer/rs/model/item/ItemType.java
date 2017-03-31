package org.maxgamer.rs.model.item;

import org.maxgamer.rs.assets.MultiAsset;
import org.maxgamer.rs.assets.IDX;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Bonuses;
import org.maxgamer.rs.model.entity.mob.combat.AttackStyle;
import org.maxgamer.rs.model.item.condition.ItemMetadataSet;
import org.maxgamer.rs.model.item.weapon.EquipmentType;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.repository.ItemTypeRepository;
import org.maxgamer.rs.util.BufferUtils;
import org.maxgamer.rs.util.Log;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "Item")
public class ItemType {
    @Id
    protected int id;
    @Column
    protected String name;
    /* Cache Values */
    protected transient int interfaceModelId;
    // The IDs of the models to paint on characters (Eg on the real character,
    // not an interface)
    protected transient int maleWornModelId1 = -1;
    protected transient int maleWornModelId2 = -1;
    protected transient int femaleWornModelId1 = -1;
    protected transient int femaleWornModelId2 = -1;
    @Column
    private String examine;
    @Column
    private boolean tradeable;
    @Column
    private boolean droppable;
    @Column
    private long maxStack;
    @Column
    private int value;
    @Column
    private int lowAlchemy;
    @Column
    private int highAlchemy;
    @Column
    private double weight;
    @OneToOne(mappedBy = "item")
    private EquipmentType weapon;
    @OneToMany(mappedBy = "weapon")
    private List<ItemAmmoType> ammo = new LinkedList<>();
    @OneToOne(mappedBy = "item")
    private AmmoType projectile;
    /*
     * private int modelZoom; private int modelRotation1; private int
     * modelRotation2;
     */
    private transient int modelOffset1;
    private transient int modelOffset2;
    /* private int stackable; */
    /* private int value; */
    private transient boolean membersOnly;
    private transient String[] groundOptions = new String[]{null, "Take", null, null, null};
    private transient String[] inventoryOptions = new String[]{null, null, null, null, "Drop"};
    private transient boolean unnoted;

    /*
     * private int[] originalModelColors; private int[] modifiedModelColors;
     * private int[] textureColour1; private int[] textureColour2; private
     * byte[] unknownArray1; private int[] unknownArray2;
     */
    /*
     * private int colourEquip1; private int colourEquip2; private int certId;
     * private int certTemplateId;
     */
    private transient int[] stackIds;
    private transient int[] stackAmounts;
    private transient int teamId;
    /**
     * Known indexes: Broken pieces (pickaxe handle, broken pickaxe) have 59 ->
     * 1 Strange fruit has 1264 -> Eat Lobster Pot has 770 -> 10 //Fishing skill
     * ID 771 -> 16 Fishing Rod has 770 -> 10 //Fishing skill ID 771 -> 5
     * Limpwurt Root 23 -> 26 White Berries 23 -> 59 Jangerberries 23 -> 48
     * Grimy Kwuarm 770 -> 15 //Herblroe skill ID 771 -> 54 764 -> 33 23 -> 56
     * Tortstol 770 -> 15 771 -> 75 764 -> 33 23 -> 79 *All* Potions 59 -> 1
     * Wizard Hat 624 -> 1 Blurite Sword 644 -> 292 //Quest ID or something? 686
     * -> 6 //Quest ID or something? Phoenix x'bow 644 -> 175 //Quest ID or
     * something? 686 -> 17 //Quest ID or something? 23 -> 1 Bronze Dart 686 ->
     * 18 23 -> 1 Steel Dart 686 -> 18 749 -> 4 750 -> 40 23 -> 40 687 -> 1 Rune
     * Dart 749 -> 4 686 -> 18 750 -> 40 23 -> 40 Magic shortbow(861) -> {749=4,
     * 686=16, 750=50, 23=50, 687=1} Iron arrow(884) -> {23=1} Steel arrow(886)
     * -> {749=4, 750=5, 23=5} Rune arrow(892) -> {749=4, 750=40, 23=40} Index
     * 23 appears to be range level requirement Index 749 appears to be skill
     * requirement (combat?) Index 750 appears to be ^ level requirement Index
     * 751 skill requirement Index 752 level requirement ^
     */
    private transient HashMap<Integer, Object> clientScriptData;
    /*
     * private int lendId; private int lendTemplateId;
     */
    private transient ItemMetadataSet flags;

    private ItemType() {

    }

    private ItemType(int id) {
        if (id != (short) id) throw new IllegalArgumentException("Id's must be a short.");
        this.id = (short) id;
    }

    private static void discard(ByteBuffer bb, int op, int bytes) {
        for (int i = 0; i < bytes; i++) {
            bb.get();
        }
    }

    @PostLoad
    public void cache() throws IOException {
        MultiAsset a = Core.getCache().archive(IDX.ITEMS, id >> 8);
        ByteBuffer bb = a.get(id & 0xFF);
        if (bb == null) {
            throw new FileNotFoundException("ItemID " + id + " not available in cache.");
        }
        this.readOpcodeValues(bb);
    }

    public AmmoType getProjectile() {
        return projectile;
    }

    public void setProjectile(AmmoType projectile) {
        this.projectile = projectile;
    }

    public List<ItemAmmoType> getAmmo() {
        return ammo;
    }

    public void setAmmo(List<ItemAmmoType> ammo) {
        this.ammo = ammo;
    }

    public int getCharges() {
        int start = this.name.lastIndexOf('(');
        int end = this.name.lastIndexOf(')');

        if (start == -1 || end == -1 || start > end)
            return 0;

        try {
            return Integer.parseInt(this.name.substring(start + 1, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public ItemType toCharges(int n) {
        try {
            int end = this.name.lastIndexOf('(');
            String next = this.name.substring(0, end) + '(' + n + ')';

            List<ItemType> options = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).findByName(next);
            for (ItemType proto : options) {
                if (proto.isNoted() == this.isNoted())
                    return proto;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Object> getScriptData() {
        if (clientScriptData == null)
            return Collections.emptyMap();
        return new HashMap<>(clientScriptData);
    }

    /**
     * The weapon data for this item, may be null
     *
     * @return the weapon data, possibly null
     */
    public EquipmentType getWeapon() {
        return weapon;
    }

    public String getName() {
        if (name == null) {
            ItemType def = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).find(this.id - 1);
            if (def != null) {
                this.name = def.getName();
            } else {
                this.name = "null";
            }
        }
        return name;
    }

    public boolean stacksWith(int id) {
        if (stackIds == null)
            return false;

        for (int stackId : this.stackIds) {
            if (stackId == id)
                return true;
        }
        return false;
    }

    public synchronized ItemMetadataSet getMetadata() {
        if (flags == null) {
            if (this.clientScriptData == null) {
                this.clientScriptData = new HashMap<>(0);
            }

            flags = new ItemMetadataSet(this.clientScriptData);
        }

        return flags;
    }

    public boolean isMembers() {
        return membersOnly;
    }

    public String getExamine() {
        return examine;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public boolean isDroppable() {
        return droppable;
    }

    public long getMaxStack() {
        return maxStack;
    }

    public int getValue() {
        return value;
    }

    public int getLowAlchemy() {
        return lowAlchemy;
    }

    public int getHighAlchemy() {
        return highAlchemy;
    }

    public double getWeight() {
        return weight;
    }

    public int getTeamId() {
        return teamId;
    }

    public boolean isWieldable() {
        for (String option : inventoryOptions) {
            if (option == null)
                continue;
            option = option.toLowerCase();
            if (option.equals("wear") || option.equals("wield")
                    || option.equals("equip")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fetches the attack style in the given slot for this weapon (if this is a
     * weapon). Eg, the name of the attack ("Bash", "Chop"), the bonus type used
     * (PersonaCombatStats.BONUS_ATK_CRUSH, etc) and the skills rewarded (Eg
     * Attack or Strength). If called on a non-weapon, returns null. If the
     * given slot is invalid, returns null.
     *
     * @param slot the slot, 1-4, to get data for.
     * @return the attack style data or null if none.
     */
    public AttackStyle getAttackStyle(int slot) {
        if ((slot < 1 || slot > 4) && slot != -1) {
            throw new IllegalArgumentException("Slot must be between 1 and 4 inclusive.");
        }

        Integer v = getMetadata().getAttackType();
        if (v == null) {
            return new AttackStyle(1, "Punch", Bonuses.ATK_CRUSH, SkillType.ATTACK);
        }

        return AttackStyle.getStyle(v.intValue(), slot);
    }

    public boolean isNoted() {
        if (unnoted) return false;
        if (getMaxStack() <= 1) return false;

        try {
            // The item immediately before this item should be the unnoted version. If it is,
            // they should have the same name. If not, then this cannot be a noted version.
            ItemType last = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).find(id - 1);
            if (last.getName().equals(this.getName())) {
                return true;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWeapon() {
        return weapon != null;
    }

    public String[] getInventoryOptions() {
        return inventoryOptions.clone();
    }

    public String[] getGroundOptions() {
        return groundOptions.clone();
    }

    private void readOpcodeValues(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.get() & 0xFF;
            if (opcode == 0)
                break;
            readValues(buffer, opcode);
        }
        if (buffer.remaining() > 0) {
            Log.warning("ItemDefinition Buffer remaining " + buffer.remaining());
        }
    }

    private void readValues(ByteBuffer buffer, int opcode) {
        switch (opcode) {
            case 4:
            /* modelZoom = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                break;
            case 5:
            /* modelRotation1 = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                break;
            case 6:
            /* modelRotation2 = */
                discard(buffer, opcode, 2);/* & 0xFFFF */
                break;
            case 11:
                //stackable = true;
                break;
            case 12:
            /* value = */
                buffer.getInt();
                break;
            case 23:
                maleWornModelId1 = buffer.getShort() & 0xFFFF;
                break;
            case 25:
                maleWornModelId2 = buffer.getShort() & 0xFFFF;
                break;
            case 26:
                femaleWornModelId2 = buffer.getShort() & 0xFFFF;
                break;
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
                inventoryOptions[opcode - 35] = BufferUtils.readRS2String(buffer);
                break;
            case 40: {
                int length = buffer.get() & 0xFF;
                int[] originalModelColors = new int[length];
                int[] modifiedModelColors = new int[length];
                for (int index = 0; index < length; index++) {
                    originalModelColors[index] = buffer.getShort() & 0xFFFF;
                    modifiedModelColors[index] = buffer.getShort() & 0xFFFF;
                }
                break;
            }
            case 41: {
                int length = buffer.get() & 0xFF;
                int[] textureColour1 = new int[length];
                int[] textureColour2 = new int[length];
                for (int index = 0; index < length; index++) {
                    textureColour1[index] = buffer.getShort() & 0xFFFF;
                    textureColour2[index] = buffer.getShort() & 0xFFFF;
                }
                break;
            }
            case 42: {
                int length = buffer.get() & 0xFF;
                byte[] unknownArray1 = new byte[length];
                for (int index = 0; index < length; index++)
                    unknownArray1[index] = buffer.get();
                break;
            }
            case 65:
                unnoted = true;
                break;
            case 78:
            /* colourEquip1 = */
                buffer.getShort()/* & 0xFFFF */;
                break;
            case 79:
            /* colourEquip2 = */
                buffer.getShort()/* & 0xFFFF */;
                break;
            case 91:
                // buffer.getShort();
                discard(buffer, opcode, 2);
                break;
            case 98:
            /* certTemplateId = */
                discard(buffer, opcode, 2); /* & 0xFFFF */
                break;
            case 110:
                discard(buffer, opcode, 2);
                break;
            case 111:
                discard(buffer, opcode, 2);
                break;
            case 115:
                teamId = buffer.get() & 0xFF;
                break;
            case 122:
            /* lendTemplateId = */
                buffer.getShort()/* & 0xFFFF */;
                break;
            case 130:
                discard(buffer, opcode, 1);
                discard(buffer, opcode, 2);
                break;
            case 139:
                discard(buffer, opcode, 2);
                break;
            case 249: {
                int length = buffer.get() & 0xFF;
                if (clientScriptData == null) {
                    clientScriptData = new HashMap<>();
                }
                for (int index = 0; index < length; index++) {
                    boolean stringInstance = buffer.get() == 1;
                    int key = BufferUtils.getTriByte(buffer);
                    Object value = stringInstance ? BufferUtils
                            .readRS2String(buffer) : buffer.getInt();
                    clientScriptData.put(key, value);
                }
                break;
            }
            case 140:
                discard(buffer, opcode, 2);
                break;
            case 134:
                discard(buffer, opcode, 1);
                break;
            case 132: {
                int length = buffer.get() & 0xFF;
                int[] unknownArray2 = new int[length];
                for (int index = 0; index < length; index++) {
                    unknownArray2[index] = buffer.getShort() & 0xFFFF;
                }
                break;
            }
            case 129:
            case 128:
            case 127:
                discard(buffer, opcode, 1);
                discard(buffer, opcode, 2);
                break;
            case 126:
            case 125:
                buffer.get();
                buffer.get();
                buffer.get();
                break;
            case 121:
            /* lendId = */
                buffer.getShort()/* & 0xFFFF */;
                break;
            case 114:
                buffer.get();
                break;
            case 113:
                buffer.get();
                break;
            case 112:
                discard(buffer, opcode, 2);
                break;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                if (stackIds == null) {
                    stackIds = new int[10];
                    stackAmounts = new int[10];
                }
                stackIds[opcode - 100] = buffer.getShort() & 0xFFFF;
                stackAmounts[opcode - 100] = buffer.getShort() & 0xFFFF;
                break;
            case 96:
                buffer.get();
                // certId = buffer.getShort();
                break;
            case 97:
            /* certTemplateId = */
                buffer.getShort();
                break;
            case 95:
            case 93:
            case 92:
            case 90: // unknown
                discard(buffer, opcode, 2);
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                groundOptions[opcode - 30] = BufferUtils.readRS2String(buffer);
                break;
            case 24:
                femaleWornModelId1 = buffer.getShort();
                break;
            case 18:
                discard(buffer, opcode, 2);
                break;
            case 16:
                membersOnly = true;
                break;
            case 8:
                modelOffset2 = buffer.getShort() & 0xFFFF;
                if (modelOffset2 > 32767)
                    modelOffset2 -= 65536;
                modelOffset2 <<= 0;
                break;
            case 7:
                modelOffset1 = buffer.getShort() & 0xFFFF;
                if (modelOffset1 > 32767)
                    modelOffset1 -= 65536;
                modelOffset1 <<= 0;
                break;
            case 2:
                name = BufferUtils.readRS2String(buffer);
                break;
            case 1:
                interfaceModelId = buffer.getShort() & 0xFFFF;
                break;
            default:
                System.out.println("Unknown opcode " + opcode);
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        // determine fields declared in this class only (no fields of
        // superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields) {
            if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                continue; // Field is static
            }
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                // requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    public boolean isStackable() {
        return maxStack > 1;
    }

    public int getRenderAnimation() {
        Integer v = getMetadata().getRenderAnimation();
        if (v == null) return 1426;

        return v;
    }

    public ItemStack toItem() {
        return ItemStack.create(this.id);
    }

    public ItemStack toItem(int quantity) {
        return ItemStack.create(this.id, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType that = (ItemType) o;

        return this.id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}