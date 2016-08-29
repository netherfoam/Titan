package org.maxgamer.rs.util;

/**
 * Argument validation class that has convenience methods for validating input arguments
 *
 * @author netherfoam
 */
public class Prove {
    /**
     * Validates the given object. If o is null, this throws an IllegalArgumentException with the given message
     * @param o the object
     * @param message the message
     * @throws IllegalArgumentException if the object is null
     */
    public static void isNotNull(Object o, String message) throws IllegalArgumentException {
        if(o == null) fail(message);
    }

    /**
     * Validates the given object. If s is null or empty, this throws an IllegalArgumentException with the given message
     * @param s the string
     * @param message the message
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void isNotEmpty(String s, String message) throws IllegalArgumentException {
        isNotNull(s, message);
        if(s.isEmpty()) fail(message);
    }

    /**
     * Validates the given iterable. If it is null or empty, this throws an IllegalArgumentException with the given message
     * @param it the iterable object
     * @param message the message
     * @throws IllegalArgumentException if the iterable object is null or empty
     */
    public static void isNotEmpty(Iterable<?> it, String message) throws IllegalArgumentException {
        isNotNull(it, message);
        if(!it.iterator().hasNext()) fail(message);
    }

    /**
     * Validates the given boolean. If b is false, this throws an IllegalArgumentException with the given message
     * @param b the boolean
     * @param message the message
     * @throws IllegalArgumentException if the boolean is false
     */
    public static void isTrue(boolean b, String message) throws IllegalArgumentException {
        if(!b) fail(message);
    }

    /**
     * Validates the given boolean. If b is true, this throws an IllegalArgumentException with the given message
     * @param b the boolean
     * @param message the message
     * @throws IllegalArgumentException if the boolean is true
     */
    public static void isFalse(boolean b, String message) throws IllegalArgumentException {
        isTrue(!b, message);
    }

    /**
     * Compares the given object. If they are the same reference (a == b) this succeeds. If they are equal (a.equals(b))
     * this returns false.
     * @param a the first object
     * @param b the second object
     * @param message the message
     * @throws IllegalArgumentException if a != b && !a.equals(b)
     */
    public static void isEqual(Object a, Object b, String message) throws IllegalArgumentException {
        if(a == b) return;
        if(a == null || b == null) fail(message);
        if(!a.equals(b)) fail(message);
    }

    /**
     * Convenience method that throws an {@link IllegalArgumentException} with the given message
     * @param message the message
     * @throws IllegalArgumentException the exception
     */
    private static void fail(String message) throws IllegalArgumentException {
        throw new IllegalArgumentException(message);
    }
}
