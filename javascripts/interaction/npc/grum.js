module.exports = {
    talkTo: function(player, npc){
        this.trade(player, npc);
    },

    trade: function(player, npc){
        vendor("Grum's Gold Exchange");
    }
}