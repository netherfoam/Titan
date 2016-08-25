package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.combat.mage.Spellbook;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class SpellbookCmd implements PlayerCommand {
	
	@Override
	public void execute(Player player, String[] args) throws Exception {
		Spellbook book;
		if (args.length < 1) {
			book = player.getSpellbook();
			if (book == Spellbook.ANCIENT) {
				book = Spellbook.LUNAR;
			}
			else if (book == Spellbook.LUNAR) {
				book = Spellbook.MODERN;
			}
			else if (book == Spellbook.MODERN) {
				book = Spellbook.ANCIENT;
			}
			else {
				book = Spellbook.MODERN;
			}
			
			player.setSpellbook(book);
			player.sendMessage("Spellbook swapped.");
			return;
		}
		
		String b = args[0].toLowerCase();
		if ("modern".startsWith(b) || b.equals("0")) {
			book = Spellbook.MODERN;
		}
		else if ("ancient".startsWith(b) || b.equals("1")) {
			book = Spellbook.ANCIENT;
		}
		else if ("lunar".startsWith(b) || b.equalsIgnoreCase("2")) {
			book = Spellbook.LUNAR;
		}
		else {
			player.sendMessage("Unrecognised spellbook " + args[0] + " valid options are modern, ancient or lunar");
			return;
		}
		
		player.setSpellbook(book);
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
}