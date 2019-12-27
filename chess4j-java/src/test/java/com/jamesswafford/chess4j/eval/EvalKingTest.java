package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalKing.*;

public class EvalKingTest {

    private Board board = Board.INSTANCE;

    @Test
    public void testEvalKing() {

        // as odd as this position is, it has all material on the board.
        // therefore, there should be no scaling of king safety.

        board.setPos("rnbq1rk1/pppppppp/bn6/8/BN6/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");

//        assertEquals(KING_PST[G1.value()] + evalKingSafety(board, true),
//                evalKing(board, ));
    }

    @Test
    public void testEvalKing_endGame() {

    }

    @Test
    public void testEvalKingSafety_middleFiles() {

    }

    @Test
    public void testEvalKingSafety_kingSide() {

    }

    @Test
    public void testEvalKingSafety_queenSide() {

    }

}
