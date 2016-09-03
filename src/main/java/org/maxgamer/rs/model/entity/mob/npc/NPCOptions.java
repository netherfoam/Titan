package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.persona.Persona;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * @author netherfoam TODO: This is not used
 */
public class NPCOptions {
    public static final NPCOption ATTACK = new NPCOption("Attack") {
        @Override
        public void run(Persona clicker, NPC target) {
            clicker.setTarget(target);
        }
    };
    private final static HashMap<String, NPCOption> options = new HashMap<String, NPCOption>();

    static {
        for (Field f : NPCOptions.class.getDeclaredFields()) {
            try {
                if ((f.getModifiers() & Modifier.STATIC) == 0) continue; //Field is not static.
                Object v = f.get(null);
                if (v instanceof NPCOption == false) continue;

                NPCOption option = (NPCOption) v;
                options.put(option.getText().toUpperCase(), option);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static NPCOption getOption(String text) {
        if (text == null) throw new NullPointerException("NPCOption text may not be a null value");
        return options.get(text.toUpperCase());
    }
}