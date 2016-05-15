importClass(org.maxgamer.rs.model.item.ground.GroundItemStack);

function drop(player, item) {
	player.getInventory().remove(item);
	var ground = new GroundItemStack(item, player, 30, 180);
	ground.setLocation(player.getLocation());
}