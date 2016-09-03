package org.maxgamer.rs.model.requirement;

/**
 * @param <T> the type this requirement uses to check for passing
 * @author Albert Beaupre
 */
public interface Requirement<T> {

    /**
     * Returns true if the specified {@code type} passes this
     * {@code Requirement} . If the specified {@code type} does not pass this
     * {@code Requirement}, return false.
     *
     * @param type the type to check for passing
     * @return true if the type passes; return false otherwise
     */
    boolean passes(T type);

}
