package com.jamesswafford.chess4j.eval;

import io.vavr.Tuple2;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class EvalTermsVector {

    public int[] terms = new int[] {
              2,  // KING_SAFETY_PAWN_ONE_AWAY
            -31,  // KING_SAFETY_PAWN_TWO_AWAY
            -31,  // KING_SAFETY_PAWN_FAR_AWAY
            -79,  // KING_SAFETY_MIDDLE_OPEN_FILE

              7, 41, 14, 47, 36, 30, 19, 58,     // KING_PST
             37, 41, 36,-21, 14, 30, 33,-19,
              6, 43, -9,-17,-14,  9, 29, 13,
            -53,  3,-18,-60,-40,-14, 14, -7,
            -52,-15,-32,-30,-34,-42,-37,  5,
            -19,  8, 10,-10,-38,-40,-31,-21,
             -6,-13,  0,-30,-41,-25, 16, 26,
              6, 26, 20,-86,-21,-48, 38, 44,

            -17, 70, 43, 74, 66, 57,  8, 21,   // KING_ENDGAME_PST
            40, 75, 76, 41, 63, 73, 72, 17,
            50, 83, 51, 53, 56, 61, 75, 50,
            19, 57, 52, 25, 39, 52, 60, 40,
            -8, 41, 29, 31, 30, 34, 34, 17,
            -4, -9, 20,  7, 20, 21, 14,  6,
            -39,  5,-15, 13,  9,  9,-11,-25,
            -12,-29,-15, -4,-33, -1,-31,-55,

            -22, -15,  5,-35, -5,  5,  8,-13,  // BISHOP_PST
            -23, -19, -7, -4,  3, -5, -9,-32,
            -7,   1, -2,  0,  7, 25, 26,  4,
            -13,  -7,  9, 15,  1,  2,-14, -3,
            -22,  -1,  3,  9,  6,-10, -8,-19,
            -10, -12, -3, -9,  0,  2,-10,-25,
            -4, -22,-24,-22,-11, -5,-13, -9,
            -57, -15,-48,-22,-25,-38,-21,-56,

            -82,-16,  8, -3, -7, -7,-17,-75,    // KNIGHT_PST
            -25, -6, 16, 15, -3,-15,  7,-70,
            -9,  3, 25, 18, 30, 33, 21,-15,
            -7,  9, 22, 42, 12, 27,-14, -9,
            -19,  8,  6, -4,  0, 13, 16,-21,
            -39,-33,-14,  7, 14,-10, -3,-43,
            -56,-41,-17,-10,-13,-32,-31, -3,
            -72,-27,-56,-45,-45,-41,-28,-98,

             -3,  // KNIGHT TROPISM

            2, 10, 10, 19, 14,  6, -2,  2,    // ROOK_PST
            4,  7, 10, 11,  6, 13, 12, 10,
            -4,  2,  4, 11, 10, 11,  7,  7,
            -6, -3, -9, -2,-21,  0,  4,-15,
            -31, -3,-21,-21,-13,-13,-20,-16,
            -38,-29,-37,-26,-29,-27,-22,-33,
            -53,-35,-35,-37,-33,-23,-31,-39,
            -35,-31,-28,-23,-22,-25,-41,-43,

             24,  // ROOK_OPEN_FILE
             13,  // ROOK_HALF_OPEN_FILE

            19,  49, 63, 55, 53, 99, 87, 65,   // QUEEN_PST
            -6,  12, 32, 58, 75, 79, 53, 58,
            -4,  20, 40, 56, 83, 99, 95, 53,
            -12,   7, 26, 48, 41, 60, 43, 31,
            -11,  15, 10, 25, 29, 45, 27, 36,
            -9,   3,  3,  8, 21, 15,  4,  7,
            -17,  -3,  3,  1,  6, 13, 15, 19,
            -18, -10, -9, -6, -9,-12,-26, 28,

              6,  // MAJOR_ON_7TH
             40,  // CONNECTED_MAJORS_ON_7TH

            0,  0,  0,  0,  0,  0,  0,  0,   // PAWN_PST
            127,108,112,120, 76, 89,106, 83,
            69, 47, 63, 48, 45, 47, 58, 37,
            17,  2,  2,  5,  5,  2,  11, 8,
            0, -6, -6, -8, -6,  0, -9,-10,
            -11,-11,-14,-14,  1, -2, -6, -8,
            -5,-17,-18,-25,-11,  6, -5,-17,
            0,  0,  0,  0,  0,  0,  0,  0,

             35,  // PASSED_PAWN
            -13,  // ISOLATED_PAWN
             -4   // DOUBLED_PAWN
    };

    public static final int KING_SAFETY_PAWN_ONE_AWAY_IND = 0;
    public static final int KING_SAFETY_PAWN_TWO_AWAY_IND = 1;
    public static final int KING_SAFETY_PAWN_FAR_AWAY_IND = 2;
    public static final int KING_SAFETY_MIDDLE_OPEN_FILE_IND = 3;
    public static final int KING_PST_IND = 4;
    public static final int KING_ENDGAME_PST_IND = 68;
    public static final int BISHOP_PST_IND = 132;
    public static final int KNIGHT_PST_IND = 196;
    public static final int KNIGHT_TROPISM_IND = 260;
    public static final int ROOK_PST_IND = 261;
    public static final int ROOK_OPEN_FILE_IND = 325;
    public static final int ROOK_HALF_OPEN_FILE_IND = 326;
    public static final int QUEEN_PST_IND = 327;
    public static final int MAJOR_ON_7TH_IND = 391;
    public static final int CONNECTED_MAJORS_ON_7TH_IND = 392;
    public static final int PAWN_PST_IND = 393;
    public static final int PASSED_PAWN_IND = 457;
    public static final int ISOLATED_PAWN_IND = 458;
    public static final int DOUBLED_PAWN_IND = 459;

    private static final Map<String, Tuple2<Integer, Integer>> indexMap = new HashMap<>();
    static {
        indexMap.put("KING_SAFETY_PAWN_ONE_AWAY", new Tuple2<>(KING_SAFETY_PAWN_ONE_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_PAWN_TWO_AWAY", new Tuple2<>(KING_SAFETY_PAWN_TWO_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_PAWN_FAR_AWAY", new Tuple2<>(KING_SAFETY_PAWN_FAR_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_MIDDLE_OPEN_FILE", new Tuple2<>(KING_SAFETY_MIDDLE_OPEN_FILE_IND, 1));
        indexMap.put("KING_PST", new Tuple2<>(KING_PST_IND, 64));
        indexMap.put("KING_ENDGAME_PST", new Tuple2<>(KING_ENDGAME_PST_IND, 64));
        indexMap.put("BISHOP_PST", new Tuple2<>(BISHOP_PST_IND, 64));
        indexMap.put("KNIGHT_PST", new Tuple2<>(KNIGHT_PST_IND, 64));
        indexMap.put("KNIGHT_TROPISM", new Tuple2<>(KNIGHT_TROPISM_IND, 1));
        indexMap.put("ROOK_PST", new Tuple2<>(ROOK_PST_IND, 64));
        indexMap.put("ROOK_OPEN_FILE", new Tuple2<>(ROOK_OPEN_FILE_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE", new Tuple2<>(ROOK_HALF_OPEN_FILE_IND, 1));
        indexMap.put("QUEEN_PST", new Tuple2<>(QUEEN_PST_IND, 64));
        indexMap.put("MAJOR_ON_7TH", new Tuple2<>(MAJOR_ON_7TH_IND, 1));
        indexMap.put("CONNECTED_MAJORS_ON_7TH", new Tuple2<>(CONNECTED_MAJORS_ON_7TH_IND, 1));
        indexMap.put("PAWN_PST", new Tuple2<>(PAWN_PST_IND, 64));
        indexMap.put("PASSED_PAWN", new Tuple2<>(PASSED_PAWN_IND, 1));
        indexMap.put("ISOLATED_PAWN", new Tuple2<>(ISOLATED_PAWN_IND, 1));
        indexMap.put("DOUBLED_PAWN", new Tuple2<>(DOUBLED_PAWN_IND, 1));
    }

    public static Set<String> getKeys() {
        return indexMap.keySet();
    }

    public EvalTermsVector() { }

    public EvalTermsVector(EvalTermsVector evalTermsVector) {
        System.arraycopy(evalTermsVector.terms, 0, terms, 0, terms.length);
    }

    public List<Integer> getVals(String key) {
        if (!indexMap.containsKey(key)) {
            throw new IllegalArgumentException("invalid key " + key);
        }
        Tuple2<Integer, Integer> v = indexMap.get(key);
        int[] myterms = Arrays.copyOfRange(terms, v._1, v._1 + v._2);
        return Arrays.stream(myterms)
                .boxed()
                .collect(Collectors.toList());
    }

    public void setVal(String key, List<Integer> vals) {
        Tuple2<Integer, Integer> indexTuple = indexMap.get(key);
        if (vals.size() != indexTuple._2) {
            throw new IllegalArgumentException("length of values is invalid.  expected: "  +
                    indexTuple._2 + ", received: " + vals.size());
        }
        for (int i=0;i<vals.size();i++) {
            terms[indexTuple._1 + i] = vals.get(i);
        }
    }
}
