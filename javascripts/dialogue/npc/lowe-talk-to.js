chat(player, "Hello.");
chat(npc, "Welcome to Lowe's Archery Emporium. Do you want to see my wares?");
var opt = option(["Yes, please.", "No, I prefer to bash things close up."]);
if (opt == 0) {
	chat(player, "Yes, please.");
	//TODO: Open correct shop.
} else if (opt == 1) {
	chat(player, "No, I prefer to bash things close up.");
	chat(npc, "Humph, philistine.");
}