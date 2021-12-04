package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalTermsVectorFileUtil {

    public static void write(EvalTermsVector etv, Writer writer) throws IOException {
        writeLine(writer, "KING_SAFETY_PAWN_ONE_AWAY", etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND]);
        writeLine(writer, "KING_SAFETY_PAWN_TWO_AWAY", etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND]);
        writeLine(writer, "KING_SAFETY_PAWN_FAR_AWAY", etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND]);
        writeLine(writer, "KING_SAFETY_MIDDLE_OPEN_FILE", etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND]);
        writePST(writer, "KING_PST_IND", Arrays.copyOfRange(etv.terms, KING_PST_IND, KING_PST_IND+64));
        writePST(writer, "KING_ENDGAME_PST", Arrays.copyOfRange(etv.terms, KING_ENDGAME_PST_IND, KING_ENDGAME_PST_IND+64));
        writePST(writer, "BISHOP_PST", Arrays.copyOfRange(etv.terms, BISHOP_PST_IND, BISHOP_PST_IND+64));
        writePST(writer, "KNIGHT_PST", Arrays.copyOfRange(etv.terms, KNIGHT_PST_IND, KNIGHT_PST_IND+64));
        writeLine(writer, "KNIGHT_TROPISM", etv.terms[KNIGHT_TROPISM_IND]);
        writePST(writer, "ROOK_PST", Arrays.copyOfRange(etv.terms, ROOK_PST_IND, ROOK_PST_IND+64));
        writeLine(writer, "ROOK_OPEN_FILE", etv.terms[ROOK_OPEN_FILE_IND]);
        writeLine(writer, "ROOK_HALF_OPEN_FILE", etv.terms[ROOK_HALF_OPEN_FILE_IND]);
        writePST(writer, "QUEEN_PST", Arrays.copyOfRange(etv.terms, QUEEN_PST_IND, QUEEN_PST_IND+64));
        writeLine(writer, "MAJOR_ON_7TH", etv.terms[MAJOR_ON_7TH_IND]);
        writeLine(writer, "CONNECTED_MAJORS_ON_7TH", etv.terms[CONNECTED_MAJORS_ON_7TH_IND]);
        writePST(writer, "PAWN_PST", Arrays.copyOfRange(etv.terms, PAWN_PST_IND, PAWN_PST_IND+64));
        writeLine(writer, "PASSED_PAWN", etv.terms[PASSED_PAWN_IND]);
        writeLine(writer, "ISOLATED_PAWN", etv.terms[ISOLATED_PAWN_IND]);
        writeLine(writer, "DOUBLED_PAWN", etv.terms[DOUBLED_PAWN_IND]);
    }

    public static void read(EvalTermsVector etv, BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static void writeLine(Writer writer, String key, Integer val) throws IOException {
        writer.write(key + ": " + val + "\n");
    }

    private static void writePST(Writer writer, String key, int[] val) throws IOException {
        writer.write(key + ": [\n");
        for (int r=0;r<8;r++) {
            writer.write("\t");
            for (int c=0;c<8;c++) {
                writer.write(String.format("%4d ", val[r*8+c]));
            }
            writer.write("\n");
        }
        writer.write("]\n");
    }
}
