package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalPawn.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalPawnTest {

    private final EvalWeightsVector weights = new EvalWeightsVector();

    @Test
    public void testEvalPawn() {

        Board board = new Board();

        assertEquals(weights.weights[PAWN_PST_IND + E2.value()], evalPawn(weights, board, E2, false));

        // test the symmetry
        assertEquals(evalPawn(weights, board, E2, false), evalPawn(weights, board, E7, false));
    }

    @Test
    public void testEvalPawn_endGame() {

        Board board = new Board();

        assertEquals(weights.weights[PAWN_ENDGAME_PST_IND + E2.value()], evalPawn(weights, board, E2, true));

        // test the symmetry
        assertEquals(evalPawn(weights, board, E2, true), evalPawn(weights, board, E7, true));
    }

    @Test
    public void testEvalPawn_wiki3() {

        Board board = new Board("8/8/1PP2PbP/3r4/8/1Q5p/p5N1/k3K3 b - - 0 1");

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

        assertEquals(weights.weights[PAWN_PST_IND + B6.value()] + weights.weights[PASSED_PAWN_IND],
                evalPawn(weights, board, B6, false));

        // the black pawn on A2 is passed and isolated
        assertEquals(weights.weights[PAWN_PST_IND + A7.value()] + weights.weights[PASSED_PAWN_IND] +
                        weights.weights[ISOLATED_PAWN_IND],
                evalPawn(weights, board, A2, false));
    }
}
