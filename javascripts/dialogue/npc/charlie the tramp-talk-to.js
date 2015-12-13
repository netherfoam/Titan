importClass(org.maxgamer.rs.model.item.ItemStack);
importClass(org.maxgamer.rs.interfaces.impl.dialogue.SpeechDialogue);

chat(player, "Howdy.");
chat(npc, "Spare some change guv?");
var opt = option(["Who are you?", "Sorry, I haven't got any.", "Go get a job!", "Ok. Here you go.", "Is there anything down this alleyway?"]);
if (opt == 0) {
	chat(player, "Who are you?");
	chat(npc, "Charles. Charles E. Trampin' at your service. Now, about that change you were going to give me...");
	var opt = option(["Sorry, I haven't got any.", "Go get a job!", "Ok. Here you go.", "Is there anything down this alleyway?"]);
	if (opt == 0) {
		chat(player, "Sorry, I haven't got any.");
		chat(npc, "Thanks anyway.", SpeechDialogue.SAD);
	} else if (opt == 1) {
		chat(player, "Go get a job!", SpeechDialogue.MEAN_FACE);
		chat(npc, "You startin? I hope your nose falls off!", SpeechDialogue.MEAN_FACE);
	} else if (opt == 2) {
		chat(player, "Ok. Here you go.");
		if (player.getInventory().contains(ItemStack.create(995, 1))){
				player.getInventory().remove(1, ItemStack.create(995, 1));
				player.sendMessage("One coin has been removed from your inventory.");
				chat(npc, "Hey, thanks a lot!");
				var opt = option(["No problem.", "Don't I get some sort of quest hint or something now?"]);
				if (opt == 0) {
					chat(player, "No problem.");
				} else if (opt == 1) {
					chat(player, "So... don't I get some sort of quest hint or something now?");
					chat(npc, "Huh? What do you mean? That wasn't why I asked you for money.", SpeechDialogue.CONFUSED);
					chat(npc, "I just need to eat...");
				}
		} else {
			player.sendMessage("Not enough money.");
		}
	} else if (opt == 3) {
		chat(player, "Is there anything down this alleyway?");
		chat(npc, "Funny you should mention that...there is actually.");
		chat(npc, "The ruthless and notorious criminal gang known as the Black Arm Gang have their headquarters down there.");
		var opt = option(["Thanks for the warning!", "Do you think they would let me join?"]);
		if (opt == 0) {
			chat(player, "Thanks for the warning!");
			chat(npc, "Don't worry about it.");
			chat(player, "Do you think they would let me join?");
			chat(npc, "No. You're a collaborator with the Phoenix Gang. There's no way they'll let you join now.");
			var opt = option(["How did you know I was in the Phoenix Gang?", "Any ideas how I could get in there then?"]);
			if (opt == 0) {
				chat(player, "How did YOU know I was in the Phoenix Gang?");
				chat(npc, "In my current profession I spend a lot of time on the street and you hear these sorta things sometimes.");
				chat(player, "Any ideas how I could get in there then?");
				chat(npc, "Hmmm. I dunno.");
				chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
				chat(npc, "the ranks of the Black Arm Gang for you.");
				chat(player, "If you find someone like that, tell 'em to come to me first.");
				var opt = option(["Ok. Good plan!", "Like who?"]);
				if (opt == 0) {
					chat(player, "Ok. Good plan!");
					chat(npc, "I'm not just a pretty face!");
				} else if (opt == 1) {
					chat(player, "Like who?");
					chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
				}
			} else if (opt == 1) {
				chat(player, "Any ideas how I could get in there then?");
				chat(npc, "Hmmm. I dunno.");
				chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
				chat(npc, "the ranks of the Black Arm Gang for you.");
				chat(player, "If you find someone like that, tell 'em to come to me first.");
				var opt = option(["Ok. Good plan!", "Like who?"]);
				if (opt == 0) {
					chat(player, "Ok. Good plan!");
					chat(npc, "I'm not just a pretty face!");
				} else if (opt == 1) {
					chat(player, "Like who?");
					chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
				}
			}
		} else if (opt == 1) {
			chat(player, "Do you think they would let me join?");
			chat(npc, "No. You're a collaborator with the Phoenix Gang. There's no way they'll let you join now.");
			var opt = option(["How did you know I was in the Phoenix Gang?", "Any ideas how I could get in there then?"]);
			if (opt == 0) {
				chat(player, "How did YOU know I was in the Phoenix Gang?");
				chat(npc, "In my current profession I spend a lot of time on the street and you hear these sorta things sometimes.");
				chat(player, "Any ideas how I could get in there then?");
				chat(npc, "Hmmm. I dunno.");
				chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
				chat(npc, "the ranks of the Black Arm Gang for you.");
				chat(player, "If you find someone like that, tell 'em to come to me first.");
				var opt = option(["Ok. Good plan!", "Like who?"]);
				if (opt == 0) {
					chat(player, "Ok. Good plan!");
					chat(npc, "I'm not just a pretty face!");
				} else if (opt == 1) {
					chat(player, "Like who?");
					chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
				}
			} else if (opt == 1) {
				chat(player, "Any ideas how I could get in there then?");
				chat(npc, "Hmmm. I dunno.");
				chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
				chat(npc, "the ranks of the Black Arm Gang for you.");
				chat(player, "If you find someone like that, tell 'em to come to me first.");
				var opt = option(["Ok. Good plan!", "Like who?"]);
				if (opt == 0) {
					chat(player, "Ok. Good plan!");
					chat(npc, "I'm not just a pretty face!");
				} else if (opt == 1) {
					chat(player, "Like who?");
					chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
				}
			}
		}
	}
} else if (opt == 1) {
	chat(player, "Sorry, I haven't got any.");
	chat(npc, "Thanks anyway.", SpeechDialogue.SAD);
} else if (opt == 2) {
	chat(player, "Go get a job!", SpeechDialogue.MEAN_FACE);
	chat(npc, "You startin? I hope your nose falls off!", SpeechDialogue.MEAN_FACE);
} else if (opt == 3) {
	chat(player, "Ok. Here you go.");
	if (player.getInventory().contains(ItemStack.create(995, 1))){
			player.getInventory().remove(1, ItemStack.create(995, 1));
			player.sendMessage("One coin has been removed from your money pouch.");
			chat(npc, "Hey, thanks a lot!");
			var opt = option(["No problem.", "Don't I get some sort of quest hint or something now?"]);
			if (opt == 0) {
				chat(player, "No problem.");
			} else if (opt == 1) {
				chat(player, "So... don't I get some sort of quest hint or something now?");
				chat(npc, "Huh? What do you mean? That wasn't why I asked you for money.", SpeechDialogue.CONFUSED);
				chat(npc, "I just need to eat...");
			}
	} else {
		player.sendMessage("Not enough money.");
	}
} else if (opt == 4) {
	chat(player, "Is there anything down this alleyway?");
	chat(npc, "Funny you should mention that...there is actually.");
	chat(npc, "The ruthless and notorious criminal gang known as the Black Arm Gang have their headquarters down there.");
	var opt = option(["Thanks for the warning!", "Do you think they would let me join?"]);
	if (opt == 0) {
		chat(player, "Thanks for the warning!");
		chat(npc, "Don't worry about it.");
		chat(player, "Do you think they would let me join?");
		chat(npc, "No. You're a collaborator with the Phoenix Gang. There's no way they'll let you join now.");
		var opt = option(["How did you know I was in the Phoenix Gang?", "Any ideas how I could get in there then?"]);
		if (opt == 0) {
			chat(player, "How did YOU know I was in the Phoenix Gang?");
			chat(npc, "In my current profession I spend a lot of time on the street and you hear these sorta things sometimes.");
			chat(player, "Any ideas how I could get in there then?");
			chat(npc, "Hmmm. I dunno.");
			chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
			chat(npc, "the ranks of the Black Arm Gang for you.");
			chat(player, "If you find someone like that, tell 'em to come to me first.");
			var opt = option(["Ok. Good plan!", "Like who?"]);
			if (opt == 0) {
				chat(player, "Ok. Good plan!");
				chat(npc, "I'm not just a pretty face!");
			} else if (opt == 1) {
				chat(player, "Like who?");
				chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
			}
		} else if (opt == 1) {
			chat(player, "Any ideas how I could get in there then?");
			chat(npc, "Hmmm. I dunno.");
			chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
			chat(npc, "the ranks of the Black Arm Gang for you.");
			chat(player, "If you find someone like that, tell 'em to come to me first.");
			var opt = option(["Ok. Good plan!", "Like who?"]);
			if (opt == 0) {
				chat(player, "Ok. Good plan!");
				chat(npc, "I'm not just a pretty face!");
			} else if (opt == 1) {
				chat(player, "Like who?");
				chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
			}
		}
	} else if (opt == 1) {
		chat(player, "Do you think they would let me join?");
		chat(npc, "No. You're a collaborator with the Phoenix Gang. There's no way they'll let you join now.");
		var opt = option(["How did you know I was in the Phoenix Gang?", "Any ideas how I could get in there then?"]);
		if (opt == 0) {
			chat(player, "How did YOU know I was in the Phoenix Gang?");
			chat(npc, "In my current profession I spend a lot of time on the street and you hear these sorta things sometimes.");
			chat(player, "Any ideas how I could get in there then?");
			chat(npc, "Hmmm. I dunno.");
			chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
			chat(npc, "the ranks of the Black Arm Gang for you.");
			chat(player, "If you find someone like that, tell 'em to come to me first.");
			var opt = option(["Ok. Good plan!", "Like who?"]);
			if (opt == 0) {
				chat(player, "Ok. Good plan!");
				chat(npc, "I'm not just a pretty face!");
			} else if (opt == 1) {
				chat(player, "Like who?");
				chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
			}
		} else if (opt == 1) {
			chat(player, "Any ideas how I could get in there then?");
			chat(npc, "Hmmm. I dunno.");
			chat(npc, "Your best bet would probably be to find someone else... Someone who ISN'T a member of the Phoenix Gang, and get them to infiltrate");
			chat(npc, "the ranks of the Black Arm Gang for you.");
			chat(player, "If you find someone like that, tell 'em to come to me first.");
			var opt = option(["Ok. Good plan!", "Like who?"]);
			if (opt == 0) {
				chat(player, "Ok. Good plan!");
				chat(npc, "I'm not just a pretty face!");
			} else if (opt == 1) {
				chat(player, "Like who?");
				chat(npc, "There's plenty of other adventurers about besides yourself. I'm sure if you asked one of them nicely they would help you.");
			}
		}
	}
}