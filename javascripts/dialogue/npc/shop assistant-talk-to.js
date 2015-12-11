chat(player, "Good day.");
chat(npc, "Can I help you at all?");
var opt = option(["Yes, please. What are you selling?", "How should I use your shop?", "What happened to your building?", "No, thanks."]);
if (opt == 0) {
	chat(player, "Yes, please. What are you selling?");
	//TODO: Open correct shop.
} else if (opt == 1) {
	chat(player, "How should I use your shop?");
	chat(npc, "I'm glad you ask! The shop has two sections to it: 'Main stock' and 'Free sample items'.");
	chat(npc, "From 'Main Stock' you can buy as many of the stocked items as you wish. I also offer free samples to help get you started");
	chat(npc, "and to keep you coming back. Once you take a free sample, I won't give you another for about a half an hour.");
	chat(npc, "I'm not made of money, you know!");
	chat(npc, "You can also sell most items to the shop.");
	chat(player, "Thank you.");
} else if (opt == 2) {
	chat(player, "What happened to your building?");
	chat(npc, "We sustained some damage at the start of the Battle of Lumbridge but Foreman George and his team have done a fantastic job");
	chat(npc, "and patched up the damage perfectly!");
	chat(npc, "Foreman George has some rewards to give out for those who helped our store return to its former glory.");
	think("Foreman George has a special title that can be claimed by Titan members.");
	chat(npc, "Now we're properly back in business, would you like to see our wares?");
	var opt = option(["Yes, please.", "No, thanks."]);
	if (opt == 0) {
		chat(player, "Yes, please.");
		//TODO: Open correct shop.
	} else if (opt == 1) {
		chat(player, "No, thanks.");
	}
} else if (opt == 3) {
	chat(player, "No, thanks.");
}