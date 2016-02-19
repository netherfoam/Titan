importClass(org.maxgamer.rs.lib.Erratic);
importClass(org.maxgamer.rs.model.map.Location);

// Order is important
require("ai/selectors.js");
// require("ai/movement.js");

require("ai/tasks.js");

var survival = [
	{
		/* Eat when low health */
		condition: function(body){
			return body.getHealth() < body.getMaxHealth() / 2 && items().option("Eat").first() != null;
		},
		task: function(body){
			var food = selection().first();
			if(body.use(food, "Eat")){
				body.say("Yum, " + food.getName());
				wait();
			}
			else{
				body.use(food, "Drop");
				body.say("Eww, " + food.getName());
			}
		}
	},
	{
		/* Fight back when under attack */
		condition: function(body){
			if(npcs().attacking(body).first() != null && (body.getTarget() == null || body.getTarget().getTarget() != body)){
				return true;
			}
		},
		task: function(body){
			body.setTarget(npcs().attacking(body).first());
			while(body.getTarget() != null){
				wait();
			}
		}
	}
];

function danger(body){
	for (var i = 0; i < survival.length; i++) {
		task = survival[i];
		if(task.condition(body)){
			queue(task.task, survival.length - i);
			return;
		}
	}


}