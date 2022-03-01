package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvalTermsVectorTest {

    @Test
    public void testGetVal() {
        EvalTermsVector etv = new EvalTermsVector();
        assertEquals(24, etv.terms[EvalTermsVector.ROOK_OPEN_FILE_IND]);
        assertEquals(List.of(24), etv.getVals("ROOK_OPEN_FILE"));

        assertEquals(64, etv.getVals("ROOK_PST").size());

        assertEquals(List.of(-4), etv.getVals("DOUBLED_PAWN"));
    }

    @Test
    public void testSetVal() {
        EvalTermsVector etv = new EvalTermsVector();
        etv.setVal("MAJOR_ON_7TH", List.of(999));
        assertEquals(999, etv.terms[EvalTermsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), etv.getVals("MAJOR_ON_7TH"));
    }

    @Test
    public void copyConstructor() {
        EvalTermsVector etv = new EvalTermsVector();
        etv.setVal("MAJOR_ON_7TH", List.of(999));

        EvalTermsVector etv2 = new EvalTermsVector(etv);
        assertEquals(etv, etv2);

        assertEquals(999, etv2.terms[EvalTermsVector.MAJOR_ON_7TH_IND]);
        assertEquals(List.of(999), etv2.getVals("MAJOR_ON_7TH"));

        // should be able to change independently
        etv2.setVal("ROOK_OPEN_FILE", List.of(75));
        assertNotEquals(etv, etv2);
    }
}
