module.exports = {
    talkTo: function(player, npc){
        chat(player, "Good day to you.");
        chat(npc, "Greetings, citizen.");
        var opt = option(["How's everything going today?", "I'll let you get on with it, then."]);
        if (opt == 0) {
            chat(player, "How's everything going today?");
            chat(npc, "Fairly uneventful so far. Varrock's not the hotbed of crime that Ardougne is, after all.");
            chat(player, "I'll let you get on with it, then.");
            chat(npc, "Stay safe, citizen.");
        } else if (opt == 1) {
            chat(player, "I'll let you get on with it, then.");
            chat(npc, "Stay safe, citizen.");
        }
    }
}