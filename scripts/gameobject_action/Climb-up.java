boolean run(Persona p, GameObject obj){
	Location src = p.getLocation();
	Location dest;
	if(src.y >= 6400){
		dest = src.add(0, -6400, 0);
	}
	else{
		dest = src.add(0, 0, 1);
	}
	
	ObjectTeleportAction mc = new ObjectTeleportAction(p, new Animation(828), dest);
	
	p.getActions().clear();
	p.getActions().queue(mc);
	return true;
}
