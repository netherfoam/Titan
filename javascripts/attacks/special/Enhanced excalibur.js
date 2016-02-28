importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.skill.SkillType);

function prepare(attacker, target, damage){
	attacker.animate(1168);
	attacker.graphics(247);
	attacker.say("For Camelot!");

	var skills = attacker.getSkills();
	skills.buff(SkillType.DEFENCE, 8);
	//Heal 40 lp every 2 seconds for 200 lp max 
	for(var i = 0; i < 5; i++) {
		attacker.heal(40);
		wait(3);
	}
}

function takeConsumables(attacker){
	var e = attacker.getAttackEnergy();
	if (e < 100) {
		attacker.sendMessage("You do not have enough special attack energy.");
		return false;
	}
	attacker.setAttackEnergy(e - 100);
	return true;
}
