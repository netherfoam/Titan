package org.maxgamer.rs;

/**
 * @author netherfoam
 */
public class Assert {
    public static void isPositive(Number n, String message) {
        if(n == null || n.doubleValue() < 0) fail(message);
    }

    public static void isTrue(boolean b, String message) {
        if(!b) fail(message);
    }

    public static void notNull(Object o) {
        notNull(o, "Parameter may not be null");
    }

    public static void notNull(Object o, String message) {
        if(o == null) fail(message);
    }

    public static void equal(int a, int b) {
        if(a == b) return;

        fail(a, b);
    }

    public static void equal(long a, long b) {
        if(a == b) return;

        fail(a, b);
    }

    public static void equal(short a, short b) {
        if(a == b) return;

        fail(a, b);
    }

    public static void equal(Object a, Object b) {
        if(a == b) return;
        if(a == null || b == null) return;
        if(a.equals(b)) return;

        fail(a, b);
    }

    public static void fail(Object a, Object b) {
        fail("Assert " + a + " == " + b);
    }

    public static void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
