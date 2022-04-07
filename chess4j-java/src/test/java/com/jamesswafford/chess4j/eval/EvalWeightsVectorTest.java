package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvalWeightsVectorTest {

    @Test
    public void testGetVal() {
        EvalWeightsVector weights = new EvalWeightsVector();
        assertEquals(24, weights.weights[EvalWeightsVector.ROOK_OPEN_FILE_IND]);
        assertEquals(List.of(24), weights.getVals("ROOK_OPEN_FILE"));

        assertEquals(64, weights.getVals("ROOK_PST").size());

        assertEquals(List.of(-4), weights.getVals("DOUBLED_PAWN"));
    }

    @Test
    public void testSetVal() {
        EvalWeightsVector weights = new EvalWeightsVector();
        weights.setVal("MAJOR_ON_7TH", List.of(999));
        assertEquals(999, weights.weights[EvalWeightsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), weights.getVals("MAJOR_ON_7TH"));
    }

    @Test
    public void copyConstructor() {
        EvalWeightsVector weights = new EvalWeightsVector();
        weights.setVal("MAJOR_ON_7TH", List.of(999));

        EvalWeightsVector weights2 = new EvalWeightsVector(weights);
        assertEquals(weights, weights2);

        assertEquals(999, weights2.weights[EvalWeightsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), weights2.getVals("MAJOR_ON_7TH"));

        // should be able to change independently
        weights2.setVal("ROOK_OPEN_FILE", List.of(75));
        assertNotEquals(weights, weights2);
    }
}
