package com.jamesswafford.chess4j.board.squares;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Direction.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;

public class DirectionTest {

    @Test
    public void directionTo() {

        System.out.println(getDirectionTo(E4, E6));

        assertEquals(North.getInstance(), getDirectionTo(E4, E6).get());

        assertEquals(South.getInstance(), getDirectionTo(E6, E4).get());

        assertEquals(West.getInstance(), getDirectionTo(H4, E4).get());

        assertEquals(East.getInstance(), getDirectionTo(A4, E4).get());

        assertEquals(SouthWest.getInstance(), getDirectionTo(H7, E4).get());

        assertEquals(SouthEast.getInstance(), getDirectionTo(C6, E4).get());

        assertEquals(NorthWest.getInstance(), getDirectionTo(H1, E4).get());

        assertEquals(NorthEast.getInstance(), getDirectionTo(B1, E4).get());

        assertFalse(getDirectionTo(E4, A1).isPresent());
    }

}
