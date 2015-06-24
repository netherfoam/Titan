package gameobject_action;

import java.util.Map;

import org.maxgamer.rs.model.action.ObjectTeleportAction;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.script.OptionHandler;

public class ClimbDown implements OptionHandler {
	public void run(Mob p, Map<String, Object> args) {
		Location src = p.getLocation();
		Location dest;
		if (src.z == 0) {
			dest = src.add(0, 6400, 0);
		}
		else {
			dest = src.add(0, 0, -1);
		}
		
		ObjectTeleportAction mc = new ObjectTeleportAction(p, new Animation(828), dest);
		
		p.getActions().clear();
		p.getActions().queue(mc);
	}
}