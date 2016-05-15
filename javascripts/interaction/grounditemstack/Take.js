function take(player, g) {
	var item = g.getItem();
	if(player.getInventory().hasRoom(item)) {
		player.getInventory().add(item);
		g.destroy();
	}
	else {
		player.sendMessage("You need more space to pick that up.");
	}
}