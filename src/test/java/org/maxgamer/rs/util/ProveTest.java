package org.maxgamer.rs.util;

import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class ProveTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        Prove.isNotNull(null, "expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyString() {
        Prove.isNotEmpty("", "expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCollection() {
        Prove.isNotEmpty(Collections.emptyList(), "expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEqual() {
        Prove.isEqual(UUID.randomUUID(), UUID.randomUUID(), "expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFalse() {
        Prove.isFalse(true, "expected");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrue() {
        Prove.isTrue(false, "expected");
    }
}
