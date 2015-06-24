package org.maxgamer.rs.script;

import java.util.Map;

import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

public interface OptionHandler{
	public abstract void run(Mob mob, Map<String, Object> args) throws SuspendExecution;
}