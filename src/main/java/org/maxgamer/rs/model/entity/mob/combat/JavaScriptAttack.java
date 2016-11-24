package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.javascript.JavaScriptInvocation;

public class JavaScriptAttack extends Attack {
    private DamageType type;
    private String module;

    public JavaScriptAttack(Mob attacker, int emote, int graphics, String module, DamageType type) {
        super(attacker, emote, graphics);
        this.type = type;
        this.module = module;
        this.type = type;
    }

    @Override
    public boolean prepare(Mob target, AttackResult damage) {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getJsScope(), module, "prepare", target, damage);
        Object o = call.call();
        if(o instanceof Boolean) {
            return (Boolean) o;
        }

        if(o != null) {
            throw new IllegalStateException("Expected script to return boolean or void, got " + o);
        }

        return true;
    }

    @Override
    public void perform(final Mob target, final AttackResult damage) {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getJsScope(), module, "perform", target, damage);
        call.call();
    }

    @Override
    public boolean takeConsumables() {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getJsScope(), module, "takeConsumables");
        Object o = call.call();
        if(o instanceof Boolean) {
            return (Boolean) o;
        }

        if(o != null) {
            throw new IllegalStateException("Expected script to return boolean or void, got " + o);
        }

        return true;
    }

    @Override
    public int getMaxDistance() {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getJsScope(), module, "getMaxDistance");
        Object o = call.call();
        if(o instanceof Number) {
            return ((Number) o ).intValue();
        }

        return 1;
    }

    @Override
    public int getWarmupTicks() {
        JavaScriptInvocation call = new JavaScriptInvocation(Core.getServer().getJsScope(), module, "getWarmupTicks");
        Object o = call.call();
        if(o instanceof Number) {
            return ((Number) o ).intValue();
        }

        return 4;
    }
}