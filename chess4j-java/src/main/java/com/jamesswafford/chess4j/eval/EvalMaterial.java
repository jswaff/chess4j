package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

import static com.jamesswafford.chess4j.pieces.Bishop.BLACK_BISHOP;
import static com.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static com.jamesswafford.chess4j.pieces.King.BLACK_KING;
import static com.jamesswafford.chess4j.pieces.King.WHITE_KING;
import static com.jamesswafford.chess4j.pieces.Knight.BLACK_KNIGHT;
import static com.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static com.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static com.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static com.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static com.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static com.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static com.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;

public class EvalMaterial {

    public static final int QUEEN_VAL  = 975;
    public static final int ROOK_VAL   = 500;
    public static final int KNIGHT_VAL = 325;
    public static final int BISHOP_VAL = 325;
    public static final int PAWN_VAL   = 100;
    public static final int BISHOP_PAIR = 50;

    private static final Map<Piece, Integer> pieceValMap;

    static {
        pieceValMap = new HashMap<>();
        pieceValMap.put(WHITE_KING, Constants.INFINITY);
        pieceValMap.put(WHITE_QUEEN, QUEEN_VAL);
        pieceValMap.put(WHITE_ROOK, ROOK_VAL);
        pieceValMap.put(WHITE_BISHOP, BISHOP_VAL);
        pieceValMap.put(WHITE_KNIGHT, KNIGHT_VAL);
        pieceValMap.put(WHITE_PAWN, PAWN_VAL);

        pieceValMap.put(BLACK_KING, Constants.INFINITY);
        pieceValMap.put(BLACK_QUEEN, QUEEN_VAL);
        pieceValMap.put(BLACK_ROOK, ROOK_VAL);
        pieceValMap.put(BLACK_BISHOP, BISHOP_VAL);
        pieceValMap.put(BLACK_KNIGHT, KNIGHT_VAL);
        pieceValMap.put(BLACK_PAWN, PAWN_VAL);
    }

    public static int evalMaterial(Board board) {
        int pawnMaterial =
                (board.getNumPieces(WHITE_PAWN) - board.getNumPieces(BLACK_PAWN))  * PAWN_VAL;
        return pawnMaterial
                + evalNonPawnMaterial(board, true)
                - evalNonPawnMaterial(board, false);
    }

    public static int evalNonPawnMaterial(Board board, boolean forWhite) {

        if (forWhite) {
            int numPawns = board.getNumPieces(WHITE_PAWN);

            // raise the knight's value 1/16 for each pawn above 5, and lower for each
            // pawn below 5.
            int knightAdj = (numPawns - 5) * 6;

            // lower the rook's value 1/8 for each pawn above 5, and raise for each
            // pawn above 5.
            int rookAdj = (numPawns - 5) * -12;

            return board.getNumPieces(WHITE_QUEEN) * QUEEN_VAL
                    + board.getNumPieces(WHITE_ROOK) * (ROOK_VAL + rookAdj)
                    + board.getNumPieces(WHITE_KNIGHT) * (KNIGHT_VAL + knightAdj)
                    + board.getNumPieces(WHITE_BISHOP) * BISHOP_VAL
                    + (board.getNumPieces(WHITE_BISHOP) > 1 ? BISHOP_PAIR : 0);
        } else {
            int numPawns = board.getNumPieces(BLACK_PAWN);

            // raise the knight's value 1/16 for each pawn above 5, and lower for each
            // pawn below 5.
            int knightAdj = (numPawns - 5) * 6;

            // lower the rook's value 1/8 for each pawn above 5, and raise for each
            // pawn below 5.
            int rookAdj = (numPawns - 5) * -12;

            return board.getNumPieces(BLACK_QUEEN) * QUEEN_VAL
                    + board.getNumPieces(BLACK_ROOK) * (ROOK_VAL + rookAdj)
                    + board.getNumPieces(BLACK_KNIGHT) * (KNIGHT_VAL + knightAdj)
                    + board.getNumPieces(BLACK_BISHOP) * BISHOP_VAL
                    + (board.getNumPieces(BLACK_BISHOP) > 1 ? BISHOP_PAIR: 0);
        }
    }

    public static int evalBishopPair(Board board) {
        int score = 0;
        if (Long.bitCount(board.getWhiteBishops()) > 1) score += BISHOP_PAIR;
        if (Long.bitCount(board.getBlackBishops()) > 1) score -= BISHOP_PAIR;
        return score;
    }

    public static int evalPawnMaterial(Board board, boolean forWhite) {

        if (forWhite) {
            return board.getNumPieces(WHITE_PAWN) * PAWN_VAL;
        } else {
            return board.getNumPieces(BLACK_PAWN) * PAWN_VAL;
        }
    }

    public static int evalPiece(Piece piece) {
        return pieceValMap.get(piece);
    }

}
