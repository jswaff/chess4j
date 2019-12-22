package com.jamesswafford.chess4j.board.squares;

import org.junit.Test;

import static junit.framework.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class RankTest {

    @Test
    public void value() {
        assertEquals(0, RANK_8.getValue());
        assertEquals(1, RANK_7.getValue());
        assertEquals(2, RANK_6.getValue());
        assertEquals(3, RANK_5.getValue());
        assertEquals(4, RANK_4.getValue());
        assertEquals(5, RANK_3.getValue());
        assertEquals(6, RANK_2.getValue());
        assertEquals(7, RANK_1.getValue());

        assertEquals(RANK_8, rank(0));
        assertEquals(RANK_7, rank(1));
        assertEquals(RANK_6, rank(2));
        assertEquals(RANK_5, rank(3));
        assertEquals(RANK_4, rank(4));
        assertEquals(RANK_3, rank(5));
        assertEquals(RANK_2, rank(6));
        assertEquals(RANK_1, rank(7));
    }

    @Test
    public void label() {
        assertEquals("8", RANK_8.getLabel());
        assertEquals("7", RANK_7.getLabel());
        assertEquals("6", RANK_6.getLabel());
        assertEquals("5", RANK_5.getLabel());
        assertEquals("4", RANK_4.getLabel());
        assertEquals("3", RANK_3.getLabel());
        assertEquals("2", RANK_2.getLabel());
        assertEquals("1", RANK_1.getLabel());

        assertEquals(RANK_8, rank("8"));
        assertEquals(RANK_7, rank("7"));
        assertEquals(RANK_6, rank("6"));
        assertEquals(RANK_5, rank("5"));
        assertEquals(RANK_4, rank("4"));
        assertEquals(RANK_3, rank("3"));
        assertEquals(RANK_2, rank("2"));
        assertEquals(RANK_1, rank("1"));
    }

    @Test
    public void north() {
        assertFalse(RANK_8.north().isPresent());
        assertEquals(RANK_8, RANK_7.north().get());
        assertEquals(RANK_7, RANK_6.north().get());
        assertEquals(RANK_6, RANK_5.north().get());
        assertEquals(RANK_5, RANK_4.north().get());
        assertEquals(RANK_4, RANK_3.north().get());
        assertEquals(RANK_3, RANK_2.north().get());
        assertEquals(RANK_2, RANK_1.north().get());
    }

    @Test
    public void south() {
        assertEquals(RANK_7, RANK_8.south().get());
        assertEquals(RANK_6, RANK_7.south().get());
        assertEquals(RANK_5, RANK_6.south().get());
        assertEquals(RANK_4, RANK_5.south().get());
        assertEquals(RANK_3, RANK_4.south().get());
        assertEquals(RANK_2, RANK_3.south().get());
        assertEquals(RANK_1, RANK_2.south().get());
        assertFalse(RANK_1.south().isPresent());
    }

    @Test
    public void northOf() {
        assertTrue(RANK_8.northOf(RANK_1));
        assertTrue(RANK_7.northOf(RANK_5));
        assertFalse(RANK_4.northOf(RANK_4));
        assertFalse(RANK_3.northOf(RANK_6));
    }

    @Test
    public void southOf() {
        assertTrue(RANK_1.southOf(RANK_8));
        assertTrue(RANK_5.southOf(RANK_7));
        assertFalse(RANK_4.southOf(RANK_4));
        assertFalse(RANK_6.southOf(RANK_3));
    }

    @Test
    public void distance() {
        assertEquals(0, RANK_7.distance(RANK_7));
        assertEquals(7, RANK_1.distance(RANK_8));
        assertEquals(7, RANK_8.distance(RANK_1));
        assertEquals(2, RANK_7.distance(RANK_5));
    }

    @Test
    public void flip() {
        assertEquals(RANK_1, RANK_8.flip());
        assertEquals(RANK_2, RANK_7.flip());
        assertEquals(RANK_3, RANK_6.flip());
        assertEquals(RANK_4, RANK_5.flip());
        assertEquals(RANK_5, RANK_4.flip());
        assertEquals(RANK_6, RANK_3.flip());
        assertEquals(RANK_7, RANK_2.flip());
        assertEquals(RANK_8, RANK_1.flip());
    }
}
