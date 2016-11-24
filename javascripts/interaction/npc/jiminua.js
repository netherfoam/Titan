module.exports = {
    talkTo: function(player, npc){
        // TODO: Dialogues
        this.trade(player, npc);
    },

    trade: function(player, npc){
        vendor("Jiminua's Jungle Store");
    }
}