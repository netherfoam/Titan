package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.PersonaOption;
import org.maxgamer.rs.model.entity.mob.persona.PersonaOptions;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;

/**
 * @author netherfoam
 */
public class PlayerOptions extends PersonaOptions {
	/**
	 * Constructs a new PlayerOptions for the given player. This does not modify
	 * the player.
	 * @param player the player to construct the options for.
	 * @throws NullPointerException if the player is null
	 */
	public PlayerOptions(Player player) {
		super(player);
	}
	
	/**
	 * Sets a menu option for when a player right clicks on another player.
	 * @param position The position in the menu. Items are sorted by this
	 *        number, lowest at the top.
	 * @param option Contains the display text of this option, and the method to
	 *        run if it is clicked.
	 * @param aboveWalk If you would like the option to be above the walk
	 *        option, or below it.
	 */
	public void set(int position, PersonaOption option, boolean aboveWalk) {
		super.set(position, option, aboveWalk);
		PlayerOptions.sendOption((Player) player, position, option, aboveWalk);
	}
	
	private static void sendOption(Player p, int slot, PersonaOption option, boolean top) {
		RSOutgoingPacket out = new RSOutgoingPacket(120);
		out.writeShortA(top ? 42 : 65535); //Sprite ID for the option.. What?
		out.writeByteS(top ? 1 : 0); //Boolean, if true it will appear above Walk Here.
		out.writePJStr1(option.getText()); //Option text
		out.writeByteS(slot); //The slot for the option.
		p.write(out);
	}
	
	/**
	 * Resends all options to this player. You shouldn't really need this, ever.
	 */
	public void refreshOptions() {
		for (int i = 0; i < MAX_OPTIONS; i++) {
			sendOption((Player) player, i, options[i], tops[i]);
		}
	}
}