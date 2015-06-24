package org.maxgamer.rs.script;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

public abstract class ScriptSpace {
	/**
	 * The mob who started this script
	 */
	private Mob mob;
	
	/**
	 * The arguments for invoking the script, contains information like
	 * "target" = tree or "option" = "Chop-down"
	 */
	private Map<String, Object> args;
	
	public ScriptSpace(Mob mob, Map<String, Object> args) {
		super();
		this.mob = mob;
		this.args = args;
	}

	/**
	 * @return the mob
	 */
	public Mob getMob() {
		return mob;
	}
	
	/**
	 * @return the args
	 */
	public Map<String, Object> getArgs() {
		return args;
	}
	
	/**
	 * Runs this script. This may suspend the current fiber.
	 * @throws SuspendExecution
	 */
	public abstract void run() throws SuspendExecution;
}