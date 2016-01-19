importClass(org.maxgamer.rs.module.people.api.Pathing);
importClass(org.maxgamer.rs.module.people.api.POI);
importClass(org.maxgamer.rs.lib.Erratic);

var body;

function move(dest){
	var action = body.move(dest);
	while(action.getPath().isEmpty() == false){
		wait();
	}
	return !action.getPath().hasFailed() && action.getPath().isEmpty();
}

function travel(dest, radius){
	radius = radius || 2;

	var action;
	do{
		action = Pathing.findPath(body, body.getLocation(), dest.add(-radius, -radius), dest.add(radius, radius), body.getSizeX(), body.getSizeY());
		if(action == null){
			return false;
		}

		body.getActions().queue(action);

		while(action.isQueued()){
			wait();
		}
	} while(action.isComplete() == false);

	return true;
}

function wander(distance){
	distance = distance || 20;
	var dest = body.getLocation().add(Erratic.nextInt(-distance, distance), Erratic.nextInt(-distance, distance));
	move(dest);
}

function poi(){
	var dest = POI.getPOI(body);

	if(dest == null){
		/* We couldn't find a good POI to visit */
		return false;
	}
	else{
		/* We must reach this POI */
		return travel(dest, 3);
	}
}