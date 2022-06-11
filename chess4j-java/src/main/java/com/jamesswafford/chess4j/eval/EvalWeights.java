package com.jamesswafford.chess4j.eval;

import io.vavr.Tuple2;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class EvalWeights {

    public int[] vals = new int[] {

            100,  // PAWN_VAL
            975,  // QUEEN_VAL
            500,  // ROOK_VAL
            325,  // BISHOP_VAL
            325,  // KNIGHT_VAL
             50,  // BISHOP_PAIR
              6,  // KNIGHT_KAUFMAN_ADJ
            -12,  // ROOK_KAUFMAN_ADJ

              2,  // KING_SAFETY_PAWN_ONE_AWAY
              1,  // KING_SAFETY_WING_PAWN_ONE_AWAY
            -31,  // KING_SAFETY_PAWN_TWO_AWAY
            -16,  // KING_SAFETY_WING_PAWN_TWO_AWAY
            -31,  // KING_SAFETY_PAWN_FAR_AWAY
            -16,  // KING_SAFETY_WING_PAWN_FAR_AWAY
            -79,  // KING_SAFETY_MIDDLE_OPEN_FILE

              7, 41, 14, 47, 36, 30, 19, 58,     // KING_PST
             37, 41, 36,-21, 14, 30, 33,-19,
              6, 43, -9,-17,-14,  9, 29, 13,
            -53,  3,-18,-60,-40,-14, 14, -7,
            -52,-15,-32,-30,-34,-42,-37,  5,
            -19,  8, 10,-10,-38,-40,-31,-21,
             -6,-13,  0,-30,-41,-25, 16, 26,
              6, 26, 20,-86,-21,-48, 38, 44,

            -17, 70, 43, 74, 66, 57,  8, 21,     // KING_ENDGAME_PST
             40, 75, 76, 41, 63, 73, 72, 17,
             50, 83, 51, 53, 56, 61, 75, 50,
             19, 57, 52, 25, 39, 52, 60, 40,
             -8, 41, 29, 31, 30, 34, 34, 17,
             -4, -9, 20,  7, 20, 21, 14,  6,
            -39,  5,-15, 13,  9,  9,-11,-25,
            -12,-29,-15, -4,-33, -1,-31,-55,

            -22,-15,  5,-35, -5,  5,  8,-13,     // BISHOP_PST
            -23,-19, -7, -4,  3, -5, -9,-32,
             -7,  1, -2,  0,  7, 25, 26,  4,
            -13, -7,  9, 15,  1,  2,-14, -3,
            -22, -1,  3,  9,  6,-10, -8,-19,
            -10,-12, -3, -9,  0,  2,-10,-25,
             -4,-22,-24,-22,-11, -5,-13, -9,
            -57,-15,-48,-22,-25,-38,-21,-56,

            -22,-15,  5,-35, -5,  5,  8,-13,     // BISHOP_ENDGAME_PST
            -23,-19, -7, -4,  3, -5, -9,-32,
            -7,  1, -2,  0,  7, 25, 26,  4,
            -13, -7,  9, 15,  1,  2,-14, -3,
            -22, -1,  3,  9,  6,-10, -8,-19,
            -10,-12, -3, -9,  0,  2,-10,-25,
            -4,-22,-24,-22,-11, -5,-13, -9,
            -57,-15,-48,-22,-25,-38,-21,-56,

            2,                                   // BISHOP_MOBILITY
            3,                                   // BISHOP_ENDGAME_MOBILITY

            -82,-16,  8, -3, -7, -7,-17,-75,     // KNIGHT_PST
            -25, -6, 16, 15, -3,-15,  7,-70,
             -9,  3, 25, 18, 30, 33, 21,-15,
             -7,  9, 22, 42, 12, 27,-14, -9,
            -19,  8,  6, -4,  0, 13, 16,-21,
            -39,-33,-14,  7, 14,-10, -3,-43,
            -56,-41,-17,-10,-13,-32,-31, -3,
            -72,-27,-56,-45,-45,-41,-28,-98,

            -82,-16,  8, -3, -7, -7,-17,-75,     // KNIGHT_ENDGAME_PST
            -25, -6, 16, 15, -3,-15,  7,-70,
            -9,  3, 25, 18, 30, 33, 21,-15,
            -7,  9, 22, 42, 12, 27,-14, -9,
            -19,  8,  6, -4,  0, 13, 16,-21,
            -39,-33,-14,  7, 14,-10, -3,-43,
            -56,-41,-17,-10,-13,-32,-31, -3,
            -72,-27,-56,-45,-45,-41,-28,-98,

             -2,  // KNIGHT TROPISM
             -3,  // KNIGHT_TROPISM_ENDGAME

              2, 10, 10, 19, 14,  6, -2,  2,     // ROOK_PST
              4,  7, 10, 11,  6, 13, 12, 10,
             -4,  2,  4, 11, 10, 11,  7,  7,
             -6, -3, -9, -2,-21,  0,  4,-15,
            -31, -3,-21,-21,-13,-13,-20,-16,
            -38,-29,-37,-26,-29,-27,-22,-33,
            -53,-35,-35,-37,-33,-23,-31,-39,
            -35,-31,-28,-23,-22,-25,-41,-43,

              2, 10, 10, 19, 14,  6, -2,  2,     // ROOK_ENDGAME_PST
              4,  7, 10, 11,  6, 13, 12, 10,
             -4,  2,  4, 11, 10, 11,  7,  7,
             -6, -3, -9, -2,-21,  0,  4,-15,
            -31, -3,-21,-21,-13,-13,-20,-16,
            -38,-29,-37,-26,-29,-27,-22,-33,
            -53,-35,-35,-37,-33,-23,-31,-39,
            -35,-31,-28,-23,-22,-25,-41,-43,

             24,  // ROOK_OPEN_FILE
             23,  // ROOK_OPEN_FILE_ENDGAME
             13,  // ROOK_HALF_OPEN_FILE
             14,  // ROOK_HALF_OPEN_FILE_ENDGAME

             19, 49, 63, 55, 53, 99, 87, 65,     // QUEEN_PST
             -6, 12, 32, 58, 75, 79, 53, 58,
             -4, 20, 40, 56, 83, 99, 95, 53,
            -12,  7, 26, 48, 41, 60, 43, 31,
            -11, 15, 10, 25, 29, 45, 27, 36,
             -9,  3,  3,  8, 21, 15,  4,  7,
            -17, -3,  3,  1,  6, 13, 15, 19,
            -18,-10, -9, -6, -9,-12,-26, 28,

             19, 49, 63, 55, 53, 99, 87, 65,    // QUEEN_ENDGAME_PST
             -6, 12, 32, 58, 75, 79, 53, 58,
             -4, 20, 40, 56, 83, 99, 95, 53,
            -12,  7, 26, 48, 41, 60, 43, 31,
            -11, 15, 10, 25, 29, 45, 27, 36,
             -9,  3,  3,  8, 21, 15,  4,  7,
            -17, -3,  3,  1,  6, 13, 15, 19,
            -18,-10, -9, -6, -9,-12,-26, 28,

             1,                                  // QUEEN_MOBILITY
             2,                                  // QUEEN_ENDGAME_MOBILITY

             6,                                  // MAJOR_ON_7TH
             7,                                  // MAJOR_ON_7TH_ENDGAME
            40,                                  // CONNECTED_MAJORS_ON_7TH
            41,                                  // CONNECTED_MAJORS_ON_7TH_ENDGAME

              0,  0,  0,  0,  0,  0,  0,  0,     // PAWN_PST
            127,108,112,120, 76, 89,106, 83,
             69, 47, 63, 48, 45, 47, 58, 37,
             17,  2,  2,  5,  5,  2, 11,  8,
              0, -6, -6, -8, -6,  0, -9,-10,
            -11,-11,-14,-14,  1, -2, -6, -8,
             -5,-17,-18,-25,-11,  6, -5,-17,
              0,  0,  0,  0,  0,  0,  0,  0,

              0,  0,  0,  0,  0,  0,  0,  0,     // PAWN_ENDGAME_PST
            127,108,112,120, 76, 89,106, 83,
             69, 47, 63, 48, 45, 47, 58, 37,
             17,  2,  2,  5,  5,  2, 11,  8,
              0, -6, -6, -8, -6,  0, -9,-10,
            -11,-11,-14,-14,  1, -2, -6, -8,
             -5,-17,-18,-25,-11,  6, -5,-17,
              0,  0,  0,  0,  0,  0,  0,  0,

             35,  // PASSED_PAWN
             36,  // PASSED_PAWN_ENDGAME
            -13,  // ISOLATED_PAWN
            -12,  // ISOLATED_PAWN_ENDGAME
             -4,  // DOUBLED_PAWN
             -5   // DOUBLED_PAWN_ENDGAME
    };

    public static final int PAWN_VAL_IND   = 0;
    public static final int QUEEN_VAL_IND  = 1;
    public static final int ROOK_VAL_IND   = 2;
    public static final int BISHOP_VAL_IND = 3;
    public static final int KNIGHT_VAL_IND = 4;
    public static final int BISHOP_PAIR_IND = 5;
    public static final int KNIGHT_KAUFMAN_ADJ = 6;
    public static final int ROOK_KAUFMAN_ADJ = 7;

    public static final int KING_SAFETY_PAWN_ONE_AWAY_IND = 8;
    public static final int KING_SAFETY_WING_PAWN_ONE_AWAY_IND = 9;
    public static final int KING_SAFETY_PAWN_TWO_AWAY_IND = 10;
    public static final int KING_SAFETY_WING_PAWN_TWO_AWAY_IND = 11;
    public static final int KING_SAFETY_PAWN_FAR_AWAY_IND = 12;
    public static final int KING_SAFETY_WING_PAWN_FAR_AWAY_IND = 13;
    public static final int KING_SAFETY_MIDDLE_OPEN_FILE_IND = 14;
    public static final int KING_PST_MG_IND = 15;
    public static final int KING_PST_EG_IND = 79;
    public static final int BISHOP_PST_MG_IND = 143;
    public static final int BISHOP_PST_EG_IND = 207;
    public static final int BISHOP_MOBILITY_MG_IND = 271;
    public static final int BISHOP_MOBILITY_EG_IND = 272;
    public static final int KNIGHT_PST_MG_IND = 273;
    public static final int KNIGHT_PST_EG_IND = 337;
    public static final int KNIGHT_TROPISM_MG_IND = 401;
    public static final int KNIGHT_TROPISM_EG_IND = 402;
    public static final int ROOK_PST_MG_IND = 403;
    public static final int ROOK_PST_EG_IND = 467;
    public static final int ROOK_OPEN_FILE_MG_IND = 531;
    public static final int ROOK_OPEN_FILE_EG_IND = 532;
    public static final int ROOK_HALF_OPEN_FILE_MG_IND = 533;
    public static final int ROOK_HALF_OPEN_FILE_EG_IND = 534;
    public static final int QUEEN_PST_MG_IND = 535;
    public static final int QUEEN_PST_EG_IND = 599;
    public static final int QUEEN_MOBILITY_MG_IND = 663;
    public static final int QUEEN_MOBILITY_EG_IND = 664;
    public static final int MAJOR_ON_7TH_MG_IND = 665;
    public static final int MAJOR_ON_7TH_EG_IND = 666;
    public static final int CONNECTED_MAJORS_ON_7TH_MG_IND = 667;
    public static final int CONNECTED_MAJORS_ON_7TH_EG_IND = 668;
    public static final int PAWN_PST_MG_IND = 669;
    public static final int PAWN_PST_EG_IND = 733;
    public static final int PASSED_PAWN_MG_IND = 797;
    public static final int PASSED_PAWN_EG_IND = 798;
    public static final int ISOLATED_PAWN_MG_IND = 799;
    public static final int ISOLATED_PAWN_EG_IND = 800;
    public static final int DOUBLED_PAWN_MG_IND = 801;
    public static final int DOUBLED_PAWN_EG_IND = 802;

    private static final Map<String, Tuple2<Integer, Integer>> indexMap = new HashMap<>();
    static {
        indexMap.put("PAWN_VAL", new Tuple2<>(PAWN_VAL_IND, 1));
        indexMap.put("QUEEN_VAL", new Tuple2<>(QUEEN_VAL_IND, 1));
        indexMap.put("ROOK_VAL", new Tuple2<>(ROOK_VAL_IND, 1));
        indexMap.put("BISHOP_VAL", new Tuple2<>(BISHOP_VAL_IND, 1));
        indexMap.put("KNIGHT_VAL", new Tuple2<>(KNIGHT_VAL_IND, 1));
        indexMap.put("BISHOP_PAIR", new Tuple2<>(BISHOP_PAIR_IND, 1));
        indexMap.put("KNIGHT_KAUFMAN_ADJ", new Tuple2<>(KNIGHT_KAUFMAN_ADJ, 1));
        indexMap.put("ROOK_KAUFMAN_ADJ", new Tuple2<>(ROOK_KAUFMAN_ADJ, 1));

        indexMap.put("KING_SAFETY_PAWN_ONE_AWAY", new Tuple2<>(KING_SAFETY_PAWN_ONE_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_WING_PAWN_ONE_AWAY", new Tuple2<>(KING_SAFETY_WING_PAWN_ONE_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_PAWN_TWO_AWAY", new Tuple2<>(KING_SAFETY_PAWN_TWO_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_WING_PAWN_TWO_AWAY", new Tuple2<>(KING_SAFETY_WING_PAWN_TWO_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_PAWN_FAR_AWAY", new Tuple2<>(KING_SAFETY_PAWN_FAR_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_WING_PAWN_FAR_AWAY", new Tuple2<>(KING_SAFETY_WING_PAWN_FAR_AWAY_IND, 1));
        indexMap.put("KING_SAFETY_MIDDLE_OPEN_FILE", new Tuple2<>(KING_SAFETY_MIDDLE_OPEN_FILE_IND, 1));
        indexMap.put("KING_PST_MG", new Tuple2<>(KING_PST_MG_IND, 64));
        indexMap.put("KING_PST_EG", new Tuple2<>(KING_PST_EG_IND, 64));
        indexMap.put("BISHOP_PST_MG", new Tuple2<>(BISHOP_PST_MG_IND, 64));
        indexMap.put("BISHOP_PST_EG", new Tuple2<>(BISHOP_PST_EG_IND, 64));
        indexMap.put("BISHOP_MOBILITY_MG", new Tuple2<>(BISHOP_MOBILITY_MG_IND, 1));
        indexMap.put("BISHOP_MOBILITY_EG", new Tuple2<>(BISHOP_MOBILITY_EG_IND, 1));
        indexMap.put("KNIGHT_PST_MG", new Tuple2<>(KNIGHT_PST_MG_IND, 64));
        indexMap.put("KNIGHT_PST_EG", new Tuple2<>(KNIGHT_PST_EG_IND, 64));
        indexMap.put("KNIGHT_TROPISM_MG", new Tuple2<>(KNIGHT_TROPISM_MG_IND, 1));
        indexMap.put("KNIGHT_TROPISM_EG", new Tuple2<>(KNIGHT_TROPISM_EG_IND, 1));
        indexMap.put("ROOK_PST_MG", new Tuple2<>(ROOK_PST_MG_IND, 64));
        indexMap.put("ROOK_PST_EG", new Tuple2<>(ROOK_PST_EG_IND, 64));
        indexMap.put("ROOK_OPEN_FILE_MG", new Tuple2<>(ROOK_OPEN_FILE_MG_IND, 1));
        indexMap.put("ROOK_OPEN_FILE_EG", new Tuple2<>(ROOK_OPEN_FILE_EG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_MG", new Tuple2<>(ROOK_HALF_OPEN_FILE_MG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_EG", new Tuple2<>(ROOK_HALF_OPEN_FILE_EG_IND, 1));
        indexMap.put("QUEEN_PST_MG", new Tuple2<>(QUEEN_PST_MG_IND, 64));
        indexMap.put("QUEEN_PST_EG", new Tuple2<>(QUEEN_PST_EG_IND, 64));
        indexMap.put("QUEEN_MOBILITY_MG", new Tuple2<>(QUEEN_MOBILITY_MG_IND, 1));
        indexMap.put("QUEEN_MOBILITY_EG", new Tuple2<>(QUEEN_MOBILITY_EG_IND, 1));
        indexMap.put("MAJOR_ON_7TH_MG", new Tuple2<>(MAJOR_ON_7TH_MG_IND, 1));
        indexMap.put("MAJOR_ON_7TH_EG", new Tuple2<>(MAJOR_ON_7TH_EG_IND, 1));
        indexMap.put("CONNECTED_MAJORS_ON_7TH_MG", new Tuple2<>(CONNECTED_MAJORS_ON_7TH_MG_IND, 1));
        indexMap.put("CONNECTED_MAJORS_ON_7TH_EG", new Tuple2<>(CONNECTED_MAJORS_ON_7TH_EG_IND, 1));
        indexMap.put("PAWN_PST_MG", new Tuple2<>(PAWN_PST_MG_IND, 64));
        indexMap.put("PAWN_PST_EG", new Tuple2<>(PAWN_PST_EG_IND, 64));
        indexMap.put("PASSED_PAWN_MG", new Tuple2<>(PASSED_PAWN_MG_IND, 1));
        indexMap.put("PASSED_PAWN_EG", new Tuple2<>(PASSED_PAWN_EG_IND, 1));
        indexMap.put("ISOLATED_PAWN_MG", new Tuple2<>(ISOLATED_PAWN_MG_IND, 1));
        indexMap.put("ISOLATED_PAWN_EG", new Tuple2<>(ISOLATED_PAWN_EG_IND, 1));
        indexMap.put("DOUBLED_PAWN_MG", new Tuple2<>(DOUBLED_PAWN_MG_IND, 1));
        indexMap.put("DOUBLED_PAWN_EG", new Tuple2<>(DOUBLED_PAWN_EG_IND, 1));
    }

    public static Set<String> getKeys() {
        return indexMap.keySet();
    }

    public EvalWeights() { }

    public EvalWeights(EvalWeights evalWeights) {
        System.arraycopy(evalWeights.vals, 0, vals, 0, vals.length);
    }

    public List<Integer> getVals(String key) {
        if (!indexMap.containsKey(key)) {
            throw new IllegalArgumentException("invalid key " + key);
        }
        Tuple2<Integer, Integer> v = indexMap.get(key);
        int[] myterms = Arrays.copyOfRange(vals, v._1, v._1 + v._2);
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
            this.vals[indexTuple._1 + i] = vals.get(i);
        }
    }
}
