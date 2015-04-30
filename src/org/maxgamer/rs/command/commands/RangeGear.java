package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerException;

/**
 * @author alva
 */
public class RangeGear implements PlayerCommand {
	@Override
	public void execute(Player sender, String[] args) {
		try {
			sender.getInventory().add(ItemStack.create(9185, 1)); //Rune cbow
			sender.getInventory().add(ItemStack.create(11718, 1)); //Arma helmet
			sender.getInventory().add(ItemStack.create(11720, 1)); //arma chest
			sender.getInventory().add(ItemStack.create(11722, 1)); //arma skirt
			sender.getInventory().add(ItemStack.create(9245, 10000)); //Onyx bolts (e)
			sender.getInventory().add(ItemStack.create(15019, 1)); //archers ring
			sender.getInventory().add(ItemStack.create(6524, 1)); //Obby Shield
			sender.getInventory().add(ItemStack.create(10499, 1)); //ava's accu
			sender.getInventory().add(ItemStack.create(2577, 1)); //ranger boots
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