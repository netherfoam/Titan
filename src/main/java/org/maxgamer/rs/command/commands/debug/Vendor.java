package org.maxgamer.rs.command.commands.debug;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.interfaces.impl.primary.VendorInterface;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.vendor.VendorItem;
import org.maxgamer.rs.model.item.vendor.VendorType;
import org.maxgamer.rs.util.Erratic;

/**
 * @author netherfoam
 */
@CmdName(names = {"shop"})
public class Vendor implements PlayerCommand {

    @Override
    public void execute(Player player, String[] args) throws Exception {
        ItemStack[] items = new ItemStack[40];
        for (int i = 0; i < items.length; i++) {
            try {
                items[i] = ItemStack.create(Erratic.nextInt(0, 10000), 100);
            } catch (Exception e) {
                items[i] = ItemStack.create(995);
            }
        }

        VendorType vendor = new VendorType();
        for (ItemStack item : items) {
            vendor.getItems().add(new VendorItem(vendor, item.getDefinition(), item.getAmount()));
        }
        vendor.setName("Admin Shop");
        vendor.setFlags(1);
        vendor.reset();

        player.getWindow().open(new VendorInterface(player, vendor));
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }
}