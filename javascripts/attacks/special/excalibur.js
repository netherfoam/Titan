importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.skill.SkillType);

module.exports = {
    prepare: function(attacker, target, damage){
        attacker.animate(1168);
        attacker.graphics(247);

        var skills = attacker.getSkills();
        skills.buff(SkillType.DEFENCE, 8);

        attacker.say("For Camelot!");
    },

    takeConsumables: function(attacker){
        var e = attacker.getAttackEnergy();
        if (e < 100) {
            attacker.sendMessage("You do not have enough special attack energy.");
            return false;
        }
        attacker.setAttackEnergy(e - 100);

        return true;
    }
}