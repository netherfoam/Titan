package org.maxgamer.rs.interfaces.impl.frame;

import org.maxgamer.rs.interfaces.Pane;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class GamePane extends Pane {
	public GamePane(Player p) {
		super(p, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 548 : 746));
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		switch (buttonId) {
			case 173: //Top right 'X' for Exit
				break;
			case 172: //Top right '?' for Advisor
			case 171: //Compass for resetting camera
			case 225: //XP counter
				break;
			case 179: //World Map button
				getPlayer().getPanes().add(new WorldMapPane(player));
				break;
			case 36: //Combat Styles
			case 37: //Task System
			case 38: //Stats
			case 39: //Quest Journals
				break;
			case 40: //Inventory
				break;
			case 41: //Worn Equipment
				break;
			case 42: //Prayer List
				break;
			case 43: //Magic Spellbook
			case 45: //Friends List
			case 46: //Ignore List
			case 47: //Clan Chat
			case 48: //Options
			case 49: //Emotes
			case 50: //Music Player
				break;
			case 51: //Notes; unlock int components are sent here, because in the constructor of NotesInterface they won't pop up. TODO: fix this.
				player.getProtocol().unlockInterfaceComponent(34, 44, false);//Unlocks the actual Notes display
				player.getProtocol().unlockInterfaceComponent(34, 13, true);//Unlocks the actual Notes display
				player.getProtocol().unlockInterfaceComponent(34, 3, true);//Unlocks the add+ button.
				break;
		}
	}
}