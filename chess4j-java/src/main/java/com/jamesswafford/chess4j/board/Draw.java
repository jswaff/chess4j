package com.jamesswafford.chess4j.board;

import com.jamesswafford.chess4j.board.squares.Square;

import java.util.List;

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

public class Draw {

    public static boolean isDraw(Board board, List<Undo> undos) {
        return isDrawBy50MoveRule(board) || isDrawLackOfMaterial(board) ||
                isDrawByRep(board, undos);
    }

    public static boolean isDrawBy50MoveRule(Board board) {
        return board.getFiftyCounter() >= 100;
    }

    /**
     * Determine if a position is drawn by lack of mating material.
     *
     * From the xboard documentation:
     * Note that (in accordance with FIDE rules) only KK, KNK, KBK and KBKB with
     * all bishops on the same color can be claimed as draws on the basis of
     * insufficient mating material. The end-games KNNK, KBKN, KNKN and KBKB with
     * unlike bishops do have mate positions, and cannot be claimed. Complex draws
     * based on locked Pawn chains will not be recognized as draws by most
     * interfaces, so do not claim in such positions, but just offer a draw or play
     * on.
     *
     * @param board
     * @return
     */
    public static boolean isDrawLackOfMaterial(Board board) {

        if (board.getNumPieces(BLACK_PAWN) > 0 || board.getNumPieces(WHITE_PAWN) > 0 ||
            board.getNumPieces(BLACK_ROOK) > 0 || board.getNumPieces(WHITE_ROOK) > 0 ||
            board.getNumPieces(BLACK_QUEEN) > 0 || board.getNumPieces(WHITE_QUEEN) > 0)
        {
            return false;
        }

        int numBlackKnights = board.getNumPieces(BLACK_KNIGHT);
        int numWhiteKnights = board.getNumPieces(WHITE_KNIGHT);
        int numKnights = numBlackKnights + numWhiteKnights;

        int numBlackBishops = board.getNumPieces(BLACK_BISHOP);
        int numWhiteBishops = board.getNumPieces(WHITE_BISHOP);
        int numBishops = numBlackBishops + numWhiteBishops;

        // if there are any knights at all, this must be a KNK ending to be a draw.
        if (numKnights > 1) {
            return false;
        }
        if (numKnights == 1 && numBishops > 0) {
            return false;
        }

        // if there is more than one bishop on either side, it isn't a draw.
        if (numBlackBishops > 1 || numWhiteBishops > 1) {
            return false;
        }

        // are there opposing bishops on different color squares? - not a draw
        if (numWhiteBishops == 1 && numBlackBishops == 1) {

            Square wSq = Square.valueOf(Bitboard.lsb(board.getWhiteBishops()));
            Square bSq = Square.valueOf(Bitboard.lsb(board.getBlackBishops()));

            if (wSq.isLight() != bSq.isLight()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDrawByRep(Board board, List<Undo> undos) {
        long currentZobristKey = board.getZobristKey();

        long numPrevVisits = undos.stream()
                .filter(u -> u.getZobristKey() == currentZobristKey)
                .count();

        return numPrevVisits >= 2L;
    }

}
