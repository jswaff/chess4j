package com.jamesswafford.chess4j.board.squares;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class SquareTest {

    @Test
    public void getValue() {
        assertEquals(0, A8.value());
        assertEquals(7, H8.value());
        assertEquals(56, A1.value());
        assertEquals(63, H1.value());
        assertEquals(42, C3.value());
        assertEquals(36, E4.value());
    }

    @Test
    public void testToString() {
        assertEquals("a8", A8.toString());
        assertEquals("h8", H8.toString());
        assertEquals("a1", A1.toString());
        assertEquals("h1", H1.toString());
        assertEquals("c3", C3.toString());
        assertEquals("e4", E4.toString());
    }

    @Test
    public void testFileSquares() {
        assertTrue(fileSquares(FILE_B).contains(B2));
        assertFalse(fileSquares(FILE_B).contains(C2));
    }

    @Test
    public void testRankSquares() {
        assertTrue(Square.rankSquares(RANK_2).contains(B2));
        assertFalse(Square.rankSquares(RANK_2).contains(B3));
    }

    @Test
    public void testFlipVertical() {
        assertEquals(A8, A1.flipVertical());
        assertEquals(E2, E7.flipVertical());
        assertEquals(C6, C3.flipVertical());
        assertEquals(B5, B4.flipVertical());
        assertEquals(H7, H2.flipVertical());
    }

    @Test
    public void testFlipHorizontal() {
        assertEquals(A8, H8.flipHorizontal());
        assertEquals(E2, D2.flipHorizontal());
        assertEquals(C6, F6.flipHorizontal());
        assertEquals(B5, G5.flipHorizontal());
        assertEquals(H7, A7.flipHorizontal());
    }

    @Test
    public void testIsLightSquare() {
        assertTrue(A8.isLight());
        assertFalse(B8.isLight());
        assertTrue(C8.isLight());
        assertFalse(A7.isLight());
        assertTrue(B7.isLight());
        assertFalse(C7.isLight());
        assertTrue(A6.isLight());
        assertFalse(B6.isLight());
        assertTrue(C6.isLight());
    }

    @Test
    public void testValueOf() {
        assertEquals(Square.valueOf(FILE_A, RANK_8), Square.valueOf(0));
        assertEquals(Square.valueOf(FILE_H, RANK_8), Square.valueOf(7));
        assertEquals(Square.valueOf(FILE_A, RANK_1), Square.valueOf(56));
        assertEquals(Square.valueOf(FILE_H, RANK_1), Square.valueOf(63));
        assertEquals(Square.valueOf(FILE_C, RANK_3), Square.valueOf(42));
        assertEquals(Square.valueOf(FILE_E, RANK_4), Square.valueOf(36));

        assertEquals(A8, Square.valueOf(FILE_A, RANK_8));
        assertEquals(H8, Square.valueOf(FILE_H, RANK_8));
        assertEquals(A1, Square.valueOf(FILE_A, RANK_1));
        assertEquals(H1, Square.valueOf(FILE_H, RANK_1));
        assertEquals(C3, Square.valueOf(FILE_C, RANK_3));
        assertEquals(E4, Square.valueOf(FILE_E, RANK_4));

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
        assertEquals(1, A1.fileDistance(B1));
        assertEquals(7, A1.fileDistance(H8));
        assertEquals(7, A1.fileDistance(H4));
        assertEquals(1, B3.fileDistance(C7));
        assertEquals(7, H2.fileDistance(A3));
        assertEquals(4, F3.fileDistance(B8));
        assertEquals(0, E1.fileDistance(E2));
    }

    @Test
    public void testRankDistance() {
        assertEquals(0, A1.rankDistance(B1));
        assertEquals(3, A1.rankDistance(C4));
        assertEquals(1, H8.rankDistance(A7));
        assertEquals(7, G8.rankDistance(B1));
    }


    @Test
    public void testDistance() {
        assertEquals(0, E4.distance(E4));
        assertEquals(7, A1.distance(A8));
        assertEquals(3, H1.distance(E4));
        assertEquals(4, H8.distance(E4));
    }
}
