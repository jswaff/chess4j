package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

public class EvalBishopTest {

    Board board = Board.INSTANCE;

    @Test
    public void testEvalBishop() {
        assertEquals(BISHOP_PST[C1.value()], evalBishop(true, C1));

        // test the symmetry
        assertEquals(evalBishop(true, C1), evalBishop(false, C8));
    }


}
