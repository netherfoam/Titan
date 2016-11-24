module.exports = {
    tips: ["Did you know? You burn food less often on a range than on a fire!",
                "Did you know most skills have right click 'Make-X' options to help train you faster?",
                "If a player isn't sure of the rules, send them to me! I'll be happy to remind them!",
                "Players can not trim armour. Don't fall for this popular scam!",
                "Melee armour actually gives you disadvantages when using magic attacks. It may be better to take off your armour entirely!",
                "Did you know you can wear a shield with a crossbow?",
                "Never tell your password to anyone, not even your best friend!",
                "Never let anyone else use your account.",
                "Titan will never email you asking for your log-in details.",
                "Did you know having a bank pin can help secure your valuable items?",
                "Never question a penguin.",
                "Be careful when fighting wizards! Wearing heavy armour can lower your Magic resistance!",
                "If you think someone knows your password - change it!",
                "Take time to check the second trade window carefully. Don't be scammed!"],

    talkTo: function(player, npc){
        var random = this.tips[Math.floor(Math.random() * tips.length)];
        chat(npc, "Hear ye! Hear ye! Player Moderators massive help to Tita-");
        chat(npc, "Oh, hello citizen. Are you here to find out about Player Moderators? Or perhaps would you like to know about the laws of the land?");
        var opt = option(["Tell me about Player Moderators.", "Tell me about the Rules of Titan.", "Can you give me a handy tip please?"]);
        if (opt == 0) {
            chat(player, "Tell me about Player Moderators.");
            chat(npc, "Of course. What would you like to know?");
            var opt = option(["What is a Player Moderator?", "What can Player Moderators do?", "How do I become a Player Moderator?", "What can Player Moderators not do?", "No thanks, I'm fine."]);
            if (opt == 0) {
                chat(player, "What is a Player Moderator?");
                chat(npc, "Player Moderators are normal players of the game, just like you. However, since they have shown themselves to be trustworthy and active reporters,");
                chat(npc, "they have been invited by Titan's Administrators to monitor the game and take appropriate action when they see rule breaking. You can spot a Player");
                chat(npc, "Moderator in game by looking at the chat screen - when a Player Moderator speaks, a silver crown appears to the left of their name. Remember, if there's");
                chat(npc, "no silver crown there, they are not a Player Moderator! You can check out the website if you'd like more information.");
                chat(player, "Thanks!");
            } else if (opt == 1) {
                chat(player, "What can Player Moderators do?");
                chat(npc, "Player Moderators, or 'P-Mods', have the ability to mute rule breakers and Administrators view their reports as a priority so that action is taken");
                chat(npc, "as quickly as possible. P-Mods also have access to the Player Moderation Centre. Within the Centre are tools to help them Moderate Titan.")
                chat(npc, "These tools include dedicated forums, the Player Moderator Guidelines and the Player Moderator Code of Conduct.");
                chat(player, "Thanks!");
            } else if (opt == 2) {
                chat(player, "How do I become a Player Moderator?");
                chat(npc, "Administrators of Titan pick players who spend their time and effort to help better the Titan community. To increase your chances of becoming a");
                chat(npc, "Player Moderator: Keep your account secure! This is very important, as a player with poor security will never be a P-Mod. Read our Security Tips for more information.");
                chat(npc, "Play by the rules! The rules of Titan are enforced for a reason, to make the game a fair and enjoyable environment for all.");
                chat(npc, "Report accurately! When Administrators consider an account for review they look for quality, not quantity. Ensure your reports are of a high");
                chat(npc, "quality by following the report guidelines.");
                chat(npc, "Be excellent to each other! Treat others as you would want to be treated yourself. Respect your fellow player. More information can be found on the website.");
                chat(player, "Thanks!");
            } else if (opt == 3) {
                chat(player, "What can Player Moderators not do?");
                chat(npc, "P-Mods cannot ban your account - they can only report offences. Administrators then take action based on the evidence received. If you lose your password or get scammed");
                chat(npc, "by another player, P-Mods cannot help you to get your account back. All they can do is recommend you to go to Player Support. They cannnot retrieve any items you may have");
                chat(npc, "lost and they certainly do not receive any free items from Administrators for moderating the game. They are players who give their all to help the community,");
                chat(npc, "out of the goodness of their hearts! P-Mods do not work for Administrators and so cannot make you a Moderator, or recommend other account to become Moderators.");
                chat(npc, "If you wish to become a Moderator, feel free to ask me.");
                chat(player, "Thanks!");
            } else if (opt == 4) {
                chat(player, "No thanks, I'm fine.");
            }
        } else if (opt == 1) {
            chat(player, "Tell me about the Rules of Titan.");
            chat(npc, "At once. Take a look at my book here.");
            //TODO: Hand a rules book to the player / Or show book interface.
        } else if (opt == 2) {
            chat(player, "Can you give me a handy tip please?");
            chat(npc, random);
        }
    }
}