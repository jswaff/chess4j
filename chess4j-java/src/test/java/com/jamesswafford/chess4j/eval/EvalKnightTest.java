package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

public class EvalKnightTest {

    private Board board = new Board();

    @Test
    public void testEvalKnight() {
        board.resetBoard();

        assertEquals(KNIGHT_PST[B1.value()] + KNIGHT_TROPISM * B1.distance(E8),
                evalKnight(board, true, B1));

        // test the symmetry
        assertEquals(evalKnight(board, true, B1),
                evalKnight(board, false, B8));
    }

}
