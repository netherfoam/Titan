module.exports = {
    take: function(player, g) {
        var item = g.getItem();
        if(g.isDestroyed()) {
            // TODO I don't feel like it's my responsibility to check this, here.
            return;
        }
        if(player.getInventory().hasRoom(item)) {
            player.getInventory().add(item);
            g.destroy();
        }
        else {
            player.sendMessage("You need more space to pick that up.");
        }
    }
}