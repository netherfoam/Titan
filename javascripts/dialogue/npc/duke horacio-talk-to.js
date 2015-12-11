importClass(org.maxgamer.rs.model.item.ItemStack);
importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

var receivedShieldThisSession = false;

chat(player, "Hello, Duke Horacio.");
chat(npc, "Greetings. Welcome to my castle.");
var opt = option(["I seek a shield that will protect me from dragonbreath.", "Have you any quests for me?", "Where can I find money?", "What happened to the castle?"]);
if (opt == 0) {
	chat(player, "I seek a shield that will protect me from dragonbreath.");
	if (receivedShieldThisSession == false) {
		chat(npc, "A knight going on a dragon quest, hmm? What dragon do you intend to slay?", SpeechDialogue.CONFUSED);
		//TODO: Check if finished dragon slayer quest or not; if so: "Of course. ow you've slain Elvarg, you've earned right to it!";
		chat(npc, "You haven't slain Elvarg yet, take this shield.");
		//TODO: itemdialogue.
		if (player.getInventory().isFull() == true) {
			player.sendMessage("Not enough space!");
		} else {
			player.getInventory().add(ItemStack.create(1540, 1));//Error:  Cannot convert Anti-dragon shield(1540) x1 to java.lang.Integer (javascripts\dialogue\npc\duke horacio-talk-to.js#19)
			receivedShieldThisSession = true;
		}
	} else {
		chat(npc, "What, another one? You adventurers do like to stockpile, don't you... any particular dragon in mind?", SpeechDialogue.CONFUSED);
		//TODO: Check if finished dragon slayer quest or not; if so: "Of course. ow you've slain Elvarg, you've earned right to it!";
		chat(npc, "You haven't slain Elvarg yet, take this shield.");
		//TODO: itemdialogue.
		if (player.getInventory().isFull() == true) {
			player.sendMessage("Not enough space!");
		} else {
			player.getInventory().add(ItemStack.create(1540, 1));
		}
	}
} else if (opt == 1) {
	chat(player, "Have you any quests for me?");
	//TODO: Check if finished rune mysteries quest or not; if so: "No, all is well.";
	chat(npc, "Well, it's not really a quest but I recently discovered this strange talisman.");
	chat(npc, "It seems to be a mystical and I have never seen anything like it before. Would you take it to the head wizard at ");
	chat(npc, "the Wizards' Tower for me? It's just south-west of here and should not take you very long at all. I would be awfully grateful.");
	var opt = option(["Sure, no problem.", "Not right now."]);
	if (opt == 0) {
		chat(player, "Sure, no problem.");
		chat(npc, "Thank you very much, stranger. I am sure the head wizard will reward you for such an interesting find.");
		player.sendMessage("The Duke hands you an <col=08088A>air talisman.</col>");
		//TODO: itemdialogue.
		if (player.getInventory().isFull() == true) {
			player.sendMessage("Not enough space!");
		} else {
			player.getInventory().add(ItemStack.create(1438, 1));
		}
	} else if (opt == 1) {
		chat(player, "Not right now.");
	}
} else if (opt == 2) {
	chat(player, "Where can I find money?");
	chat(npc, "I hear many of the local people earn money by learning a skill. Many people get by in life by becoming ");
	chat(npc, "accomplished smiths, cooks, miners and woodcutters.");
} else if (opt == 3) {
	chat(player, "What happened to the castle?");
	chat(player, "Can you give me a quick summary of what happened?");
	chat(npc, "Yes, the castle suffered badly during the Battle of Lumbridge - Saradomin and Zamorak were fighting in the crater", SpeechDialogue.SAD);
	chat(npc, "you can see to the west. Right after they arrived in the world again they started fighting, ");
	chat(npc, "and the forces unleashed were too much for the walls. Whole sections collapsed completely.");
	chat(npc, "Fortunately my guards were ready and able, and immediately set to work helping people of Lumbridge to stay");
	chat(npc, "clear of the battlefield. They did a fine job of protecting us and have been exemplary in their conduct. I am proud of them.");
	chat(npc, "When the battle was over, people came from all over the world to rebuild Lumbridge. I was overwhelmed by their generosity.");
	chat(npc, "When the gods started fighting I was worried it would mean the end of Lumbridge, but now the town is back on its feet.");
}