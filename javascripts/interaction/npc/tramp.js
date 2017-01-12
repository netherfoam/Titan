
module.exports = {
    talkTo: function(player, npc){
        chat(npc, "Got any spare change, mate?");
        var opt = option(["Yes, I can spare a little money.", "Sorry, you'll have to earn it yourself."], "What would you like to say?");
        if (opt == 0) {
            chat(player, "Yes, I can spare a little money.");
            if (player.getInventory().contains(ItemStack.create(995, 1))){
                player.getInventory().remove(1, ItemStack.create(995, 1));
                player.sendMessage("One coin has been removed from your inventory.");
                chat(npc, "Thanks, mate!");
            } else {
                player.sendMessage("Not enough money.");
            }
        } else if (opt == 1) {
            chat(player, "Sorry, you'll have to earn it yourself, just like I did.");
            chat(npc, "Please yourself.");
        }
    }
}