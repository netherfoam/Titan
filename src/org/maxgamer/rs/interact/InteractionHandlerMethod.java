package org.maxgamer.rs.interact;

import co.paralleluniverse.fibers.SuspendExecution;
import com.google.common.collect.ImmutableMap;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Internal implementation of InteractionManager, this allows us to invoke a method on an InteractionHandler.
 * 
 * @author netherfoam
 */
public class InteractionHandlerMethod {
	@SuppressWarnings("unchecked")
	private static <T> Class<T> wrap(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
	}

	private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new ImmutableMap.Builder<Class<?>, Class<?>>()
			.put(boolean.class, Boolean.class).put(byte.class, Byte.class).put(char.class, Character.class)
			.put(double.class, Double.class).put(float.class, Float.class).put(int.class, Integer.class)
			.put(long.class, Long.class).put(short.class, Short.class).put(void.class, Void.class).build();

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
		if(m.getParameterTypes().length < 3){
			throw new IllegalArgumentException("Parameter must take at least 3 arguments: source, target, ...objects]");
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
	 * @param bag the arguments for the interaction
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
	public void run(Mob source, Interactable target, Object[] bag) throws SuspendExecution, NotHandledException {
		Class<?>[] types = method.getParameterTypes();
		
		if(types[0].isInstance(source) == false){
			if(this.debug) {
				Log.debug("The interaction method " + this + " declined arg0=" + source + " because it's not an instance of " + types[0]);
			}
			throw new NotHandledException();
		}
		if(types[1].isInstance(target) == false) {
			if(this.debug) {
				Log.debug("The interaction method " + this + " declined arg0=" + target + " because it's not an instance of " + types[1]);
			}
			throw new NotHandledException();
		}
		
		try{
			ArrayList<Object> args = new ArrayList<Object>(Arrays.asList(bag));
			
			// If we don't have the right number of arguments, skip it.
			if(types.length != args.size() + 2) {
				if(this.debug) {
					Log.debug("Incorrect number of arguments for " + this + ", given " + source + ", " + target + ", " + Arrays.toString(bag));
				}
				throw new NotHandledException();
			}
			Object[] sorted = new Object[types.length];
			sorted[0] = source;
			sorted[1] = target;
			
			for(Object arg : args){
				if(arg == null){
					throw new NullPointerException("Argument may not be null when invoking a new interaction");
				}
				
				for(int i = 2; i < sorted.length; i++){
					if(sorted[i] != null) continue; // Skip arguments we've already found 
					
					try{
						if(types[i].isPrimitive()){
							// Fields are probably declared as int but we'll always received the boxed version 'Integer'. Same applies
							// to other primitive types.
							types[i] = wrap(types[i]);
						}
						
						sorted[i] = types[i].cast(arg);
						// We found the slot for this argument!
						break;
					}
					catch(ClassCastException e){
						continue;
					}
				}
			}
			
			for(int i = 0; i < sorted.length; i++){
				if(sorted[i] == null){
					if(this.debug) {
						Log.debug("Incorrect arguments for " + this + ", given " + source + ", " + target + ", " + Arrays.toString(bag));
					}
					// We don't have the right argument!
					throw new NotHandledException();
				}
			}
			
			method.invoke(handler, sorted);
		} catch(InvocationTargetException e){
			if(e.getTargetException() instanceof NotHandledException) throw (NotHandledException) e.getTargetException();
			
			Log.warning("Exception occurred while running interaction handler. Arguments were " + source + ", " + target + ", " + Arrays.toString(bag));
			e.getTargetException().printStackTrace();
		} catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
	}
}