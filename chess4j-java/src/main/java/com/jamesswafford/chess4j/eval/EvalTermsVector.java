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

              0,  0,  0,  0,  0,  0,  0,  0,    // KING_ENDGAME_PST
              0, 10, 10, 10, 10, 10, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 10, 10, 10, 10, 10,  0,
              0,  0,  0,  0,  0,  0,  0,  0,

              0,  0,  0,  0,  0,  0,  0,  0,     // BISHOP_PST
              0,  7,  7,  7,  7,  7,  7,  0,
              0,  7, 15, 15, 15, 15,  7,  0,
              0,  7, 15, 20, 20, 15,  7,  0,
              0,  7, 15, 20, 20, 15,  7,  0,
              0,  7, 15, 15, 15, 15,  7,  0,
              0,  7,  7,  7,  7,  7,  7,  0,
              0,  0,  0,  0,  0,  0,  0,  0,

             -5, -5, -5, -5, -5, -5, -5, -5,     // KNIGHT_PST
             -5,  0, 10, 10, 10, 10,  0, -5,
             -5,  0, 15, 20, 20, 15,  0, -5,
             -5,  5, 10, 15, 15, 10,  5, -5,
             -5,  0, 10, 15, 15, 10,  5, -5,
             -5,  0,  8,  0,  0,  8,  0, -5,
             -5,  0,  0,  5,  5,  0,  0, -5,
            -10,-10, -5, -5, -5, -5,-10,-10,

             -3,  // KNIGHT TROPISM

              0,  0,  0,  0,  0,  0,  0,  0,      // ROOK_PST
              0,  0,  0,  0,  0,  0,  0,  0,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
              0,  0,  0,  0,  0,  0,  0,  0,

             24,  // ROOK_OPEN_FILE
             13,  // ROOK_HALF_OPEN_FILE

             -1, -1, -1, -1, -1, -1, -1, -1,     // QUEEN_PST
             -1,  0,  0,  0,  0,  0,  0, -1,
             -1,  0,  1,  1,  1,  1,  0, -1,
             -1,  0,  1,  2,  2,  1,  0, -1,
             -1,  0,  1,  2,  2,  1,  0, -1,
             -1,  0,  1,  1,  1,  1,  0, -1,
             -1,  0,  0,  0,  0,  0,  0, -1,
             -1, -1, -1, -1, -1, -1, -1, -1,

              6,  // MAJOR_ON_7TH
             40,  // CONNECTED_MAJORS_ON_7TH

              0,  0,  0,  0,  0,  0,  0,  0,     // PAWN_PST
             30, 30, 30, 30, 30, 30, 30, 30,
             14, 14, 14, 18, 18, 14, 14, 14,
              7,  7,  7, 10, 10,  7,  7,  7,
              5,  5,  5,  7,  7,  5,  5,  5,
              3,  3,  3,  5,  5,  3,  3,  3,
              0,  0,  0, -3, -3,  0,  0,  0,
              0,  0,  0,  0,  0,  0,  0,  0,

             35,  // PASSED_PAWN
            -13,  // ISOLATED_PAWN
            - 4   // DOUBLED_PAWN
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
