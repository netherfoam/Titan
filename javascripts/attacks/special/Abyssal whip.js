function perform(attacker, target, damage){
	// TODO: Drain damage / 10 % of the target's energy
	// and give it to the attacker.  Tricky because Mobs
	// do not have energy.
	attacker.animate(11971);
	target.graphics(341);//TODO: incorrect
	wait(1);
	damage.apply(attacker);
}

function takeConsumables(attacker){
	var e = attacker.getAttackEnergy();
	if (e < 50) {
		attacker.sendMessage("You do not have enough special attack energy.");
		return false;
	}
	attacker.setAttackEnergy(e - 50);
	return true;
}