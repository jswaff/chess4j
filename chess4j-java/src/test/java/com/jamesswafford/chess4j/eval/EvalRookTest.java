package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalRookTest {

    private final EvalWeightsVector etv = new EvalWeightsVector();
    private final Board board = new Board();

    @Test
    public void testEvalRook() {

        board.resetBoard();

        assertEquals(etv.terms[ROOK_PST_IND + A1.value()], evalRook(etv, board, A1, false));

        // test the symmetry
        assertEquals(evalRook(etv, board, A1, false), evalRook(etv, board, A8, false));
    }

    @Test
    public void testEvalRook_endGame() {

        board.resetBoard();

        assertEquals(etv.terms[ROOK_ENDGAME_PST_IND + A1.value()], evalRook(etv, board, A1, true));

        // test the symmetry
        assertEquals(evalRook(etv, board, A1, true), evalRook(etv, board, A8, true));
    }

    @Test
    public void testEvalRook_bankRankMate() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(etv.terms[ROOK_PST_IND + F7.value()] + etv.terms[MAJOR_ON_7TH_IND] +
                        etv.terms[ROOK_OPEN_FILE_IND],
                evalRook(etv, board, F7, false));
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        board.setPos("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        assertEquals(etv.terms[ROOK_PST_IND + D1.value()] + etv.terms[ROOK_OPEN_FILE_IND],
                evalRook(etv, board, D8, false));
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        board.setPos("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(etv.terms[ROOK_PST_IND + C5.value()], evalRook(etv, board, C5, false));

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(etv.terms[ROOK_PST_IND + C5.value()] + etv.terms[ROOK_HALF_OPEN_FILE_IND],
                evalRook(etv, board, C5, false));
    }
}
