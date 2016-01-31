function talkTo(player, npc){
	chat(player, "Hello.");
	chat(npc, "Greetings, brave warrior. What can I do for you?");
	var opt = option(["Who are you?","What do you do here?","Nothing, actually."]);
	if (opt == 0) {
		chat(player, "Who are you?");
		chat(npc, "I am Nastroth. Like my brother, Mandrith, I'm a collector of ancient artefacts. I'm just not as excited about it as he is.");
		var opt = option(["Why aren't you excited about it?","What are these ancient artefacts?", "Who is Mandrith?","Okay. goodbye, then."]);
		if (opt == 0) {
			chat(player, "Why aren't you excited about it?");
			chat(npc, "Truth be told, I'd much rather be out there with the rest of you, breaking bones and cracking skulls.");
			var opt = option(["Then why aren't you?","That's not what I do!","Oh, okay."]);
			if (opt == 0) {
				chat(player, "Then why aren't you?");
				chat(npc, "My days of battle are over. Now I spend my time here, collecting ancient artefacts.");
				var opt = option(["What are these ancient artefacts?","Have fun with that."]);
				if (opt == 0) {
					chat(player, "What are these ancient artefacts?");
					chat(npc, "As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dirt");
					chat(npc, "where they were once left forgotten. If you happen to come across any of these ancient items, bring them to");
					chat(npc, "me or my brother Mandrith in Edgeville, and we will pay you a fair price for them.");
					chat(npc, "We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
					var opt = option(["Who is Mandrith?","Why won't you buy weapons or armour?", "That sounds great. Goodbye."]);
					if (opt == 0) {
						chat(player, "Who is Mandrith?");
						chat(npc, "Mandrith is my overly excited brother and the one who made me wear this outfit. We share the same purpose.");
						chat(npc, "Collecting ancient artefacts, but he is located closer to the Wilderness, in a small village called Edgeville.");
						var opt = option(["What are these ancient artefacts?", " Oh, okay."]);
						if (opt == 0) {
							chat(player, "What are these ancient artefacts?");
							chat(npc, "As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dirt");
							chat(npc, "where they were once left forgotten. If you happen to come across any of these ancient items, bring them to");
							chat(npc, "me or my brother Mandrith in Edgeville, and we will pay you a fair price for them.");
							chat(npc, "We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
						} else if (opt == 1) {
							chat(player, "Oh, okay.");
						}
					} else if (opt == 1) {
						chat(player, "Why won't you buy weapons or armour?");
						chat(npc, "They should be used as they were meant to be used, not traded for money. Mandrith and I only collect ancient artefacts.");
					} else if (opt == 2) {
						chat(player, "That sounds great. Goodbye.");
					}
				} else if (opt == 1) {
					chat(player, "Have fun with that.");
				}
			} else if (opt == 1) {
				chat(player, "That's not what I do!");
				chat(npc, "Oh. My apologies.");
			} else if (opt == 2) {
				chat(player, "Oh, okay.");
			}
		} else if (opt == 1) {
			chat(player, "What are these ancient artefacts?");
			chat(player, "What are these ancient artefacts?");
			chat(npc, "As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dirt");
			chat(npc, "where they were once left forgotten. If you happen to come across any of these ancient items, bring them to");
			chat(npc, "me or my brother Mandrith in Edgeville, and we will pay you a fair price for them.");
			chat(npc, "We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
			var opt = option(["Who is Mandrith?","Why won't you buy weapons or armour?", "That sounds great. Goodbye."]);
			if (opt == 0) {
				chat(player, "Who is Mandrith?");
				chat(npc, "Mandrith is my overly excited brother and the one who made me wear this outfit. We share the same purpose.");
				chat(npc, "Collecting ancient artefacts, but he is located closer to the Wilderness, in a small village called Edgeville.");
				var opt = option(["What are these ancient artefacts?", " Oh, okay."]);
				if (opt == 0) {
					chat(player, "What are these ancient artefacts?");
					chat(npc, "As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dirt");
					chat(npc, "where they were once left forgotten. If you happen to come across any of these ancient items, bring them to");
					chat(npc, "me or my brother Mandrith in Edgeville, and we will pay you a fair price for them.");
					chat(npc, "We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
				} else if (opt == 1) {
					chat(player, "Oh, okay.");
				}
			} else if (opt == 1) {
				chat(player, "Why won't you buy weapons or armour?");
				chat(npc, "They should be used as they were meant to be used, not traded for money. Mandrith and I only collect ancient artefacts.");
			} else if (opt == 2) {
				chat(player, "That sounds great. Goodbye.");
			}
		} else if (opt == 2) {
			chat(player, "Who is Mandrith?");
			chat(npc, "Mandrith is my overly excited brother and the one who made me wear this outfit. We share the same purpose.");
			chat(npc, "Collecting ancient artefacts, but he is located closer to the Wilderness, in a small village called Edgeville.");
			var opt = option(["What are these ancient artefacts?", " Oh, okay."]);
			if (opt == 0) {
				chat(player, "What are these ancient artefacts?");
				chat(npc, "As the blood and sweat of warriors is spilled on the ground, relics of the God Wars are drawn out from the dirt");
				chat(npc, "where they were once left forgotten. If you happen to come across any of these ancient items, bring them to");
				chat(npc, "me or my brother Mandrith in Edgeville, and we will pay you a fair price for them.");
				chat(npc, "We don't accept them in noted form, though, remember that. Also, we don't want to buy any weapons or armour.");
			} else if (opt == 1) {
				chat(player, "Oh, okay.");
			}
		} else if (opt == 3) {
			chat(player, "Okay. Goodbye then.");
		}
		
	} else if (opt == 1) {
		chat(player, "What do you do here?");
		chat(npc, "I collect ancient artefacts acquired by warriors in return for money.");
	} else if (opt == 2) {
		chat(player, "Nothing, actually.");
	}
}