package org.maxgamer.rs.structure.configs;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Document this
 */
public class HybridConfigSection extends ConfigSection {
    private ConfigSection[] sources;

    public HybridConfigSection(ConfigSection... sources) {
        this.sources = sources;
    }

    @Override
    public boolean isEmpty() {
        for (ConfigSection section : sources) {
            if (!section.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public Object getObject(String s) {
        for (ConfigSection section : sources) {
            Object o = section.getObject(s);

            if (o != null) {
                return o;
            }
        }

        return null;
    }

    @Override
    public ConfigSection getSection(String key, ConfigSection fallback) {
        ConfigSection[] children = fallback == null ? new ConfigSection[sources.length] : new ConfigSection[sources.length + 1];

        for (int i = 0; i < children.length; i++) {
            children[i] = sources[i].getSection(key);
        }

        if (fallback != null) {
            // The last item becomes the fallback, if it was given
            children[children.length - 1] = fallback;
        }

        return new HybridConfigSection(children);
    }

    @Override
    public Collection<String> keys() {
        Set<String> keys = new HashSet<>();

        for (ConfigSection section : this.sources) {
            for (String k : section.keys()) {
                keys.add(k);
            }
        }

        return keys;
    }
}
