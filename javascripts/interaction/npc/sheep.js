module.exports = {
    shear: function(player, npc) {
        if(player.has(ItemStack.create(1735)) == false){
            player.sendMessage("You need a pair of shears to shear sheep.");
            return;
        }

        player.animate(893);
        wait(1);
        player.getInventory().add(ItemStack.create(1737));
        player.sendMessage("You shear the sheep.");
    },

    use: function(player, npc, item) {
        if(item.getId() == 1735){
            shear(player, npc);
        }
        else{
            player.sendMessage("Nothing interesting happens.");
        }
    }
}