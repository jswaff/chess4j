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

import static com.jamesswafford.chess4j.eval.EvalTermsVector.*;

public class EvalKing {

    public static final int[] KING_PST = {
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -30,-30,-30,-30,-30,-30,-30,-30,
            -20,-20,-20,-20,-20,-20,-20,-20,
            -10,-10,-10,-10,-10,-10,-10,-10,
              0, 10, 20,-25,  0,-25, 20,  0
    };

    public static final int[] KING_ENDGAME_PST = {
              0,  0,  0,  0,  0,  0,  0,  0,
              0, 10, 10, 10, 10, 10, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 25, 25, 20, 10,  0,
              0, 10, 20, 20, 20, 20, 10,  0,
              0, 10, 10, 10, 10, 10, 10,  0,
              0,  0,  0,  0,  0,  0,  0,  0 };


    // returns a score from the perspective of white
    public static int evalKing(EvalTermsVector etv, Board b, Square kingSq, boolean endGame) {

        assert(kingSq == b.getKingSquare(Color.WHITE) || kingSq == b.getKingSquare(Color.BLACK));

        int score = 0;

        if (kingSq == b.getKingSquare(Color.WHITE)) {
            if (endGame) {
                score += KING_ENDGAME_PST[kingSq.value()];
            } else {
                score += KING_PST[kingSq.value()];
                score += evalKingSafety(etv, b, true);
            }
        } else {
            if (endGame) {
                score += KING_ENDGAME_PST[kingSq.flipVertical().value()];
            } else {
                score += KING_PST[kingSq.flipVertical().value()];
                score += evalKingSafety(etv, b, false);
            }
        }

        return score;
    }

    // this will return a score from the perspective of the player
    public static int evalKingSafety(EvalTermsVector etv, Board board, boolean forWhite) {
        int score = 0;

        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(F2) == WHITE_PAWN);
                else if (board.getPiece(F3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(F4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(G2) == WHITE_PAWN);
                else if (board.getPiece(G3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(G4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(H2) == WHITE_PAWN);
                else if (board.getPiece(H3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND] /2;
                } else if (board.getPiece(H4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND] /2;
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND] /2;
                }

            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C2) == WHITE_PAWN);
                else if (board.getPiece(C3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(C4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(B2) == WHITE_PAWN);
                else if (board.getPiece(B3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(B4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(A2) == WHITE_PAWN);
                else if (board.getPiece(A3) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND] /2;
                } else if (board.getPiece(A4) == WHITE_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND] /2;
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND] /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(FILE_E)) {
                if (board.getPiece(F7) == BLACK_PAWN);
                else if (board.getPiece(F6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(F5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(G7) == BLACK_PAWN);
                else if (board.getPiece(G6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(G5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(H7) == BLACK_PAWN);
                else if (board.getPiece(H6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND] /2;
                } else if (board.getPiece(H5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND] /2;
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND] /2;
                }
            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C7) == BLACK_PAWN);
                else if (board.getPiece(C6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(C5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(B7) == BLACK_PAWN);
                else if (board.getPiece(B6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(B5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(A7) == BLACK_PAWN);
                else if (board.getPiece(A6) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_ONE_AWAY_IND] /2;
                } else if (board.getPiece(A5) == BLACK_PAWN) {
                    score += etv.terms[KING_SAFETY_PAWN_TWO_AWAY_IND] /2;
                } else {
                    score += etv.terms[KING_SAFETY_PAWN_FAR_AWAY_IND] /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += etv.terms[KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
        }

        return score;
    }

}
