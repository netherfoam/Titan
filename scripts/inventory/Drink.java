package inventory;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.ContainerState;
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
		ATTACK(new BuffEffect(SkillType.ATTACK, 1.10)),
		STRENGTH(new BuffEffect(SkillType.STRENGTH, 1.10)),
		DEFENCE(new BuffEffect(SkillType.DEFENCE, 1.10)),
		RANGE(new BuffEffect(SkillType.RANGE, 1.10)),
		MAGIC(new BuffEffect(SkillType.MAGIC, 1.10)),
		
		SUPER_ATTACK(new BuffEffect(SkillType.ATTACK, 1.15)),
		SUPER_STRENGTH(new BuffEffect(SkillType.STRENGTH, 1.15)),
		SUPER_DEFENCE(new BuffEffect(SkillType.DEFENCE, 1.15)),
		SUPER_RANGE(new BuffEffect(SkillType.RANGE, 1.15)),
		SUPER_MAGIC(new BuffEffect(SkillType.MAGIC, 1.15)),
		
		PRAYER_POTION(new Effect(){
			@Override
			public void apply(Mob m) {
				m.getSkills().restore(SkillType.PRAYER, m.getSkills().getLevel(SkillType.PRAYER, false) * 0.10);
			}
		}),
		
		SUPER_PRAYER_POTION(new Effect(){
			@Override
			public void apply(Mob m) {
				m.getSkills().restore(SkillType.PRAYER, m.getSkills().getLevel(SkillType.PRAYER, false) * 0.15);
			}
		}),
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
		else if(name.startsWith("prayer potion")){
			Potion.PRAYER_POTION.apply(m);
		}
		else if(name.startsWith("super attack")){
			Potion.SUPER_ATTACK.apply(m);
		}
		else if(name.startsWith("super strength")){
			Potion.SUPER_STRENGTH.apply(m);
		}
		else if(name.startsWith("super defence")){
			Potion.SUPER_DEFENCE.apply(m);
		}
		else if(name.startsWith("super range")){
			Potion.SUPER_RANGE.apply(m);
		}
		else if(name.startsWith("super magic")){
			Potion.SUPER_MAGIC.apply(m);
		}
		else if(name.startsWith("super prayer")){
			Potion.SUPER_PRAYER_POTION.apply(m);
		}
		else{
			m.sendMessage("That Drink isn't implemented.");
			return;
		}
		
		if(m instanceof InventoryHolder){
			ContainerState i = ((InventoryHolder) m).getInventory().getState();
			int slot = (int) args.get("slot");
			
			i.remove(slot, item);
			int charges = item.getCharges();
			if(charges > 1){
				i.add(slot, item.setCharges(charges - 1));
			}
			else{
				i.add(slot, ItemStack.create(229, item.getAmount(), item.getHealth()));
			}
			i.apply();
		}
		
		m.animate(829, 5);
		Action.wait(1);
	}
}