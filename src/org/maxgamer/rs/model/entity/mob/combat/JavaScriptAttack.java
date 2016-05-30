package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.javascript.JavaScriptCall;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Undefined;

import java.io.File;
import java.io.IOException;

public class JavaScriptAttack extends Attack{
	private JavaScriptFiber fiber;
	private DamageType type;
	private File file;
	
	public JavaScriptAttack(Mob attacker, int emote, int graphics, File file, DamageType type) throws IOException {
		super(attacker, emote, graphics);
		this.fiber = new JavaScriptFiber();
		this.fiber.parse("lib/core.js");
		if(this.fiber.parse(file).isFinished() == false){
			throw new EvaluatorException("May not pause during parsing of file");
		}
		this.type = type;
	}

	@Override
	public boolean prepare(Mob target, AttackResult damage) {
		try {
			JavaScriptCall call = this.fiber.invoke("prepare", attacker, target, damage);
			if(call.isFinished() == false){
				throw new EvaluatorException("May not pause during prepare()");
			}
			if(call.getResult() == null || call.getResult() == Undefined.instance){
				return true;
			}
			if(call.getResult() instanceof Boolean == false){
				throw new EvaluatorException("prepare() returned " + call.getResult() + " instead of a boolean.");
			}
			return (boolean) call.getResult();
		}
		catch (NoSuchMethodException e) {
			switch(type){
				case MELEE:
					damage.add(MeleeAttack.roll(attacker, target));
					break;
				case RANGE:
					damage.add(RangeAttack.roll(attacker, target));
					break;
				default:
					throw new IllegalStateException("Attack damage cannot be inferred, please define a prepare(attacker, target, damage) method in " + file);
			}
			return true;
		}
	}
	
	@Override
	public void perform(final Mob target, final AttackResult damage){
		try {
			this.fiber.invoke("perform", attacker, target, damage);
			
			// We allow pauses to be performed here, but they are not tied to the action. The attack action is finalised.
			// However the file may continue to execute in the background 
		}
		catch (NoSuchMethodException e) {
			super.emote = new Animation(attacker.getCombatStats().getAttackAnimation());
			super.perform(target, damage);
		}
	}

	@Override
	public boolean takeConsumables() {
		try {
			JavaScriptCall call = this.fiber.invoke("takeConsumables", attacker);
			if(call.isFinished() == false){
				throw new EvaluatorException("May not pause during takeConsumables()");
			}
			if(call.getResult() == null || call.getResult() == Undefined.instance){
				return true;
			}
			if(call.getResult() instanceof Boolean == false){
				throw new EvaluatorException("takeConsumables() returned " + call.getResult() + " instead of a boolean.");
			}
			return (boolean) call.getResult();
		}
		catch (NoSuchMethodException e) {
			return true;
		}
	}

	@Override
	public int getMaxDistance() {
		try {
			JavaScriptCall call = this.fiber.invoke("getMaxDistance", attacker);
			if(call.isFinished() == false){
				throw new EvaluatorException("May not pause during getMaxDistance()");
			}
			if(call.getResult() == null || call.getResult() == Undefined.instance){
				return 1;
			}
			if(call.getResult() instanceof Number == false){
				throw new EvaluatorException("getMaxDistance() returned " + call.getResult() + " instead of a number.");
			}
			return ((Number) call.getResult()).intValue();
		}
		catch (NoSuchMethodException e) {
			switch(type){
				case MELEE:
					return 1;
				case MAGE:
					return 10;
				case RANGE:
					if(attacker.getAttackStyle().getName().equalsIgnoreCase("Long range")){
						return 15;
					}
					else{
						return 10;
					}
				default:
					throw new IllegalStateException("Attack range cannot be inferred, please define a getMaxDistance(attacker) method in " + file);
			}
		}
	}

	@Override
	public int getWarmupTicks() {
		try {
			JavaScriptCall call = this.fiber.invoke("getWarmupTicks", attacker);
			if(call.isFinished() == false){
				throw new EvaluatorException("May not pause during getWarmupTicks()");
			}
			if(call.getResult() == null || call.getResult() == Undefined.instance){
				return 4;
			}
			if(call.getResult() instanceof Number == false){
				throw new EvaluatorException("getWarmupTicks() returned " + call.getResult() + " instead of a number.");
			}
			return ((Number) call.getResult()).intValue();
		}
		catch (NoSuchMethodException e) {
			return 4;
		}
	}
}