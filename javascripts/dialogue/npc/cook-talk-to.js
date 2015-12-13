importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);
importClass(org.maxgamer.rs.model.item.ItemStack);

chat(player, "Greetings.");
chat(npc, "What do you want? I'm busy!", SpeechDialogue.MEAN_FACE);
var opt = option(["Can you sell me any food?", "Can you give me any free food?", "I don't want anything from this horrible kitchen."], "What would you like to say?");
if (opt == 0) {
	chat(player, "Can you sell me any food?");
	chat(npc, "I suppose I could sell you some cabbage, if you're willing to pay for it. Cabbage is good for you.");
	if (player.getInventory().isFull() == true) {
		chat(player, "Oh, I haven't got enough space to carry it.");
		chat(npc, "Why are you asking me to sell you food if you can't carry it? Go away!", SpeechDialogue.MEAN_FACE);
	} else {
		var opt= option(["Alright, I'll buy a cabbage.", "No thanks, I don't like cabbage."], "What would you like to say?");
		if (opt == 0) {
			chat(player, "Alright, I'll buy a cabbage.");
			if (player.getInventory().contains(ItemStack.create(995, 1))){
				if (player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().remove(1, ItemStack.create(995, 1));
					player.getInventory().add(1, ItemStack.create(1965, 1));
					player.sendMessage("One coin has been removed from your inventory.");
					chat(npc, "It's a deal. Now, make sure you eat it all up. Cabbage is good for you.");
				} else {
					player.sendMessage("Not enough space.");
				}
			} else {
				player.sendMessage("Not enough money.");
			}
		} else if (opt == 1) {
			chat(player, "No thanks, I don't like cabbage.");
			chat(npc, "Bah! People these days only appreciate junk food.", SpeechDialogue.MEAN_FACE);
		}
	}
} else if (opt == 1) {
	chat(player, "Can you give me any free food?");
	chat(npc, "Can you give me any free money?", SpeechDialogue.MEAN_FACE);
	chat(player, "Why should I give you free money?");
	chat(npc, "Why should I give you free food?");
	chat(player, "Oh, forget it.");
} else if (opt == 2) {
	chat(player, "I don't want anything from this horrible kitchen.");
	chat(npc, "How dare you? I put a lot of effort into cleaning this kitchen. My daily sweat and elbow-grease keep this kitchen clean!", SpeechDialogue.MEAN_FACE);
	chat(player, "Ewww!", SpeechDialogue.WHAT_THE_CRAP);
	chat(npc, "Oh, just leave me alone.", SpeechDialogue.MEAN_FACE);
}