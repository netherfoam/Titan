package org.maxgamer.rs.command.commands;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.maxgamer.rs.command.CommandSender;
import org.maxgamer.rs.command.PlayerCommand;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.events.mob.persona.PersonaChatEvent;
import org.maxgamer.rs.interfaces.Interface;
import org.maxgamer.rs.interfaces.impl.primary.BookInterface;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;

/**
 * @author netherfoam
 */
public class JSCommand implements PlayerCommand, EventListener {
	private WeakHashMap<CommandSender, Program> scripters = new WeakHashMap<CommandSender, Program>(1);
	
	public JSCommand() {
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
	
	@EventHandler(priority = EventPriority.LOW, consumer=true)
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
		e.consume();
	}
	
	public class Program extends BookInterface {
		public Map<String, String> getClassNames() {
			ClassLoader loader = this.getClass().getClassLoader();
			
			try {
				Map<String, String> classNames = new HashMap<String, String>(256);
				while (loader != null) {
					Field f = ClassLoader.class.getDeclaredField("classes");
					f.setAccessible(true);
					
					@SuppressWarnings("unchecked")
					Vector<Class<?>> classes = (Vector<Class<?>>) f.get(loader);
					for (Class<?> c : new Vector<Class<?>>(classes)) {
						classNames.put(c.getSimpleName(), c.getName());
					}
					loader = loader.getParent();
				}
				return classNames;
			}
			catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
			catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		private StringBuilder text;
		private int line = 0;
		
		public Program(Player p) {
			super(p);
			text = new StringBuilder(128);
		}
		
		public void run() {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine js = manager.getEngineByName("JavaScript");
			
			getPlayer().sendMessage("Running script...");
			try {
				js.eval(text.toString());
				Invocable inv = (Invocable) js;
				
				Object o = inv.invokeFunction("run", this.getPlayer());
				if (o == null || o == Void.TYPE || o == bsh.Primitive.VOID) {
					getPlayer().sendMessage("...Complete!");
				}
				else {
					getPlayer().sendMessage("...Complete, Result: " + o.getClass().getName());
				}
			}
			catch (ScriptException e) {
				getPlayer().sendMessage("...Failed! Error evaluating script.");
				getPlayer().sendMessage(e.getMessage());
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
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
			this.append("function run(self){\n");
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
