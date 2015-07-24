package gameobject.BankBooth;

import java.util.Map;

import org.maxgamer.rs.interfaces.impl.primary.BankInterface;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

@Script(type=GameObject.class, names={"Bank booth"}, options={"Use-quickly", "Use"})
public class UseQuickly extends ActionHandler {
	@Override
	public void run(Mob p, Map<String, Object> args) {
		((Player) p).getWindow().open(new BankInterface((Player) p));
	}
}