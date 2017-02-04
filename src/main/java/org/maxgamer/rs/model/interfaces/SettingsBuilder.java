package org.maxgamer.rs.model.interfaces;

import java.util.BitSet;

/**
 * A class used to configure the bitwise settings for an interface. The settings
 * available include enabling/disabling the primary left click, or right click
 * options, using items/spells/interface components on ground
 * items/npcs/objects/players/yourself/interface components, configuring the
 * interface event depth (how high up the hierarchy parents are notified of
 * clicks), and whether the interface components itself can be the target of a
 * 'use with' action
 *
 * @author netherfoam
 */
public class SettingsBuilder {
    /**
     * Contains the value which should be sent in access mask packet.
     */
    private int value;

    public static void main(String[] args) {
        System.out.println(convert(2));
    }

    /**
     * Converts the given value into compilable code which will more clearly
     * describe what is going on instead of using a magic number. Use this when
     * taking code from other sources such as Dementhium
     *
     * @param value the value to decompile
     * @return the string representation of code, eg. SettingsBuilder sb = new
     * SettingsBuilder();\n sb.setPrimaryOption(true);\n
     * sb.setcanUseOnNPCs(true);\n
     */
    public static String convert(int value) {
        StringBuilder sb = new StringBuilder();
        SettingsBuilder b = SettingsBuilder.decompile(value);

        sb.append("SettingsBuilder sb = new SettingsBuilder();\n");
        if (b.hasPrimaryOption()) {
            sb.append("sb.setPrimaryOption(true);\n");
        }
        for (int i = 0; i < 10; i++) {
            if (b.hasSecondaryOption(i)) {
                sb.append("sb.setSecondaryOption(").append(i).append(", true);\n");
            }
        }

        boolean ground = b.canUseOnGroundItems();
        boolean npc = b.canUseOnNPCs();
        boolean object = b.canUseOnObjects();
        boolean players = b.canUseOnOtherPlayers();
        boolean self = b.canUseOnSelf();
        boolean component = b.canUseOnInterfaceComponent();

        sb.append("sb.setUseOnSettings(").append(ground).append(", ").append(npc).append(", ").append(object).append(", ").append(players).append(", ").append(self).append(", ").append(component).append(");\n");
        sb.append("sb.setInterfaceDepth(").append(b.getInterfaceDepth()).append(");\n");

        if (b.isUseOnTarget()) {
            sb.append("sb.setIsUseOnTarget(true);\n");
        }

        return sb.toString();
    }

