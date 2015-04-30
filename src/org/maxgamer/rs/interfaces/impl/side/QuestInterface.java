package org.maxgamer.rs.interfaces.impl.side;

import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public class QuestInterface extends Interface {
	
	public QuestInterface(Player p) {
		//190 for quests
		
		super(p, p.getWindow(), (short) 190, (short) (p.getSession().getScreenSettings().getDisplayMode() < 2 ? 205 : 90), true);
	}
	
	@Override
	public boolean isServerSidedClose() {
		return true;
	}
	
	@Override
	public void onOpen() {
		super.onOpen();
		
		//sendConfig(player, 101, 100);// Number of completed Quests
		//sendConfig(player, 904, 100);// Total number of quests available.
		//sendConfig(player, 281, 1000);// Unlock questlist and chat sort tabs
		//sendConfig(player, 1384, 512);// Clear quest sort options
		
		/*
		 * sendConfig(player, 130, 4);// Black Knight's Fortress
		 * sendConfig(player, 29, 2);// Cook's Assistant sendConfig(player, 222,
		 * 3);// Demon Slayer sendConfig(player, 31, 100);// Doric's Quest
		 * sendConfig(player, 176, 10);// Dragon Slayer sendConfig(player, 32,
		 * 3);// Ernest The Chicken sendConfig(player, 62, 6);// Goblin
		 * Diplomacy sendConfig(player, 160, 2);// Imp Catcher
		 * sendConfig(player, 122, 7);// The Knight's Sword sendConfig(player,
		 * 71, 4);// Pirate's Treasure sendConfig(player, 273, 110);// Prince
		 * Ali Rescue sendConfig(player, 107, 5);// The Restless Ghost
		 * sendConfig(player, 144, 100);// Romeo & Juliet sendConfig(player, 63,
		 * 6);// Rune Mysteries sendConfig(player, 179, 21);// Sheep Shearer
		 * sendConfig(player, 145, 7);// Shield of Arrav sendConfig(player, 178,
		 * 3);// Vampire Slayer sendConfig(player, 67, 3);// Witch's Potion
		 * sendConfig(player, 293, 100);// Big Chompy Bird Hunting
		 * sendConfig(player, 68, 100);// Biohazard sendConfig(player, 655,
		 * 200);// Cabin Fever sendConfig(player, 10, 100);// Clocktower
		 * sendConfig(player, 399, 100);// Creature of Fenkenstrain
		 * sendConfig(player, 314, 100);// Death Plateau sendConfig(player, 131,
		 * 100);// The Dig Site sendConfig(player, 80, 100);// Drudric Ritual
		 * sendConfig(player, 0, 100);// Dwarf Cannon sendConfig(player, 355,
		 * 200);// Eadgars Ruse sendConfig(player, 299, -1);// Elemental
		 * Workshop 1 sendConfig(player, 148, 100);// Family Crest
		 * sendConfig(player, 17, 100);// Fight Arena sendConfig(player, 11,
		 * 100);// Fishing Contest sendConfig(player, 347, 100);// The Fremennik
		 * Trails sendConfig(player, 65, 100);// The waterfall quest
		 * sendConfig(player, 180, 100);// Gertrude's Cat sendConfig(player,
		 * 150, 200);// The Grand Tree sendConfig(player, 382, 100);// Haunted
		 * Mine sendConfig(player, 223, 100);// Hazeel Cult sendConfig(player,
		 * 188, 100);// Heros Quest sendConfig(player, 5, 100);// The Holy Grail
		 * sendConfig(player, 287, 200);// In Search of the Myreque
		 * sendConfig(player, 175, 100);// Jungle Potion sendConfig(player, 139,
		 * 100);// Legands Quest sendConfig(player, 147, 100);// Lost City
		 * sendConfig(player, 14, 100);// Merlin's Crystal sendConfig(player,
		 * 365, 100);// Monkey Madness sendConfig(player, 30, 100);// Monks
		 * friend sendConfig(player, 517, 100);// Mourning's Ends Part 1
		 * sendConfig(player, 192, 100);// Murder mystery sendConfig(player,
		 * 307, 200);// Nature Spirit sendConfig(player, 112, 100);//
		 * Observatory Quest sendConfig(player, 416, 300);// One Small Favour
		 * sendConfig(player, 165, 100);// Plague City sendConfig(player, 302,
		 * 100);// Priest in Peril sendConfig(player, 328, 100);// Regicide
		 * sendConfig(player, 402, 100);// Roving Elves sendConfig(player, 600,
		 * 100);// Rum deal sendConfig(player, 76, 100);// Scorpian Catcher
		 * sendConfig(player, 159, 100);// Sea Slug sendConfig(player, 339,
		 * 100);// Shades of Mort'ton sendConfig(player, 60, 100);// Sheep
		 * Herder sendConfig(player, 116, 100);// Shilo Village
		 * sendConfig(player, 320, 100);// Tai Bwo Wannai Trio
		 * sendConfig(player, 26, 100);// Temple of Ikov sendConfig(player, 359,
		 * 100);// Throne of Miscellania sendConfig(player, 197, 100);// The
		 * Tourist Trap sendConfig(player, 226, 100);// Witch's House
		 * sendConfig(player, 111, 100);// Tree Gnome Village sendConfig(player,
		 * 200, 100);// Tribal Totem sendConfig(player, 385, 100);// Troll
		 * Romance sendConfig(player, 317, 100);// Troll Stronghold
		 * sendConfig(player, 212, 100);// Watchtower sendConfig(player, 980,
		 * 200);// The Great Brain Robbery sendConfig(player, 939, -1);// Animal
		 * Magnetism sendConfig(player, 433, 200);// Between a Rock...
		 * sendConfig(player, 964, 100);// Contact! sendConfig(player, 455,
		 * 400);// Zogre Flesh Eaters sendConfig(player, 869, 400);// Darkness
		 * of Hallowvale sendConfig(player, 794, 100);// Death to the Dorgeshuun
		 */
	}
	
	@Override
	public boolean isMobile() {
		return true;
	}
	
	@Override
	public void onClick(int option, int buttonId, int slotId, int itemId) {
		// TODO Auto-generated method stub
		
	}
}