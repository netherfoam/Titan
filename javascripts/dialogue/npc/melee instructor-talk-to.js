importClass(org.maxgamer.rs.model.skill.SkillType);
importClass(org.maxgamer.rs.model.item.ItemStack);
importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

chat(player, "Hello.");
chat(npc, "Good day.");
var opt = option(["Who are you?", "What is this place?", "What is that cape you're wearing?", "What's happening in Lumbridge?"], "What would you like to talk about?");
if (opt == 0) {
	chat(player, "Who are you?");
	chat(npc, "My name is Harlan, a master of defence! I help out at the acadamy, although I take more of background role nowadays.");
} else if (opt == 1) {
	chat(player, "What is this place?");
	chat(npc, "This is the Combat Academy. This is a safe place for intrepid adventurers - like yourself - to improve your combat skills.");
	chat(player, "Sounds interesting, What can I do here?");
	chat(npc, "I provide training dummies for practicing combat in a safe environment.");
	chat(player, "Training dummies?", SpeechDialogue.CONFUSED);
	chat(npc, "The training dummies are there for you to practice combat to your heart's content, without the usual stress of mortal peril.");
	chat(npc, "'Practice makes perfect', as they say, but training dummies are no substitute for real combat experience, I'm afraid.");
	chat(npc, "We have punchbags inside our Combat Academy. Everyone can attack these and they can take as much punishment as you care to dish out.");	
	chat(player, "Thanks for the advice.");
} else if (opt == 2) {
	chat(player, "What is that cape you're wearing?");
	chat(npc, "Ah, this a Skillcape of Defence. I have mastered the art of defence and wear it proudly to show others.");
	chat(player, "Hmm, interesting.");
	if (player.getSkills().getLevel(SkillType.DEFENCE, false) < 99) {
		chat(npc, "Come back to me when you have mastered the fine art of defence and we will talk again.");
	} else {
		chat(npc, "Ah, but I can see you are already a master in the fine art of defence. Perhaps you have come to me to purchase a Skillcape of Defence,");
		chat(npc, "and thus join the elite few who have mastered this exacting skill?");
		var opt = option(["Yes, please sell me a Skillcape of Defence.", "No, thanks."]);
		if (opt == 0) {
			chat(player, "May I buy a Skillcape of Defence, please?");
			chat(npc, "Of course. It'll cost you 99,000 coins.")
			var opt = option(["Okay.", "No, that's too much!"]);
			if (opt == 0) {
				chat(player, "Okay.");
				if (player.getInventory().contains(ItemStack.create(995, 99000))){
					if (player.getInventory().getFreeSlots() >= 2) {
						player.getInventory().remove(1, ItemStack.create(995, 99000));
						player.getInventory().add(1, ItemStack.create(9753, 1));
						player.getInventory().add(1, ItemStack.create(9755, 1));
					} else {
						player.sendMessage("Not enough space.");
					}
				} else {
					player.sendMessage("Not enough money.");
				}
			} else if (opt == 1) {
				chat(player, "No, that's too much.");
			}
		} else if (opt == 1) {
			chat(player, "No, thanks.");
		} 
	}
} else if (opt == 3) {
	chat(player, "What's happening in Lumbridge?");
	chat(npc, "Not long ago, there was a great battle near here, between Saradomin and Zamorak. It lasted for months!");
	chat(npc, "You can see the devastation around still. Even with the rebuilding effort, it will take years for Lumbridge to fully recover.", SpeechDialogue.SAD);
}
