package inventory;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.script.ActionHandler;
import org.maxgamer.rs.script.Script;

import co.paralleluniverse.fibers.SuspendExecution;

@Script(type=ItemStack.class, options={"Drink"})
public class Drink extends ActionHandler{
	private static interface Effect{
		public abstract void apply(Mob m);
	}
	
	private static class BuffEffect implements Effect{
		private double multiplier;
		private SkillType skill;
		
		public BuffEffect(SkillType s, double m){
			this.skill = s;
			this.multiplier = m;
		}
		
		public void apply(Mob m){
			m.getSkills().buff(this.skill, this.multiplier);
		}
	}
	
	private static enum Potion{
		ATTACK(new BuffEffect(SkillType.STRENGTH, 1.10)),
		STRENGTH(new BuffEffect(SkillType.STRENGTH, 1.10)),
		DEFENCE(new BuffEffect(SkillType.STRENGTH, 1.10)),
		RANGE(new BuffEffect(SkillType.STRENGTH, 1.10)),
		MAGIC(new BuffEffect(SkillType.STRENGTH, 1.10)),
		
		;
		
		private Effect[] effects;
		
		private Potion(Effect... effects){
			this.effects = effects;
		}
		
		private void apply(Mob m){
			for(Effect e : this.effects){
				e.apply(m);
			}
		}
	}
	
	@Override
	public void run(Mob m, Map<String, Object> args) throws SuspendExecution {
		ItemStack item = (ItemStack) args.get("item");
		
		String name = item.getName().toLowerCase();
		
		if(name.startsWith("attack potion")){
			Potion.ATTACK.apply(m);
		}
		else if(name.startsWith("strength potion")){
			Potion.STRENGTH.apply(m);
		}
		else if(name.startsWith("defence potion")){
			Potion.DEFENCE.apply(m);
		}
		else if(name.startsWith("range potion")){
			Potion.RANGE.apply(m);
		}
		else if(name.startsWith("magic potion")){
			Potion.MAGIC.apply(m);
		}
		
		m.animate(829, 3);
	}
}