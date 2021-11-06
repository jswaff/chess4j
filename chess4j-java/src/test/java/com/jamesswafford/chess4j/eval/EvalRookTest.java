package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalRook.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

public class EvalRookTest {

    private final EvalTermsVector etv = new EvalTermsVector();
    private final Board board = new Board();

    @Test
    public void testEvalRook() {

        board.resetBoard();

        assertEquals(ROOK_PST[A1.value()], evalRook(etv, board, A1));

        // test the symmetry
        assertEquals(evalRook(etv, board, A1), evalRook(etv, board, A8));
    }

    @Test
    public void testEvalRook_bankRankMate() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(ROOK_PST[F7.value()] + MAJOR_ON_7TH + ROOK_OPEN_FILE,
                evalRook(etv, board, F7));
    }

    @Test
    public void testEvalRook_rookOpenFile() {

        board.setPos("3r3k/8/8/8/8/8/8/7K b - - 0 1");

        assertEquals(ROOK_PST[D1.value()] + ROOK_OPEN_FILE,
                evalRook(etv, board, D8));
    }

    @Test
    public void testEvalRook_rookOpenHalfOpenFile() {

        // friendly pawn but no enemy -- not half open (or open)
        board.setPos("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(ROOK_PST[C5.value()], evalRook(etv, board, C5));

        // enemy pawn on C makes it half open
        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");

        assertEquals(ROOK_PST[C5.value()] + ROOK_HALF_OPEN_FILE,
                evalRook(etv, board, C5));
    }
}
