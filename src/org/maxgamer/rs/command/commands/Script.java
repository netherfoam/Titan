package org.maxgamer.rs.command.commands;

import java.util.WeakHashMap;

import org.maxgamer.event.EventHandler;
import org.maxgamer.event.EventListener;
import org.maxgamer.event.EventPriority;
import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.events.mob.persona.PersonaChatEvent;
import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.interfaces.impl.primary.BookInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.UtilEvalError;

/**
 * @author netherfoam
 */
public class Script implements PlayerCommand, EventListener {
	private WeakHashMap<CommandSender, Program> scripters = new WeakHashMap<CommandSender, Program>(1);
	
	public Script() {
		Core.getServer().getEvents().register(this);
	}
	
	@Override
	public void execute(Player sender, String[] args) {
		if (scripters.containsKey(sender)) {
			scripters.remove(sender);
			Interface iface = sender.getWindow().getInterface(BookInterface.INTERFACE_ID);
			if (iface != null && iface instanceof Program) {
				sender.getWindow().close(iface);
			}
			sender.sendMessage("Cancelled scripting.");
			return;
		}
		else {
			Program prog = new Program(sender);
			scripters.put(sender, prog);
			sender.getWindow().open(prog);
			sender.sendMessage("Now scripting");
		}
	}
	
	@Override
	public int getRankRequired() {
		return Rights.ADMIN;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onChat(PersonaChatEvent e) {
		if (e.getMob() instanceof Player == false) {
			return;
		}
		
		Program p = scripters.get(e.getMob());
		if (p == null) return;
		
		e.setCancelled(true);
		String s = e.getMessage();
		p.append(s);
		
		if (p.isReady()) {
			p.run();
		}
	}
	
	public class Program extends BookInterface {
		private StringBuilder text;
		private int line = 0;
		
		public Program(Player p) {
			super(p);
			text = new StringBuilder(128);
		}
		
		public void run() {
			Interpreter i = new Interpreter();
			getPlayer().sendMessage("Running script...");
			try {
				StringBuilder imports = new StringBuilder(8192);
				for (String imp : Core.CLASS_LOADER.getClassNames().values()) {
					imports.append("import " + imp + ";\n");
				}
				i.eval(imports.toString() + text.toString());
				Object o = i.getNameSpace().getMethod("run", new Class[] { Player.class }).invoke(new Object[] { this.getPlayer() }, i);
				if (o == null || o == Void.TYPE || o == bsh.Primitive.VOID) {
					getPlayer().sendMessage("...Complete!");
				}
				else {
					getPlayer().sendMessage("...Complete, Result: " + o.getClass().getName());
				}
			}
			catch (EvalError e) {
				getPlayer().sendMessage("...Failed! Error evaluating script.");
				getPlayer().sendMessage(e.getMessage());
				e.printStackTrace();
			}
			catch (UtilEvalError e) {
				getPlayer().sendMessage("...Failed! Error evaluating script.");
				getPlayer().sendMessage(e.getMessage());
				e.printStackTrace();
			}
			finally {
				getPlayer().getWindow().close(this);
				scripters.remove(getPlayer());
			}
		}
		
		@Override
		public void onOpen() {
			setTitle("Scripting");
			super.onOpen();
			this.append("void run(Player self){\n");
		}
		
		public void append(String line) {
			this.text.append(line + "\n");
			
			for (String s : line.split("\\n")) {
				if (s.length() <= 30) {
					s = s.replaceAll("<", "<lt>").replaceAll(">", "<gt>");
					setLine(this.line, s);
				}
				else {
					setLine(this.line, s.substring(0, 30).replaceAll("<", "<lt>").replaceAll(">", "<gt>"));
					setLine(this.line + 15, s.substring(30, Math.min(s.length(), 60)).replaceAll("<", "<lt>").replaceAll(">", "<gt>"));
				}
				
				this.line++;
				
				if (this.line % 15 == 0) {
					this.line = 0;
					setText(""); //Clears all lines
				}
			}
		}
		
		public boolean isReady() {
			int nested = 0;
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == '{') {
					nested++;
				}
				else if (c == '}') {
					nested--;
				}
			}
			
			//All opening blocks must be closed.
			if (nested == 0) return true;
			return false;
		}
	}
}
