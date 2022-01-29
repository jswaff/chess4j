package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalQueenTest {

    private final Board board = new Board();
    private final EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void testEvalQueen() {

        board.resetBoard();

        assertEquals(etv.terms[QUEEN_PST_IND + D1.value()], evalQueen(etv, board, D1, false));

        // test symmetry
        assertEquals(evalQueen(etv, board, D1, false), evalQueen(etv, board, D8, false));
    }

    @Test
    public void testEvalQueen_bankRankMate() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(etv.terms[QUEEN_PST_IND + C7.value()] + etv.terms[MAJOR_ON_7TH_IND] +
                        etv.terms[CONNECTED_MAJORS_ON_7TH_IND],
                evalQueen(etv, board, C7, false));
    }
}
