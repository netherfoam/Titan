//include("lib/dialogue.js");

function rub(player, item){
	var opt = option(["Edgeville", "Karumja", "Draynor", "Al-Kharid", "Cancel"]);
	var dest = null;
	if(opt == 0){
		dest = new Location(3092, 3494, 0);
	}
	else if(opt == 1){
		dest = new Location(2923, 3146, 0);
	}
	else if(opt == 2){
		dest = new Location(3104, 3249, 0);
	}
	else if(opt == 3){
		dest = new Location(3292, 3169, 0);
	}
	else{
		// Cancel
		return;
	}

	importClass(org.maxgamer.rs.model.entity.mob.combat.mage.TeleportSpell);

	var spell = new TeleportSpell(1, 1576, 8939, 5, dest);
	spell.cast(player);
}