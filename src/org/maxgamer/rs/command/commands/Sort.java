package org.maxgamer.rs.command.commands;

import java.io.IOException;
import java.util.Comparator;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class Sort implements PlayerCommand {
	
	@Override
	public void execute(final Player p, String[] args) throws IOException {
		p.getInventory().sort(new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack o1, ItemStack o2) {
				return o2.getDefinition().getValue() - o1.getDefinition().getValue();
			}
		});
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}