importClass(org.maxgamer.rs.model.entity.mob.combat.mage.TeleportSpell);

module.exports = {
    enter: function(player, object){
        if(object.getId() == 2492){
            // Rune essence mine portal
            var spell = new TeleportSpell(1, 1576, 8939, 5, new Location(3253, 3401, 0));
            spell.cast(player);
            wait(2);
        }
    }
}