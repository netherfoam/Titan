var body;

function survival(){
	if(body.getHealth() < body.getMaxHealth() / 2){
		/* We must eat */
		var food = items().option("Eat").first();
		if(food != null){
			body.use(food, "Eat");
		}

		return true;
	}

	if(body.getTarget() != null){
		/* We're attacking something */
		return true;
	}

	if(npcs().attacking(body).first() != null){
		/* We're under attack */
		setTarget(selected().first());
		return true;
	}



	
}