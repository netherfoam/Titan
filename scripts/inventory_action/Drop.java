boolean run(Player p, ItemStack item, int slot){
	try{
		p.getInventory().remove(slot, item);
		ground = new GroundItemStack(item, p, 30, 180);
		ground.setLocation(p.getLocation());
	}
	catch(ContainerException e){
		p.getCheats().log(10, "Player attempted to drop an item they don't have: " + item);
	}
	self.yield();
	return true;
}