package org.maxgamer.rs.structure;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * Runs an action for specific arguments. The {@code ArgumentalRunnable} class is useful for an
 * {@code enum} field variable to access an outside dynamic (non-static) variable.
 *
 * @author Albert Beaupre
 */
public interface ArgumentalRunnable {

    /**
     * Executes an action for the specified {@code args}.
     *
     * @param args the arguments being handled upon execution
     */
    void run(Object... args) throws SuspendExecution;

}
