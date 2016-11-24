importClass(org.maxgamer.rs.model.interfaces.impl.dialogue.SpeechDialogue);

module.exports = {
    talkTo: function(player, npc){
        chat(player, "Hello there.");
        chat(npc, "Hello, I suppose. I'm Lachtopher. Could you lend me some money?");
        chat(player, "Lend you money? I really don't think so. Don't you have any of your own?", SpeechDialogue.CONFUSED);
        chat(npc, "I spent it all and I can't be bothered to earn any more.");
        chat(player, "Right and you want my hard-earned money instead? No chance!", SpeechDialogue.CONFUSED);
        chat(npc, "You're just like my sister, Victoria. She won't give me any money.");
        chat(player, "Your sister sounds like she has the right idea.");
        chat(npc, "Yeah, I've heared it all before. 'Oh,' she says, 'It's easy to make money: just complete Tasks for cash.'");
        chat(player, "Well, if you want to make money...");
        chat(npc, "That's just it. I don't want to make money. I just want to have money.");
        chat(player, "I've had it with you! I don't think I've come across a less worth while person.", SpeechDialogue.MEAN_FACE);
        chat(player, "I think I'll call you Lazy Lactopher, from now on.", SpeechDialogue.MEAN_FACE);
    }
}