package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;

import static com.jamesswafford.chess4j.eval.EvalWeightsVector.*;

public class EvalRookTest {

    private final EvalWeightsVector weights = new EvalWeightsVector();

    @Test
    public void testEvalRook() {

        Board board = new Board();

        assertEquals(weights.weights[ROOK_PST_IND + A1.value()], evalRook(weights, board, A1, false));

        // test the symmetry
        assertEquals(evalRook(weights, board, A1, false), evalRook(weights, board, A8, false));
    }

    @Test
    public void testEvalRook_endGame() {

        Board board = new Board();

        assertEquals(weights.weights[ROOK_ENDGAME_PST_IND + A1.value()], evalRook(weights, board, A1, true));

        // test the symmetry
        assertEquals(evalRook(weights, board, A1, true), evalRook(weights, board, A8, true));
    }

    @Test
    public void testEvalRook_bankRankMate() {

        Board board = new Board("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + F7.value()] + weights.weights[MAJOR_ON_7TH_IND] +
                        weights.weights[ROOK_OPEN_FILE_IND],
                evalRook(weights, board, F7, false));
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        Board board = new Board("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + D1.value()] + weights.weights[ROOK_OPEN_FILE_IND],
                evalRook(weights, board, D8, false));
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        Board board = new Board("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + C5.value()], evalRook(weights, board, C5, false));

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(weights.weights[ROOK_PST_IND + C5.value()] + weights.weights[ROOK_HALF_OPEN_FILE_IND],
                evalRook(weights, board, C5, false));
    }
}
