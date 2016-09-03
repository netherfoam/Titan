package org.maxgamer.rs.module;

import org.maxgamer.rs.event.Event;

public class ModuleEvent extends Event {
    private Module module;

    public ModuleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}