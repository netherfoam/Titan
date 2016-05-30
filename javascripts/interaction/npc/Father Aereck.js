importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

function talkTo(player, npc){
	chat(npc, "Welcome back to the church of holy Saradomin. What can I do for you today?");
	var opt = option(["Who's Saradomin?", "Nice place you've got here.", "I'm looking for a quest!", "Can you tell me about the craters?", "Nothing."]);
	//TODO: GraveStone select
	if(opt == 0){
		chat(player, "Who's Saradomin?");
		chat(npc, "Surely you have heared of our god, Saradomin?", SpeechDialogue.CONFUSED);
		chat(npc, "He who created the forces of goodness and purity in this world? I cannot believe your ignorance!");
		chat(npc, "This is the god with more followers than any other...at least in this part of the world.");
		chat(npc, "He who created this world along with his brothers and Guthix and Zamorak?");
		var opt = option(["Oh, THAT Saradomin...", "Oh, sorry. I'm not from this world.", "Can you tell me about the craters?"]);
		if (opt == 0) {
			chat(player, "Oh, THAT Saradomin...");
			chat(npc, "There is only one Saradomin.", SpeechDialogue.CONFUSED)
			chat(player, "Yeah. I, uh, Thought you said something else.");
		}
		else if (opt == 1) {
			chat(player, "Oh, sorry. I'm not from this world.");
			chat(npc, "...", SpeechDialogue.CONFUSED);
			chat(npc, "That's...strange.");
			chat(npc, "I thought things not from this world were all, you know, slime and tentacles.");
			var opt = option(["Not me.", "I am! Do you like my disguise?"]);
			if (opt == 0) {
				chat(player, "Not me.");
				chat(npc, "Well, I can see that. Still, there's something special about you.", SpeechDialogue.CONFUSED);
				chat(player, "Thanks, I think.");
			}
			else if (opt == 1) {
				chat(player, "I am! Do you like my disguise?");
				chat(npc, "Argh! Avaunt, foul create from another dimension! Avaunt! Begone in the name of Saradomin!", SpeechDialogue.MEAN_FACE);
				chat(player, "Okay, okay, I was only joking!", SpeechDialogue.SCARED);
			}
		}
		else if (opt == 2){
			chat(player, "Can you tell me about the craters?");
			chat(npc, "Ah, Lumbridge Battlefield - the site of the return of Saradomin! That was a momentous day for the church, and I was there!");
			chat(npc, "In a titanic battle that lasted for months, the mighty Saradomin drove back the forces of Zamorak to the Underworld where they belong.");
			chat(npc, "And now Saradomin is here, we can be sure that we will be safe under his divine protection.");
			chat(npc, "All praise Saradomin!");
		}
	}
	else if(opt == 1){
		chat(player, "Nice place you've got here.");
		chat(npc, "It is, isn't it? It was built over two centuries ago.");
	}
	else if(opt == 2){
		chat(player, "I'm looking for a quest!");
		chat(npc, "That's lucky, I need someone to do a quest for me.");
		//TODO: Quest.
	}
	else if (opt == 3){
		chat(player, "Can you tell me about the craters?");
		chat(npc, "Ah, Lumbridge Battlefield - the site of the return of Saradomin! That was a momentous day for the church, and I was there!");
		chat(npc, "In a titanic battle that lasted for months, the mighty Saradomin drove back the forces of Zamorak to the Underworld where they belong.");
		chat(npc, "And now Saradomin is here, we can be sure that we will be safe under his divine protection.");
		chat(npc, "All praise Saradomin!");
	}
	else if (opt = 4){
		chat(player, "Nothing.");
	}
}