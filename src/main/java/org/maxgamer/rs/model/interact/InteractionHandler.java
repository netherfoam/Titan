package org.maxgamer.rs.model.interact;

/**
 * This interface represents an InteractionHandler. An InteractionHandler should specify one or more @Interact methods and
 * implement this interface. This API allows developers to flexibly write Action code. The methods implemented with @Interact
 * must throw NotHandledException if the method does not handle the interaction, and may throw SuspendExecution if the Action
 * lasts for more than a single tick.
 *
 * @author netherfoam
 */
public interface InteractionHandler {

}