package org.maxgamer.rs.model.interact;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.interact.use.Use;
import org.maxgamer.rs.util.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Internal implementation of InteractionManager, this allows us to invoke a method on an InteractionHandler.
 * 
 * @author netherfoam
 */
public class InteractionHandlerMethod {
	/**
	 * The method to invoke
	 */
	private Method method;
	
	/**
	 * The object to run the handler on
	 */
	private InteractionHandler handler;
	
	/**
	 * True if the debug flag was specified on the @Interact annotation
	 */
	private boolean debug = false;

	/**
	 * Constructs a new InteractionHandler method, based on the given Method
	 * @param handler the handler object
	 * @param m the method to invoke on the handler
	 * @param debug true if the call should be debugged if it is skipped
	 */
	public InteractionHandlerMethod(InteractionHandler handler, Method m, boolean debug) {
		if(m.getParameterTypes().length != 3){
			throw new IllegalArgumentException("Parameter must take at 3 arguments: source, target, usage]");
		}
		this.handler = handler;
		this.method = m;
		this.debug = debug;
	}
	
	/**
	 * Returns true if this method should be debugged
	 * @return true if this method should be debugged
	 */
	public boolean isDebug() {
		return this.debug;
	}
	
	/**
	 * @return class_name.method_name(..)
	 */
	@Override
	public String toString(){
		return handler.getClass().getCanonicalName() + "." + method.getName() + "(..)";
	}

	/**
	 * @return The method to execute
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return The handler to execute the method on
	 */
	public InteractionHandler getHandler() {
		return handler;
	}

	/**
	 * Attempts to run this InteractionHandlerMethod.
	 * @param source the source interactor
	 * @param target the interacted target
	 * @param usage the arguments for the interaction
	 * 
	 * This will attempt to arrange the arguments in a fitting order, if their types do not correspond with
	 * the method header. However, if two arguments are of the same type, then their order will be as given
	 * by the method invocation.  Eg run(Player, ItemStack, [int, String]) would call the method
	 * public void run(Player p, ItemStack i, String option, int id), or
	 * public void run(Player p, ItemStack i, int id, String option).
	 * 
	 *  If an exception occurs during this handler, this does NOT raise a NotHandledException, prints the trace
	 *  and returns.
	 * 
	 * @throws SuspendExecution required for Action parking 
	 * @throws NotHandledException if this handler doesn't handle the specified argument types or values
	 */
	public void run(Mob source, Interactable target, Use usage) throws SuspendExecution, NotHandledException {
		Class<?>[] types = method.getParameterTypes();
		
		if(types[0].isInstance(source) == false){
			if(this.debug) {
				Log.debug("The interaction method " + this + " declined arg0=" + source + " because it's not an instance of " + types[0]);
			}
			throw new NotHandledException();
		}

		if(types[1].isInstance(target) == false) {
			if(this.debug) {
				Log.debug("The interaction method " + this + " declined arg1=" + target + " because it's not an instance of " + types[1]);
			}
			throw new NotHandledException();
		}

		if(types[2].isInstance(usage) == false) {
			if(this.debug) {
				Log.debug("The interaction method " + this + " declined arg2=" + usage + " because it's not an instance of " + types[2]);
			}
			throw new NotHandledException();
		}
		
		try{
			method.invoke(handler, source, target, usage);
		} catch(InvocationTargetException e){
			if(e.getTargetException() instanceof NotHandledException) throw (NotHandledException) e.getTargetException();
			
			Log.warning("Exception occurred while running interaction handler. Arguments were " + source + ", " + target + ", " + usage);
			Throwable inner = e.getTargetException();
			while(inner != null){
				inner.printStackTrace();
				inner = inner.getCause();
			}
		} catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
	}
}