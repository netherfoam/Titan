package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;

/**
 * @author netherfoam
 */
public class Gear implements PlayerCommand {
	@Override
	public void execute(Player sender, String[] args) {
		try {
			sender.getInventory().add(ItemStack.create(4716, 1)); //Dharok helm
			sender.getInventory().add(ItemStack.create(4718, 1)); //Dharok axe
			sender.getInventory().add(ItemStack.create(4720, 1)); //Dharok platebody
			sender.getInventory().add(ItemStack.create(4722, 1)); //Dharok legs
			sender.getInventory().add(ItemStack.create(4151, 1)); //Whip
			sender.getInventory().add(ItemStack.create(5698, 1)); //ddp++
			sender.getInventory().add(ItemStack.create(6524, 1)); //Obby Shield
			sender.getInventory().add(ItemStack.create(11732, 1)); //Dragon boots
			sender.getInventory().add(ItemStack.create(13003, 1)); //Rune gauntlets
			sender.getInventory().add(ItemStack.create(6568, 1)); //Obby cape
			sender.getInventory().add(ItemStack.create(2572, 1)); //Ring of wealth
			sender.getInventory().add(ItemStack.create(6585, 1)); //Amulet of fury
			
			sender.getInventory().add(ItemStack.create(995, 1000000));
			sender.sendMessage("Geared up and ready to go!");
		}
		catch (ContainerException e) {
			sender.sendMessage("Not enough room.");
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
}