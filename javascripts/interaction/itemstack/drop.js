importClass(org.maxgamer.rs.model.item.ground.GroundItemStack);

module.exports = {
    drop: function(player, item) {
        player.getInventory().remove(item);
        var ground = new GroundItemStack(item, player, 30, 180);
        ground.setLocation(player.getLocation());
    }
}