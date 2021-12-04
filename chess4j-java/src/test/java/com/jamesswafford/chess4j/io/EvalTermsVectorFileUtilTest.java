package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.junit.Test;

import java.io.*;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;
import static org.junit.Assert.*;

public class EvalTermsVectorFileUtilTest {

    EvalTermsVector etv = new EvalTermsVector();

    @Test
    public void writeTest() throws IOException {
        StringWriter sw = new StringWriter();
        EvalTermsVectorFileUtil.write(etv, sw);
        System.out.println(sw);
        String config = sw.toString();
        assertContains(config, "KING_SAFETY_PAWN_ONE_AWAY");
        assertContains(config, "KING_SAFETY_PAWN_TWO_AWAY");
        assertContains(config, "KING_SAFETY_PAWN_FAR_AWAY");
        assertContains(config, "KING_SAFETY_MIDDLE_OPEN_FILE");
        assertContains(config, "KING_PST_IND");
        assertContains(config, "KING_ENDGAME_PST");
        assertContains(config, "BISHOP_PST");
        assertContains(config, "KNIGHT_PST");
        assertContains(config, "KNIGHT_TROPISM");
        assertContains(config, "ROOK_PST");
        assertContains(config, "ROOK_OPEN_FILE");
        assertContains(config, "ROOK_HALF_OPEN_FILE");
        assertContains(config, "QUEEN_PST");
        assertContains(config, "MAJOR_ON_7TH");
        assertContains(config, "CONNECTED_MAJORS_ON_7TH");
        assertContains(config, "PAWN_PST");
        assertContains(config, "PASSED_PAWN");
        assertContains(config, "ISOLATED_PAWN");
        assertContains(config, "DOUBLED_PAWN");
    }

    @Test
    public void readTest() throws IOException {
        // test three default values, then read a file in and ensure those values changed

        EvalTermsVector etv = new EvalTermsVector();
        assertEquals(etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND], -10);
        assertEquals(etv.terms[ROOK_PST_IND+63], 0);
        assertEquals(etv.terms[DOUBLED_PAWN_IND], -10);

        File file = new File(getClass().getResource("/eval.config").getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            EvalTermsVectorFileUtil.read(etv, br);
        }

        //assertEquals(etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND], -12);
        //assertEquals(etv.terms[ROOK_PST_IND+63], 9);
        //assertEquals(etv.terms[DOUBLED_PAWN_IND], -11);
    }

    private static void assertContains(String config, String key) {
        assertTrue(config.contains(key + ": "));
    }

}
