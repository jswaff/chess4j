package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Bitboard;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.board.squares.File;
import dev.jamesswafford.chess4j.pieces.Pawn;
import io.vavr.Tuple2;

public class EvalKing {

    // returns a score from the perspective of white
    public static Tuple2<Integer, Integer> evalKing(EvalWeights weights, Board board, Square kingSq) {

        assert (kingSq == board.getKingSquare(Color.WHITE) || kingSq == board.getKingSquare(Color.BLACK));

        int mg, eg;

        if (kingSq == board.getKingSquare(Color.WHITE)) {
            mg = weights.vals[EvalWeights.KING_PST_MG_IND + kingSq.value()] + evalKingSafety(weights, board, true);
            eg = weights.vals[EvalWeights.KING_PST_EG_IND + kingSq.value()];
        } else {
            mg = -(weights.vals[EvalWeights.KING_PST_MG_IND + kingSq.flipVertical().value()] + evalKingSafety(weights, board, false));
            eg = -weights.vals[EvalWeights.KING_PST_EG_IND + kingSq.flipVertical().value()];
        }

        return new Tuple2<>(mg, eg);
    }

    // this will return a score from the perspective of the player
    public static int evalKingSafety(EvalWeights weights, Board board, boolean forWhite) {
        int score = 0;

        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(File.FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(Square.F2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.F3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.F4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.G2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.G3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.G4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.H2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.H3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.H4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND];
                }

            } else if (kingSq.file().westOf(File.FILE_D)) {
                if (board.getPiece(Square.C2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.C3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.C4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.B2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.B3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.B4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.A2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.A3) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.A4) == Pawn.WHITE_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND];
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    score += weights.vals[EvalWeights.KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(File.FILE_E)) {
                if (board.getPiece(Square.F7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.F6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.F5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.G7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.G6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.G5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.H7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.H6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.H5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND];
                }
            } else if (kingSq.file().westOf(File.FILE_D)) {
                if (board.getPiece(Square.C7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.C6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.C5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.B7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.B6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.B5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND];
                }

                if (board.getPiece(Square.A7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.A6) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND];
                } else if (board.getPiece(Square.A5) == Pawn.BLACK_PAWN) {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND];
                } else {
                    score += weights.vals[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND];
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    score += weights.vals[EvalWeights.KING_SAFETY_MIDDLE_OPEN_FILE_IND];
                }
            }
        }

        return score;
    }

    public static void extractKingFeatures(double[] features, Board board, Square kingSq, double phase) {
        if (kingSq == board.getKingSquare(Color.WHITE)) {
            features[EvalWeights.KING_PST_EG_IND + kingSq.value()] += (1-phase);
            features[EvalWeights.KING_PST_MG_IND + kingSq.value()] += phase;
            extractKingSafetyFeatures(features, board, true, phase);
        } else {
            features[EvalWeights.KING_PST_EG_IND + kingSq.flipVertical().value()] -= (1-phase);
            features[EvalWeights.KING_PST_MG_IND + kingSq.flipVertical().value()] -= phase;
            extractKingSafetyFeatures(features, board, false, phase);
        }
    }

    public static void extractKingSafetyFeatures(double[] features, Board board, boolean forWhite, double phase) {
        Square kingSq;
        if (forWhite) {
            kingSq = board.getKingSquare(Color.WHITE);
            // which side are we on?
            if (kingSq.file().eastOf(File.FILE_E)) {
                // check that pawns on f,g,h are not too far away
                if (board.getPiece(Square.F2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.F3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.F4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] += phase;
                }

                if (board.getPiece(Square.G2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.G3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.G4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] += phase;
                }

                if (board.getPiece(Square.H2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.H3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.H4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND] += phase;
                }

            } else if (kingSq.file().westOf(File.FILE_D)) {
                if (board.getPiece(Square.C2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.C3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.C4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] += phase;
                }

                if (board.getPiece(Square.B2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.B3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.B4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] += phase;
                }

                if (board.getPiece(Square.A2) == Pawn.WHITE_PAWN) ;
                else if (board.getPiece(Square.A3) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND] += phase;
                } else if (board.getPiece(Square.A4) == Pawn.WHITE_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND] += phase;
                } else {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND] += phase;
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    features[EvalWeights.KING_SAFETY_MIDDLE_OPEN_FILE_IND] += phase;
                }
            }
            // scale down with material?
        } else {
            kingSq = board.getKingSquare(Color.BLACK);
            if (kingSq.file().eastOf(File.FILE_E)) {
                if (board.getPiece(Square.F7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.F6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.F5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] -= phase;
                }

                if (board.getPiece(Square.G7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.G6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.G5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] -= phase;
                }

                if (board.getPiece(Square.H7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.H6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.H5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND] -= phase;
                }
            } else if (kingSq.file().westOf(File.FILE_D)) {
                if (board.getPiece(Square.C7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.C6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.C5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] -= phase;
                }

                if (board.getPiece(Square.B7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.B6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.B5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_PAWN_FAR_AWAY_IND] -= phase;
                }

                if (board.getPiece(Square.A7) == Pawn.BLACK_PAWN) ;
                else if (board.getPiece(Square.A6) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_ONE_AWAY_IND] -= phase;
                } else if (board.getPiece(Square.A5) == Pawn.BLACK_PAWN) {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_TWO_AWAY_IND] -= phase;
                } else {
                    features[EvalWeights.KING_SAFETY_WING_PAWN_FAR_AWAY_IND] -= phase;
                }
            } else {
                // check if open file
                if (((board.getWhitePawns() | board.getBlackPawns())
                        & Bitboard.files[kingSq.file().getValue()]) == 0) {
                    features[EvalWeights.KING_SAFETY_MIDDLE_OPEN_FILE_IND] -= phase;
                }
            }
        }
    }

}