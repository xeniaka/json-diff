package com.ainexka.jsondiff.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DiffTest {

    @Test
    public void whenOffsetIsDifferent_thenEqualsReturnsFalse() {
        //given
        int offset1 = 1;
        int offset2 = 2;
        Diff diff1 = new Diff(offset1, 10);
        Diff diff2 = new Diff(offset2, 10);

        //when
        boolean result = diff1.equals(diff2);

        //then
        assertFalse(result);
    }

    @Test
    public void whenLengthIsDifferent_thenEqualsReturnsFalse() {
        //given
        int length1 = 10;
        int length2 = 22;
        Diff diff1 = new Diff(1, length1);
        Diff diff2 = new Diff(1, length2);

        //when
        boolean result = diff1.equals(diff2);

        //then
        assertFalse(result);
    }

    @Test
    public void whenOffsetAndLengthAreDifferent_thenEqualsReturnsFalse() {
        //given
        Diff diff1 = new Diff(1, 10);
        Diff diff2 = new Diff(2, 20);

        //when
        boolean result = diff1.equals(diff2);

        //then
        assertFalse(result);
    }

    @Test
    public void whenOffsetAndLengthAreTheSame_thenEqualsReturnsTrue() {
        //given
        Diff diff1 = new Diff(1, 10);
        Diff diff2 = new Diff(1, 10);

        //when
        boolean result = diff1.equals(diff2);

        //then
        assertTrue(result);
    }

    @Test
    public void whenObjectIsTheSame_thenEqualsReturnsTrue() {
        //given
        Diff diff = new Diff(1, 10);

        //when
        boolean result = diff.equals(diff);

        //then
        assertTrue(result);
    }

    @Test
    public void whenObjectsOfDifferentClasses_thenEqualsReturnsFalse() {
        //given
        Diff diff = new Diff(1, 10);
        Number number = new Integer(11);

        //when
        boolean result = diff.equals(number);

        //then
        assertFalse(result);
    }

    @Test
    public void whenObjectIsNotNull_thenReturnExpectedHashcode() {
        //given
        Diff diff = new Diff(1, 10);
        Number number = new Integer(11);

        //when
        int result = diff.hashCode();

        //then
        assertTrue(result > 0);
    }

}
