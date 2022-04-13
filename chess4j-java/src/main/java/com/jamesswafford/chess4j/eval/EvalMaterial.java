package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.pieces.*;

import static com.jamesswafford.chess4j.pieces.Bishop.BLACK_BISHOP;
import static com.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static com.jamesswafford.chess4j.pieces.Knight.BLACK_KNIGHT;
import static com.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static com.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static com.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static com.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static com.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static com.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static com.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;
import static com.jamesswafford.chess4j.eval.MaterialType.*;

public class EvalMaterial {

    public static int evalMaterial(EvalWeights weights, Board board) {
        int pawnMaterial =
                (board.getNumPieces(WHITE_PAWN) - board.getNumPieces(BLACK_PAWN))  * weights.vals[EvalWeights.PAWN_VAL_IND];
        return pawnMaterial
                + evalNonPawnMaterial(weights, board, true)
                - evalNonPawnMaterial(weights, board, false);
    }

    public static int evalNonPawnMaterial(EvalWeights weights, Board board, boolean forWhite) {

        if (forWhite) {
            int numPawns = board.getNumPieces(WHITE_PAWN);

            // raise the knight's value 1/16 for each pawn above 5, and lower for each
            // pawn below 5.
            int knightAdj = 0; // FIXME (numPawns - 5) * 6;

            // lower the rook's value 1/8 for each pawn above 5, and raise for each
            // pawn above 5.
            int rookAdj = 0; // FIXME (numPawns - 5) * -12;

            return board.getNumPieces(WHITE_QUEEN) * weights.vals[EvalWeights.QUEEN_VAL_IND]
                    + board.getNumPieces(WHITE_ROOK) * (weights.vals[EvalWeights.ROOK_VAL_IND] + rookAdj)
                    + board.getNumPieces(WHITE_KNIGHT) * (weights.vals[EvalWeights.KNIGHT_VAL_IND] + knightAdj)
                    + board.getNumPieces(WHITE_BISHOP) * weights.vals[EvalWeights.BISHOP_VAL_IND]
                    + (board.getNumPieces(WHITE_BISHOP) > 1 ? weights.vals[EvalWeights.BISHOP_PAIR_IND]: 0);
        } else {
            int numPawns = board.getNumPieces(BLACK_PAWN);

            // raise the knight's value 1/16 for each pawn above 5, and lower for each
            // pawn below 5.
            int knightAdj = 0; // FIXME (numPawns - 5) * 6;

            // lower the rook's value 1/8 for each pawn above 5, and raise for each
            // pawn below 5.
            int rookAdj = 0; // FIXME (numPawns - 5) * -12;

            return board.getNumPieces(BLACK_QUEEN) * weights.vals[EvalWeights.QUEEN_VAL_IND]
                    + board.getNumPieces(BLACK_ROOK) * (weights.vals[EvalWeights.ROOK_VAL_IND] + rookAdj)
                    + board.getNumPieces(BLACK_KNIGHT) * (weights.vals[EvalWeights.KNIGHT_VAL_IND] + knightAdj)
                    + board.getNumPieces(BLACK_BISHOP) * weights.vals[EvalWeights.BISHOP_VAL_IND]
                    + (board.getNumPieces(BLACK_BISHOP) > 1 ? weights.vals[EvalWeights.BISHOP_PAIR_IND]: 0);
        }
    }

    public static void extractMaterialFeatures(int[] features, Board board) {
        features[EvalWeights.PAWN_VAL_IND] = board.getNumPieces(WHITE_PAWN) - board.getNumPieces(BLACK_PAWN);
        features[EvalWeights.QUEEN_VAL_IND] = board.getNumPieces(WHITE_QUEEN) - board.getNumPieces(BLACK_QUEEN);
        features[EvalWeights.ROOK_VAL_IND] = board.getNumPieces(WHITE_ROOK) - board.getNumPieces(BLACK_ROOK);
        features[EvalWeights.KNIGHT_VAL_IND] = board.getNumPieces(WHITE_KNIGHT) - board.getNumPieces(BLACK_KNIGHT);
        features[EvalWeights.BISHOP_VAL_IND] = board.getNumPieces(WHITE_BISHOP) - board.getNumPieces(BLACK_BISHOP);
        features[EvalWeights.BISHOP_PAIR_IND] = (board.getNumPieces(WHITE_BISHOP) > 1 ? 1 : 0) -
                (board.getNumPieces(BLACK_BISHOP) > 1 ? 1 : 0);
    }

