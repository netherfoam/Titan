package org.maxgamer.rs.command.commands;

import org.maxgamer.rs.command.CmdName;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.GenericCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.module.Module;
import org.maxgamer.rs.util.Log;

import java.io.File;

/**
 * Re/loads the given module. This does not work for hotswapping code.
 *
 * @author netherfoam
 */
@CmdName(names = {"modules"})
public class ModuleCmd implements GenericCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        //Lowercase first arg if given
        if (args.length > 0) args[0] = args[0].toLowerCase();

        if (args.length > 0) {
            //if (args[0].equals("list")) {
            if ("list".startsWith(args[0])) {
                sender.sendMessage("Active Modules (" + Core.getServer().getModules().getModules().size() + "):");
                for (Module m : Core.getServer().getModules().getModules()) {
                    sender.sendMessage(m.getName() + ": " + m.getJar());
                }
                return;
            } else if ("reload".startsWith(args[0])) {
                if (args.length < 2) {
                    sender.sendMessage("Arg0: 'reload'");
                    sender.sendMessage("Arg1: Module name (Specified in module.yml in .jar)");
                    return;
                }

                Module m = Core.getServer().getModules().getModule(args[1]);
                if (m == null) {
                    sender.sendMessage("Module not found.");
                    return;
                }

                Core.getServer().getModules().unload(m);
                String module = m.getJar().getName();
                try {
                    m = Core.getServer().getModules().load(new File("modules", module));
                    sender.sendMessage("Reloaded " + m.getName());
                    sender.sendMessage("Please be aware that some modules may break when reloading, if issues occur then you should restart.");
                } catch (Throwable e) {
                    sender.sendMessage("There was an issue reloading " + m.getName());
                    sender.sendMessage("Message: " + e.getMessage());
                    Log.warning("There was an issue reloading " + m.getName());
                    e.printStackTrace(); //To console
                }

                return;
            } else if ("load".startsWith(args[0])) {
                if (args.length < 2) {
                    sender.sendMessage("Arg0: 'load'");
                    sender.sendMessage("Arg1: Module.jar name (Eg MusicModule)");
                    return;
                }

                Module m = Core.getServer().getModules().getModule(args[1]);
                if (m != null) {
                    sender.sendMessage("Module already loaded!");
                    return;
                }
                File f = new File("modules", args[1] + ".jar");
                if (f.exists() == false) {
                    sender.sendMessage("Module " + args[1] + ".jar not found in modules/ folder");
                    return;
                }
                try {
                    m = Core.getServer().getModules().load(f);
                    sender.sendMessage("Loaded " + m.getName());
                } catch (Throwable e) {
                    sender.sendMessage("There was an issue reloading " + m.getName());
                    sender.sendMessage("Message: " + e.getMessage());
                    Log.warning("There was an issue reloading " + m.getName());
                    e.printStackTrace(); //To console
                }
                return;
            } else if ("unload".startsWith(args[0])) {
                if (args.length < 2) {
                    sender.sendMessage("Arg0: 'unload'");
                    sender.sendMessage("Arg1: Module name (Specified in module.yml in .jar)");
                    return;
                }

                Module m = Core.getServer().getModules().getModule(args[1]);
                if (m == null) {
                    sender.sendMessage("Module not found.");
                    return;
                }
                Core.getServer().getModules().unload(m);
                sender.sendMessage("Unloaded " + m.getName());
                sender.sendMessage("Please be aware that some modules may break when unloading, if issues occur then you should restart.");
                return;
            }
        }
        sender.sendMessage("Valid args are:");
        sender.sendMessage("Arg0: reload | load | unload | list");
    }

    @Override
    public int getRankRequired() {
        return Rights.ADMIN;
    }

}