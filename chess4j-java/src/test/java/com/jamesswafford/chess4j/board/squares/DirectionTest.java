package com.jamesswafford.chess4j.board.squares;

import org.junit.Test;

import static junit.framework.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Direction.*;

public class DirectionTest {

    @Test
    public void directionTo() {

        System.out.println(getDirectionTo(Square.valueOf(FILE_E, RANK_4), Square.valueOf(FILE_E, RANK_6)));

        assertEquals(North.getInstance(),
                getDirectionTo(Square.valueOf(FILE_E, RANK_4), Square.valueOf(FILE_E, RANK_6)).get());

        assertEquals(South.getInstance(),
                getDirectionTo(Square.valueOf(FILE_E, RANK_6), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(West.getInstance(),
                getDirectionTo(Square.valueOf(FILE_H, RANK_4), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(East.getInstance(),
                getDirectionTo(Square.valueOf(FILE_A, RANK_4), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(SouthWest.getInstance(),
                getDirectionTo(Square.valueOf(FILE_H, RANK_7), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(SouthEast.getInstance(),
                getDirectionTo(Square.valueOf(FILE_C, RANK_6), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(NorthWest.getInstance(),
                getDirectionTo(Square.valueOf(FILE_H, RANK_1), Square.valueOf(FILE_E, RANK_4)).get());

        assertEquals(NorthEast.getInstance(),
                getDirectionTo(Square.valueOf(FILE_B, RANK_1), Square.valueOf(FILE_E, RANK_4)).get());

        assertFalse(getDirectionTo(Square.valueOf(FILE_E, RANK_4), Square.valueOf(FILE_A, RANK_1)).isPresent());
    }

}
