package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.*;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTableEntry {

    private final long zobristKey;
    private long val;

    private static final Map<Piece,Long> pieceToLongMap;
    private static final Map<Long,Piece> longToPieceMap;

    static {
        pieceToLongMap = new HashMap<>();
        longToPieceMap = new HashMap<>();

        // first three bits are for piece.  4th bit is for color (1=White)
        pieceToLongMap.put(Pawn.BLACK_PAWN, 1L);
        pieceToLongMap.put(Pawn.WHITE_PAWN, 9L);

        pieceToLongMap.put(Knight.BLACK_KNIGHT, 2L);
        pieceToLongMap.put(Knight.WHITE_KNIGHT, 10L);

        pieceToLongMap.put(Bishop.BLACK_BISHOP, 3L);
        pieceToLongMap.put(Bishop.WHITE_BISHOP, 11L);

        pieceToLongMap.put(Rook.BLACK_ROOK, 4L);
        pieceToLongMap.put(Rook.WHITE_ROOK, 12L);

        pieceToLongMap.put(Queen.BLACK_QUEEN, 5L);
        pieceToLongMap.put(Queen.WHITE_QUEEN, 13L);

        pieceToLongMap.put(King.BLACK_KING, 6L);
        pieceToLongMap.put(King.WHITE_KING, 14L);

        // create the map linking the other direction
        for (Piece p : pieceToLongMap.keySet()) {
            longToPieceMap.put(pieceToLongMap.get(p),p);
        }
    }


    public TranspositionTableEntry(long zobristKey, TranspositionTableEntryType entryType, int score, int depth,
                                   Move move) {
        this.zobristKey = zobristKey;
        buildStoredValue(entryType, score, depth, move);
    }

    public TranspositionTableEntry(long zobristKey, long val) {
        this.zobristKey = zobristKey;
        this.val = val;
    }

    private void buildStoredValue(TranspositionTableEntryType entryType, int score, int depth, Move move) {
        // bits 0-1 are the entry type
        val = entryType.ordinal();
        assert(val <= 3);

        assert(depth >= 0);
        assert(depth < 256);
        val |= ((long)depth) << 2;

        assert(score >= -32767);
        assert(score <= 32767);
        val |= ((long)score + 32767) << 10;

        // move from square
        if (move != null) {
            val |= ((long)move.from().value()) << 26;
            val |= ((long)move.to().value()) << 32;
            val |= pieceToLongMap.get(move.piece()) << 38;
            if (move.captured() != null) {
                val |= pieceToLongMap.get(move.captured()) << 42;
            }
            if (move.promotion() != null) {
                val |= pieceToLongMap.get(move.promotion()) << 46;
            }
            if (move.isCastle()) {
                val |= 1L << 50;
            }
            if (move.isEpCapture()) {
                val |= 1L << 51;
            }
        }
    }

    public TranspositionTableEntryType getType() {
        return TranspositionTableEntryType.values[(int)(val & 3)];
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public long getVal() { return val; }

    public int getScore() {
        return (int)((val >> 10) & 0xFFFF) - 32767;
    }

    public Move getMove() {
        Move move = null;

        if (((val >> 26) & 0x3FFFFFF) > 0) {
            Square fromSq = Square.valueOf((int)(val >> 26) & 0x3F);
            Square toSq = Square.valueOf((int)(val >> 32) & 0x3F);
            Piece piece = longToPieceMap.get((val >> 38) & 0xF);
            Piece captured = null;
            if (((val >> 42) & 0xF) > 0) {
                captured = longToPieceMap.get((val >> 42) & 0xF);
            }

            Piece promotion = null;
            if (((val >> 46) & 0xF) > 0) {
                promotion = longToPieceMap.get((val >> 46) & 0xF);
            }

            boolean castle = ((val >> 50) & 1)==1;
            boolean epCapture = ((val >> 51) & 1)==1;

            move = new Move(piece, fromSq, toSq, captured, promotion, castle, epCapture);
        }

        return move;
    }

    public int getDepth() {
        return (int)((val >> 2) & 0xFF);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TranspositionTableEntry)) {
            return false;
        }
        TranspositionTableEntry that = (TranspositionTableEntry)obj;

        return (this.zobristKey == that.zobristKey) && (this.val == that.val);
    }

    @Override
    public int hashCode() {
        int hc = (int)this.zobristKey;
        hc = hc * (int)this.val;

        return hc;
    }

    public static int sizeOf() {
        return Long.SIZE * 2 / Byte.SIZE;
    }
}
