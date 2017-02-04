package org.maxgamer.rs.model.interfaces.impl.side;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.SettingsBuilder;
import org.maxgamer.rs.model.interfaces.SideInterface;
import org.maxgamer.rs.model.interfaces.Window;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerListener;
import org.maxgamer.rs.model.item.inventory.Inventory;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.util.Log;

import java.util.Arrays;

/**
 * @author netherfoam
 */
public class InventoryInterface extends SideInterface { // TODO: Add 'ContainerInterface' for InventoryInterface/BankInterface/EquipmentInterface/etc because they're all similar in protocol
    public static final short INTERFACE_ID = 149;
    public static final int CONTAINER_ID = 93;
    private static final SettingsBuilder INTERFACE_CONFIG;

    static {
        INTERFACE_CONFIG = new SettingsBuilder();
        INTERFACE_CONFIG.setSecondaryOption(0, true); // These correspond to the opcode we get
        INTERFACE_CONFIG.setSecondaryOption(1, true);
        INTERFACE_CONFIG.setSecondaryOption(2, true);
        INTERFACE_CONFIG.setSecondaryOption(6, true);
        INTERFACE_CONFIG.setSecondaryOption(7, true); // Eg, clicking the 7th option will result in the option 7 opcode being sent
        INTERFACE_CONFIG.setSecondaryOption(9, true);
        INTERFACE_CONFIG.setUseOnSettings(true, true, true, true, false, true);
        INTERFACE_CONFIG.setInterfaceDepth(1);
        INTERFACE_CONFIG.setIsUseOnTarget(true);
    }

    private ContainerListener listener;

    public InventoryInterface(Player p) {
        // 206 (fixed) or 91 (full)
        // Container ID is 93, but Interface ID is 149
        super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 206 : 91));
        setChildId(INTERFACE_ID);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setAccessMask(INTERFACE_CONFIG.getValue(), 0, 27, 0);
        /*
         * This appears to be when a player uses an item on another item (Eg
         * 'Use with' -> 'other item')
         */
        setAccessMask(new SettingsBuilder().setSecondaryOption(4, true).getValue(), 28, 55, 0);

        for (int i = 0; i < this.getPlayer().getInventory().getSize(); i++) {
            ItemStack item = getPlayer().getInventory().get(i);
            if (item == null)
                // We only show items we have. TODO: This could be optimized by sending an array of slots and items at once
                continue;
            getPlayer().getProtocol().setItem(CONTAINER_ID, false, item, i);
        }

        this.listener = new ContainerListener() {
            @Override
            public void onSet(Container c, int slot, ItemStack old) {
                assert (isOpen()); // Should always be true if called
                getPlayer().getProtocol().setItem(CONTAINER_ID, false, c.get(slot), slot);
            }
        };

        getPlayer().getInventory().addListener(this.listener);
    }

    @Override
    public void onClose() {
        super.onClose();

        // We hide all items we sent them. TOOD: This could be optimized by
        // sending an array of slots and items at once
        for (int i = 0; i < this.getPlayer().getInventory().getSize(); i++) {
            ItemStack item = getPlayer().getInventory().get(i);
            if (item != null)
                continue;
            getPlayer().getProtocol().setItem(CONTAINER_ID, false, null, i);
        }
        getPlayer().getInventory().removeListener(this.listener);
    }

    @Override
    public void onDrag(Window to, int fromItemId, int toItemId, int tabId, int fromSlot, int toSlot) {
        // Client adds a +28 offset when moving items around in the inventory.
        toSlot -= Inventory.SIZE;

        if (fromSlot < 0 || fromSlot >= getPlayer().getInventory().getSize() || toSlot < 0 || toSlot >= getPlayer().getInventory().getSize()) {
            // TODO: Can this be triggered normally, or does it require a hack?
            getPlayer().getCheats().log(10, "Player attempted to move item in inventory to or from an invalid slot. From: " + fromSlot + " to " + toSlot);
            return;
        }

        // TODO: Allow inserting items
        Inventory inv = getPlayer().getInventory();

        // Swap fromSlot and toSlot around.
        ItemStack temp = inv.get(fromSlot);
        if (temp == null) {
            // Shouldn't be doable. Has no positive effect, but it's still a
            // hack.
            getPlayer().getCheats().log(1, "Player attempted to move a NULL item from inventory slot to another slot");
            return;
        }
        inv.set(fromSlot, inv.get(toSlot));
        inv.set(toSlot, temp);
    }

    @Override
    public void onClick(int option, int buttonId, int slot, int itemId) {
        ItemStack item = getPlayer().getInventory().get(slot);
        if (item == null || item.getId() != itemId) {
            getPlayer().getCheats().log(5, "Item click ID mismatch");
            return;
        }

        if (item.getInventoryOptions() == null) {
            getPlayer().getCheats().log(5, " attempted to use item item, but item options are null for " + item);
            return;
        }

        // When the client sends us this, the option counts fields which are set
        // to not display.
        // We do not want these fields. So for each one that is set to false, we
        // must decrease the
        // option number by 1.
        for (int i = option; i >= 0; i--) {
            if (!INTERFACE_CONFIG.hasSecondaryOption(i)) {
                option--;
            }
        }

        if (option == item.getInventoryOptions().length) {
            // Examine
            player.sendMessage(item.getId() + (item.getHealth() != 0 ? "(Health:" + item.getHealth() + ")" : "") + ": " + item.getExamine());
            player.sendMessage("Noted: " + item.isNoted());
            return;
        }

        if (option > item.getInventoryOptions().length) {
            Log.warning("Bad option: " + option + ", available are " + Arrays.toString(item.getInventoryOptions()));
            return;
        }

        String s = item.getInventoryOptions()[option];
        if (s == null) {
            getPlayer().getCheats().log(10, "Player attempted to use option " + option + " on item " + item + ", but item option is null. Options are: " + Arrays.toString(item.getInventoryOptions()));
            return;
        }

        player.use(item, s, slot);
    }

    public void onClick(final Mob target, int buttonId, int slotId, int itemId, boolean run) {
        final ItemStack item = this.getPlayer().getInventory().get(slotId);
        if (item == null) {
            getPlayer().getCheats().log(5, "Player attempted to use a null item on " + target);
            return;
        }
        if (item.getId() != itemId) {
            getPlayer().getCheats().log(5, "Player attempted to use itemId " + itemId + ", but slot " + slotId + " is " + item.getId());
            return;
        }

        AStar finder = new AStar(5);
        Path path = finder.findPath(getPlayer(), target);
        if (!path.hasFailed()) {
            if (!path.isEmpty()) {
                path.removeLast();
            }

            if (!path.isEmpty()) {
                WalkAction walk = new WalkAction(getPlayer(), path) {
                    @Override
                    public void run() throws SuspendExecution {
                        super.run();
                        getPlayer().use(target, item);
                    }
                };
                getPlayer().getActions().queue(walk);
            } else {
                getPlayer().use(target, item);
            }
        } else {
            getPlayer().sendMessage("I can't reach that!");
        }
    }

    @Override
    public void onUse(Window to, int fromButtonId, int fromItemId, int fromSlot, int toButtonId, int toItemId, int toSlot) {
        ItemStack source = this.getPlayer().getInventory().get(fromSlot);
        ItemStack target = this.getPlayer().getInventory().get(toSlot);

        this.getPlayer().use(source, target);
    }

    @Override
    public boolean isMobile() {
        return true;
    }
}
