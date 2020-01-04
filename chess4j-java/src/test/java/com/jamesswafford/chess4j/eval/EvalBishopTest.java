package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

public class EvalBishopTest {

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        assertEquals(BISHOP_PST[C1.value()], evalBishop(board, C1));

        // test the symmetry
        assertEquals(evalBishop(board, C1), evalBishop(board, C8));
    }


}