    public static int evalPiece(Piece piece) {
        EvalWeights weights = Globals.getEvalWeights();
        if (piece instanceof Pawn) return weights.vals[EvalWeights.PAWN_VAL_IND];
        if (piece instanceof Knight) return weights.vals[EvalWeights.KNIGHT_VAL_IND];
        if (piece instanceof Bishop) return weights.vals[EvalWeights.BISHOP_VAL_IND];
        if (piece instanceof Rook) return weights.vals[EvalWeights.ROOK_VAL_IND];
        if (piece instanceof Queen) return weights.vals[EvalWeights.QUEEN_VAL_IND];
        if (piece instanceof King) return Constants.INFINITY;
        throw new IllegalArgumentException("Illegal argument for piece: " + piece);
    }

    public static MaterialType calculateMaterialType(Board board) {

        int numWhitePawns = board.getNumPieces(WHITE_PAWN);
        int numWhiteBishops = board.getNumPieces(WHITE_BISHOP);
        int numWhiteKnights = board.getNumPieces(WHITE_KNIGHT);
        int numWhiteRooks = board.getNumPieces(WHITE_ROOK);
        int numWhiteQueens = board.getNumPieces(WHITE_QUEEN);
        int numWhiteTotal = numWhitePawns + numWhiteBishops + numWhiteKnights + numWhiteRooks + numWhiteQueens;

        int numBlackPawns = board.getNumPieces(BLACK_PAWN);
        int numBlackBishops = board.getNumPieces(BLACK_BISHOP);
        int numBlackKnights = board.getNumPieces(BLACK_KNIGHT);
        int numBlackRooks = board.getNumPieces(BLACK_ROOK);
        int numBlackQueens = board.getNumPieces(BLACK_QUEEN);
        int numBlackTotal = numBlackPawns + numBlackBishops + numBlackKnights + numBlackRooks + numBlackQueens;

        if (numWhiteTotal > 2 || numBlackTotal > 2) return OTHER;

        if (numWhiteTotal == 0) {
            if (numBlackTotal == 0) {
                return KK;
            } else if (numBlackTotal == 1) {
                if (numBlackKnights == 1) {
                    return KKN;
                } else if (numBlackBishops == 1) {
                    return KKB;
                }
            } else if (numBlackTotal == 2) {
                if (numBlackKnights == 2) {
                    return KKNN;
                }
            }
        } else if (numWhiteTotal == 1) {
            if (numWhitePawns == 1) {
                if (numBlackTotal == 1) {
                    if (numBlackKnights == 1) {
                        return KPKN;
                    } else if (numBlackBishops == 1) {
                        return KPKB;
                    }
                }
            } else if (numWhiteKnights == 1) {
                if (numBlackTotal == 0) {
                    return KNK;
                } else if (numBlackTotal == 1) {
                    if (numBlackPawns == 1) {
                        return KNKP;
                    } else if (numBlackKnights == 1) {
                        return KNKN;
                    } else if (numBlackBishops == 1) {
                        return KNKB;
                    }
                }
            } else if (numWhiteBishops == 1) {
                if (numBlackTotal == 0) {
                    return KBK;
                } else if (numBlackTotal == 1) {
                    if (numBlackPawns == 1) {
                        return KBKP;
                    } else if (numBlackKnights == 1) {
                        return KBKN;
                    } else if (numBlackBishops == 1) {
                        return KBKB;
                    }
                }
            }
        } else if (numWhiteTotal == 2) {
            if (numWhiteKnights == 2) {
                if (numBlackTotal == 0) {
                    return KNNK;
                }
            }
        }

        return OTHER;
    }
}
