importClass(org.maxgamer.rs.lib.Erratic);
importClass(org.maxgamer.rs.model.map.Location);

var body;

// Order is important
require("ai/selectors.js");
require("ai/movement.js");
require("ai/attacks.js");

function pick(){
	while(body.getInventory().isFull() == false){
		var eatable = items().option("Eat");

		if(eatable.count() >= 12){
			return;
		}

		var plants = objects().name("Potato", "Cabbage", "Onion").reachable();
		if(plants.first() == null){
			return;
		}
		
		var plant = plants.nearest();
		if(body.move(plant) == false){
			/* Couldn't pick it! */
			return;
		}

		if(body.use(plants.nearest(), "Pick")){
			wait();
			wait();
			continue;
		}
	}
}

function danger(){
	if(body.getHealth() < body.getMaxHealth() / 2){
		/* We must eat */
		var food = items().option("Eat").first();

		if(food != null){
			if(body.use(food, "Eat")){
				body.say("Yum, " + food.getName());
				wait();
				return true;
			}
			else{
				body.use(food, "Drop");
				body.say("Eww, " + food.getName());
				wait();
				return true;
			}
		}
	}

	if(body.getTarget() != null){
		/* We're attacking something */
		return true;
	}

	if(npcs().attacking(body).first() != null){
		/* We're under attack */

		if(selected().reachable().first() != null){
			/* We can attack back! */
			
			setTarget(selected().first());
			return true;
		}
		else{
			/* We need to flee! */
			wander(15);
		}
	}


	return false;
}

function goals(){
	while(true){
		wait();

		if(body.getInventory().isFull() == false){
			var ground = grounds().option("Take").reachable().nearest();
			if(ground != null && move(ground.getLocation()) && ground.isVisible(body)){
				body.use(ground, "Take");
				wait();
			}
		}
		else{
			body.say("Time to drop this off at the bank!");
			var start = body.getLocation();
			var loc = new Location(3208, 3220, 2);
			if(travel(loc, 1) == false){
				body.say("I couldn't find a bank!");
				continue;
			}

			for(var i = 0; i < body.getInventory().getSize(); i++){
				var item = body.getInventory().getItem(i);
				if(item == null) continue;

				var bank = body.getBank().getState();
				var inv = body.getInventory().getState();

				try{
					inv.remove(item);
					bank.add(item);
				}
				catch(e){
					continue;
				}

				inv.apply();
				bank.apply();

				if(i % 2 == 1) wait();
			}

			wait();

			if(travel(start, 3) == false){
				body.say("I can't find my way back!");
				continue;
			}
		}

		style();
		armour();

		pick();

		var target = npcs().option("Attack").reachable().nearest();
		if(target != null){
			body.setTarget(target);
			continue;
		}
		else if(body.getLocation().z > 0){
			travel(body.getLocation().add(0, 0, -body.getLocation().z));
			continue;
		}
		else{
			wander();
		}
	}
}