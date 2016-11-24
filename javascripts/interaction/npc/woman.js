module.exports = {
    talkTo: function(player, npc){
        chat(player, "Nice to meet you.");
        chat(npc, "Sorry, I don't speak to strangers. They're weird.");
    }
}