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
             -2,  // KNIGHT TROPISM
              0,  0,  0,  0,  0,  0,  0,  0,      // ROOK_PST
              0,  0,  0,  0,  0,  0,  0,  0,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
             -5,  0,  0,  0,  0,  0,  0, -5,
              0,  0,  0,  0,  0,  0,  0,  0,
             25,  // ROOK_OPEN_FILE
             15   // ROOK_HALF_OPEN_FILE
    };

    public static int KING_SAFETY_PAWN_ONE_AWAY_IND = 0;
    public static int KING_SAFETY_PAWN_TWO_AWAY_IND = 1;
    public static int KING_SAFETY_PAWN_FAR_AWAY_IND = 2;
    public static int KING_SAFETY_MIDDLE_OPEN_FILE_IND = 3;
    public static int KING_PST_IND = 4;
    public static int KING_ENDGAME_PST_IND = 68;
    public static int BISHOP_PST_IND = 132;
    public static int KNIGHT_PST_IND = 196;
    public static int KNIGHT_TROPISM_IND = 260;
    public static int ROOK_PST_IND = 261;
    public static int ROOK_OPEN_FILE_IND = 325;
    public static int ROOK_HALF_OPEN_FILE_IND = 326;
}
