package com.chromosundrift.vectorbrat;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for empirical record of essential invariants in libraries, the universe and everything.
 */
public class SanityTest {

    @Test
    public void enumSetMutability() {
        EnumSet<TestEnum> maybeMutable = EnumSet.noneOf(TestEnum.class);
        assertEquals(0, maybeMutable.size());
        maybeMutable.add(TestEnum.FOO);
        assertTrue(maybeMutable.contains(TestEnum.FOO));
    }

    private enum TestEnum {
        FOO, BAR, BAZ
    }
}
