module.exports = {
    talkTo: function(player, npc){
        chat(player, "Can you teach me the basics of smelting please?");
        chat(npc, "You'll need to have mined some ore to smelt first. Go see the mining tutor to the south if you're not sure how to do this.");
    }
}
