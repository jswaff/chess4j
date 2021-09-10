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


    public static final int KING_SAFETY_PAWN_ONE_AWAY = -10;
    public static final int KING_SAFETY_PAWN_TWO_AWAY = -20;
    public static final int KING_SAFETY_PAWN_FAR_AWAY = -30;
    public static final int KING_SAFETY_MIDDLE_OPEN_FILE = -50;

    // returns a score from the perspective of white
    public static int evalKing(Board b, Square kingSq, boolean endGame) {

        assert(kingSq == b.getKingSquare(Color.WHITE) || kingSq == b.getKingSquare(Color.BLACK));

        int score = 0;

        if (kingSq == b.getKingSquare(Color.WHITE)) {
            if (endGame) {
                score += KING_ENDGAME_PST[kingSq.value()];
            } else {
                int enemyNonPawnMat = EvalMaterial.evalNonPawnMaterial(b, false);
                score += KING_PST[kingSq.value()];
                score += Eval.scale(evalKingSafety(b, true), enemyNonPawnMat);
            }
        } else {
            if (endGame) {
                score += KING_ENDGAME_PST[kingSq.flipVertical().value()];
            } else {
                int enemyNonPawnMat = EvalMaterial.evalNonPawnMaterial(b, true);
                score += KING_PST[kingSq.flipVertical().value()];
                score += Eval.scale(evalKingSafety(b, false), enemyNonPawnMat);
            }
        }

        return score;
    }

    // this will return a score from the perspective of the player
    public static int evalKingSafety(Board board, boolean forWhite) {
        int score = 0;

        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(F2) == WHITE_PAWN);
                else if (board.getPiece(F3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(F4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(G2) == WHITE_PAWN);
                else if (board.getPiece(G3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(G4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(H2) == WHITE_PAWN);
                else if (board.getPiece(H3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(H4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }

            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C2) == WHITE_PAWN);
                else if (board.getPiece(C3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(C4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(B2) == WHITE_PAWN);
                else if (board.getPiece(B3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(B4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(A2) == WHITE_PAWN);
                else if (board.getPiece(A3) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(A4) == WHITE_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += KING_SAFETY_MIDDLE_OPEN_FILE;
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(FILE_E)) {
                if (board.getPiece(F7) == BLACK_PAWN);
                else if (board.getPiece(F6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(F5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(G7) == BLACK_PAWN);
                else if (board.getPiece(G6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(G5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(H7) == BLACK_PAWN);
                else if (board.getPiece(H6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(H5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else if (kingSq.file().westOf(FILE_D)) {
                if (board.getPiece(C7) == BLACK_PAWN);
                else if (board.getPiece(C6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(C5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(B7) == BLACK_PAWN);
                else if (board.getPiece(B6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY;
                } else if (board.getPiece(B5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY;
                }

                if (board.getPiece(A7) == BLACK_PAWN);
                else if (board.getPiece(A6) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_ONE_AWAY /2;
                } else if (board.getPiece(A5) == BLACK_PAWN) {
                    score += KING_SAFETY_PAWN_TWO_AWAY /2;
                } else {
                    score += KING_SAFETY_PAWN_FAR_AWAY /2;
                }
            } else {
                // check if open file
                if ( ((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()])==0)
                {
                    score += KING_SAFETY_MIDDLE_OPEN_FILE;
                }
            }
        }

        return score;
    }

}
