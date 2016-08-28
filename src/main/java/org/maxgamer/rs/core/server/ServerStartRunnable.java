package org.maxgamer.rs.core.server;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.item.inventory.Equipment;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;
import org.maxgamer.rs.model.javascript.TimeoutError;
import org.maxgamer.rs.util.log.Log;
import org.mozilla.javascript.ContinuationPending;

import java.io.File;
import java.io.IOException;

/**
 * @author netherfoam
 */
public class ServerStartRunnable implements Runnable {
    private Server server;

    public ServerStartRunnable(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // We should initialize everything before loading user content
            // Eg commands, events, modules
            Log.info("Loading Map...");
            server.getMaps();
            Log.info("...Map Loaded!");

            Log.debug("Loading Commands...");
            server.getCommands();
            Log.debug("Loading Modules...");
            server.getModules().load();
            Log.debug("Modules Loaded!");

            server.getLogon().start();

            Core.submit(server.getAutosave(), server.getAutosave().getInterval(), true);

            File startup = new File("startup.js");
            if (startup.exists()) {
                try {
                    JavaScriptFiber js = new JavaScriptFiber(Core.CLASS_LOADER);
                    js.parse(startup);
                }
                catch(TimeoutError e){
                    Log.warning("Startup script timed out.");
                }
                catch (ContinuationPending e) {
                    //TODO: Allow them.
                    Log.warning("Can't use continuations in startup.js");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Equipment.load();

            server.getThread().submit(server.getTicker());
            //Note that Server.this doesn't start the network, that is done by the
            //logon server when a connection is established. The logon server
            //will also drop the connection if the logon connection is lost
            Log.info("Server initialized!");
            server.getThread().resetUsage();
        }
        catch (Throwable t) {
            t.printStackTrace();
            Log.severe("Exception was raised while booting server. Shutting down...");
            try {
                Core.getServer().shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
