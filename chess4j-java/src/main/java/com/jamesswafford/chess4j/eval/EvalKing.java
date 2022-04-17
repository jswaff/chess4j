package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;

import static com.jamesswafford.chess4j.board.squares.File.FILE_D;
import static com.jamesswafford.chess4j.board.squares.File.FILE_E;
import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static com.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;

import static com.jamesswafford.chess4j.eval.EvalWeights.*;

public class EvalKing {

    // returns a score from the perspective of white
    public static int evalKing(EvalWeights weights, Board board, Square kingSq, boolean endgame) {

        assert (kingSq == board.getKingSquare(Color.WHITE) || kingSq == board.getKingSquare(Color.BLACK));

        int score = 0;

        if (kingSq == board.getKingSquare(Color.WHITE)) {
            if (endgame) {
                score += weights.vals[KING_ENDGAME_PST_IND + kingSq.value()];
            } else {
                score += weights.vals[KING_PST_IND + kingSq.value()];
                score += evalKingSafety(weights, board, true);
            }
        } else {
            if (endgame) {
                score += weights.vals[KING_ENDGAME_PST_IND + kingSq.flipVertical().value()];
            } else {
                score += weights.vals[KING_PST_IND + kingSq.flipVertical().value()];
                score += evalKingSafety(weights, board, false);
            }
        }

        return score;
    }

    // this will return a score from the perspective of the player
    public static int evalKingSafety(EvalWeights weights, Board board, boolean forWhite) {
        int score = 0;

        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(F2) == WHITE_PAWN) ;
                else if (board.getPiece(F3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(F4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(G2) == WHITE_PAWN) ;
                else if (board.getPiece(G3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(G4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(H2) == WHITE_PAWN) ;
                else if (board.getPiece(H3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(H4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C2) == WHITE_PAWN) ;
                else if (board.getPiece(C3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(C4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(B2) == WHITE_PAWN) ;
                else if (board.getPiece(B3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(B4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(A2) == WHITE_PAWN) ;
                else if (board.getPiece(A3) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(A4) == WHITE_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    score += weights.vals[KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(FILE_E)) {
                if (board.getPiece(F7) == BLACK_PAWN) ;
                else if (board.getPiece(F6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(F5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(G7) == BLACK_PAWN) ;
                else if (board.getPiece(G6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(G5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(H7) == BLACK_PAWN) ;
                else if (board.getPiece(H6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(H5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }
            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C7) == BLACK_PAWN) ;
                else if (board.getPiece(C6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(C5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(B7) == BLACK_PAWN) ;
                else if (board.getPiece(B6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(B5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(A7) == BLACK_PAWN) ;
                else if (board.getPiece(A6) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(A5) == BLACK_PAWN) {
                    score += weights.vals[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    score += weights.vals[KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
        }

        return score;
    }

    public static void extractKingFeatures(double[] features, Board board, Square kingSq, double phase) {
        if (kingSq == board.getKingSquare(Color.WHITE)) {
            features[KING_ENDGAME_PST_IND + kingSq.value()] += (1-phase);
            features[KING_PST_IND + kingSq.value()] += phase;
            extractKingSafetyFeatures(features, board, true);
        } else {
            features[KING_ENDGAME_PST_IND + kingSq.flipVertical().value()] -= (1-phase);
            features[KING_PST_IND + kingSq.flipVertical().value()] -= phase;
            extractKingSafetyFeatures(features, board, false);
        }
    }

    public static void extractKingSafetyFeatures(double[] features, Board board, boolean forWhite) {
        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(F2) == WHITE_PAWN) ;
                else if (board.getPiece(F3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(F4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }

                if (board.getPiece(G2) == WHITE_PAWN) ;
                else if (board.getPiece(G3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(G4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }

                if (board.getPiece(H2) == WHITE_PAWN) ;
                else if (board.getPiece(H3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(H4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }

            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C2) == WHITE_PAWN) ;
                else if (board.getPiece(C3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(C4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }

                if (board.getPiece(B2) == WHITE_PAWN) ;
                else if (board.getPiece(B3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(B4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }

                if (board.getPiece(A2) == WHITE_PAWN) ;
                else if (board.getPiece(A3) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]++;
                } else if (board.getPiece(A4) == WHITE_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]++;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]++;
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    features[KING_SAFETY_MIDDLE_OPEN_FILE_IND]++;
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(FILE_E)) {
                if (board.getPiece(F7) == BLACK_PAWN) ;
                else if (board.getPiece(F6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(F5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }

                if (board.getPiece(G7) == BLACK_PAWN) ;
                else if (board.getPiece(G6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(G5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }

                if (board.getPiece(H7) == BLACK_PAWN) ;
                else if (board.getPiece(H6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(H5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }
            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C7) == BLACK_PAWN) ;
                else if (board.getPiece(C6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(C5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }

                if (board.getPiece(B7) == BLACK_PAWN) ;
                else if (board.getPiece(B6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(B5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }

                if (board.getPiece(A7) == BLACK_PAWN) ;
                else if (board.getPiece(A6) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_ONE_AWAY_IND]--;
                } else if (board.getPiece(A5) == BLACK_PAWN) {
                    features[KING_SAFETY_PAWN_TWO_AWAY_IND]--;
                } else {
                    features[KING_SAFETY_PAWN_FAR_AWAY_IND]--;
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    features[KING_SAFETY_MIDDLE_OPEN_FILE_IND]--;
                }
            }
        }
    }

}