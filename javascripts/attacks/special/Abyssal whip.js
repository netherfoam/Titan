function perform(attacker, target, damage){
	// TODO: Drain damage / 10 % of the target's energy
	// and give it to the attacker.  Tricky because Mobs
	// do not have energy.
	damage.apply(attacker);
}

function takeConsumables(attacker){
	return true;
}
