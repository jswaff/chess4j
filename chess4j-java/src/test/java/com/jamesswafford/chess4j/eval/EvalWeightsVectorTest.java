package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvalWeightsVectorTest {

    @Test
    public void testGetVal() {
        EvalWeightsVector etv = new EvalWeightsVector();
        assertEquals(24, etv.weights[EvalWeightsVector.ROOK_OPEN_FILE_IND]);
        assertEquals(List.of(24), etv.getVals("ROOK_OPEN_FILE"));

        assertEquals(64, etv.getVals("ROOK_PST").size());

        assertEquals(List.of(-4), etv.getVals("DOUBLED_PAWN"));
    }

    @Test
    public void testSetVal() {
        EvalWeightsVector etv = new EvalWeightsVector();
        etv.setVal("MAJOR_ON_7TH", List.of(999));
        assertEquals(999, etv.weights[EvalWeightsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), etv.getVals("MAJOR_ON_7TH"));
    }

    @Test
    public void copyConstructor() {
        EvalWeightsVector etv = new EvalWeightsVector();
        etv.setVal("MAJOR_ON_7TH", List.of(999));

        EvalWeightsVector etv2 = new EvalWeightsVector(etv);
        assertEquals(etv, etv2);

        assertEquals(999, etv2.weights[EvalWeightsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), etv2.getVals("MAJOR_ON_7TH"));

        // should be able to change independently
        etv2.setVal("ROOK_OPEN_FILE", List.of(75));
        assertNotEquals(etv, etv2);
    }
}
