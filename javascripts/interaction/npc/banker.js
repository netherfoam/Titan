module.exports = {
    talkTo: function(player, npc){
        chat(npc, "Good day. How may I help you?");
        var opt = option(["I'd like to access my bank account, please.", "I'd like to check my PIN settings.", "I'd like to see my collection box.", "What is this place?"], "What would you like to say?");
        if (opt == 0) {
            chat(player, "I'd like to access my bank account, please.");
            this.bank(player, npc);
        } else if (opt == 1) {
            chat(player, "I'd like to check my PIN settings.");
            //TODO: Open PIN Settings tab.
        } else if (opt == 2) {
            chat(player, "I'd like to see my collection box.");
            //TODO: Open pin before opening the grand exchange collection box.
        } else if (opt == 3) {
            chat(player, "What is this place?");
            chat(npc, "This is a branch of the Bank of Titan. We have branches in many towns.");
            var opt = option(["And what do you do?", "Didn't you used to be called the Bank of Varrock?"], "What would you like to say?");
            if (opt == 0) {
                chat(player, "And what do you do?");
                chat(npc, "We will look after your items and money for you. Leave your valuables with us if you want to keep them safe.");
            } else if (opt == 1) {
                chat(player, "Didn't you used to be called the Bank of Varrock?");
                chat(npc, "Yes we did, but people kept on coming into our branches outside of Varrock and telling us that our signs were wrong.");
                chat(npc, "They acted if we didn't know what town we were in or something.");
            }
        }
    },

    bank: function(player, npc){
        //TODO: Open pin before opening bank.
        var iface = new org.maxgamer.rs.model.interfaces.impl.primary.BankInterface(player);
        player.getWindow().open(iface);
    }
}