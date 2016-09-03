package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.item.ItemStack;

import java.util.HashMap;
import java.util.Map.Entry;

public abstract class SmithingInterface<T> extends PrimaryInterface {
    public static final int POS_DAGGER = 0;
    public static final int POS_HATCHET = 1;
    public static final int POS_MACE = 2;
    public static final int POS_MED_HELM = 3;
    public static final int POS_CROSSBOW_BOLTS = 4;
    public static final int POS_SWORD = 5;
    public static final int POS_NAILS = 7;
    public static final int POS_ARROW_TIPS = 11;
    public static final int POS_SCIMITAR = 12;
    public static final int POS_CROSSBOW_LIMBS = 13;
    public static final int POS_LONGSWORD = 14;
    public static final int POS_THROWING_KNIFE = 15;
    public static final int POS_FULL_HELM = 16;
    public static final int POS_SQUARE_SHIELD = 17;
    public static final int POS_WARHAMMER = 20;
    public static final int POS_BATTLEAXE = 21;
    public static final int POS_CHAINBODY = 22;
    public static final int POS_KITESHIELD = 23;
    public static final int POS_2H_SWORD = 25;
    public static final int POS_PLATESKIRT = 26;
    public static final int POS_PLATELEGS = 27;
    public static final int POS_PLATEBODY = 28;

    public static final int INTERFACE_ID = 300;
    public static final int MAX_ITEMS = 30;
    public static final int MAKE_ALL = Integer.MAX_VALUE;

    private static int[] CHILD_IDS = new int[MAX_ITEMS];

    static {
        //Credits: Dementhium
        int counter = 18;
        for (int i = 0; i < CHILD_IDS.length; i++) {
            if (counter == 250) {
                counter = 267;
            }
            CHILD_IDS[i] = counter;
            counter += 8;
        }
    }

    private HashMap<Integer, Option> items = new HashMap<Integer, Option>();

    public SmithingInterface(Player p) {
        super(p);
        setChildId(INTERFACE_ID);
    }

    public void set(int pos, T t, ItemStack icon) {
        items.put(pos, new Option(t, icon));

        if (isOpen()) {
            getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[pos], icon);
        }
    }

    public T get(int pos) {
        Option o = items.get(pos);
        if (o == null) return null;
        return o.t;
    }

    @Override
    public void onOpen() {
        for (Entry<Integer, Option> entry : items.entrySet()) {
            Option o = entry.getValue();
            if (o != null) getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[entry.getKey()], o.icon);
            else getPlayer().getProtocol().sendItemOnInterface(getChildId(), CHILD_IDS[entry.getKey()], null);
            // Intesting note for the future:
            // You can change the names of items from "mace" "sword" etc using this:
            // setString(CHILD_IDS[i]+1, items[i].getName());
            // You can change the costs of items from "1 Bar" "5 Bars" etc using this:
            // setString(CHILD_IDS[i]+2, "9001 Bars");
        }
    }

    @Override
    public boolean isMobile() {
        return false;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        final int pos = (buttonId - 21) / 8;
        int remainder = (buttonId - 21) % 8;

        if (items.get(pos) == null) {
            getPlayer().getCheats().log(5, "Attempted to make an item from a NULL position in smithing interface");
            return;
        }

        getPlayer().getWindow().close(this);

        if (remainder == 0) {
            //Make all
            select(items.get(pos).t, MAKE_ALL);
        } else if (remainder == 1) {
            //Make X
            getPlayer().getWindow().open(new IntRequestInterface(getPlayer(), "How many would you like to make?") {
                @Override
                public void onInput(long value) {
                    select(items.get(pos).t, value);
                }
            });
        } else if (remainder == 2) {
            //Make 5
            select(items.get(pos).t, 5);
        } else if (remainder == 3) {
            //Make 1
            select(items.get(pos).t, 1);
        }
    }

    public abstract void select(T t, long quantity);

    private class Option {
        private ItemStack icon;
        private T t;

        private Option(T t, ItemStack icon) {
            this.t = t;
            this.icon = icon;
        }
    }
}
