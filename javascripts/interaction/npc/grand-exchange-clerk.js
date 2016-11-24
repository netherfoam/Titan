module.exports = {
    talkTo: function(player, npc){
        chat(player, "Hi there.");
        chat(npc, "Good day to you, sir. How can I help?");
        var opt = option(["I want to access the Grand Exchange, please.", "I want to collect my items.", "Can I see a history of my offers?", "Can you help me with item sets?", "I'm fine, actually."]);
        if (opt == 0) {
            chat(player, "I want to access the Grand Exchange, please.");
            chat(npc, "Only too happy to help you, sir.");
            //TODO: Open PIN before accessing.
            //TODO: Open G.E. Interface.
        } else if (opt == 1) {
            chat(player, "I want to collect my items.");
            chat(npc, "As you wish, sir.");
            //TODO: Open PIN before accessing.
            //TODO: Open G.E. Collection Box.
        } else if (opt == 2) {
            chat(player, "Can I see a history of my offers?");
            chat(npc, "If that is you wish.");
            //TODO: Open G.E. History.
        } else if (opt == 3) {
            chat(player, "Can you help me with item sets?");
            chat(npc, "It would be my pleasure, sir.");
            //TODO: Open G.E. Item Sets.
        } else if (opt == 4) {
            chat(player, "I'm fine, actually.");
            chat(npc, "If you say so, sir.");
        }
    }
}