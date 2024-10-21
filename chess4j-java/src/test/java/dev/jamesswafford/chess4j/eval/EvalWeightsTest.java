package dev.jamesswafford.chess4j.eval;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvalWeightsTest {

    @Test
    public void testGetVal() {
        EvalWeights weights = new EvalWeights();
        assertEquals(30, weights.vals[EvalWeights.ROOK_OPEN_FILE_MG_IND]);
        assertEquals(List.of(30), weights.getVals("ROOK_OPEN_FILE_MG"));

        assertEquals(64, weights.getVals("ROOK_PST_MG").size());

        assertEquals(List.of(-7), weights.getVals("DOUBLED_PAWN_MG"));
        assertEquals(List.of(-8), weights.getVals("DOUBLED_PAWN_EG"));

        assertEquals(List.of(-2,-8,-12,-10,-10,-9,-7,-6,-3,0,2,4,6,6,9,11,13,12,16,14,12,11,5,4,2,1,0,0), 
            weights.getVals("QUEEN_MOBILITY_MG"));
    }

    @Test
    public void testSetVal() {
        EvalWeights weights = new EvalWeights();
        weights.setVal("MAJOR_ON_7TH_MG", List.of(999));
        assertEquals(999, weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND]);
        assertEquals(List.of(999), weights.getVals("MAJOR_ON_7TH_MG"));
    }

    @Test
    public void copyConstructor() {
        EvalWeights weights = new EvalWeights();
        weights.setVal("MAJOR_ON_7TH_MG", List.of(999));

        EvalWeights weights2 = new EvalWeights(weights);
        assertEquals(weights, weights2);

        assertEquals(999, weights2.vals[EvalWeights.MAJOR_ON_7TH_MG_IND]);
        assertEquals(List.of(999), weights2.getVals("MAJOR_ON_7TH_MG"));

        // should be able to change independently
        weights2.setVal("ROOK_OPEN_FILE_MG", List.of(75));
        assertNotEquals(weights, weights2);
    }
}
