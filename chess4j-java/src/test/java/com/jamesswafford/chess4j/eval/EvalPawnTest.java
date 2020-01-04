package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalPawn.*;

public class EvalPawnTest {

    private Board board = new Board();

    @Test
    public void testEvalPawn() {

        board.resetBoard();

        assertEquals(PAWN_PST[E2.value()], evalPawn(board, E2));

        // test the symmetry
        assertEquals(evalPawn(board, E2), evalPawn(board, E7));
    }

    @Test
    public void testEvalPawn_wiki3() {

        board.setPos("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - - 0 1");

        /*
        - - - - - - - -
        - - - - - - - -
        - P P - - P b P    black to move
        - - - r - - - -    no ep
        - - - - - - - -    no castling rights
        - Q - - - - - p
        p - - - - - N -
        k - - - K - - -
        */

        assertEquals(PAWN_PST[B6.value()] + PASSED_PAWN,
                evalPawn(board, B6));

        // the black pawn on A2 is passed and isolated
        assertEquals(PAWN_PST[A7.value()] + PASSED_PAWN + ISOLATED_PAWN,
                evalPawn(board, A2));
    }
}
