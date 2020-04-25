package com.jamesswafford.chess4j.utils;

import java.util.Collections;
import java.util.List;

import com.jamesswafford.chess4j.App;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.pieces.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;


public final class MoveUtils {

    private static final  Logger LOGGER = LogManager.getLogger(MoveUtils.class);

    private MoveUtils() { }

    public static void putMoveAtTop(List<Move> moves,Move m) {
        if (moves.remove(m)) {
            moves.add(0, m);
        }
    }

    public static void putMoveAtTop(Move[] moves,Move m) {

        for (int i=1;i<moves.length;i++) {
            if (moves[i].equals(m)) {
                swap(moves,0,i);
                break;
            }
        }
    }

    public static void swap(List<Move> moves,int ind1,int ind2) {
        Collections.swap(moves, ind1, ind2);
    }

    public static void swap(Move[] moves,int ind1,int ind2) {
        Move tmp = moves[ind1];
        moves[ind1] = moves[ind2];
        moves[ind2] = tmp;
    }

    public static int indexOf(List<Move> moves,Move move,int from) {
        for (int i=from;i<moves.size();i++) {
            if (move.equals(moves.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(Move[] moves,Move move,int from) {

        for (int i=from;i<moves.length;i++) {
            if (move.equals(moves[i])) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isLineValid(List<Move> moveLine, Board board) {
        Board myBoard = board.deepCopy();

        for (Move move : moveLine) {
            if (!isLegalMove(move, myBoard)) {
                LOGGER.debug("# invalid line! - " + PrintLine.getMoveString(moveLine));
                return false;
            }
            myBoard.applyMove(move);
        }

        return true;
    }

    public static Move fromNativeMove(Long nativeMove, Color ptm) {
        Square fromSq = Square.valueOf((int)(nativeMove & 0x3F));
        Square toSq = Square.valueOf((int)((nativeMove >> 6) & 0x3F));
        Piece piece = fromNativePiece((int)((nativeMove >> 12) & 0x07), ptm);
        Piece promoPiece = fromNativePiece((int)((nativeMove >> 15) & 0x07), ptm);
        Piece capturedPiece = fromNativePiece((int)((nativeMove >> 18) & 0x07), Color.swap(ptm));
        boolean isEpCapture = (int)((nativeMove >> 21) & 0x01) == 1;
        boolean isCastle = (int)((nativeMove >> 22) & 0x01) == 1;

        Move converted = new Move(piece, fromSq, toSq, capturedPiece, promoPiece, isCastle, isEpCapture);

        assert (toNativeMove(converted).equals(nativeMove));

        return converted;
    }

    public static Long toNativeMove(Move mv) {
        Long nativeMv = 0L;

        nativeMv = (long)mv.from().value() & 0x3F;
        nativeMv |= ((long)mv.to().value() & 0x3F) << 6;
        nativeMv |= (toNativePiece(mv.piece()) & 0x07) << 12;
        if (mv.promotion() != null) {
            nativeMv |= (toNativePiece(mv.promotion()) & 0x07) << 15;
        }
        if (mv.captured() != null) {
            nativeMv |= (toNativePiece(mv.captured()) & 0x07) << 18;
        }
        if (mv.isEpCapture()) {
            nativeMv |= 1L << 21;
        }
        if (mv.isCastle()) {
            nativeMv |= 1L << 22;
        }

        return nativeMv;
    }

    private static Piece fromNativePiece(int pieceType, Color pieceColor) {
        boolean isWhite = pieceColor.isWhite();

        switch (pieceType) {
            case 0:
                return null;
            case 1:
                return isWhite ? WHITE_PAWN : BLACK_PAWN;
            case 2:
                return isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case 3:
                return isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case 4:
                return isWhite ? WHITE_ROOK : BLACK_ROOK;
            case 5:
                return isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case 6:
                return isWhite ? WHITE_KING : BLACK_KING;
            default:
                throw new IllegalArgumentException("Don't know how to translate native piece: " + pieceType);
        }
    }

    private static long toNativePiece(Piece piece) {
        if (piece.getClass() == Pawn.class) {
            return 1;
        } else if (piece.getClass() == Knight.class) {
            return 2;
        } else if (piece.getClass() == Bishop.class) {
            return 3;
        } else if (piece.getClass() == Rook.class) {
            return 4;
        } else if (piece.getClass() == Queen.class) {
            return 5;
        } else if (piece.getClass() == King.class) {
            return 6;
        }
        throw new IllegalArgumentException("Invalid piece type in toNativePiece: " + piece);
    }

    private static boolean isLegalMove(Move move, Board board) {
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(board);
        return legalMoves.contains(move);
    }

}
