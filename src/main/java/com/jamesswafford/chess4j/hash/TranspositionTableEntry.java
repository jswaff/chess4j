package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.*;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTableEntry {

    private long zobristKey;
    private long val;

    private TranspositionTableEntryType entryType;
    private int score;
    private int depth;
    private Move move;

    private static Map<Piece,Long> pieceToLongMap;
    private static Map<Long,Piece> longToPieceMap;

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


    public TranspositionTableEntry(long zobristKey,
            TranspositionTableEntryType entryType,int score,int depth,Move move) {
        this.zobristKey=zobristKey;
        this.entryType=entryType;
        this.score=score;
        this.depth=depth;
        this.move=move;
        buildStoredValue(entryType,score,depth,move);
    }

    private void buildStoredValue(TranspositionTableEntryType entryType,int score,int depth,Move move) {
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
        assert(entryType == TranspositionTableEntryType.values[(int)(val & 3)]);
        return TranspositionTableEntryType.values[(int)(val & 3)];
    }

    public long getZobristKey() {
        return zobristKey;
    }

    public int getScore() {
        int myScore = (int)((val >> 18) & 0xFFFF);
        if (((val >> 34) & 1) == 1) {
            myScore = -myScore;
        }
        assert(myScore == score);
        return myScore;
    }

    public Move getMove() {
        Move myMove = null;

        if (((val >> 35) & 67108863) > 0) { // 2^26 - 1
            Square fromSq = Square.valueOf((int)(val >> 35) & 63);
            assert(fromSq==move.from());

            Square toSq = Square.valueOf((int)(val >> 41) & 63);
            assert(toSq==move.to());

            Piece piece = longToPieceMap.get((val >> 47) & 15L);
            assert(piece==move.piece());

            Piece captured = null;
            if (((val >> 51) & 1) == 1) {
                captured = longToPieceMap.get((val >> 52) & 15L);
                assert(captured==move.captured());
            }

            Piece promotion = null;
            if (((val >> 56) & 1) == 1) {
                promotion = longToPieceMap.get((val >> 57) & 15L);
                assert(promotion==move.promotion());
            }

            boolean castle = ((val >> 61) & 1)==1;
            assert(castle==move.isCastle());

            boolean epCapture = ((val >> 62) & 1)==1;
            assert(epCapture==move.isEpCapture());

            myMove = new Move(piece,fromSq,toSq,captured,promotion,castle,epCapture);

            assert(move.equals(myMove));
        }

        return myMove;
    }

    public int getDepth() {
        assert(depth == (int)((val >> 2) & 0xFFFF));
        return depth;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TranspositionTableEntry)) {
            return false;
        }
        TranspositionTableEntry that = (TranspositionTableEntry)obj;
        if (this.getZobristKey() != that.getZobristKey())
            return false;
        if (this.getScore() != that.getScore())
            return false;
        if (this.getDepth() != that.getDepth())
            return false;
        if (this.getMove()==null) {
            if (that.getMove() != null) {
                return false;
            }
        } else {
            if (!this.getMove().equals(that.getMove())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hc = (int)this.getZobristKey();
        hc = hc * 31 + this.getScore();
        hc = hc * 17 + this.getDepth();
        hc = hc * 31 + (this.getMove()==null ? 0 : this.getMove().hashCode());

        return hc;
    }

}
