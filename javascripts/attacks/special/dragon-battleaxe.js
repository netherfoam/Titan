/**
 * @author Dirk Jamieson
 * @date 2 Feb 2016
 */
importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.skill.SkillType);

module.exports = {
    prepare: function(attacker, target, damage){
        attacker.animate(1056);
        attacker.graphics(246);

        var skills = attacker.getSkills();

        var loss = 0;
        loss += skills.buff(SkillType.ATTACK, 0.90);
        loss += skills.buff(SkillType.DEFENCE, 0.90);
        loss += skills.buff(SkillType.RANGE, 0.90);
        loss += skills.buff(SkillType.MAGIC, 0.90);

        var gain = (-loss / 4 + 10) / skills.getLevel(SkillType.STRENGTH) + 1;
        skills.buff(SkillType.STRENGTH, gain);

        attacker.say("Raarrrrrgggggghhhhhhh!");
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