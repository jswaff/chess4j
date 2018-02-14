package com.jamesswafford.chess4j.board;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.Rank;

public class RankTest {

    @Test
    public void testValue() {
        Assert.assertEquals(0, Rank.RANK_8.getValue());
        Assert.assertEquals(1, Rank.RANK_7.getValue());
        Assert.assertEquals(2, Rank.RANK_6.getValue());
        Assert.assertEquals(3, Rank.RANK_5.getValue());
        Assert.assertEquals(4, Rank.RANK_4.getValue());
        Assert.assertEquals(5, Rank.RANK_3.getValue());
        Assert.assertEquals(6, Rank.RANK_2.getValue());
        Assert.assertEquals(7, Rank.RANK_1.getValue());

        Assert.assertEquals(Rank.RANK_8, Rank.rank(0));
        Assert.assertEquals(Rank.RANK_7, Rank.rank(1));
        Assert.assertEquals(Rank.RANK_6, Rank.rank(2));
        Assert.assertEquals(Rank.RANK_5, Rank.rank(3));
        Assert.assertEquals(Rank.RANK_4, Rank.rank(4));
        Assert.assertEquals(Rank.RANK_3, Rank.rank(5));
        Assert.assertEquals(Rank.RANK_2, Rank.rank(6));
        Assert.assertEquals(Rank.RANK_1, Rank.rank(7));
    }

    @Test
    public void testLabel() {
        Assert.assertEquals("8", Rank.RANK_8.getLabel());
        Assert.assertEquals("7", Rank.RANK_7.getLabel());
        Assert.assertEquals("6", Rank.RANK_6.getLabel());
        Assert.assertEquals("5", Rank.RANK_5.getLabel());
        Assert.assertEquals("4", Rank.RANK_4.getLabel());
        Assert.assertEquals("3", Rank.RANK_3.getLabel());
        Assert.assertEquals("2", Rank.RANK_2.getLabel());
        Assert.assertEquals("1", Rank.RANK_1.getLabel());

        Assert.assertEquals(Rank.RANK_8, Rank.rank("8"));
        Assert.assertEquals(Rank.RANK_7, Rank.rank("7"));
        Assert.assertEquals(Rank.RANK_6, Rank.rank("6"));
        Assert.assertEquals(Rank.RANK_5, Rank.rank("5"));
        Assert.assertEquals(Rank.RANK_4, Rank.rank("4"));
        Assert.assertEquals(Rank.RANK_3, Rank.rank("3"));
        Assert.assertEquals(Rank.RANK_2, Rank.rank("2"));
        Assert.assertEquals(Rank.RANK_1, Rank.rank("1"));
    }

    @Test
    public void testNorth() {
        Assert.assertFalse(Rank.RANK_8.north().isPresent());
        Assert.assertEquals(Rank.RANK_8, Rank.RANK_7.north().get());
        Assert.assertEquals(Rank.RANK_7, Rank.RANK_6.north().get());
        Assert.assertEquals(Rank.RANK_6, Rank.RANK_5.north().get());
        Assert.assertEquals(Rank.RANK_5, Rank.RANK_4.north().get());
        Assert.assertEquals(Rank.RANK_4, Rank.RANK_3.north().get());
        Assert.assertEquals(Rank.RANK_3, Rank.RANK_2.north().get());
        Assert.assertEquals(Rank.RANK_2, Rank.RANK_1.north().get());
    }

    @Test
    public void testSouth() {
        Assert.assertEquals(Rank.RANK_7, Rank.RANK_8.south().get());
        Assert.assertEquals(Rank.RANK_6, Rank.RANK_7.south().get());
        Assert.assertEquals(Rank.RANK_5, Rank.RANK_6.south().get());
        Assert.assertEquals(Rank.RANK_4, Rank.RANK_5.south().get());
        Assert.assertEquals(Rank.RANK_3, Rank.RANK_4.south().get());
        Assert.assertEquals(Rank.RANK_2, Rank.RANK_3.south().get());
        Assert.assertEquals(Rank.RANK_1, Rank.RANK_2.south().get());
        Assert.assertFalse(Rank.RANK_1.south().isPresent());
    }

    @Test
    public void testNorthOf() {
        Assert.assertTrue(Rank.RANK_8.northOf(Rank.RANK_1));
        Assert.assertTrue(Rank.RANK_7.northOf(Rank.RANK_5));
        Assert.assertFalse(Rank.RANK_4.northOf(Rank.RANK_4));
        Assert.assertFalse(Rank.RANK_3.northOf(Rank.RANK_6));
    }

    @Test
    public void testSouthOf() {
        Assert.assertTrue(Rank.RANK_1.southOf(Rank.RANK_8));
        Assert.assertTrue(Rank.RANK_5.southOf(Rank.RANK_7));
        Assert.assertFalse(Rank.RANK_4.southOf(Rank.RANK_4));
        Assert.assertFalse(Rank.RANK_6.southOf(Rank.RANK_3));
    }

    @Test
    public void testDistance() {
        Assert.assertEquals(0, Rank.RANK_7.distance(Rank.RANK_7));
        Assert.assertEquals(7, Rank.RANK_1.distance(Rank.RANK_8));
        Assert.assertEquals(7, Rank.RANK_8.distance(Rank.RANK_1));
        Assert.assertEquals(2, Rank.RANK_7.distance(Rank.RANK_5));
    }

    @Test
    public void testFlip() {
        Assert.assertEquals(Rank.RANK_1, Rank.RANK_8.flip());
        Assert.assertEquals(Rank.RANK_2, Rank.RANK_7.flip());
        Assert.assertEquals(Rank.RANK_3, Rank.RANK_6.flip());
        Assert.assertEquals(Rank.RANK_4, Rank.RANK_5.flip());
        Assert.assertEquals(Rank.RANK_5, Rank.RANK_4.flip());
        Assert.assertEquals(Rank.RANK_6, Rank.RANK_3.flip());
        Assert.assertEquals(Rank.RANK_7, Rank.RANK_2.flip());
        Assert.assertEquals(Rank.RANK_8, Rank.RANK_1.flip());
    }
}
