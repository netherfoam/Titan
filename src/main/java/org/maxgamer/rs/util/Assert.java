package org.maxgamer.rs.util;

/**
 * Assertions class that allows us to make assumptions about code at runtime. This is to validate
 * external cores and assure that everything is behaving as expected. It is not supposed to be used
 * for test assertions.
 *
 * @author netherfoam
 */
public class Assert {
    /**
     * Fails if the given number is less than 0
     * @param n the number
     * @param message the message
     */
    public static void isPositive(Number n, String message) {
        if(n == null || n.doubleValue() < 0) fail(message);
    }

    /**
     * Fails if the given value is false
     * @param b the value
     * @param message the message
     */
    public static void isTrue(boolean b, String message) {
        if(!b) fail(message);
    }

    /**
     * Fails if the given value is true
     * @param b the value
     * @param message the message
     */
    public static void isFalse(boolean b, String message) {
        if(b) fail(message);
    }

    /**
     * Fails if the given object is null
     * @param o the object
     * @param message the message
     */
    public static void notNull(Object o, String message) {
        if(o == null) fail(message);
    }

    /**
     * Fails if the given numbers are not equal
     * @param a the expected number
     * @param b the actual number
     */
    public static void equal(int a, int b) {
        if(a == b) return;

        fail(a, b);
    }

    /**
     * Fails if the given numbers are not equal
     * @param a the expected number
     * @param b the actual number
     */
    public static void equal(long a, long b) {
        if(a == b) return;

        fail(a, b);
    }

    /**
     * Fails if the given numbers are not equal
     * @param a the expected number
     * @param b the actual number
     */
    public static void equal(short a, short b) {
        if(a == b) return;

        fail(a, b);
    }

    /**
     * Fails if the given objects are not equal. This uses deep comparison
     * @param a the expected object
     * @param b the actual object
     */
    public static void equal(Object a, Object b) {
        if(a == b) return;
        if(a == null || b == null) return;
        if(a.equals(b)) return;

        fail(a, b);
    }

    private static void fail(Object a, Object b) {
        fail("Assert " + a + " == " + b);
    }

    /**
     * Fails with the given message
     * @param message the message
     */
    public static void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
