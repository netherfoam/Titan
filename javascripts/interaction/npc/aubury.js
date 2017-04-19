importClass(org.maxgamer.rs.model.map.Location);

module.exports = {
    talkTo: function(player, npc){
        chat(player, "Salutations!");
        chat(npc, "Do you want to buy some runes?");
        var opt = option(["Yes, please.", "No thanks.", "Can you teleport me to the rune essence?"], "Choose an option:");
        if (opt == 0) {
            chat(player, "Yes, please.");
            this.trade(player, npc);
        } else if (opt == 1) {
            chat(player, "No thanks.");
        } else if (opt == 2) {
            chat(player,  "Can you teleport me to the rune essence?");
            this.teleport(player, npc);
        }
    },

    teleport: function(player, npc){
        npc.say("Senventior Disthine Molenko!");
        npc.face(player);
        npc.graphics(343);
        npc.animate(1818);
        wait(3);
        player.teleport(new Location(2911, 4832, 0));
    },

    trade: function(player, npc){
        vendor("Aubury's Rune Shop");
    }
}

