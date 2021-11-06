package com.jamesswafford.chess4j.eval;

public class EvalTermsVector {

    int[] terms = new int[] {
            -10,  // KING_SAFETY_PAWN_ONE_AWAY
            -20,  // KING_SAFETY_PAWN_TWO_AWAY
            -30,  // KING_SAFETY_PAWN_FAR_AWAY
            -50,  // KING_SAFETY_MIDDLE_OPEN_FILE
            -30,-30,-30,-30,-30,-30,-30,-30,    // KING_PST
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -20,-20,-20,-20,-20,-20,-20,-20,
            -10,-10,-10,-10,-10,-10,-10,-10,
              0, 10, 20,-25,  0,-25, 20,  0,
              0,  0,  0,  0,  0,  0,  0,  0,    // KING_ENDGAME_PST
              0, 10, 10, 10, 10, 10, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 10, 10, 10, 10, 10,  0,
              0,  0,  0,  0,  0,  0,  0,  0,
              0,  0,  0,  0,  0,  0,  0,  0,    // BISHOP_PST
              0,  7,  7,  7,  7,  7,  7,  0,
              0,  7, 15, 15, 15, 15,  7,  0,
              0,  7, 15, 20, 20, 15,  7,  0,
              0,  7, 15, 20, 20, 15,  7,  0,
              0,  7, 15, 15, 15, 15,  7,  0,
              0,  7,  7,  7,  7,  7,  7,  0,
              0,  0,  0,  0,  0,  0,  0,  0
    };

    public static int KING_SAFETY_PAWN_ONE_AWAY_IND = 0;
    public static int KING_SAFETY_PAWN_TWO_AWAY_IND = 1;
    public static int KING_SAFETY_PAWN_FAR_AWAY_IND = 2;
    public static int KING_SAFETY_MIDDLE_OPEN_FILE_IND = 3;
    public static int KING_PST_IND = 4;
    public static int KING_ENDGAME_PST_IND = 68;
    public static int BISHOP_PST_IND = 132;
}
