package org.maxgamer.rs.model.skill.prayer;

import org.maxgamer.rs.event.AutoRegister;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.Damage;
import org.maxgamer.rs.model.entity.mob.combat.DamageType;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.events.mob.MobAttackEvent;

/**
 * Listener class that handles prayer on-attack effects, like protect and deflect prayers.
 * @author netherfoam
 */
@AutoRegister
public class PrayerListener implements EventListener {
	
	@EventHandler(priority = EventPriority.LOWEST, consumer=false, skipIfCancelled=true)
	public void onAttack(MobAttackEvent e) {
		Mob target = e.getTarget();
		Damage d = e.getDamage();
		
		if(target instanceof Persona){
			Persona p = (Persona) target;
			if(d.getType() == DamageType.MELEE){
				if(p.getPrayer().isEnabled(PrayerType.PROTECT_FROM_MELEE) || p.getPrayer().isEnabled(PrayerType.DEFLECT_MELEE)){
					d = new Damage(d.getHit() / 2, d.getType(), target);
				}
			}
			
			if(d.getType() == DamageType.MAGE){
				if(p.getPrayer().isEnabled(PrayerType.PROTECT_FROM_MAGIC) || p.getPrayer().isEnabled(PrayerType.DEFLECT_MAGIC)){
					d = new Damage(d.getHit() / 2, d.getType(), target);
				}
			}
			
			if(d.getType() == DamageType.RANGE || d.getType() == DamageType.CANNON){
				if(p.getPrayer().isEnabled(PrayerType.PROTECT_FROM_MISSILES) || p.getPrayer().isEnabled(PrayerType.DEFLECT_MISSILES)){
					d = new Damage(d.getHit() / 2, d.getType(), target);
				}
			}
			
			e.setDamage(d);
		}
	}
}