    /**
     * Debug, validates the given settings builder and attempts to reconstruct
     * it from a high-level perspective and prints necesary information.
     *
     * @param b
     */
    public static void test(SettingsBuilder b) {
        BitSet bits = BitSet.valueOf(new long[]{b.value});
        System.out.println("Set Bits: " + bits.toString());

        int offset = 0;
        System.out.println(offset + ": StandardOption: " + ((b.value & 0x1) != 0));
        offset++;

        for (int i = 0; i < 10; i++) {
            System.out.println(offset + ": Right Click Option#" + i + ": " + ((b.value & (0x1 << (offset++))) != 0));
        }

        System.out.println(offset + ": Use On Ground Items: " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Use On NPCs " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Use On Objects " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Use On Players " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Use On Self " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Use On Interface Components " + ((b.value & 0x1 << offset++) != 0));
        System.out.println(offset + ": Depth: " + ((b.value & 0x7 << offset) >> 18));
        offset += 3;

        //What are these other values

        offset = 22;
        System.out.println(offset + ": Allow As Use On Target: " + ((b.value & 0x1 << offset) != 0));

        SettingsBuilder out = new SettingsBuilder();
        out.setSecondaryOption(0, true);
        out.setSecondaryOption(1, true);
        out.setSecondaryOption(2, true);
        out.setSecondaryOption(6, true);
        out.setSecondaryOption(7, true);
        out.setSecondaryOption(9, true);

        out.setUseOnSettings(true, true, true, true, false, true);
        out.setInterfaceDepth(1);
        out.setIsUseOnTarget(true);

        System.out.println(out.value + " vs " + b.value + ": " + (out.value == b.value));
    }

    /**
     * Constructs a SettingsBuilder out of the given value
     *
     * @param value the flags
     * @return the SettingsBuilder not null
     */
    public static SettingsBuilder decompile(int value) {
        SettingsBuilder b = new SettingsBuilder();
        b.value = value;

        return b;
    }

    /**
     * True if the settings have a left click option
     *
     * @return True if the settings have a left click option
     */
    public boolean hasPrimaryOption() {
        return (value & 0x1) != 0;
    }

    /**
     * True if the settings have the right click option for the given id.
     *
     * @param optionId the option id, value is 0-9
     * @return True if the settings have the right click option for the given
     * id.
     */
    public boolean hasSecondaryOption(int optionId) {
        if (optionId < 0 || optionId > 9) throw new IllegalArgumentException("Bad option requested: " + optionId);
        return (value & (0x1 << (optionId + 1))) != 0;
    }

    /**
     * True if the settings allow use with items on the ground
     *
     * @return True if the settings allow use with items on the ground
     */
    public boolean canUseOnGroundItems() {
        return (value & (0x1 << (11))) != 0;
    }

    /**
     * True if the settings allow use with npcs
     *
     * @return True if the settings allow use with npcs
     */
    public boolean canUseOnNPCs() {
        return (value & (0x1 << (12))) != 0;
    }

    /**
     * True if the settings allow use with objects
     *
     * @return True if the settings allow use with objects
     */
    public boolean canUseOnObjects() {
        return (value & (0x1 << (13))) != 0;
    }

    /**
     * True if the settings allow use with other players (not necessarily
     * yourself)
     *
     * @return True if the settings allow use with other players (not
     * necessarily yourself)
     */
    public boolean canUseOnOtherPlayers() {
        return (value & (0x1 << (14))) != 0;
    }

    /**
     * True if the settings allow use on themselves
     *
     * @return True if the settings allow use on themselves
     */
    public boolean canUseOnSelf() {
        return (value & (0x1 << (15))) != 0;
    }

    /**
     * True if the settings allow use on other interface components, eg, high
     * alchemy is used on items.
     *
     * @return True if the settings allow use on other interface components, eg,
     * high alchemy is used on items.
     */
    public boolean canUseOnInterfaceComponent() {
        return (value & (0x1 << (16))) != 0;
    }

    /**
     * 0-7, the depth up the chain to notify parent containers when a button is
     * clicked.
     *
     * @return 0-7, the depth up the chain to notify parent containers when a
     * button is clicked. The higher the depth, the further back the
     * parent.
     */
    public int getInterfaceDepth() {
        int bits = (value & (0x7 << 18));
        return bits >> 18;
    }

    /**
     * Set's interface events depth. For example, we have inventory interface
     * which is opened on gameframe interface (548 or 746). If depth is 1 , then
     * the clicks in inventory will also invoke click event handler scripts on
     * gameframe interface.
     */
    public SettingsBuilder setInterfaceDepth(int depth) {
        if (depth < 0 || depth > 7) throw new IllegalArgumentException("depth must be 0-7.");
        value &= ~(0x7 << 18);
        value |= (depth << 18);
        return this;
    }

    /**
     * True if components can be a catalyst in the 'Use With' functionality. For
     * example, items should set this to true when in the inventory to allow for
     * alchemy, while items in the bank should not.
     *
     * @return true if the components can be a catalyst in teh 'Use With'
     * functionality
     */
    public boolean isUseOnTarget() {
        return (value & (0x1 << 22)) != 0;
    }

    /**
     * Set's standard option settings. Great example of standard click option is
     * the Continue button in dialog interface. If the option is not allowed the
     * packet won't be send to server.
     *
     * @param allowed
     */
    public SettingsBuilder setPrimaryOption(boolean allowed) {
        value &= ~(0x1);
        if (allowed) value |= 0x1;
        return this;
    }

    /**
     * Set's right click option settings. Great example of right click option is
     * the Dismiss option in summoning orb. If specified option is not allowed ,
     * it will not appear in right click menu and the packet will not be send to
     * server when clicked.
     */
    public SettingsBuilder setSecondaryOption(int optionID, boolean allowed) {
        if (optionID < 0 || optionID > 9) throw new IllegalArgumentException("optionID must be 0-9.");
        value &= ~(0x1 << (optionID + 1)); // disable
        if (allowed) value |= (0x1 << (optionID + 1));
        return this;
    }

    /**
     * Sets use on settings. By use on , I mean the options such as Cast in
     * spellbook or use in inventory. If nothing is allowed then 'use' option
     * will not appear in right click menu.
     */
    public SettingsBuilder setUseOnSettings(boolean canUseOnGroundItems, boolean canUseOnNpcs, boolean canUseOnObjects, boolean canUseOnNonselfPlayers, boolean canUseOnSelfPlayer, boolean canUseOnInterfaceComponent) {
        int useFlag = 0;
        if (canUseOnGroundItems) useFlag |= 0x1;
        if (canUseOnNpcs) useFlag |= 0x2;
        if (canUseOnObjects) useFlag |= 0x4;
        if (canUseOnNonselfPlayers) useFlag |= 0x8;
        if (canUseOnSelfPlayer) useFlag |= 0x10;
        if (canUseOnInterfaceComponent) useFlag |= 0x20;
        value &= ~(0x7F << 11); // disable
        value |= useFlag << 11;
        return this;
    }

    /**
     * Set's canUseOnFlag. if it's true then other interface components can do
     * use on this interface component. Example would be using High alchemy
     * spell on the inventory item. If inventory component where items are
     * stored doesn't allow the canUseOn , it would not be possible to use High
     * Alchemy on that item.
     */
    public SettingsBuilder setIsUseOnTarget(boolean allow) {
        value &= ~(1 << 22);
        if (allow) value |= (1 << 22);
        return this;
    }

    public int getValue() {
        return value;
    }

}