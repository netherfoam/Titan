package org.maxgamer.rs.core;

/**
 * @author netherfoam
 */
public class CoreShutdownHook implements Runnable {
    @Override
    public void run() {
        //This must be done by the server thread.
        try {
            Core.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
