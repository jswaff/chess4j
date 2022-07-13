package com.jamesswafford.chess4j.eval;

import io.vavr.Tuple2;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class EvalWeights {

    public int[] vals = new int[] {

            100,  // PAWN_VAL
           1044,  // QUEEN_VAL
            563,  // ROOK_VAL
            338,  // BISHOP_VAL
            406,  // KNIGHT_VAL
             40,  // BISHOP_PAIR
              4,  // KNIGHT_KAUFMAN_ADJ
             -9,  // ROOK_KAUFMAN_ADJ

             -6,  // KING_SAFETY_PAWN_ONE_AWAY
              3,  // KING_SAFETY_WING_PAWN_ONE_AWAY
            -16,  // KING_SAFETY_PAWN_TWO_AWAY
             -4,  // KING_SAFETY_WING_PAWN_TWO_AWAY
            -27,  // KING_SAFETY_PAWN_FAR_AWAY
            -26,  // KING_SAFETY_WING_PAWN_FAR_AWAY
            -46,  // KING_SAFETY_MIDDLE_OPEN_FILE

            // KING_PST_MG
            -30,-30,-30,-30,-30,-30,-30,-30,-30,-26,-26,-30,-30,-26,-26,-30,-27,-18,-17,-24,-25,-15,-16,-29,-27,-17,-10,-15,-14,-6,-10,-28,-30,-21,-10,-9,-8,-2,-9,-32,-24,-20,-14,-17,-17,-9,-14,-34,-14,-17,-17,-45,-50,-35,-7,3,-22,-3,-29,-66,-58,-74,-6,-4,

            // KING_PST_EG
            -31,-30,-30,-30,-30,-30,-30,-30,-30,-19,-25,-29,-29,-21,-17,-30,-26,-7,-5,-15,-16,2,2,-28,-27,-6,5,0,0,10,2,-28,-31,-17,1,4,4,0,-10,-36,-33,-21,-14,-10,-11,-13,-24,-40,-24,-26,-24,-30,-26,-20,-31,-51,-23,-29,-33,-58,-67,-39,-47,-81,

            // BISHOP_PST_MG
            -1,0,0,1,0,-1,0,0,-13,8,7,7,11,11,6,-1,-3,13,26,25,26,29,23,15,-8,16,18,41,31,27,17,5,-4,8,10,21,26,1,7,4,-7,8,5,7,4,7,7,9,-1,9,8,-6,-2,0,25,1,-6,-1,-18,-22,-24,-12,-5,-4,

            // BISHOP_PST_EG
            0,1,1,4,2,1,0,0,-8,13,9,9,12,11,7,-1,-1,15,22,23,24,30,19,13,-2,20,19,30,30,24,23,6,-4,11,20,22,22,15,7,3,-7,7,13,16,16,9,4,7,-3,-4,1,0,0,-5,1,-7,-8,-2,-24,-18,-17,-15,-6,-5,

            // BISHOP_MOBILITY_MG
           -25,-11,-6,-1,3,6,9,12,14,17,19,21,23,25,  

           // BISHOP_MOBILITY_EG
           -50,-22,-11,-2,6,12,18,24,29,34,38,42,46,50,  

            // KNIGHT_PST_MG
            -43,-30,-31,-30,-30,-31,-30,-32,-39,-36,-22,-19,-23,-28,-36,-36,-35,-14,2,6,-1,-6,-17,-32,-26,-4,6,12,-1,16,-5,-17,-29,-21,-7,-8,-2,-8,-6,-27,-45,-26,-18,-7,0,-11,-16,-39,-33,-33,-31,-16,-21,-21,-33,-30,-9,-43,-20,-36,-29,-27,-40,-9,

            // KNIGHT_PST_EG
            -40,-30,-30,-30,-30,-30,-30,-32,-39,-35,-25,-15,-21,-31,-37,-37,-35,-16,2,1,-6,-6,-22,-34,-28,-7,3,12,8,8,-7,-24,-30,-20,0,2,1,-3,-12,-30,-49,-27,-22,-8,-9,-23,-23,-47,-32,-32,-35,-25,-25,-29,-34,-34,-12,-51,-17,-36,-32,-35,-39,-9,

             -8,  // KNIGHT TROPISM_MG
             -9,  // KNIGHT_TROPISM_EG

            // ROOK_PST_MG
            25,24,18,17,18,18,17,26,4,6,14,19,18,15,10,14,5,16,20,21,22,28,19,10,-5,3,10,15,10,17,9,7,-21,-10,-12,-11,-14,-5,0,-7,-34,-22,-25,-25,-24,-19,-2,-18,-40,-29,-29,-30,-29,-14,-14,-25,-21,-21,-17,-13,-14,-9,-6,-21,

            // ROOK_PST_EG
            29,28,24,22,23,24,23,28,15,19,20,24,22,18,14,16,15,17,21,19,18,26,15,14,6,10,13,14,11,15,9,7,-12,-3,-3,-5,-7,-1,1,-6,-28,-16,-20,-20,-20,-15,-4,-15,-34,-25,-25,-27,-27,-18,-15,-20,-17,-15,-10,-16,-14,-4,-11,-26,

             36,  // ROOK_OPEN_FILE_MG
             27,  // ROOK_OPEN_FILE_EG
             20,  // ROOK_HALF_OPEN_FILE_MG
             14,  // ROOK_HALF_OPEN_FILE_EG

            // QUEEN_PST_MG
            -1,7,8,11,18,10,9,12,-30,-47,-6,0,12,22,6,14,-17,-8,-1,10,24,43,43,43,-17,-5,-2,7,22,31,35,34,-15,-4,-3,1,3,17,15,18,-17,-7,-2,-8,-6,0,8,0,-20,-12,-4,-7,-7,-7,-8,-4,-13,-23,-19,-8,-18,-28,-11,-6,

            // QUEEN_PST_EG
            -1,8,11,13,18,12,8,11,-25,-33,-5,2,16,22,6,10,-15,-6,1,12,25,40,37,40,-15,-3,-1,12,27,32,34,34,-14,-3,-1,11,10,19,16,17,-14,-6,-1,-6,-6,2,6,-1,-17,-12,-16,-15,-15,-16,-14,-5,-11,-20,-25,-20,-25,-27,-10,-6,

             0,  // QUEEN_MOBILITY_MG
             8,  // QUEEN_MOBILITY_EG

             6,  // MAJOR_ON_7TH_MG
            29,  // MAJOR_ON_7TH_EG
            62,  // CONNECTED_MAJORS_ON_7TH_MG
            62,  // CONNECTED_MAJORS_ON_7TH_EG

            // PAWN_PST_MG
            0,0,0,0,0,0,0,0,108,87,76,70,57,49,44,52,13,43,49,43,42,55,41,22,-22,-19,-14,5,15,20,-2,-15,-32,-32,-21,-13,-7,-9,-14,-33,-32,-39,-27,-23,-10,-21,-9,-30,-38,-43,-41,-32,-33,-16,-6,-33,0,0,0,0,0,0,0,0,

            // PAWN_PST_EG
            0,0,0,0,0,0,0,0,124,103,89,77,62,52,57,68,84,73,62,47,41,46,49,51,26,16,8,-12,-12,-5,3,2,2,-2,-18,-19,-17,-16,-15,-16,-9,-11,-16,-13,-8,-10,-20,-23,2,-6,-3,-15,-1,2,-17,-25,0,0,0,0,0,0,0,0,

            // PASSED_PAWN_MG
            0,50,40,30,20,10,0,0,
            
            // PASSED_PAWN_EG 
            0,100,70,60,40,20,0,0,
            
            -13,  // ISOLATED_PAWN_MG
            -14,  // ISOLATED_PAWN_EG
             -5,  // DOUBLED_PAWN_MG
             -8   // DOUBLED_PAWN_EG
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
    public static final int BISHOP_MOBILITY_EG_IND = 285;
    public static final int KNIGHT_PST_MG_IND = 299;
    public static final int KNIGHT_PST_EG_IND = 363;
    public static final int KNIGHT_TROPISM_MG_IND = 427;
    public static final int KNIGHT_TROPISM_EG_IND = 428;
    public static final int ROOK_PST_MG_IND = 429;
    public static final int ROOK_PST_EG_IND = 493;
    public static final int ROOK_OPEN_FILE_MG_IND = 557;
    public static final int ROOK_OPEN_FILE_EG_IND = 558;
    public static final int ROOK_HALF_OPEN_FILE_MG_IND = 559;
    public static final int ROOK_HALF_OPEN_FILE_EG_IND = 560;
    public static final int QUEEN_PST_MG_IND = 561;
    public static final int QUEEN_PST_EG_IND = 625;
    public static final int QUEEN_MOBILITY_MG_IND = 689;
    public static final int QUEEN_MOBILITY_EG_IND = 690;
    public static final int MAJOR_ON_7TH_MG_IND = 691;
    public static final int MAJOR_ON_7TH_EG_IND = 692;
    public static final int CONNECTED_MAJORS_ON_7TH_MG_IND = 693;
    public static final int CONNECTED_MAJORS_ON_7TH_EG_IND = 694;
    public static final int PAWN_PST_MG_IND = 695;
    public static final int PAWN_PST_EG_IND = 759;
    public static final int PASSED_PAWN_MG_IND = 823;
    public static final int PASSED_PAWN_EG_IND = 831;
    public static final int ISOLATED_PAWN_MG_IND = 839;
    public static final int ISOLATED_PAWN_EG_IND = 840;
    public static final int DOUBLED_PAWN_MG_IND = 841;
    public static final int DOUBLED_PAWN_EG_IND = 842;

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
        indexMap.put("BISHOP_MOBILITY_MG", new Tuple2<>(BISHOP_MOBILITY_MG_IND, 14));
        indexMap.put("BISHOP_MOBILITY_EG", new Tuple2<>(BISHOP_MOBILITY_EG_IND, 14));
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
        indexMap.put("PASSED_PAWN_MG", new Tuple2<>(PASSED_PAWN_MG_IND, 8));
        indexMap.put("PASSED_PAWN_EG", new Tuple2<>(PASSED_PAWN_EG_IND, 8));
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
