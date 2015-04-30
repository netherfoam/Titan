
//IDs of food - Cabbage, potato, lobster, swordfish, shark
static int[] ids = new int[]{1965, 1942, 379, 373, 385};
//Heal amounts of foods as above
static int[] hps = new int[]{30,   30,   120, 140, 200};

//The food the player ate
int food = -1;
//True if we're running this for the first time
boolean first = true;
boolean run(Persona p, ItemStack item, int slot){
	if(first){
		first = false;
		
		//Find the food
		for(int i = 0; i < ids.length; i++){
			if(ids[i] == item.getId()){
				food = i;
				//Remove the food
				//p.getInventory().remove(slot, item);
				//Do an animation action, we continue when it's finished
				p.getActions().insertBefore(self, new AnimateAction(p, 829, false));
				if(p instanceof Client){
					p.getProtocol().sendSound(2393, 255, 255);
				}
				//We've done nothing important here, so yield and let the animation run
				self.yield();
				return false;
			}
		}
		//Not implemented
		if(p instanceof Client){
			p.sendMessage("That food has not been implemented yet, sorry!");
		}
		return true;
	}
	p.sendMessage("Reached removing and healing");
	//We are guaranteed to have a valid food here. Attempt to heal the player.
	p.setHealth(Math.min(p.getHealth() + hps[food], p.getMaxHealth()));
	p.getInventory().remove(slot, item);

	return true;
}