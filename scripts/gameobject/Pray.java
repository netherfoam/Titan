package gameobject;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type = GameObject.class, options = { "Pray" })
public class Pray extends ActionHandler {
	
	@Override
	public void run(Mob mob, Map<String, Object> args) throws SuspendExecution {
		GameObject object = (GameObject) args.get("target");
		String name = object.getName();
		//TODO: Dialogue for Zaros altar
		if (name.equalsIgnoreCase("altar") || name.equalsIgnoreCase("chaos altar")) {
			
			if (mob.getSkills().getLevel(SkillType.PRAYER, true) == mob.getSkills().getLevel(SkillType.PRAYER, false)) {
				mob.sendMessage("Your prayer points are already full.");
				return;
			}
			
			mob.getSkills().restore(SkillType.PRAYER, mob.getSkills().getLevel(SkillType.PRAYER, false));
			mob.animate(645, 20);
			mob.sendMessage("You pray to the gods and you recharge your prayer points.");
			
		}
		else {
			mob.sendMessage("That Pray is not yet implemented, sorry!");
			return;
		}
		
		Action.wait(3);
	}
	
}
