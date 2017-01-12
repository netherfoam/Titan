importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.skill.SkillType);

module.exports = {
    prepare: function(attacker, target, damage){
        attacker.animate(2890);
        attacker.graphics(483);

        var skills = target.getSkills();

        var loss = 0;
        loss += skills.buff(SkillType.ATTACK, 0.95);
        loss += skills.buff(SkillType.DEFENCE, 0.95);
        loss += skills.buff(SkillType.STRENGTH, 0.95);
    },

    takeConsumables: function(attacker){
        var e = attacker.getAttackEnergy();
        if (e < 50) {
            attacker.sendMessage("You do not have enough special attack energy.");
            return false;
        }
        attacker.setAttackEnergy(e - 50);
        return true;
    }
}