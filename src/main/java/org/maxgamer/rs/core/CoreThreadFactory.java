package org.maxgamer.rs.core;

import java.util.concurrent.ThreadFactory;

/**
 * @author netherfoam
 */
public class CoreThreadFactory implements ThreadFactory {
    private int nextThreadId = 0;

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "ExecutorService " + nextThreadId++);
        t.setContextClassLoader(Core.CLASS_LOADER);
        t.setPriority(Thread.NORM_PRIORITY);
        t.setDaemon(true);
        return t;
    }
}
