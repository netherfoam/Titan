int count = 0;
public boolean run(Persona p, GameObject g){
	count++;
	
	if(count == 1){
		p.animate(827, 10);
	}
	
	if(count < 3) return false;
	
	try{
		ItemStack item;
		String name = g.getName().toLowerCase();
		if(name.equals("cabbage")) item = ItemStack.create(1965);
		else if(name.equals("potato")) item = ItemStack.create(1942);
		else if(name.equals("wheat")) item = ItemStack.create(1947);
		else if(name.equals("flax")) item = ItemStack.create(1779);
		else if(name.equals("nettles")) item = ItemStack.create(4241);
		else{
			return;
		}
		p.getInventory().add(item);
		
		g.hide(20); //Hide the plant for 20s
	}
	catch(ContainerException e){
		if(p instanceof Client){
			((Client) p).sendMessage("You need more room to pick that.");
		}
	}
	
	return true;
}