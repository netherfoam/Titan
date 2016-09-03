package org.maxgamer.rs.command;

import org.maxgamer.rs.core.Core;

import java.util.Scanner;

/**
 * @author netherfoam
 */
public class ConsoleSender implements CommandSender {
    private boolean run = true;
    private Thread reader;

    public ConsoleSender() {
        reader = new Thread("Console-Reader") {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                while (sc.hasNextLine() && run) {
                    final String line = sc.nextLine();

                    Core.submit(new Runnable() {
                        @Override
                        public void run() {
                            Core.getServer().getCommands().handle(ConsoleSender.this, CommandManager.COMMAND_PREFIX + line);
                        }
                    }, false);
                }
                sc.close();
            }
        };
        reader.setDaemon(true);
        reader.start();
    }

    @Override
    public void sendMessage(String msg) {
        //Strip colours.
        msg = msg.replaceAll("<col=[0-9A-Fa-f]{3,6}>", "").replaceAll("</col>", "");
        System.out.println(msg);
    }

    @Override
    public String getName() {
        return "Console";
    }

    public void stop() {
        run = false;
        reader.interrupt();
    }
}