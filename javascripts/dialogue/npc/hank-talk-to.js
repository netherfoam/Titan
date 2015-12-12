chat(player, "Good day.");
chat(npc, "Good day to you!");
var opt = option(["What happened to your shop?", "By Saradomin?", "Have you got any supplies left?"]);
if (opt == 0) {
	chat(player, "What happened to your shop?");
	chat(npc, "It got turfed out by Saradomin of all the things, but looking at it now, you wouldn't be able to tell!");
	chat(npc, "Foreman George and his men have done a sterling job and rebuilt it from scratch. (Just like Titan!)");
	chat(npc, "Anyow, back to business! I have to restock and fill those shelves, but in the meantime I still have fishing supplies, if you're interested.");
	var opt = option(["Yes, please!", "No, thanks!"]);
	if (opt == 0) {
		chat(player, "Yes, please!");
		//TODO: Open correct shop.
	} else if (opt == 1) {
		chat(player, "No, thanks!");
	}
		
} else if (opt == 1) {
	chat(player, "By Saradoming?");
	chat(npc, "Yes, it was during the battle of Lumbridge. Saradomin decided that my shop was here in the street, trying to peddle what's left of my wares.");
	chat(npc, "I should probably not speak ill of Saradomin now, after he crushed Zamorak like that.");
	var opt = option(["What exactly happened to your shop?", "Can I see what you have?"]);
	if (opt == 0) {
		chat(player, "What exactly happened to your shop?");
		chat(npc, "It got turfed out by Saradomin of all the things, but looking at it now, you wouldn't be able to tell!");
		chat(npc, "Foreman George and his men have done a sterling job and rebuilt it from scratch. (Just like Titan!)");
		chat(npc, "Anyow, back to business! I have to restock and fill those shelves, but in the meantime I still have fishing supplies, if you're interested.");
		var opt = option(["Yes, please!", "No, thanks!"]);
		if (opt == 0) {
			chat(player, "Yes, please!");
			//TODO: Open correct shop.
		} else if (opt == 1) {
			chat(player, "No, thanks!");
		}
	} else if (opt == 1) {
		chat(player, "Can I see what you have?");
		//TODO: Open correct shop.
	}
} else if (opt == 2) {
	chat(player, "Have you got any supplies left?");
	chat(npc, "Yes, I salvaged some supplies from the wreckage, so I can still buy and sell fishing supplies.");
	//TODO: Open correct shop.
}