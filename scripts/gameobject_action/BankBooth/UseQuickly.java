package gameobject_action.BankBooth;

import java.util.Map;

import org.maxgamer.rs.interfaces.impl.primary.BankInterface;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.script.OptionHandler;

public class UseQuickly implements OptionHandler {
	
	public void run(Mob p, Map<String, Object> args) {
		((Player) p).getWindow().open(new BankInterface((Player) p));
	}
}