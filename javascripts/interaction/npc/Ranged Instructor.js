importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(player, "Salutations!");
	chat(npc, "Greetings, adventurer.");
	chat(npc, "How can I help you?");
	var opt = option(["What is this place?", "Who are you?", "Can I have a free bow and arrows?"]);
	if (opt == 0) {
		chat(player, "What is this place?");
		chat(npc, "This is the Combat Academy. This is a safe place for intrepid adventurers - like yourself - to improve your combat skills.");
		chat(player, "Sounds interesting, What can I do here?");
		chat(npc, "I provide training dummies for practicing combat in a safe environment.");
		chat(player, "Training dummies?", SpeechDialogue.CONFUSED);
		chat(npc, "The training dummies are there for you to practice combat to your heart's content, without the usual stress of mortal peril.");
		chat(npc, "'Practice makes perfect', as they say, but training dummies are no substitute for real combat experience, I'm afraid.");
		chat(npc, "We have punchbags inside our Combat Academy. Everyone can attack these and they can take as much punishment as you care to dish out.");	
		chat(player, "Thanks for the advice.");
	} else if (opt == 1) {
		chat(player, "Who are you?");
		chat(npc, "My name is Nemarti, I'm the ranged instructor at the Combat Academy.");
	} else if (opt == 2) {
		chat(player, "Can I have a free bow and arrows?");
		chat(npc, "Well we used to give free samples like: runes, arrows, a bow, a sword and a shield. But due to cheaters and thieves we're out of stock.");
		chat(npc, "If you desperately need a bow and some arrows, talk to Lowie in Varrock. He offers excellent stuff for a fair price.");
	}
}