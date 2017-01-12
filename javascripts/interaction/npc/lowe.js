module.exports = {
    talkTo: function(player, npc){
        chat(player, "Hello.");
        chat(npc, "Welcome to Lowe's Archery Emporium. Do you want to see my wares?");
        var opt = option(["Yes, please.", "No, I prefer to bash things close up."]);
        if (opt == 0) {
            chat(player, "Yes, please.");
            trade(player, npc);
        } else if (opt == 1) {
            chat(player, "No, I prefer to bash things close up.");
            chat(npc, "Humph, philistine.");
        }
    },

    trade: function(player, npc){
        vendor("Lowe's Archery Emporium");
    }
}