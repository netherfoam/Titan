function eat(body){
	if(body.use(food, "Eat")){
		body.say("Yum, " + food.getName());
	}
	else{
		body.use(food, "Drop");
		body.say("Eww, " + food.getName());
	}
}

function fight(body, target){
	while(target.isDead() == false){
		if(body.getTarget() != target){
			body.setTarget(target);
		}

		wait();
	}
}