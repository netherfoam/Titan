importClass(org.maxgamer.rs.model.skill.SkillType);

module.exports = {
    bury: function(player, item){
    	if(!player.getInventory().contains(item)){
    		return;
    	}

    	bones = {
    		526: 10, // Normal
    		528: 10, // Burnt
    		2859: 12.5, // Monkey
    		530: 12.5, // Bat
    		3183: 20, // Big
    		532: 20, // Big
    		530: 20, // Jogre
    		4812: 25, // Zogre
    		3123: 30, // Shaikahan
    		534: 35, // Baby
    		6812: 40, // Wyvern
    		536: 50, // Dragon
    		4830: 52.5, // Fayrg
    		4832: 55, // Raurg
    		6729: 65, // Dagannoth
    		4834: 75, // Ourg
    		18830: 85 // Frost
    	};

    	player.sendMessage("You bury the bones...");

    	var exp = bones[item.getId()];
    	if(exp == null) return;

    	animate(player, 827);

    	if (typeof player.getProtocol !=  "undefined") {
    		// Has getProtocol() method
        	player.getProtocol().sendSound(2738, 0, 1);
    	}

    	if(player.getInventory().contains(item)) {
    		player.getInventory().remove(item);
    		player.getSkills().addExp(SkillType.PRAYER, exp);
    	}
    }
}


