module.exports = {
    talkTo: function(player, npc) {
        chat(npc, "How can I help you?");
        var opt = option(["Who are you?", "Oh sorry, I thought you were someone else"]);
        if(opt == 0){
            chat(player, "Who are you?");
            chat(npc, "Why, I'm Mandrith! Inspiration to combatants both mighty and puny!");
            chat(player, "Okay... Fair enough");
            opt = option(["What do you do here?", "Erm, what's with the outfit?", "I have to go now"]);

            if(opt == 0) {
                chat(player, "What do you do here?");
                chat(npc, "I am here to collect ancient artefacts acquired by adventurers in return for some well-deserved money.");
                opt = option(["What ancient artefacts?", "That sounds great, goodbye."]);
                if(opt == 0){
                    chat(player, "What ancient artefacts?");
                    chat(npc, "Haha! I can tell you are new to these parts.");
                    chat(npc, "As the blood of warriors is spilled on the ground, as it once was during the God Wars, " +
                        "relics of that age feel the call of battle and are drawn into the rays of the sun once more. " +
                        "If you happen to come across these ancient items, " +
                        "bring them to me or my brother Nastroth in Lumbridge, and we will pay you a fair price " +
                        "for them. We don't accept them noted, though, so remember that. Also we don't want to buy any " +
                        "weapons or armour.");
                    opt = option(["You have a brother?", "Why won't you buy weapons or armour?", "That sounds great. Goodbye."]);
                    if(opt == 0){
                        chat(player, "You have a brother?");
                        chat(npc, "Yes, why else would I have referred to him as such?");
                        chat(player, "You make a good point");
                    }
                    else if(opt == 1){
                        chat(player, "Why won't you buy weapons or armour?");
                        chat(npc, "They should be used as they were meant to be used, and not traded in for money.");
                    }
                    else{
                        chat(player, "That sounds great, goodbye.");
                    }
                }
                else{
                    chat(player, "That sounds great, goodbye.");
                    return;
                }
            }
            else if(opt == 1){
                chat(player, "Erm, what's with the outfit?");
                chat(npc, "You like not my kingly robes? They were my father's, and his father's before him, and his " +
                    "father's before him, and his father's before him and -");
                chat(player, "Okay! Okay! I get the picture.");
            }
            else{
                chat(player, "I have to go now.");
                return;
            }
        }
        else {
            chat(player, "Oh sorry, I thought you were someone else");
            chat(npc, "I'm not sure how you could confuse ME with anyone!");
        }
    }
}