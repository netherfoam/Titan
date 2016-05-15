package org.maxgamer.rs.interact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation specifies that the following method is to be used to handle an interaction between
 * an Entity and an Interactable target, possibly with some arguments.
 * 
 * @author netherfoam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interact {
	/**
	 * Enabling this will print debug information regarding why the interaction was selected or not selected.
	 * This is useful if your code does not trigger when it is expected, but shouldn't be used in published
	 * code. This defaults to false.
	 * 
	 * @return true if this interaction invocation is to be debugged, false otherwise
	 */
	public boolean debug() default false;
}
