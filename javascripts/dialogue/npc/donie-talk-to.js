//TODO: SpeechDialogues... (Yeah, it's a lot of work :))
chat(player, "Do..Donie..");
chat(npc, "Hello there, can I help you?");
var opt = option(["Where am I?", "How are you today?","Are there any quests I can do here?", "What's happened here lately?", "Can I buy your stick?"]);
if (opt == 0) {
	chat(npc, "This is the town of Lumbridge my friend.");
	var opt = option(["How are you today?","Do you know of any quests I can do?", "Your shoe lace is untied."]);
	if (opt == 0) {
		chat(player, "How are you today?");
		chat(npc, "Aye, not too bad thank you. Lovely weather in Zanaris this fine day.");
		chat(player, "Weather?");
		chat(npc, "Yes weather, you know.");
		chat(npc, "The state or condition of the atmosphere at a time and place, with respect to variables such as temperture, moisture, wind velocity, and barometric pressure.");
		chat(player, "...");
		chat(npc, "Not just a pretty face eh? Ha ha ha.");
	} else if (opt == 1) {
		chat(player, "Do you know of any quests I can do?");
		chat(npc, "What kind of quest are you looking for?");
		var opt = option(["I fancy a bit of a fight, anything dangerous?", "Something easy please. I'm new here.", "I'm a thinker rather than fighter, anything skill oriented?",
		                  "I want to do all kinds of things, do you know of anything like that?", "Maybe another time."]);
		if (opt == 0) {
			chat(player, "I fancy a bit of a fight, anything dangerous?");
			chat(npc, "Hmm... dangerous you say? What sort of creatures are you looking to fight?");
			var opt = option(["Big scary demons!","Vampyres!","Small... something small would be good.", "Maybe another time."]);
			if (opt == 0) {
				chat(player, "Big scary demons!");
				chat(npc, "You are a brave soul indeed.");
				chat(npc, "Now that you mention it, I heard a rumour about a Saradominist in the Varrock church");
				chat(npc, "who is rambling about some kind of greater evil... sounds demon-like if you ask me.");
				chat(npc, "Perhaps you could check it out if you are as brave as you say?");
				//TODO: Finished demon slayer quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "I've already killed the demon Delrith. He was merely a stain on my sword when I was finished with him!");
				chat(npc, "Well done! However I'm sure if you search around the world you will find more challenging foes to slay.");
			} else if (opt == 1) {
				chat(player, "Vampyres!");
				chat(npc, "Ha ha. I personally don't believe in such things.");
				chat(npc, "However, there is a man in Draynor Village who has been scaring the village folk with stories of vampyres.");
				chat(npc, "He's named Morgan and can be found in one of the village houses. Perhaps you could see what the matter is?");
				//TODO: Finished vampyre slayer quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "Oh I have already killed that nasty blood-sucking vampyre. Draynor will be safe now.");
				chat(npc, "Yeah, yeah of course you did. Everyone knows vampyres are not real...");
				chat(player, "What! I did slay the beast... I really did.");
				chat(npc, "You're not fooling anyone you know.");
				chat(player, "... Huh... But... Hey! I did... believe what you like.");				
			} else if (opt == 2) {
				chat(player, "Small... something small would be good.");
				chat(npc, "Small? Small isn't really that dangerous though is it?");
				chat(player, "Yes it can be! There could be anything from an evil chicken to a poisonous spider. They attack in numbers you know!");
				chat(npc, "Yes ok, point taken. Speaking of small monsters, I hear old Wizard Mizgog in the wizard's tower has just had all his beads taken by a gang of mischievous imps.");
				chat(npc, "Sounds like it could be a quest for you?");
				//TODO: Finished imp slayer quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "Yes I know of Mizgog and have already helped him with his imp problem. It took me ages to find those beads!");
				chat(npc, "Imps will be imps!");
				chat(npc, "The Lumbridge Cook has been having problems and the Duke is confused over some strange rocks.");
				var opt = option(["Select an Option","The Lumbridge cook?","The Duke's strange stones?","Maybe another time."]);
				if (opt == 0) {
					chat(player, "The Lumbridge cook?");
					chat(npc, "It's funny really, the cook would forget his head if it wasn't screwed on.");
					chat(npc, "This time he forgot to get the ingredients for the Duke's birthday cake.");
					chat(npc, "Perhaps you could help him? You will probably find him in the Lumbridge Castle kitchen.");
					//TODO: Finished cooks assistant quest or not; if true this message if not "Thank you, I'll check it out."
					chat(player, "I have already helped the cook in Lumbridge.");
					chat(npc, "Oh yes, so you have. I am sure the Duke will be pleased.");
				} else if (opt == 1) {
					chat(player, "The Duke's strange stones?");
					chat(npc, "Well the Duke of Lumbridge has found a strange stone that no one seems to understand.");
					chat(npc, "Perhaps you could help him? You can probably find him upstairs in Lumbridge Castle.");
					//TODO: Finished rune mysteries quest or not; if true this message if not "Thank you, I'll check it out."
					chat(player, "Yes, I have already solved the Rune mysteries.");
					chat(npc, "Ah excellent. Thank you very much adventurer.");
				} else if (opt == 2) {

				}
			} else if (opt == 3) {
				chat(npc, "You may be able to help out Fred the farmer who is in need of someones crafting expertise.");
				chat(npc, "Or, there's always Doric the Dwarf who needs errand running for him?");
				var opt = option(["Tell me about... Fred the Farmer.","Tell me about... Doric the Dwarf", "Maybe another time."]);
				if (opt == 0) {
					chat(player, "Tell me about... Fred the Farmer.")
					chat(npc, "You can find Fred next to the field of sheep in Lumbridge. Perhaps you should go and speak with him.");
					//TODO: Finished farmer fred quest or not; if true this message if not "Thank you, I'll check it out."
					chat(player, "I have already helped Farmer Fred.");
					chat(npc, "I think Fred is very thankful you helped him.");
				} else if (opt == 1) {
					chat(player, "Tell me about... Doric the Dwarf");
					chat(npc, "Doric the dwarf is located north of Falador. He might be able to help you with smithing.");
					chat(npc, "You should speak to him. He may let you use his anvils.");
					//TODO: Finished dorics quest or not; if true this message if not "Thank you, I'll check it out."
					chat(player, "Yes. I've been to see Doric already. He was happy to let me use his anvils after I ran a small errand for him.");
					chat(npc, "Oh splendid. Thank you " +player.getName()+".")
				} else if (opt == 2) {
					chat(player, "Maybe another time.");
				}
			}
		} else if (opt == 1) {
			chat(npc, "I can tell you about plenty of small easy tasks.");
		} else if (opt == 2) {
			chat(npc, "Skills play a big part when you want to progress in knowledge throughout Zanaris.");
			chat(npc, "I know of a fews skill-related quests that can get you started.");
		} else if (opt == 3) {
			chat(npc, "Of course I do. Zanaris is a huge place you know, now let me think...");
			chat(npc, "Hetty the witch in Rimmington might be able to offer help in the ways of magical abilities...");
			chat(npc, "Also, pirates are currently docked in Port Sarim, where pirates are, treasure is never far away...");
			chat(npc, "Or you could go help out Ernest, who got lost in Draynor Manor, spooky place that.");
			var opt = option(["Tell me about.. Hetty the witch.", "Tell me about.. Pirate's treasure.", "Tell me about.. Ernest and Draynor Manor.","Maybe another time."]);
			if (opt == 0) {
				chat(player, "Tell me about.. Hetty the witch.");
				chat(npc, "Hetty the witch can be found in Rimmington, south of Falador. She's currently working on some new potions.");
				chat(npc, "Perhaps you could give her a hand? She might be able offer help with your magical abilities.");
				//TODO: Finished swept away quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "I've already helped Hetty the witch.");
				chat(npc, "Well done, I'm very proud of your help");
			} else if (opt == 1) {
				chat(player, "Tell me about.. Pirate's treasure.");
				chat(npc, "RedBeard Frank in Port Sarim's bar, The Rusty Anchor might be able to tell you about the rumoured treasure that is buried somewhere in Zanaris.");
				//TODO: Finished pirates treasure quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "I've already found the treasure of the pirates.");
				chat(npc, "Well done, the treasure must be worth it!");
			} else if (opt == 2) {
				chat(player, "Tell me about.. Ernest and Draynor Manor.");
				chat(npc, "The best place to start would be at the gate to Draynor Manor. The you will find Veronica who will be able to tell you more.");
				chat(npc, "I suggest you tread carefully in that place: it's haunted.");
				//TODO: Finished ernest the chicken quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "Yeah, I found Ernest already. Professor Oddstein had turned him into a chicken!");
				chat(npc, "A chicken?!");
				chat(player, "Yeah a chicken. It could have ben worse through.");
				chat(npc, "That's true, poor guy.");
			} else if (opt == 3) {
				chat(player, "Maybe another time.");
			}
		} else if (opt == 4) {
			chat(player, "Maybe another time.");
		}
	} else if (opt == 2) {
		chat(npc, "No, it's not!");
	}
} else if (opt == 1) {
	chat(player, "How are you today?");
	chat(npc, "Aye, not too bad thank you. Lovely weather in Zanaris this fine day.");
	chat(player, "Weather?");
	chat(npc, "Yes weather, you know.");
	chat(npc, "The state or condition of the atmosphere at a time and place, with respect to variables such as temperture, moisture, wind velocity, and barometric pressure.");
	chat(player, "...");
	chat(npc, "Not just a pretty face eh? Ha ha ha.");
} else if (opt == 2) {
	chat(player, "Are there any quests I can do here?");
	chat(npc, "What kind of quest are you looking for?");
	var opt = option(["I fancy a bit of a fight, anything dangerous?", "Something easy please. I'm new here.", "I'm a thinker rather than fighter, anything skill oriented?",
	                  "I want to do all kinds of things, do you know of anything like that?", "Maybe another time."]);
	if (opt == 0) {
		chat(player, "I fancy a bit of a fight, anything dangerous?");
		chat(npc, "Hmm... dangerous you say? What sort of creatures are you looking to fight?");
		var opt = option(["Big scary demons!","Vampyres!","Small... something small would be good.", "Maybe another time."]);
		if (opt == 0) {
			chat(player, "Big scary demons!");
			chat(npc, "You are a brave soul indeed.");
			chat(npc, "Now that you mention it, I heard a rumour about a Saradominist in the Varrock church");
			chat(npc, "who is rambling about some kind of greater evil... sounds demon-like if you ask me.");
			chat(npc, "Perhaps you could check it out if you are as brave as you say?");
			//TODO: Finished demon slayer quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "I've already killed the demon Delrith. He was merely a stain on my sword when I was finished with him!");
			chat(npc, "Well done! However I'm sure if you search around the world you will find more challenging foes to slay.");
		} else if (opt == 1) {
			chat(player, "Vampyres!");
			chat(npc, "Ha ha. I personally don't believe in such things.");
			chat(npc, "However, there is a man in Draynor Village who has been scaring the village folk with stories of vampyres.");
			chat(npc, "He's named Morgan and can be found in one of the village houses. Perhaps you could see what the matter is?");
			//TODO: Finished vampyre slayer quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "Oh I have already killed that nasty blood-sucking vampyre. Draynor will be safe now.");
			chat(npc, "Yeah, yeah of course you did. Everyone knows vampyres are not real...");
			chat(player, "What! I did slay the beast... I really did.");
			chat(npc, "You're not fooling anyone you know.");
			chat(player, "... Huh... But... Hey! I did... believe what you like.");				
		} else if (opt == 2) {
			chat(player, "Small... something small would be good.");
			chat(npc, "Small? Small isn't really that dangerous though is it?");
			chat(player, "Yes it can be! There could be anything from an evil chicken to a poisonous spider. They attack in numbers you know!");
			chat(npc, "Yes ok, point taken. Speaking of small monsters, I hear old Wizard Mizgog in the wizard's tower has just had all his beads taken by a gang of mischievous imps.");
			chat(npc, "Sounds like it could be a quest for you?");
			//TODO: Finished imp slayer quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "Yes I know of Mizgog and have already helped him with his imp problem. It took me ages to find those beads!");
			chat(npc, "Imps will be imps!");
			chat(npc, "The Lumbridge Cook has been having problems and the Duke is confused over some strange rocks.");
			var opt = option(["Select an Option","The Lumbridge cook?","The Duke's strange stones?","Maybe another time."]);
			if (opt == 0) {
				chat(player, "The Lumbridge cook?");
				chat(npc, "It's funny really, the cook would forget his head if it wasn't screwed on.");
				chat(npc, "This time he forgot to get the ingredients for the Duke's birthday cake.");
				chat(npc, "Perhaps you could help him? You will probably find him in the Lumbridge Castle kitchen.");
				//TODO: Finished cooks assistant quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "I have already helped the cook in Lumbridge.");
				chat(npc, "Oh yes, so you have. I am sure the Duke will be pleased.");
			} else if (opt == 1) {
				chat(player, "The Duke's strange stones?");
				chat(npc, "Well the Duke of Lumbridge has found a strange stone that no one seems to understand.");
				chat(npc, "Perhaps you could help him? You can probably find him upstairs in Lumbridge Castle.");
				//TODO: Finished rune mysteries quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "Yes, I have already solved the Rune mysteries.");
				chat(npc, "Ah excellent. Thank you very much adventurer.");
			} else if (opt == 2) {

			}
		} else if (opt == 3) {
			chat(npc, "You may be able to help out Fred the farmer who is in need of someones crafting expertise.");
			chat(npc, "Or, there's always Doric the Dwarf who needs errand running for him?");
			var opt = option(["Tell me about... Fred the Farmer.","Tell me about... Doric the Dwarf", "Maybe another time."]);
			if (opt == 0) {
				chat(player, "Tell me about... Fred the Farmer.")
				chat(npc, "You can find Fred next to the field of sheep in Lumbridge. Perhaps you should go and speak with him.");
				//TODO: Finished farmer fred quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "I have already helped Farmer Fred.");
				chat(npc, "I think Fred is very thankful you helped him.");
			} else if (opt == 1) {
				chat(player, "Tell me about... Doric the Dwarf");
				chat(npc, "Doric the dwarf is located north of Falador. He might be able to help you with smithing.");
				chat(npc, "You should speak to him. He may let you use his anvils.");
				//TODO: Finished dorics quest or not; if true this message if not "Thank you, I'll check it out."
				chat(player, "Yes. I've been to see Doric already. He was happy to let me use his anvils after I ran a small errand for him.");
				chat(npc, "Oh splendid. Thank you " +player.getName()+".")
			} else if (opt == 2) {
				chat(player, "Maybe another time.");
			}
		}
	} else if (opt == 1) {
		chat(npc, "I can tell you about plenty of small easy tasks.");
	} else if (opt == 2) {
		chat(npc, "Skills play a big part when you want to progress in knowledge throughout Zanaris.");
		chat(npc, "I know of a fews skill-related quests that can get you started.");
	} else if (opt == 3) {
		chat(npc, "Of course I do. Zanaris is a huge place you know, now let me think...");
		chat(npc, "Hetty the witch in Rimmington might be able to offer help in the ways of magical abilities...");
		chat(npc, "Also, pirates are currently docked in Port Sarim, where pirates are, treasure is never far away...");
		chat(npc, "Or you could go help out Ernest, who got lost in Draynor Manor, spooky place that.");
		var opt = option(["Tell me about.. Hetty the witch.", "Tell me about.. Pirate's treasure.", "Tell me about.. Ernest and Draynor Manor.","Maybe another time."]);
		if (opt == 0) {
			chat(player, "Tell me about.. Hetty the witch.");
			chat(npc, "Hetty the witch can be found in Rimmington, south of Falador. She's currently working on some new potions.");
			chat(npc, "Perhaps you could give her a hand? She might be able offer help with your magical abilities.");
			//TODO: Finished swept away quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "I've already helped Hetty the witch.");
			chat(npc, "Well done, I'm very proud of your help");
		} else if (opt == 1) {
			chat(player, "Tell me about.. Pirate's treasure.");
			chat(npc, "RedBeard Frank in Port Sarim's bar, The Rusty Anchor might be able to tell you about the rumoured treasure that is buried somewhere in Zanaris.");
			//TODO: Finished pirates treasure quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "I've already found the treasure of the pirates.");
			chat(npc, "Well done, the treasure must be worth it!");
		} else if (opt == 2) {
			chat(player, "Tell me about.. Ernest and Draynor Manor.");
			chat(npc, "The best place to start would be at the gate to Draynor Manor. The you will find Veronica who will be able to tell you more.");
			chat(npc, "I suggest you tread carefully in that place: it's haunted.");
			//TODO: Finished ernest the chicken quest or not; if true this message if not "Thank you, I'll check it out."
			chat(player, "Yeah, I found Ernest already. Professor Oddstein had turned him into a chicken!");
			chat(npc, "A chicken?!");
			chat(player, "Yeah a chicken. It could have ben worse through.");
			chat(npc, "That's true, poor guy.");
		} else if (opt == 3) {
			chat(player, "Maybe another time.");
		}
	} else if (opt == 4) {
		chat(player, "Maybe another time.");
	}
} else if (opt == 3) {
	chat(player, "What's happened here lately?");
	chat(npc, "Lumbridge has recently been damaged by a battle between Saradomin and Zamorak in the crater to the west of the castle.");
	chat(npc, "The battle is over now, though. There's a Saradominist zealot keeping vigil there now.");	
} else if (opt == 4) {
	chat(player, "Can I buy your stick?");
	chat(npc, "It's not a stick! I'll have you know it's a very powerful staff!");
	chat(player, "Really? Show me what it can do!");
	chat(npc, "Um... It's a bit low on power at the moment...");
	chat(player, "It's a stick isn't it?");
	chat(npc, "... Ok it's a stick... But only while I save up for a staff. Zaff in Varrock square sells them in his shop.");
	chat(player, "Well good luck with that.");
}