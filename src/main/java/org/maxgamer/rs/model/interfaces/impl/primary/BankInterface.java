package org.maxgamer.rs.model.interfaces.impl.primary;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.PrimaryInterface;
import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.Window;
import org.maxgamer.rs.model.interfaces.impl.chat.IntRequestInterface;
import org.maxgamer.rs.model.interfaces.impl.side.BankSideInterface;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.*;
import org.maxgamer.rs.util.Calc;

/**
 * @author netherfoam
 */
public class BankInterface extends PrimaryInterface {
    public static final int BANK_CONTAINER_ID = 95;
    public static final short INTERFACE_ID = 762;
    public static final int WITHDRAW_X_CONFIG = 1249;
    private static SettingsBuilder SETTINGS = new SettingsBuilder();

    static {
        SETTINGS.setSecondaryOption(0, true);
        SETTINGS.setSecondaryOption(1, true);
        SETTINGS.setSecondaryOption(2, true);
        SETTINGS.setSecondaryOption(3, true);
        SETTINGS.setSecondaryOption(4, true);
        SETTINGS.setSecondaryOption(5, true);
        SETTINGS.setSecondaryOption(6, true);
        SETTINGS.setSecondaryOption(9, true);
        SETTINGS.setInterfaceDepth(2);
    }

    private ContainerListener listener;
    private boolean noted = false;

    private BankSideInterface side;

    public BankInterface(Player p) {
        super(p);
        setChildId(INTERFACE_ID);
        this.side = new BankSideInterface(p);
    }

    @Override
    public boolean isMobile() {
        return false;
    }

    @Override
    public void onClick(int option, int buttonId, int slotId, int itemId) {
        if (buttonId == 43) {
            //The 'X' at the top right.
            this.getPlayer().getWindow().close(this);
            return;
        }

        if (buttonId == 19) {
            this.noted = !this.noted;
        }

        if (buttonId == 33) {
            // Deposit inventory
            for (ItemStack item : this.getPlayer().getInventory()) {
                if (item == null) continue;
                ContainerState inv = this.getPlayer().getInventory().getState();
                ContainerState bank = this.getPlayer().getBank().getState();
                try {
                    inv.remove(item);
                    bank.add(item);

                    inv.apply();
                    bank.apply();
                } catch (ContainerException e) {
                    // No space
                    continue;
                }
            }
            return;
        }

        if (buttonId == 93) {
            ItemStack item = this.getPlayer().getBank().get(slotId);
            if (item == null) {
                this.getPlayer().getCheats().log(5, "Player attempted to interact with a NULL item in bank");
                return;
            }

            switch (option) {
                case 9: //Examine
                    this.getPlayer().sendMessage(item.getExamine());
                    return;
                case 0:
                    item = item.setAmount(1);
                    break;
                case 1:
                    item = item.setAmount(5);
                    break;
                case 2:
                    item = item.setAmount(10);
                    break;
                case 3: //Last 'X'
                    item = item.setAmount(this.getPlayer().getConfig().getInt("input.bank", 0));
                    break;
                case 4: //Withdraw X
                    final ItemStack stack = item;
                    this.getPlayer().getWindow().open(new IntRequestInterface(this.player, "Withdraw how many?") {
                        @Override
                        public void onInput(long value) {
                            ItemStack take = stack;
                            ItemStack give = stack;
                            if (noted) give = give.getNoted();

                            //TODO: If noted, inventory check needs to be different.
                            value = Calc.minl(value, this.getPlayer().getBank().getNumberOf(take), getPlayer().getInventory().getSpaceFor(give));

                            if (value <= 0) { //Will become null if amount <= 0
                                return;
                            }
                            take = take.setAmount(value);
                            give = give.setAmount(value);

                            ContainerState bank = this.getPlayer().getBank().getState();
                            ContainerState inv = this.getPlayer().getInventory().getState();

                            try {
                                bank.remove(take);
                                inv.add(give);
                                bank.shift();
                            } catch (ContainerException e) {
                                this.getPlayer().sendMessage("There isn't enough space!");
                                return;
                            }
                            bank.apply();
                            inv.apply();

                            this.getPlayer().getConfig().set("input.bank", value);
                            this.getPlayer().getProtocol().sendConfig(WITHDRAW_X_CONFIG, this.getPlayer().getConfig().getInt("input.bank"));
                        }
                    });
                    return;
                case 5:
                    item = item.setAmount(this.getPlayer().getBank().getNumberOf(item));
                    break;
                case 6:
                    item = item.setAmount(this.getPlayer().getBank().getNumberOf(item) - 1);
                    if (item == null) {
                        return; //Means there's only one in the bank
                    }
                    break;
            }

            if (item == null) {
                return;
            }

            ItemStack take = item;
            ItemStack give = item;
            if (noted) give = give.getNoted();

            long amount = Calc.minl(take.getAmount(), this.getPlayer().getBank().getNumberOf(take), getPlayer().getInventory().getSpaceFor(give));
            if (amount <= 0) {
                return;
            }

            give = give.setAmount(amount);
            take = take.setAmount(amount);

            ContainerState bank = this.getPlayer().getBank().getState();
            ContainerState inv = this.getPlayer().getInventory().getState();

            try {
                bank.remove(take);
                inv.add(give);
                bank.shift();
            } catch (ContainerException e) {
                this.getPlayer().sendMessage("There isn't enough space!");
                return;
            }
            bank.apply();
            inv.apply();
        }
    }

