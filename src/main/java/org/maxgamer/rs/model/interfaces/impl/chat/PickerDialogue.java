package org.maxgamer.rs.model.interfaces.impl.chat;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.impl.dialogue.Dialogue;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author netherfoam
 */
public abstract class PickerDialogue<T> extends Dialogue {
    /**
     * The global string ids which correspond to the name of the items. Eg, the
     * icon for one item could be displayed but the name of another could be
     * displayed.
     */
    public static final int[] NAME_IDS = {132, 133, 134, 135, 136, 137, 275, 316, 317, 318};
    /**
     * the global int "BConfig" ids that correspond to the item ID, used for
     * icon of item.
     */
    public static final int[] CONFIG_IDS = {755, 756, 757, 758, 759, 760, 120, 185, 87, 90};
    /**
     * The maximum amount they can select
     */
    private int maxAmount;
    /**
     * The items they can select from
     */
    private ArrayList<Option> items = new ArrayList<Option>(10);
    /**
     * The sub interface used to control the amount they're selecting
     */
    private AmountSelectorInterface amount;

    /**
     * Constructs a new ItemPickerInterface. This interface allows the given
     * player to choose an item from a list of items in the chatbox area up to a
     * maximum of 10 items. They can modify the amount of items they'd like to
     * choose, from 0 to the given maximum amount. The default amount is set to
     * the maximum amount.
     *
     * @param p         the player this interface is for
     * @param maxAmount the maximum amount they can select.
     * @param items     the items they can choose from, maximum of 10 items.
     */
    public PickerDialogue(Player p, int maxAmount) {
        super(p);
        setChildId(905);
        if (maxAmount < 0) {
            throw new IllegalArgumentException("MaxAmount must be >= 0");
        }

        this.maxAmount = maxAmount;
        this.amount = new AmountSelectorInterface();
    }

    /**
     * Adds the given item as an option for this item picker dialogue
     *
     * @param item the item to add as an option
     */
    public void add(T item, String name, int id) {
        if (items.size() >= NAME_IDS.length) throw new IllegalStateException("ItemPickerDialogue may only contain up to 10 items!");
        Option option = new Option(item, name, id);
        items.add(option);

        if (this.isOpen()) {
            getPlayer().getProtocol().sendBConfig(CONFIG_IDS[items.size() - 1], option.icon);
            getPlayer().getProtocol().sendGlobalString(NAME_IDS[items.size() - 1], option.name);
        }
    }

    /**
     * Removes the given item as an option for this item picker dialogue
     *
     * @param item the item to remove as an option
     */
    public void remove(T item) {
        Iterator<Option> iit = items.iterator();
        while (iit.hasNext()) {
            Option o = iit.next();
            if (o.item == item || o.item.equals(item)) {
                iit.remove();

                if (this.isOpen()) {
                    // TODO: This is removing the wrong item from the UI
                    getPlayer().getProtocol().sendBConfig(CONFIG_IDS[items.size() - 1], 0);
                    getPlayer().getProtocol().sendGlobalString(NAME_IDS[items.size() - 1], "");
                }
                return;
            }
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        this.amount.setString(1, "Pick an item");
        getPlayer().getWindow().open(this.amount);

        getPlayer().getProtocol().sendBConfig(754, 13); //Hmm?

        for (int i = 0; i < items.size(); i++) {
            Option item = items.get(i);
            getPlayer().getProtocol().sendBConfig(CONFIG_IDS[i], item.icon);
            getPlayer().getProtocol().sendGlobalString(NAME_IDS[i], item.name);
        }
        // Reset any old ID's
        for (int i = items.size(); i < CONFIG_IDS.length; i++) {
            getPlayer().getProtocol().sendBConfig(CONFIG_IDS[i], -1);
            getPlayer().getProtocol().sendGlobalString(NAME_IDS[i], "");
        }

        //Sets the maximum amount the user can select
        getPlayer().getProtocol().sendConfig(1363, maxAmount << 20 | amount.amount << 26);

        //I don't think this interface should be click-through in that you can click 'Walk Here' through
        //the interface. I think an access mask changing the interface depth would fix it but I don't know
        //the interface offset and length (0 to n?) or the component id of the background
        /*
         * SettingsBuilder s = new SettingsBuilder(); s.setSecondaryOption(0,
		 * true); setAccessMask(s.getValue(), 0, 20, 0);
		 */
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.amount.isOpen()) getPlayer().getWindow().close(this.amount);
    }

    @Override
    public boolean isMobile() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        Option item;
        int index = buttonId - 14;

        // Options above 7 need have different button ID's
        if (index >= 7) {
            index -= 4;
        }

        try {
            item = items.get(index);
        } catch (IndexOutOfBoundsException e) {
            getPlayer().getCheats().log(2, "Attempted to select an item which does not exist, index " + index);
            return;
        }

        try {
            getPlayer().getWindow().close(this); //Close before, in case they want to ask the player for the next item or something
            this.pick(item.item, amount.amount);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int size() {
        return this.items.size();
    }

    /**
     * Called when the player makes a selection. Not guaranteed to be called eg
     * if the player moves away or logs out before clicking on the interface.
     *
     * @param item the item they selected, may be null if they selected a zero
     *             amount
     */
    public abstract void pick(T item, int amount);

    private class AmountSelectorInterface extends Interface {
        private int amount = maxAmount;

        public AmountSelectorInterface() {
            super(PickerDialogue.this.player, PickerDialogue.this, (short) (PickerDialogue.this.player.getSession().getScreenSettings().getDisplayMode() < 2 ? -1 : 4), true);
            setChildId(916);
        }

        @Override
        public boolean isServerSidedClose() {
            return PickerDialogue.this.isServerSidedClose();
        }

        @Override
        public boolean isMobile() {
            return PickerDialogue.this.isMobile();
        }

        @Override
        public void onClick(int option, int buttonId, int slotId, int itemId) {
            //btn=20 = amount--;
            if (buttonId == 20) amount--;
            //btn=19 = amount++;
            if (buttonId == 19) amount++;
            //btn=7 = amount = 10;
            if (buttonId == 7) amount = 10;
            //btn=6 = amount = 5;
            if (buttonId == 6) amount = 5;
            //btn=5 = amount = 1;
            if (buttonId == 1) amount = 1;

            if (amount > maxAmount) amount = maxAmount;
            else if (amount < 0) amount = 0;
        }
    }

    private class Option {
        /**
         * The value that can be picked
         */
        private T item;

        /**
         * The name of the value displayed on the screen
         */
        private String name;

        /**
         * The item ID to display
         */
        private int icon;

        private Option(T item, String name, int icon) {
            this.item = item;
            this.name = name;
            this.icon = icon;
        }
    }
}