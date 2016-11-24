importClass(org.maxgamer.rs.model.entity.mob.combat.DamageType);

module.exports = {
    inspect: function(p, t){
        p.sendMessage("Inspection: " + t + " Health: " + t.getHealth() + "/" + t.getMaxHealth());
        p.sendMessage("In Combat: " + t.getDamage().isInCombat() + " Damage Taken: " + t.getDamage().getTotal(DamageType.values()) + ", Target: " + t.getTarget());
        p.sendMessage("ActionQueue: " + t.getActions());
        p.sendMessage("Rights: " + t.getRights() + ", Location: (" + t.getLocation().x + ", " + t.getLocation().y + ")");
        p.sendMessage("Inventory " + t.getInventory().getTakenSlots() + "/" + t.getInventory().getSize());
        p.sendMessage(java.util.Arrays.toString(t.getInventory().getItems()));
    }
}