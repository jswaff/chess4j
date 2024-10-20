package dev.jamesswafford.chess4j.board.squares;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SquareTest {

    @Test
    public void getValue() {
        Assert.assertEquals(0, Square.A8.value());
        Assert.assertEquals(7, Square.H8.value());
        Assert.assertEquals(56, Square.A1.value());
        Assert.assertEquals(63, Square.H1.value());
        Assert.assertEquals(42, Square.C3.value());
        Assert.assertEquals(36, Square.E4.value());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("a8", Square.A8.toString());
        Assert.assertEquals("h8", Square.H8.toString());
        Assert.assertEquals("a1", Square.A1.toString());
        Assert.assertEquals("h1", Square.H1.toString());
        Assert.assertEquals("c3", Square.C3.toString());
        Assert.assertEquals("e4", Square.E4.toString());
    }

    @Test
    public void testFileSquares() {
        assertTrue(Square.fileSquares(File.FILE_B).contains(Square.B2));
        assertFalse(Square.fileSquares(File.FILE_B).contains(Square.C2));
    }

    @Test
    public void testRankSquares() {
        assertTrue(Square.rankSquares(Rank.RANK_2).contains(Square.B2));
        assertFalse(Square.rankSquares(Rank.RANK_2).contains(Square.B3));
    }

    @Test
    public void testFlipVertical() {
        Assert.assertEquals(Square.A8, Square.A1.flipVertical());
        Assert.assertEquals(Square.E2, Square.E7.flipVertical());
        Assert.assertEquals(Square.C6, Square.C3.flipVertical());
        Assert.assertEquals(Square.B5, Square.B4.flipVertical());
        Assert.assertEquals(Square.H7, Square.H2.flipVertical());
    }

    @Test
    public void testFlipHorizontal() {
        Assert.assertEquals(Square.A8, Square.H8.flipHorizontal());
        Assert.assertEquals(Square.E2, Square.D2.flipHorizontal());
        Assert.assertEquals(Square.C6, Square.F6.flipHorizontal());
        Assert.assertEquals(Square.B5, Square.G5.flipHorizontal());
        Assert.assertEquals(Square.H7, Square.A7.flipHorizontal());
    }

    @Test
    public void testIsLightSquare() {
        assertTrue(Square.A8.isLight());
        assertFalse(Square.B8.isLight());
        assertTrue(Square.C8.isLight());
        assertFalse(Square.A7.isLight());
        assertTrue(Square.B7.isLight());
        assertFalse(Square.C7.isLight());
        assertTrue(Square.A6.isLight());
        assertFalse(Square.B6.isLight());
        assertTrue(Square.C6.isLight());
    }

    @Test
    public void testValueOf() {
        Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_8), Square.valueOf(0));
        Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_8), Square.valueOf(7));
        Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_1), Square.valueOf(56));
        Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_1), Square.valueOf(63));
        Assert.assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_3), Square.valueOf(42));
        Assert.assertEquals(Square.valueOf(File.FILE_E, Rank.RANK_4), Square.valueOf(36));

        Assert.assertEquals(Square.A8, Square.valueOf(File.FILE_A, Rank.RANK_8));
        Assert.assertEquals(Square.H8, Square.valueOf(File.FILE_H, Rank.RANK_8));
        Assert.assertEquals(Square.A1, Square.valueOf(File.FILE_A, Rank.RANK_1));
        Assert.assertEquals(Square.H1, Square.valueOf(File.FILE_H, Rank.RANK_1));
        Assert.assertEquals(Square.C3, Square.valueOf(File.FILE_C, Rank.RANK_3));
        Assert.assertEquals(Square.E4, Square.valueOf(File.FILE_E, Rank.RANK_4));

    }

    @Test
    public void testHashCodes() {
        Set<Integer> hashCodes = new HashSet<>();

        List<Square> squares = Square.allSquares();
        assertEquals(64, squares.size());

        for (Square sq : squares) {
            hashCodes.add(sq.hashCode());
        }

        assertEquals(64, hashCodes.size());
    }

    @Test
    public void testFileDistance() {
        Assert.assertEquals(1, Square.A1.fileDistance(Square.B1));
        Assert.assertEquals(7, Square.A1.fileDistance(Square.H8));
        Assert.assertEquals(7, Square.A1.fileDistance(Square.H4));
        Assert.assertEquals(1, Square.B3.fileDistance(Square.C7));
        Assert.assertEquals(7, Square.H2.fileDistance(Square.A3));
        Assert.assertEquals(4, Square.F3.fileDistance(Square.B8));
        Assert.assertEquals(0, Square.E1.fileDistance(Square.E2));
    }

    @Test
    public void testRankDistance() {
        Assert.assertEquals(0, Square.A1.rankDistance(Square.B1));
        Assert.assertEquals(3, Square.A1.rankDistance(Square.C4));
        Assert.assertEquals(1, Square.H8.rankDistance(Square.A7));
        Assert.assertEquals(7, Square.G8.rankDistance(Square.B1));
    }


    @Test
    public void testDistance() {
        Assert.assertEquals(0, Square.E4.distance(Square.E4));
        Assert.assertEquals(7, Square.A1.distance(Square.A8));
        Assert.assertEquals(3, Square.H1.distance(Square.E4));
        Assert.assertEquals(4, Square.H8.distance(Square.E4));
    }
}
