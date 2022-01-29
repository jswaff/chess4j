package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKnight.*;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalKnightTest {

    private final Board board = new Board();
    private final EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void testEvalKnight() {
        board.resetBoard();

        assertEquals(etv.terms[KNIGHT_PST_IND + B1.value()] + etv.terms[KNIGHT_TROPISM_IND] * B1.distance(E8),
                evalKnight(etv, board, B1, false));

        // test the symmetry
        assertEquals(evalKnight(etv, board, B1, false), evalKnight(etv, board, B8, false));
    }

}
