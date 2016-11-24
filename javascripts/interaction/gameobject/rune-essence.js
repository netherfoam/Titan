module.exports = {
    mine: function(player, npc){
        // A very basic essence mining script. This only requires the player to have a pick,
        // and mines until full.  Pickaxe does not modify speed. Experience value is incorrect.
        // Animation shows only bronze pickaxes.
        var pickaxes = [
            ItemStack.create(1265), ItemStack.create(1267), ItemStack.create(1269), ItemStack.create(1273),
            ItemStack.create(1271), ItemStack.create(1275), ItemStack.create(15259), ItemStack.create(13661)
        ];

        var pick = false;
        for (var i = 0; i < pickaxes.length; i++) {
            if(player.has(pickaxes[i])){
                pick = true;
                break;
            }
        };

        if(pick == false){
            player.sendMessage("You need a pickaxe to mine that.");
            return;
        }

        while(player.getInventory().isFull() == false){
            player.animate(10001);
            wait(4);

            player.getInventory().add(ItemStack.create(7936));
            player.getSkills().addExp(SkillType.MINING, 20);
        }

        player.sendMessage("Your inventory is full.");
    }
}