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

    @Test
    public void testBishopPair() {

        Board board = new Board();

        assertEquals(0, EvalBishop.evalBishopPair(board));

        board.setPos("1rb1r1k1/2q2pb1/pp1p4/2n1pPPQ/Pn1BP3/1NN4R/1PP4P/R5K1 b - -");

        assertEquals(-BISHOP_PAIR, EvalBishop.evalBishopPair(board));
    }

}
