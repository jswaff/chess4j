package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.EvalMaterial.*;
import static com.jamesswafford.chess4j.eval.EvalWeights.*;
import static com.jamesswafford.chess4j.eval.MaterialType.*;

public class EvalMaterialTest {

    @Test
    public void testEvalMaterial_initialPos() {
        assertEquals(0, evalMaterial(new EvalWeights(), new Board(), false));
    }

    @Test
    public void testEvalMaterial_pos1() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");

        assertEquals(weights.vals[BISHOP_VAL_IND], evalMaterial(weights, board, false));
    }

    @Test
    public void testEvalMaterial_pos2() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");

        // there are no pawns, so rook comes to life
        int expectedRookAdj = weights.vals[ROOK_KAUFMAN_ADJ] * 5; // 12 x 5 pawns
        assertEquals(weights.vals[BISHOP_VAL_IND] - weights.vals[ROOK_VAL_IND] + expectedRookAdj,
                evalMaterial(weights, board, false));

        assertEquals(weights.vals[BISHOP_VAL_IND] - weights.vals[ROOK_VAL_IND],
                evalMaterial(weights, board, true));
    }

    @Test
    public void testEvalNonPawnMaterial_adjustments() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

        int expectedKnightAdj = weights.vals[KNIGHT_KAUFMAN_ADJ] * -5;
        assertEquals(weights.vals[QUEEN_VAL_IND] + weights.vals[KNIGHT_VAL_IND] + expectedKnightAdj,
                evalNonPawnMaterial(weights, board, true, false));
        assertEquals(weights.vals[QUEEN_VAL_IND] + weights.vals[KNIGHT_VAL_IND],
                evalNonPawnMaterial(weights, board, true, true));

        int expectedRookAdj = weights.vals[ROOK_KAUFMAN_ADJ] * -4; // 12 x 4 pawns
        assertEquals(weights.vals[ROOK_VAL_IND] + expectedRookAdj + weights.vals[BISHOP_VAL_IND],
                evalNonPawnMaterial(weights, board, false, false));
        assertEquals(weights.vals[ROOK_VAL_IND] + weights.vals[BISHOP_VAL_IND],
                evalNonPawnMaterial(weights, board, false, true));
    }

    @Test
    public void testEvalNonPawnMaterial_bishopPair() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("8/kbb5/8/8/8/8/KBN5/8 b - - 0 1");

        // without pawns, the knight is a little less valuable
        int expectedKnightAdj = weights.vals[KNIGHT_KAUFMAN_ADJ] * -5;
        assertEquals(weights.vals[BISHOP_VAL_IND] + weights.vals[KNIGHT_VAL_IND] + expectedKnightAdj,
                evalNonPawnMaterial(weights, board, true, false));
        assertEquals(weights.vals[BISHOP_VAL_IND] + weights.vals[KNIGHT_VAL_IND],
                evalNonPawnMaterial(weights, board, true, true));

        assertEquals(weights.vals[BISHOP_VAL_IND]* 2L + weights.vals[BISHOP_PAIR_IND],
                evalNonPawnMaterial(weights, board, false, false));
        assertEquals(weights.vals[BISHOP_VAL_IND]* 2L,
                evalNonPawnMaterial(weights, board, false, true));
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
