package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKing.*;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalKingTest {

    private final Board board = new Board();
    private final EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void testEvalKing_middleGame() {

        // as odd as this position is, it has all material on the board.
        // therefore, there should be no scaling of king safety.

        board.setPos("rnbq1rk1/pppppppp/bn6/8/BN6/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        assertEquals(etv.terms[KING_PST_IND + G1.value()] + evalKingSafety(etv, board, true),
                evalKing(etv, board, G1, false));

        assertEquals(etv.terms[KING_PST_IND + G1.value()] + evalKingSafety(etv, board, false),
                evalKing(etv, board, G8, false));
    }

    @Test
    public void testEvalKing_endGame() {

        board.setPos("8/p3k3/8/8/8/8/4K3/8 w - - 0 1");

        assertEquals(etv.terms[KING_ENDGAME_PST_IND + E2.value()], evalKing(etv, board, E2, true));

        // test the symmetry
        assertEquals(evalKing(etv, board, E2, true), evalKing(etv, board, E7, true));
    }

    @Test
    public void testEvalKingSafety_middleFiles() {

        // initial position then e3... no penalty
        board.setPos("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        assertEquals(0, evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));

        // open file for both
        board.setPos("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        assertEquals(etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(etv, board, true));

        assertEquals(etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(etv, board, false));

        // remove both queens.  open e file.  put black on D8
        // white should be penalized but black is not
        board.setPos("rnbk1bnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNB1KBNR b KQ - 0 1");

        assertEquals(etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND],
                evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));
    }

    @Test
    public void testEvalKingSafety_kingSide() {

        board.setPos("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");

        assertEquals(0, evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));

        // white pawn on F3
        board.setPos("rnbq1rk1/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

        assertEquals(etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND],
                evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));

        // white pawn on G4
        board.setPos("rnbq1rk1/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");

        assertEquals(etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND],
                evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));
    }

    @Test
    public void testEvalKingSafety_queenSide() {

        // pawn on C3
        board.setPos("1krq1bnr/pppppppp/8/8/8/2P5/PP1PPPPP/1KRQ1BNR w - - 0 1");

        assertEquals(etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND],
                evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));

        // white pawn on B4
        board.setPos("1krq1bnr/pppppppp/8/8/1P6/8/P1PPPPPP/1KRQ1BNR w - - 0 1");

        assertEquals(etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND],
                evalKingSafety(etv, board, true));
        assertEquals(0, evalKingSafety(etv, board, false));

        // black pawn on A4
        board.setPos("1krq1bnr/1ppppppp/8/8/p7/8/PPPPPPPP/1KRQ1BNR b - - 0 1");

        assertEquals(0, evalKingSafety(etv, board, true));
        assertEquals(etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND]/2,
                evalKingSafety(etv, board, false));
    }

}
