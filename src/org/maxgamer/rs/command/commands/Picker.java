package org.maxgamer.rs.command.commands;

import java.io.IOException;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.interfaces.impl.chat.ItemPickerDialogue;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class Picker implements PlayerCommand {
	
	@Override
	public void execute(final Player p, String[] args) throws IOException {
		p.getWindow().open(new ItemPickerDialogue(p, 28, ItemStack.create(995), ItemStack.create(6529), ItemStack.create(7936)) {
			@Override
			public void pick(ItemStack item) {
				p.getInventory().add(item);
			}
		});
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}