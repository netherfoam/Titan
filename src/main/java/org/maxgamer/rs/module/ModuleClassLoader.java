package org.maxgamer.rs.module;

import co.paralleluniverse.fibers.instrument.QuasarURLClassLoader;

import java.net.URL;

public class ModuleClassLoader extends QuasarURLClassLoader {

    public ModuleClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL url) {
        /* This method is public */
        super.addURL(url);
    }
}