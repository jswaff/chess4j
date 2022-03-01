package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalBishopTest {

    private final EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void testEvalBishop() {

        Board board = new Board();

        assertEquals(etv.terms[BISHOP_PST_IND + C1.value()], evalBishop(etv, board, C1, false));

        // test the symmetry
        assertEquals(evalBishop(etv, board, C1, false), evalBishop(etv, board, C8, false));
    }

    @Test
    public void testEvalBishop_endGame() {

        Board board = new Board();

        assertEquals(etv.terms[BISHOP_ENDGAME_PST_IND + C1.value()], evalBishop(etv, board, C1, true));

        // test the symmetry
        assertEquals(evalBishop(etv, board, C1, true), evalBishop(etv, board, C8, true));
    }
}
