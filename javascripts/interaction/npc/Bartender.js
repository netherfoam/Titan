importClass(org.maxgamer.rs.model.item.ItemStack);

function talkTo(player, npc){
	chat(player, "Hello.");
	chat(npc, "What can I do yer for?");
	var opt = option(["A glass of your finest ale please.", "Can you recommend where an adventurer might make his fortune?", "Do you know where I can get some good equipment?"]);
	if (opt == 0) {
		chat(player, "A glass of your finest ale please.");
		chat(npc, "No problemo. That'll be 2 coins.");
		if (player.getInventory().contains(ItemStack.create(995, 2))){
			if (player.getInventory().getFreeSlots() >= 1) {
				player.getInventory().remove(1, ItemStack.create(995, 2));
				player.getInventory().add(1, ItemStack.create(1917, 1));
				player.sendMessage("2 coin have been removed from your inventory.");
				player.sendMessage("You buy a pint of beer.");
			} else {
				player.sendMessage("Not enough space.");
			}
		} else {
			player.sendMessage("Not enough money.");
		}
	} else if (opt == 1) {
		chat(player, "Can you recommend where an adventurer might make his fortune?");
		chat(npc, "Ooh I don't know if I should be giving away information, makes the computer game too easy.");
		var opt = option(["Oh ah well...", "Computer game? What are you talking about?", "Just a small clue?"]);
		if (opt == 0) {
			chat(player, "Oh ah well...");
		} else if (opt == 1) {
			chat(player, "Computer game? What are you talking about?");
			chat(npc, "The world around us... is a computer game... called Titan.");
			chat(player, "Nope, still don't understand what you are talking about. What's a computer?");
			chat(npc, "It's a sort of magic box thing, which can do all sorts of stuff.");
			chat(player, "I give up. You're obviously completely mad!");
		} else if (opt == 2) {
			chat(player, "Just a small clue?");
			chat(npc, "Go and talk to the bartender at the Jolly Boar Inn, he doesn't seem to mind giving away clues.");
		}
	} else if (opt == 2) {
		chat(player, "Do you know where I can get some good equipment?");
		chat(npc, "Well, there's the sword shop across the road, or there's also all sorts of shops around the market.");
	}
}