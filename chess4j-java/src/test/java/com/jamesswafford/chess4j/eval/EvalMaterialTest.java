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
import static com.jamesswafford.chess4j.eval.MaterialType.*;

public class EvalMaterialTest {

    @Test
    public void testEvalMaterial_initialPos() {
        assertEquals(0, evalMaterial(new Board()));
    }

    @Test
    public void testEvalMaterial_pos1() {

        Board board = new Board("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL, evalMaterial(board));
    }

    @Test
    public void testEvalMaterial_pos2() {

        Board board = new Board("6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL-ROOK_VAL-60, // rook adj 12 x 5 pawns
                evalMaterial(board));
    }

    @Test
    public void testEvalNonPawnMaterial() {

        Board board = new Board("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

        assertEquals(QUEEN_VAL + KNIGHT_VAL - 30, // 30 = knight adj 6 x 5 pawns
                evalNonPawnMaterial(board, true));

        assertEquals(ROOK_VAL + 48 + BISHOP_VAL,  // 48 = rook adj 12 x 4 pawns
                evalNonPawnMaterial(board, false));
    }

    @Test
    public void testEvalPawnMaterial() {

        Board board = new Board("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

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

    @Test
    public void testCalculateMaterialType() {
        testMaterialTypeWithSymmetry(Board.INITIAL_POS, OTHER, OTHER);
        testMaterialTypeWithSymmetry("k7/8/8/8/8/8/8/K7 w - -", KK, KK);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/8/K7 w - -", KKN, KNK);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/K7 w - -", KKB, KBK);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/P7/K7 w - -", KPKB, KBKP);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/P7/K7 w - -", KPKN, KNKP);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/8/KN6 w - -", KNKN, KNKN);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/KB6 w - -", KBKB, KBKB);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/KN6 w - -", KNKB, KBKN);
        testMaterialTypeWithSymmetry("knn5/8/8/8/8/8/8/K7 w - -", KKNN, KNNK);
    }

    private void testMaterialTypeWithSymmetry(String fen, MaterialType materialType, MaterialType invType) {
        Board board = new Board(fen);
        assertEquals(materialType, calculateMaterialType(board));
        board.flipVertical();
        assertEquals(invType, calculateMaterialType(board));
    }
}
