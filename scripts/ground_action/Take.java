boolean run(Persona p, GroundItemStack ground){
	if(ground.isDestroyed()) return true; //Dead item, end of task
	
	if(ground.getLocation().equals(p.getLocation())){
		ground.destroy();
		try{
			p.getInventory().add(ground.getItem());
		}
		catch(ContainerException e){
			if(p instanceof Client){
				((Client) p).sendMessage("You need more space to pick that up.");
			}
		}
		self.yield();
		return true;
	}
	self.yield();
	return true;
}