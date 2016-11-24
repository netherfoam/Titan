/**
 * @author Dirk Jamieson
 * @date 2 Feb 2016
 */
importClass(org.maxgamer.rs.model.entity.mob.combat.MeleeAttack);
importClass(org.maxgamer.rs.model.entity.mob.combat.Damage);

module.exports = {
    prepare: function(attacker, target, damage) {
        // This isn't right, but it will do for us.
        var d1, d2, d3, d4;

        var d1 = MeleeAttack.roll(attacker, target);

        if (d1.getHit() == 0) {
            d2 = MeleeAttack.roll(attacker, target);
        } else {
            d2 = new Damage(d1.getHit() / 2, d1.getType(), target);
        }

        if (d2.getHit() == 0) {
            d3 = MeleeAttack.roll(attacker, target);
        } else {
            d3 = new Damage(d2.getHit() / 2, d2.getType(), target);
        }

        d4 = new Damage(d3.getHit() / 2, d3.getType(), target);
        d3.setHitDelay(1);
        d4.setHitDelay(1);

        damage.add(d1);
        damage.add(d2);
        damage.add(d3);
        damage.add(d4);
    },

    perform: function(attacker, target, damage) {
        attacker.animate(10961);
        attacker.graphics(1950);
        wait(1);
        damage.apply(attacker);
    },

    takeConsumables: function(attacker) {
        var e = attacker.getAttackEnergy();
        if (e < 50) {
            attacker.sendMessage("You do not have enough special attack energy.");
            return false;
        }
        attacker.setAttackEnergy(e - 50);
        return true;
    }
}