importClass(org.maxgamer.rs.model.map.Location);
importClass(org.maxgamer.rs.core.Core);
importClass(org.maxgamer.rs.model.action.Action);

chat(player, "Salutations!");
chat(npc, "Do you want to buy some runes?");
var opt = option(["Yes, please.", "No thanks.", "Can you teleport me to the rune essence?"], "Choose an option:");
if (opt == 0) {
	chat(player, "Yes, please.");
	//TODO: Open correct shop.
} else if (opt == 1) {
	chat(player, "No thanks.");
} else if (opt == 2) {
	chat(player,  "Can you teleport me to the rune essence?");
	//TODO: NPC force chat: "Senventior Disthine Molenko!".
	npc.face(player);
	npc.graphics(343);
	npc.animate(1818);
	wait(3);
	player.teleport(new Location(Core.getServer().getMap(), 2911, 4832, 0));
}