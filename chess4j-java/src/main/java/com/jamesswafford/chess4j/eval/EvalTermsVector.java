package com.jamesswafford.chess4j.eval;

public class EvalTermsVector {

    int terms[] = new int[] {
            -10,  // KING_SAFETY_PAWN_ONE_AWAY
            -20,  // KING_SAFETY_PAWN_TWO_AWAY
            -30, // KING_SAFETY_PAWN_FAR_AWAY
            -50  // KING_SAFETY_MIDDLE_OPEN_FILE
    };

    public static int KING_SAFETY_PAWN_ONE_AWAY_IND = 0;
    public static int KING_SAFETY_PAWN_TWO_AWAY_IND = 1;
    public static int KING_SAFETY_PAWN_FAR_AWAY_IND = 2;
    public static int KING_SAFETY_MIDDLE_OPEN_FILE_IND = 3;

}
