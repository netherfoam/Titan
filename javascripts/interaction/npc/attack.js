module.exports = {
    attack: function(player, npc){
        // No changes
        if(player.getTarget() == npc) return;

    	player.setTarget(npc);

    	// We immediately tick the player's action queue, because changing
    	// target doesn't cost you a turn.
    	player.getActions().tick();
    }
}

