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
        pieceToLongMap.put(Pawn.BLACK_PAWN, 0L);
        pieceToLongMap.put(Pawn.WHITE_PAWN, 8L);

        pieceToLongMap.put(Knight.BLACK_KNIGHT, 1L);
        pieceToLongMap.put(Knight.WHITE_KNIGHT, 9L);

        pieceToLongMap.put(Bishop.BLACK_BISHOP, 2L);
        pieceToLongMap.put(Bishop.WHITE_BISHOP, 10L);

        pieceToLongMap.put(Rook.BLACK_ROOK, 3L);
        pieceToLongMap.put(Rook.WHITE_ROOK, 11L);

        pieceToLongMap.put(Queen.BLACK_QUEEN, 4L);
        pieceToLongMap.put(Queen.WHITE_QUEEN, 12L);

        pieceToLongMap.put(King.BLACK_KING, 5L);
        pieceToLongMap.put(King.WHITE_KING, 13L);

        // create the map linking the other direction
        for (Piece p : pieceToLongMap.keySet()) {
            longToPieceMap.put(pieceToLongMap.get(p),p);
        }
    }


    public TranspositionTableEntry(long zobristKey, TranspositionTableEntryType entryType, int score, int depth,
                                   Move move) {
        this.zobristKey = zobristKey;
        buildStoredValue(entryType,score,depth,move);
    }

    public TranspositionTableEntry(long zobristKey, long val) {
        this.zobristKey = zobristKey;
        this.val = val;
    }

    private void buildStoredValue(TranspositionTableEntryType entryType, int score, int depth, Move move) {
        // bits 0-1 are the entry type
        val = entryType.ordinal();
        assert(val <= 3);

        val |= ((long)depth) << 2;

        if (score > 0) {
            val |= ((long)score)<<18;
        } else {
            val |= ((long)-score)<<18;
            val |= 1L << 34;
        }

        // move from square
        if (move != null) {
            val |= ((long)move.from().value()) << 35;
            val |= ((long)move.to().value()) << 41;
            val |= pieceToLongMap.get(move.piece()) << 47;
            if (move.captured() != null) {
                val |= 1L << 51;
                val |= pieceToLongMap.get(move.captured()) << 52;
            }
            if (move.promotion() != null) {
                val |= 1L << 56;
                val |= pieceToLongMap.get(move.promotion()) << 57;
            }
            if (move.isCastle()) {
                val |= 1L << 61;
            }
            if (move.isEpCapture()) {
                val |= 1L << 62;
            }
        }
    }

    public TranspositionTableEntryType getType() {
        return TranspositionTableEntryType.values[(int)(val & 3)];
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public int getScore() {
        int score = (int)((val >> 18) & 0xFFFF);
        if (((val >> 34) & 1) == 1) {
            score = -score;
        }
        return score;
    }

    public Move getMove() {
        Move move = null;

        if (((val >> 35) & 67108863) > 0) { // 2^26 - 1
            Square fromSq = Square.valueOf((int)(val >> 35) & 63);
            Square toSq = Square.valueOf((int)(val >> 41) & 63);
            Piece piece = longToPieceMap.get((val >> 47) & 15L);
            Piece captured = null;
            if (((val >> 51) & 1) == 1) {
                captured = longToPieceMap.get((val >> 52) & 15L);
            }

            Piece promotion = null;
            if (((val >> 56) & 1) == 1) {
                promotion = longToPieceMap.get((val >> 57) & 15L);
            }

            boolean castle = ((val >> 61) & 1)==1;
            boolean epCapture = ((val >> 62) & 1)==1;

            move = new Move(piece, fromSq, toSq, captured, promotion, castle, epCapture);
        }

        return move;
    }

    public int getDepth() {
        return (int)((val >> 2) & 0xFFFF);
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
