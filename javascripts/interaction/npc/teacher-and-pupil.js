importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

module.exports = {
    talkTo: function(player, npc){
        chat(npc, "Stop pulling, we've plenty of time to see everything.", SpeechDialogue.MEAN_FACE);
    }
}