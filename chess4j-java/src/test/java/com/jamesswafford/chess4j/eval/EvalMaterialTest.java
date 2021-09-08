package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.EvalMaterial.*;
import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;

public class EvalMaterialTest {

    private final Board board = new Board();

    @Test
    public void testEvalMaterial_initialPos() {

        board.resetBoard();
        assertEquals(0, evalMaterial(board));
    }

    @Test
    public void testEvalMaterial_pos1() {

        board.setPos("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL, evalMaterial(board));
    }

    @Test
    public void testEvalMaterial_pos2() {

        board.setPos("6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL-ROOK_VAL, evalMaterial(board));

    }

    @Test
    public void testEvalNonPawnMaterial() {

        board.setPos("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

        assertEquals(QUEEN_VAL + KNIGHT_VAL, evalNonPawnMaterial(board, true));
        assertEquals(ROOK_VAL + BISHOP_VAL, evalNonPawnMaterial(board, false));
    }

    @Test
    public void testEvalPawnMaterial() {

        board.setPos("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

        assertEquals(0, evalPawnMaterial(board, true));
        assertEquals(PAWN_VAL, evalPawnMaterial(board, false));
    }

    @Test
    public void testBishopPair() {

        Board board = new Board();

        assertEquals(0, evalBishopPair(board));

        board.setPos("1rb1r1k1/2q2pb1/pp1p4/2n1pPPQ/Pn1BP3/1NN4R/1PP4P/R5K1 b - -");

        assertEquals(-BISHOP_PAIR, evalBishopPair(board));
    }

    @Test
    public void testEvalPiece() {

        assertEquals(QUEEN_VAL, evalPiece(BLACK_QUEEN));
        assertEquals(ROOK_VAL, evalPiece(WHITE_ROOK));
        assertEquals(BISHOP_VAL, evalPiece(BLACK_BISHOP));
        assertEquals(KNIGHT_VAL, evalPiece(WHITE_KNIGHT));
        assertEquals(PAWN_VAL, evalPiece(WHITE_PAWN));
    }
}
