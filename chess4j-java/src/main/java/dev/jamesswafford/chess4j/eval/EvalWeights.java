package dev.jamesswafford.chess4j.eval;

import io.vavr.Tuple2;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class EvalWeights {

    public int[] vals = new int[] {

            100,  // PAWN_VAL
           1091,  // QUEEN_VAL
            558,  // ROOK_VAL
            361,  // BISHOP_VAL
            373,  // KNIGHT_VAL
             40,  // BISHOP_PAIR
              4,  // KNIGHT_KAUFMAN_ADJ
             -5,  // ROOK_KAUFMAN_ADJ

             -6,  // KING_SAFETY_PAWN_ONE_AWAY
             10,  // KING_SAFETY_WING_PAWN_ONE_AWAY
            -19,  // KING_SAFETY_PAWN_TWO_AWAY
              6,  // KING_SAFETY_WING_PAWN_TWO_AWAY
            -28,  // KING_SAFETY_PAWN_FAR_AWAY
            -18,  // KING_SAFETY_WING_PAWN_FAR_AWAY
            -54,  // KING_SAFETY_MIDDLE_OPEN_FILE

            // KING_PST_MG
            0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,-1,0,3,4,2,2,5,5,-1,0,3,5,4,4,7,6,-2,-2,2,7,4,5,8,7,-7,-3,0,3,-1,1,3,9,-9,0,0,-2,-29,-32,-21,18,30,-11,27,0,-37,-26,-53,27,30,

            // KING_PST_EG
            -3,-1,-1,-1,-1,-1,-1,-3,-2,5,3,1,1,4,8,-3,1,15,18,9,8,24,23,-3,-2,14,26,22,23,30,22,-7,-10,5,23,29,28,23,11,-19,-11,-1,7,15,13,9,-6,-22,-9,-7,-2,-7,-3,2,-14,-36,-14,-21,-16,-30,-43,-14,-32,-73,

            // BISHOP_PST_MG
            -4,-1,-3,-3,-2,-5,0,-1,-15,-9,-5,-3,-2,2,-4,-5,-11,2,11,8,11,11,13,8,-16,8,3,34,19,12,9,-1,-11,-3,1,15,21,-7,-2,1,-13,2,-2,4,0,3,1,7,-2,4,5,-10,-6,-2,21,-2,-1,1,-18,-21,-23,-12,-5,-4,

            // BISHOP_PST_EG
            0,3,1,3,2,-1,0,0,-5,4,1,2,3,3,0,-3,-2,9,8,9,10,17,10,4,-3,9,7,19,22,9,12,3,-5,3,12,15,13,10,-1,-3,-5,1,6,7,8,-2,-4,-1,-6,-11,-8,-6,-11,-12,-10,-12,-8,-3,-14,-13,-10,-16,-6,-4,

            // BISHOP_MOBILITY_MG
           -25,-19,-14,-10,-5,2,3,3,5,10,17,20,5,7,

           // BISHOP_MOBILITY_EG
           -20,-36,-29,-17,-1,10,18,24,27,25,21,21,8,11,

           // TRAPPED BISHOP
           -125,

            // KNIGHT_PST_MG
            -12,-1,-3,-2,-1,-3,-1,-4,-7,-6,4,4,3,5,-3,-2,-4,3,7,11,14,11,8,2,0,6,12,10,10,36,14,8,-12,2,10,6,19,12,22,-8,-22,-3,6,18,26,13,8,-20,-15,-12,-7,8,4,4,-13,-10,-4,-15,-13,-7,-4,-1,-13,-6,

            // KNIGHT_PST_EG
            -8,-3,0,-2,-2,-1,-2,-4,-7,-3,0,9,6,-3,-5,-7,-5,4,10,8,5,9,1,-5,-4,3,12,12,18,11,6,-6,-7,5,18,18,23,20,10,-8,-16,-3,4,19,16,1,-3,-19,-8,-8,-8,-1,2,-10,-10,-10,-6,-26,-10,-7,-9,-13,-15,-4,

            // KNIGHT_OUTPOST
            -20,-4,-3,-4,-2,-4,-2,-8,-14,-9,4,13,9,2,-8,-9,-9,7,21,21,24,25,11,-4,2,10,19,26,24,25,19,9,2,9,11,16,14,14,8,7,-12,5,6,2,3,2,1,-4,-11,-3,-4,-1,-1,-4,-2,-12,-6,-11,-5,-4,-3,-6,-2,-3,

            // KNIGHT_SUPPORTED_OUTPOST
            0,0,0,0,0,0,0,0,0,1,1,2,2,1,1,0,1,10,16,17,13,16,10,2,4,17,18,20,20,18,17,6,10,6,19,18,10,12,6,3,-5,-1,-5,0,-2,-6,-2,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,

             -6,  // KNIGHT TROPISM_MG
             -6,  // KNIGHT_TROPISM_EG

            // ROOK_PST_MG
            9,11,4,5,6,9,9,12,1,-2,10,13,13,11,6,12,-5,11,10,18,19,22,18,11,-13,-2,3,13,5,14,10,9,-25,-15,-18,-16,-17,-6,3,-4,-31,-23,-24,-21,-22,-12,4,-12,-33,-25,-24,-24,-22,-6,-7,-20,-17,-17,-11,-2,-5,2,2,-13,

            // ROOK_PST_EG
            29,29,28,25,25,26,25,29,13,17,16,20,18,13,13,13,19,17,23,17,15,24,14,16,11,13,17,13,11,15,10,11,-3,3,6,1,0,4,5,-2,-19,-11,-14,-15,-14,-10,-6,-11,-24,-20,-19,-21,-22,-20,-13,-13,-14,-11,-8,-17,-13,-4,-11,-23,

            // ROOK_MOBILITY_MG
            -35,-30,-25,-24,-26,-21,-20,-15,-11,-11,-4,-4,6,16,13,

            // ROOK_MOBILITY_EG
            -21,-20,-14,-2,14,19,25,27,31,36,36,37,32,26,24,

             30,  // ROOK_OPEN_FILE_MG
              0,  // ROOK_OPEN_FILE_SUPPORTED_MG
             19,  // ROOK_OPEN_FILE_EG
              0,  // ROOK_OPEN_FILE_SUPPORTED_EG
             18,  // ROOK_HALF_OPEN_FILE_MG
              0,  // ROOK_HALF_OPEN_FILE_SUPPORTED_MG
             13,  // ROOK_HALF_OPEN_FILE_EG
              0,  // ROOK_HALF_OPEN_FILE_SUPPORTED_EG

            // QUEEN_PST_MG
            -2,4,6,8,12,9,8,12,-21,-43,-5,0,12,19,7,18,-16,-10,-7,4,19,36,44,46,-16,-9,-7,-2,14,24,33,35,-18,-8,-9,-6,-2,13,13,18,-18,-8,-3,-11,-7,-1,7,1,-18,-11,-2,-5,-2,-3,-5,-3,-13,-17,-10,2,-8,-25,-8,-5,

            // QUEEN_PST_EG
            1,7,10,13,16,11,8,10,-8,-2,1,7,17,16,6,10,-6,-4,2,10,20,28,24,24,-9,2,2,14,26,24,24,21,-7,-1,2,17,14,15,10,11,-9,-4,1,-3,-4,3,-1,-1,-9,-12,-28,-18,-23,-23,-15,-4,-10,-12,-17,-24,-18,-17,-6,-3,

            // QUEEN_MOBILITY_MG
            -2,-8,-12,-10,-10,-9,-7,-6,-3,0,2,4,6,6,9,11,13,12,16,14,12,11,5,4,2,1,0,0,

             // QUEEN_MOBILITY_EG
             0,-1,-3,-6,-9,-13,-16,-10,-7,-3,1,6,10,14,19,18,18,18,20,20,17,16,8,7,4,2,1,1,

           -17,  // MAJOR_ON_7TH_MG
            50,  // MAJOR_ON_7TH_EG
            10,  // CONNECTED_MAJORS_ON_7TH_MG
            15,  // CONNECTED_MAJORS_ON_7TH_EG

            // PAWN_PST_MG
            0,0,0,0,0,0,0,0,26,20,15,13,8,5,-1,0,-14,6,13,5,13,39,13,3,-24,-14,-13,7,17,24,6,-11,-29,-24,-17,-9,-2,-3,-4,-31,-28,-32,-22,-17,-6,-13,-1,-25,-32,-36,-37,-29,-29,-11,3,-22,0,0,0,0,0,0,0,0,

            // PAWN_PST_EG
            0,0,0,0,0,0,0,0,51,38,25,11,6,3,8,14,56,39,20,-6,-9,14,15,18,29,21,7,-17,-9,-5,7,5,12,10,-7,-9,-9,-5,-5,-6,4,0,-4,-7,2,0,-9,-11,11,3,6,0,13,9,-8,-16,0,0,0,0,0,0,0,0,

            // PASSED_PAWN_MG
            0,85,47,18,-14,-20,-9,0,
            
            // PASSED_PAWN_EG 
            0,156,112,63,48,25,19,0,

            -11,  // ISOLATED_PAWN_MG
            -14,  // ISOLATED_PAWN_EG
             -7,  // DOUBLED_PAWN_MG
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
    public static final int BISHOP_TRAPPED_IND = 299;
    public static final int KNIGHT_PST_MG_IND = 300;
    public static final int KNIGHT_PST_EG_IND = 364;
    public static final int KNIGHT_OUTPOST_IND = 428;
    public static final int KNIGHT_SUPPORTED_OUTPOST_IND = 492;
    public static final int KNIGHT_TROPISM_MG_IND = 556;
    public static final int KNIGHT_TROPISM_EG_IND = 557;
    public static final int ROOK_PST_MG_IND = 558;
    public static final int ROOK_PST_EG_IND = 622;
    public static final int ROOK_MOBILITY_MG_IND = 686;
    public static final int ROOK_MOBILITY_EG_IND = 701;
    public static final int ROOK_OPEN_FILE_MG_IND = 716;
    public static final int ROOK_OPEN_FILE_SUPPORTED_MG_IND = 717;
    public static final int ROOK_OPEN_FILE_EG_IND = 718;
    public static final int ROOK_OPEN_FILE_SUPPORTED_EG_IND = 719;
    public static final int ROOK_HALF_OPEN_FILE_MG_IND = 720;
    public static final int ROOK_HALF_OPEN_FILE_SUPPORTED_MG_IND = 721;
    public static final int ROOK_HALF_OPEN_FILE_EG_IND = 722;
    public static final int ROOK_HALF_OPEN_FILE_SUPPORTED_EG_IND = 723;
    public static final int QUEEN_PST_MG_IND = 724;
    public static final int QUEEN_PST_EG_IND = 788;
    public static final int QUEEN_MOBILITY_MG_IND = 852;
    public static final int QUEEN_MOBILITY_EG_IND = 880;
    public static final int MAJOR_ON_7TH_MG_IND = 908;
    public static final int MAJOR_ON_7TH_EG_IND = 909;
    public static final int CONNECTED_MAJORS_ON_7TH_MG_IND = 910;
    public static final int CONNECTED_MAJORS_ON_7TH_EG_IND = 911;
    public static final int PAWN_PST_MG_IND = 912;
    public static final int PAWN_PST_EG_IND = 976;
    public static final int PASSED_PAWN_MG_IND = 1040;
    public static final int PASSED_PAWN_EG_IND = 1048;
    public static final int ISOLATED_PAWN_MG_IND = 1056;
    public static final int ISOLATED_PAWN_EG_IND = 1057;
    public static final int DOUBLED_PAWN_MG_IND = 1058;
    public static final int DOUBLED_PAWN_EG_IND = 1059;

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
        indexMap.put("BISHOP_TRAPPED", new Tuple2<>(BISHOP_TRAPPED_IND, 1));
        indexMap.put("KNIGHT_PST_MG", new Tuple2<>(KNIGHT_PST_MG_IND, 64));
        indexMap.put("KNIGHT_PST_EG", new Tuple2<>(KNIGHT_PST_EG_IND, 64));
        indexMap.put("KNIGHT_OUTPOST", new Tuple2<>(KNIGHT_OUTPOST_IND, 64));
        indexMap.put("KNIGHT_SUPPORTED_OUTPOST", new Tuple2<>(KNIGHT_SUPPORTED_OUTPOST_IND, 64));
        indexMap.put("KNIGHT_TROPISM_MG", new Tuple2<>(KNIGHT_TROPISM_MG_IND, 1));
        indexMap.put("KNIGHT_TROPISM_EG", new Tuple2<>(KNIGHT_TROPISM_EG_IND, 1));
        indexMap.put("ROOK_PST_MG", new Tuple2<>(ROOK_PST_MG_IND, 64));
        indexMap.put("ROOK_PST_EG", new Tuple2<>(ROOK_PST_EG_IND, 64));
        indexMap.put("ROOK_MOBILITY_MG", new Tuple2<>(ROOK_MOBILITY_MG_IND, 15));
        indexMap.put("ROOK_MOBILITY_EG", new Tuple2<>(ROOK_MOBILITY_EG_IND, 15));
        indexMap.put("ROOK_OPEN_FILE_MG", new Tuple2<>(ROOK_OPEN_FILE_MG_IND, 1));
        indexMap.put("ROOK_OPEN_FILE_SUPPORTED_MG", new Tuple2<>(ROOK_OPEN_FILE_SUPPORTED_MG_IND, 1));
        indexMap.put("ROOK_OPEN_FILE_EG", new Tuple2<>(ROOK_OPEN_FILE_EG_IND, 1));
        indexMap.put("ROOK_OPEN_FILE_SUPPORTED_EG", new Tuple2<>(ROOK_OPEN_FILE_SUPPORTED_EG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_MG", new Tuple2<>(ROOK_HALF_OPEN_FILE_MG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_SUPPORTED_MG", new Tuple2<>(ROOK_HALF_OPEN_FILE_SUPPORTED_MG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_EG", new Tuple2<>(ROOK_HALF_OPEN_FILE_EG_IND, 1));
        indexMap.put("ROOK_HALF_OPEN_FILE_SUPPORTED_EG", new Tuple2<>(ROOK_HALF_OPEN_FILE_SUPPORTED_EG_IND, 1));
        indexMap.put("QUEEN_PST_MG", new Tuple2<>(QUEEN_PST_MG_IND, 64));
        indexMap.put("QUEEN_PST_EG", new Tuple2<>(QUEEN_PST_EG_IND, 64));
        indexMap.put("QUEEN_MOBILITY_MG", new Tuple2<>(QUEEN_MOBILITY_MG_IND, 28));
        indexMap.put("QUEEN_MOBILITY_EG", new Tuple2<>(QUEEN_MOBILITY_EG_IND, 28));
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

    public void reset() {
        Arrays.fill(vals, 0);
        vals[PAWN_VAL_IND] = 100;
        vals[QUEEN_VAL_IND] = 900;
        vals[ROOK_VAL_IND] = 500;
        vals[BISHOP_VAL_IND] = 300;
        vals[KNIGHT_VAL_IND] = 300;
        // trapped bishop is an exception. 
        vals[BISHOP_TRAPPED_IND] = -125;
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
