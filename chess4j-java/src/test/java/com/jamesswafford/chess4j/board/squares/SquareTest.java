package com.jamesswafford.chess4j.board.squares;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;

import static junit.framework.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class SquareTest {

    @Test
    public void getValue() {
        assertEquals(0, Square.valueOf(FILE_A, RANK_8).value());
        assertEquals(7, Square.valueOf(FILE_H, RANK_8).value());
        assertEquals(56, Square.valueOf(FILE_A, RANK_1).value());
        assertEquals(63, Square.valueOf(FILE_H, RANK_1).value());
        assertEquals(42, Square.valueOf(FILE_C, RANK_3).value());
        assertEquals(36, Square.valueOf(FILE_E, RANK_4).value());
    }

    @Test
    public void testToString() {
        assertEquals("a8", Square.valueOf(FILE_A, RANK_8).toString());
        assertEquals("h8", Square.valueOf(FILE_H, RANK_8).toString());
        assertEquals("a1", Square.valueOf(FILE_A, RANK_1).toString());
        assertEquals("h1", Square.valueOf(FILE_H, RANK_1).toString());
        assertEquals("c3", Square.valueOf(FILE_C, RANK_3).toString());
        assertEquals("e4", Square.valueOf(FILE_E, RANK_4).toString());
    }

    @Test
    public void testFileSquares() {
        assertTrue(Square.fileSquares(FILE_B).contains(Square.valueOf(FILE_B, RANK_2)));
        assertFalse(Square.fileSquares(FILE_B).contains(Square.valueOf(FILE_C, RANK_2)));
    }

    @Test
    public void testRankSquares() {
        Assert.assertTrue(Square.rankSquares(RANK_2).contains(Square.valueOf(FILE_B, RANK_2)));
        Assert.assertFalse(Square.rankSquares(RANK_2).contains(Square.valueOf(FILE_B, RANK_3)));
    }

    @Test
    public void testFlipVertical() {
        assertEquals(Square.valueOf(FILE_A, RANK_8), Square.valueOf(FILE_A, RANK_1).flipVertical());
        assertEquals(Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_7).flipVertical());
        assertEquals(Square.valueOf(FILE_C, RANK_6), Square.valueOf(FILE_C, RANK_3).flipVertical());
        assertEquals(Square.valueOf(FILE_B, RANK_5), Square.valueOf(FILE_B, RANK_4).flipVertical());
        assertEquals(Square.valueOf(FILE_H, RANK_7), Square.valueOf(FILE_H, RANK_2).flipVertical());
    }

    @Test
    public void testFlipHorizontal() {
        assertEquals(Square.valueOf(FILE_A, RANK_8), Square.valueOf(FILE_H, RANK_8).flipHorizontal());
        assertEquals(Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_D, RANK_2).flipHorizontal());
        assertEquals(Square.valueOf(FILE_C, RANK_6), Square.valueOf(FILE_F, RANK_6).flipHorizontal());
        assertEquals(Square.valueOf(FILE_B, RANK_5), Square.valueOf(FILE_G, RANK_5).flipHorizontal());
        assertEquals(Square.valueOf(FILE_H, RANK_7), Square.valueOf(FILE_A, RANK_7).flipHorizontal());
    }

    @Test
    public void testIsLightSquare() {
        assertTrue(Square.valueOf(FILE_A, RANK_8).isLight());
        assertFalse(Square.valueOf(FILE_B, RANK_8).isLight());
        assertTrue(Square.valueOf(FILE_C, RANK_8).isLight());
        assertFalse(Square.valueOf(FILE_A, RANK_7).isLight());
        assertTrue(Square.valueOf(FILE_B, RANK_7).isLight());
        assertFalse(Square.valueOf(FILE_C, RANK_7).isLight());
        assertTrue(Square.valueOf(FILE_A, RANK_6).isLight());
        assertFalse(Square.valueOf(FILE_B, RANK_6).isLight());
        assertTrue(Square.valueOf(FILE_C, RANK_6).isLight());
    }

    @Test
    public void testValueOf() {
        assertEquals(Square.valueOf(FILE_A, RANK_8), Square.valueOf(0));
        assertEquals(Square.valueOf(FILE_H, RANK_8), Square.valueOf(7));
        assertEquals(Square.valueOf(FILE_A, RANK_1), Square.valueOf(56));
        assertEquals(Square.valueOf(FILE_H, RANK_1), Square.valueOf(63));
        assertEquals(Square.valueOf(FILE_C, RANK_3), Square.valueOf(42));
        assertEquals(Square.valueOf(FILE_E, RANK_4), Square.valueOf(36));
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
        assertEquals(1,Square.valueOf(FILE_A, RANK_1).fileDistance(Square.valueOf(FILE_B, RANK_1)));
        assertEquals(7,Square.valueOf(FILE_A, RANK_1).fileDistance(Square.valueOf(FILE_H, RANK_8)));
        assertEquals(7,Square.valueOf(FILE_A, RANK_1).fileDistance(Square.valueOf(FILE_H, RANK_4)));
        assertEquals(1,Square.valueOf(FILE_B, RANK_3).fileDistance(Square.valueOf(FILE_C, RANK_7)));
        assertEquals(7,Square.valueOf(FILE_H, RANK_2).fileDistance(Square.valueOf(FILE_A, RANK_3)));
        assertEquals(4,Square.valueOf(FILE_F, RANK_3).fileDistance(Square.valueOf(FILE_B, RANK_8)));
        assertEquals(0,Square.valueOf(FILE_E, RANK_1).fileDistance(Square.valueOf(FILE_E, RANK_2)));
    }

    @Test
    public void testRankDistance() {
        assertEquals(0,Square.valueOf(FILE_A, RANK_1).rankDistance(Square.valueOf(FILE_B, RANK_1)));
        assertEquals(3,Square.valueOf(FILE_A, RANK_1).rankDistance(Square.valueOf(FILE_C, RANK_4)));
        assertEquals(1,Square.valueOf(FILE_H, RANK_8).rankDistance(Square.valueOf(FILE_A, RANK_7)));
        assertEquals(7,Square.valueOf(FILE_G, RANK_8).rankDistance(Square.valueOf(FILE_B, RANK_1)));
    }


    @Test
    public void testDistance() {
        assertEquals(0,Square.valueOf(FILE_E, RANK_4).distance(Square.valueOf(FILE_E, RANK_4)));
        assertEquals(7,Square.valueOf(FILE_A, RANK_1).distance(Square.valueOf(FILE_A, RANK_8)));
        assertEquals(3,Square.valueOf(FILE_H, RANK_1).distance(Square.valueOf(FILE_E, RANK_4)));
        assertEquals(4,Square.valueOf(FILE_H, RANK_8).distance(Square.valueOf(FILE_E, RANK_4)));
    }
}
