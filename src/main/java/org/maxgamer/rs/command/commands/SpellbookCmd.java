package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.model.entity.mob.combat.mage.AncientBook;
import org.maxgamer.rs.model.entity.mob.combat.mage.LunarBook;
import org.maxgamer.rs.model.entity.mob.combat.mage.ModernBook;
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
            if (book == AncientBook.ANCIENT) {
                book = LunarBook.LUNAR;
            } else if (book == LunarBook.LUNAR) {
                book = ModernBook.MODERN;
            } else if (book == ModernBook.MODERN) {
                book = AncientBook.ANCIENT;
            } else {
                book = ModernBook.MODERN;
            }

            player.setSpellbook(book);
            player.sendMessage("Spellbook swapped.");
            return;
        }

        String b = args[0].toLowerCase();
        if ("modern".startsWith(b) || b.equals("0")) {
            book = ModernBook.MODERN;
        } else if ("ancient".startsWith(b) || b.equals("1")) {
            book = AncientBook.ANCIENT;
        } else if ("lunar".startsWith(b) || b.equalsIgnoreCase("2")) {
            book = LunarBook.LUNAR;
        } else {
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