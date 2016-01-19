var body;

importClass(org.maxgamer.rs.model.item.WieldType);
importClass(org.maxgamer.rs.model.skill.SkillSet);
importClass(org.maxgamer.rs.model.skill.SkillType);
importClass(org.maxgamer.rs.model.entity.mob.combat.AttackStyle);

require("ai/selectors.js");

function style(){
	var skills = body.getSkills();

	var options = [SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE];

	var weakest = options[0];

	for(var i = 1; i < options.length; i++){
		importClass(java.lang.System);
		if(skills.getLevel(options[i]) < skills.getLevel(weakest)){
			weakest = options[i];
		}
	}

	var item = body.getEquipment().get(WieldType.WEAPON);
	var weapon = (item == null ? null : item.getWeapon());

	var style = 1;
	if(item == null || weapon == null){
		if(weakest == SkillType.STRENGTH){
			style = 2;
		}
		else if(weakest == SkillType.DEFENCE){
			style = 3;
		}

		body.setAttackStyle(AttackStyle.getStyle(-1, style));
	}
	else{
		var split = 4;
		var bestAtk = null;

		for(var i = 1; i <= 4; i++){
			var atk = item.getDefinition().getAttackStyle(i);
			if(atk.isType(weakest) && atk.getSkills().length < split){
				split = atk.getSkills().length;
				bestAtk = atk;
			}
		}
		body.setAttackStyle(bestAtk);
	}
}

function armour(){
	var iterator = items().option("Wield", "Wear").iterator();

	while(iterator.hasNext()){
		var item = iterator.next();
		var weapon = item.getWeapon();

		if(weapon == null) continue;
		var slot = weapon.getSlot();

		if(body.getEquipment().get(slot) == null){
			if(item.hasOption("Wear")){
				body.use(item, "Wear");
			}
			else{
				body.use(item, "Wield");
			}
			wait();
		}

	}
}
