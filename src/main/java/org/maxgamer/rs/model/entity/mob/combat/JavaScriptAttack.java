package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.javascript.JavaScriptCallFiber;
import org.maxgamer.rs.model.javascript.JavaScriptInvocation;
import org.mozilla.javascript.Undefined;

public class JavaScriptAttack extends Attack {
    private DamageType type;
    private String module;
    private int maxDistance = -1;
    private int warmupTicks = -1;

    public JavaScriptAttack(Mob attacker, int emote, int graphics, String module, DamageType type) {
        super(attacker, emote, graphics);
        this.type = type;
        this.module = module;
        this.type = type;
    }

    @Override
    public boolean prepare(Mob target, AttackResult damage) {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getScriptEnvironment(), module, "prepare", attacker, target, damage);
        Object o = call.call();
        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        if (o != null && o != Undefined.instance) {
            throw new IllegalStateException("Expected script to return boolean or void, got " + o);
        }

        return true;
    }

    @Override
    public void perform(final Mob target, final AttackResult damage) {
        JavaScriptCallFiber call = new JavaScriptCallFiber(Core.getServer().getScriptEnvironment(), module, "perform", attacker, target, damage);
        call.start();
    }

    @Override
    public boolean takeConsumables() {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getScriptEnvironment(), module, "takeConsumables", attacker);
        Object o = call.call();
        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        if (o != null) {
            throw new IllegalStateException("Expected script to return boolean or void, got " + o);
        }

        return true;
    }

    @Override
    public int getMaxDistance() {
        // We cache this value
        if (maxDistance == -1) {
            JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getScriptEnvironment(), module, "getMaxDistance", attacker);
            Object o = call.call();
            if (o instanceof Number) {
                maxDistance = ((Number) o).intValue();
            } else {
                maxDistance = 1;
            }
        }

        return maxDistance;
    }

    @Override
    public int getWarmupTicks() {
        if (warmupTicks == -1) {
            JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getScriptEnvironment(), module, "getWarmupTicks", attacker);
            Object o = call.call();
            if (o instanceof Number) {
                warmupTicks = ((Number) o).intValue();
            } else {
                warmupTicks = 4;
            }
        }

        return warmupTicks;
    }
}