    //private void sendOptions() {
    //getPlayer().getProtocol().invoke(1451); //Done by dementhium?
    //-1 = ?
    //0 = ?
    //6 = inverse scale ? rows that fit in one 'scrollbar'?
    //10 = columns
    //95 = bank container id
    //93 = bank item area component/buttonid
    //Something here is still wrong, maybe we need borders or something?
    //getPlayer().getProtocol().invoke(150, "", "", "Withdraw-All but one", "Withdraw-All", "Withdraw-X", "Withdraw-" + getPlayer().getConfig().getInt("input.bank"), "Withdraw-10", "Withdraw-5", "Withdraw-1", 0, 0, 6, 10, 95, (getChildId() << 16) | 93);
    //getPlayer().getProtocol().invoke(151, "", "", "Withdraw-All but one", "Withdraw-All", "Withdraw-X", "Withdraw-" + getPlayer().getConfig().getInt("input.bank"), "Withdraw-10", "Withdraw-5", "Withdraw-1", 0, 0, 6, 10, 95, (getChildId() << 16) | 93);
    /*
	 * for(int slot = 0; slot < getPlayer().getBank().getSize(); slot++){
	 * getPlayer().getProtocol().invoke(153, "9", "8", "7", "6", "5", "4", "3",
	 * "2", "1", 0, 0, 0, -1, 0, slot, (getChildId() << 16) | 93, slot, 95); }
	 */
    //}

    @Override
    public void onClose() {
        super.onClose();

        this.getPlayer().getWindow().close(this.side);

        //We hide all items we sent them. TODO: This could be optimized by sending an array of slots and items at once
        for (int i = 0; i < this.getPlayer().getBank().getSize(); i++) {
            ItemStack item = this.getPlayer().getBank().get(i);
            if (item != null) {
                continue;
            }
            this.getPlayer().getProtocol().setItem(BANK_CONTAINER_ID, false, null, i);
        }
        this.getPlayer().getBank().removeListener(this.listener);
    }

    @Override
    public void onDrag(Window to, int fromItemId, int toItemId, int tabId, int fromSlot, int toSlot) {
        //TODO: This is not functioning as intended. Do I need to inform the client the type of movement? (insert, swap)
        Interface iface = this.getPlayer().getWindow().getInterface(BankInterface.INTERFACE_ID);
        if (iface == null) {
            this.getPlayer().getCheats().log(10, "Player attempted to move an item around in the bank while bank interface wasn't open");
            return;
        }

        if (tabId == 93) {
            if (fromSlot < 0 || fromSlot >= this.getPlayer().getBank().getSize() || toSlot < 0 || toSlot >= this.getPlayer().getBank().getSize()) {
                //TODO: Can this be triggered normally?
                this.getPlayer().getCheats().log(10, "Player attempted to move item in bank to or from an invalid slot. From: " + fromSlot + " to " + toSlot);
                return;
            }

            //TODO: Allow inserting items
            BankContainer b = this.getPlayer().getBank();

            //Swap fromSlot and toSlot around.
            ItemStack temp = b.get(fromSlot);
            if (temp == null) {
                //Shouldn't be doable. Has no positive effect, but it's still a hack.
                this.getPlayer().getCheats().log(1, "Player attempted to move a NULL item from bank slot to another slot");
                return;
            }
            b.set(fromSlot, b.get(toSlot));
            b.set(toSlot, temp);
        } else {
            //TODO: This seems to be modifying the tabs that the player has.
            //Eg inserting the item into another tab.
        }
    }

    @Override
    public void onOpen() {
        this.setAccessMask(SETTINGS.getValue(), 0, 516, 93);
        this.getPlayer().getProtocol().sendConfig(563, 4194304);

        super.onOpen();

        this.getPlayer().getWindow().open(this.side);
        this.sendTabConfig();

        this.getPlayer().getBank().shift();
        for (int i = 0; i < this.getPlayer().getBank().getSize(); i++) {
            ItemStack item = this.getPlayer().getBank().get(i);
            if (item == null) {
                continue; //We only show items we have. TODO: This could be optimized by sending an array of slots and items at once
            }
            this.getPlayer().getProtocol().setItem(BANK_CONTAINER_ID, false, item, i);
        }

        this.listener = new ContainerListener() {
            @Override
            public void onSet(Container c, int slot, ItemStack old) {
                assert (BankInterface.this.isOpen()); //Should always be true if called
                BankInterface.this.getPlayer().getProtocol().setItem(BANK_CONTAINER_ID, false, c.get(slot), slot);

                // If this bank doesn't need shifting, this will have no effect.
                // If it does need shifting, this will be called recursively.
                getPlayer().getBank().shift();
            }
        };

        this.getPlayer().getBank().addListener(this.listener);
        this.getPlayer().getProtocol().sendConfig(WITHDRAW_X_CONFIG, this.getPlayer().getConfig().getInt("input.bank"));
    }

    public void sendTabConfig() {
        int config = 0;
        config += 0; //getItemsInTab(2);
        config += 0; //getItemsInTab(3) << 10;
        config += 0; //getItemsInTab(4) << 20;
        this.player.getProtocol().sendConfig(1246, config);
        config = 0;
        config += 0; //getItemsInTab(5);
        config += 0;//getItemsInTab(6) << 10;
        config += 0;//getItemsInTab(7) << 20;
        this.player.getProtocol().sendConfig(1247, config);
        //int tab = (Integer) player.getAttribute("currentTab", 10);
        int tab = 10;
        config = -2013265920;
        config += (134217728 * (tab == 10 ? 0 : tab - 1));
        config += 0;//getItemsInTab(8);
        config += 0;//getItemsInTab(9) << 10;
        this.player.getProtocol().sendConfig(1248, config);
    }
}