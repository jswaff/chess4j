package com.jamesswafford.chess4j.eval;

public class EvalTermsVector {

    public int[] terms = new int[] {
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
             15,  // ROOK_HALF_OPEN_FILE
             -1, -1, -1, -1, -1, -1, -1, -1,     // QUEEN_PST
             -1,  0,  0,  0,  0,  0,  0, -1,
             -1,  0,  1,  1,  1,  1,  0, -1,
             -1,  0,  1,  2,  2,  1,  0, -1,
             -1,  0,  1,  2,  2,  1,  0, -1,
             -1,  0,  1,  1,  1,  1,  0, -1,
             -1,  0,  0,  0,  0,  0,  0, -1,
             -1, -1, -1, -1, -1, -1, -1, -1,
             50,  // MAJOR_ON_7TH
             80,  // CONNECTED_MAJORS_ON_7TH
              0,  0,  0,  0,  0,  0,  0,  0,     // PAWN_PST
             30, 30, 30, 30, 30, 30, 30, 30,
             14, 14, 14, 18, 18, 14, 14, 14,
              7,  7,  7, 10, 10,  7,  7,  7,
              5,  5,  5,  7,  7,  5,  5,  5,
              3,  3,  3,  5,  5,  3,  3,  3,
              0,  0,  0, -3, -3,  0,  0,  0,
              0,  0,  0,  0,  0,  0,  0,  0,
             20,  // PASSED_PAWN
            -20,  // ISOLATED_PAWN
            -10   // DOUBLED_PAWN
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

}
