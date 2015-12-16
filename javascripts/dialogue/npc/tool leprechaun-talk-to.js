chat(player, "Greetings, leprechaun.");
chat(npc, "Ah, 'this a foine day, to be sure! Can I help ye with tool storage, or what?");
var opt = option(["What tools can you store?", "Open your tool store, please.", "Actually, I'm fine."], "What would you like to say?");
if (opt == 0) {
	chat(player, "What tools can you store?");
	chat(npc, "We'll hold onto yer rake, seed dibber, spade, secateurs, waterin' can and throwel - but mind it's not one of them fancy throwels only archaeologists use.");
	chat(npc, "We'll take a few buckets an' scarecrows off yer hands too, and even yer compost and supercompost. Tere's room in our shed for plenty of compost, so bring it on.");
	chat(npc, "Also, if ye hands us yer Farming produce, we might be able to change it into banknotes.");
	chat(npc, "So, do ye want to be using the store?");
	var opt = option(["Yes, please.", "What do you do with the tool you're storing?", "No thanks, I'll keep hold of my stuff."], "What would you like to say?");
	if (opt == 0) {
		chat(player, "Yes, please.");
		//TODO: Open tool store.
	} else if (opt == 1) {
		chat(player, "What do you do with the tool you're storing? They can't possibly all fit in your pockets!");
		chat(npc, "We leprechauns have a shed where we keep 'em. It's a magic shed, so ye can get yer items back from any of us leprechauns whenever ye want.");
		chat(npc, "Saves ye havin' to carry loads of stuff around the country!");
		chat(npc, "So...do ye want to be using the store?");
		var opt = option(["Yes, please.", "No thanks, I'll keep hold of my stuff."], "What would you like to say?");
		if (opt == 0) {
			chat(player, "Yes, please.");
			//TODO: Open tool store.
		} else if (opt == 1) {
			chat(player, "No thanks, I'll keep hold of my stuff.");
			chat(npc, "Ye must be dafter than ye look if ye likes luggin' yer tools everywhere ye goes.");
		}	
	} else if (opt == 2) {
		chat(player, "No thanks, I'll keep hold of my stuff.");
		chat(npc, "Ye must be dafter than ye look if ye likes luggin' yer tools everywhere ye goes.");
	}
} else if (opt == 1) {
	chat(player, "Open your tool store, please.");
	//TODO: Open tool store.
} else if (opt == 2) {
	chat(player, "Actually, I'm fine.");
}