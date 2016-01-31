function talkTo(player, npc){
	chat(player, "Howdy.");
	chat(npc, "Hello, do you need any help?");
	var opt = option(["Do you want to trade?", "No, thanks. I'm just looking around."], "Choose an option:");
	if (opt == 0) {
		chat(player, "Do you want to trade?");
		//TODO: Open correct shop.
	} else if (opt == 1) {
		chat(player, "No, thanks. I'm just looking around.");
		chat(npc, "Well, come and see me if you're ever in need of armour!");
	}
}