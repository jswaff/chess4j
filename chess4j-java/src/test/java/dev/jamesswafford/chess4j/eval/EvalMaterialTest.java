package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import org.junit.Assert;
import org.junit.Test;

public class EvalMaterialTest {

    @Test
    public void testEvalMaterial_initialPos() {
        Assert.assertEquals(0, EvalMaterial.evalMaterial(new EvalWeights(), new Board(), false));
    }

    @Test
    public void testEvalMaterial_pos1() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");

        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND], EvalMaterial.evalMaterial(weights, board, false));
    }

    @Test
    public void testEvalMaterial_pos2() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");

        // there are no pawns, so rook comes to life
        int expectedRookAdj = weights.vals[EvalWeights.ROOK_KAUFMAN_ADJ] * 5; // 12 x 5 pawns
        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND] - weights.vals[EvalWeights.ROOK_VAL_IND] + expectedRookAdj,
                EvalMaterial.evalMaterial(weights, board, false));

        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND] - weights.vals[EvalWeights.ROOK_VAL_IND],
                EvalMaterial.evalMaterial(weights, board, true));
    }

    @Test
    public void testEvalNonPawnMaterial_adjustments() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");

        int expectedKnightAdj = weights.vals[EvalWeights.KNIGHT_KAUFMAN_ADJ] * -5;
        Assert.assertEquals(weights.vals[EvalWeights.QUEEN_VAL_IND] + weights.vals[EvalWeights.KNIGHT_VAL_IND] + expectedKnightAdj,
                EvalMaterial.evalNonPawnMaterial(weights, board, true, false));
        Assert.assertEquals(weights.vals[EvalWeights.QUEEN_VAL_IND] + weights.vals[EvalWeights.KNIGHT_VAL_IND],
                EvalMaterial.evalNonPawnMaterial(weights, board, true, true));

        int expectedRookAdj = weights.vals[EvalWeights.ROOK_KAUFMAN_ADJ] * -4; // 12 x 4 pawns
        Assert.assertEquals(weights.vals[EvalWeights.ROOK_VAL_IND] + expectedRookAdj + weights.vals[EvalWeights.BISHOP_VAL_IND],
                EvalMaterial.evalNonPawnMaterial(weights, board, false, false));
        Assert.assertEquals(weights.vals[EvalWeights.ROOK_VAL_IND] + weights.vals[EvalWeights.BISHOP_VAL_IND],
                EvalMaterial.evalNonPawnMaterial(weights, board, false, true));
    }

    @Test
    public void testEvalNonPawnMaterial_bishopPair() {
        EvalWeights weights = new EvalWeights();
        Board board = new Board("8/kbb5/8/8/8/8/KBN5/8 b - - 0 1");

        // without pawns, the knight is a little less valuable
        int expectedKnightAdj = weights.vals[EvalWeights.KNIGHT_KAUFMAN_ADJ] * -5;
        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND] + weights.vals[EvalWeights.KNIGHT_VAL_IND] + expectedKnightAdj,
                EvalMaterial.evalNonPawnMaterial(weights, board, true, false));
        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND] + weights.vals[EvalWeights.KNIGHT_VAL_IND],
                EvalMaterial.evalNonPawnMaterial(weights, board, true, true));

        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND]* 2L + weights.vals[EvalWeights.BISHOP_PAIR_IND],
                EvalMaterial.evalNonPawnMaterial(weights, board, false, false));
        Assert.assertEquals(weights.vals[EvalWeights.BISHOP_VAL_IND]* 2L,
                EvalMaterial.evalNonPawnMaterial(weights, board, false, true));
    }

    @Test
    public void testCalculateMaterialType() {
        testMaterialTypeWithSymmetry(Board.INITIAL_POS, MaterialType.OTHER, MaterialType.OTHER);
        testMaterialTypeWithSymmetry("k7/8/8/8/8/8/8/K7 w - -", MaterialType.KK, MaterialType.KK);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/8/K7 w - -", MaterialType.KKN, MaterialType.KNK);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/K7 w - -", MaterialType.KKB, MaterialType.KBK);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/P7/K7 w - -", MaterialType.KPKB, MaterialType.KBKP);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/P7/K7 w - -", MaterialType.KPKN, MaterialType.KNKP);
        testMaterialTypeWithSymmetry("kn6/8/8/8/8/8/8/KN6 w - -", MaterialType.KNKN, MaterialType.KNKN);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/KB6 w - -", MaterialType.KBKB, MaterialType.KBKB);
        testMaterialTypeWithSymmetry("kb6/8/8/8/8/8/8/KN6 w - -", MaterialType.KNKB, MaterialType.KBKN);
        testMaterialTypeWithSymmetry("knn5/8/8/8/8/8/8/K7 w - -", MaterialType.KKNN, MaterialType.KNNK);
    }

    private void testMaterialTypeWithSymmetry(String fen, MaterialType materialType, MaterialType invType) {
        Board board = new Board(fen);
        Assert.assertEquals(materialType, EvalMaterial.calculateMaterialType(board));
        board.flipVertical();
        Assert.assertEquals(invType, EvalMaterial.calculateMaterialType(board));
    }
}
