package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

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

    private static void assertContains(String config, String key) {
        assertTrue(config.contains(key + ": "));
    }

}
