importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(player, "Hello there.");
	chat(npc, "To you too!");
	var opt = option(["What is this place?", "Who are you?", "Can I have free runes?"]);
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
		chat(npc, "I'm Mikasi, the magic instructor of the Combat Academy.");
		chat(player, "The Combat Academy?");
		chat(npc, "Well, I've traveled the world many years, training...magic. But eventually I decided to settle down and founded the training area");
		chat(npc, "with the other two instructors to help new adventurers on their quest to mastery.");
	} else if (opt == 2) {
		chat(player, "Can I have free runes?");
		chat(npc, "I'm afraid I'm out of runes. Go talk to my friend Aubury in Varrock. I'm sure he has some free samples for you.");
		chat(player, "Thanks, instructor Mikasi!");
	}
}