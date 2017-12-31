package com.jamesswafford.chess4j.hash;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.utils.PieceFactory;

public final class Zobrist {

    private static Map<Piece,Long[]> pieceMap = new HashMap<>();
    private static Map<Color,Long> playerMap = new HashMap<>();
    private static Map<CastlingRights,Long> castlingMap = new HashMap<>();
    private static Map<Square,Long> epMap = new HashMap<>();

    private Zobrist() {	}

    private static void createZobristKeys(Piece p) {
        Random r = new Random();
        Long[] keys = new Long[Square.NUM_SQUARES];
        for (int i=0;i<keys.length;i++) {
            keys[i] = r.nextLong();
        }
        pieceMap.put(p, keys);
    }

    static {
        Random r = new Random();
        createZobristKeys(Pawn.BLACK_PAWN);
        createZobristKeys(Pawn.WHITE_PAWN);
        createZobristKeys(Rook.BLACK_ROOK);
        createZobristKeys(Rook.WHITE_ROOK);
        createZobristKeys(Knight.BLACK_KNIGHT);
        createZobristKeys(Knight.WHITE_KNIGHT);
        createZobristKeys(Bishop.BLACK_BISHOP);
        createZobristKeys(Bishop.WHITE_BISHOP);
        createZobristKeys(Queen.BLACK_QUEEN);
        createZobristKeys(Queen.WHITE_QUEEN);
        createZobristKeys(King.BLACK_KING);
        createZobristKeys(King.WHITE_KING);

        playerMap.put(Color.BLACK, r.nextLong());
        playerMap.put(Color.WHITE, r.nextLong());

        Set<CastlingRights> crs = EnumSet.allOf(CastlingRights.class);
        for (CastlingRights cr : crs) {
            castlingMap.put(cr, r.nextLong());
        }

        List<Square> sqs = Square.allSquares();
        for (Square sq : sqs) {
            epMap.put(sq, r.nextLong());
        }
    }

    public static long getPieceKey(Square sq,Piece p) {
        return pieceMap.get(p)[sq.value()];
    }

    public static long getPlayerKey(Color c) {
        return playerMap.get(c);
    }

    public static long getCastlingKey(CastlingRights cr) {
        return castlingMap.get(cr);
    }

    public static long getEnPassantKey(Square sq) {
        return epMap.get(sq);
    }

    public static long getPawnKey(Board b) {

        return Square.allSquares().stream()
                .filter(sq -> b.getPiece(sq) instanceof Pawn)
                .map(sq -> getPieceKey(sq,b.getPiece(sq)))
                .reduce(0L,(x,y) -> x ^ y);
    }

    public static long getBoardKey(Board b) {

        long key = Square.allSquares().stream()
                .filter(sq -> b.getPiece(sq) != null)
                .map(sq -> getPieceKey(sq,b.getPiece(sq)))
                .reduce(0L,(x,y) -> x ^ y);

        key = EnumSet.allOf(CastlingRights.class).stream()
                .filter(cr -> b.hasCastlingRight(cr))
                .map(cr -> castlingMap.get(cr))
                .reduce(key, (x,y) -> x ^ y);

        if (b.getEPSquare() != null) {
            key ^= epMap.get(b.getEPSquare());
        }

        key ^= playerMap.get(b.getPlayerToMove());

        return key;
    }

    public static List<Long> getAllKeys() {
        List<Long> keys = new ArrayList<Long>();

        String[] strPieces = {"P","R","N","B","Q","K","p","r","n","b","q","k"};

        for (String strPiece : strPieces) {
            Piece piece = PieceFactory.getPiece(strPiece);
            Long[] pieceKeys = pieceMap.get(piece);
            for (int i=0;i<Square.NUM_SQUARES;i++) {
                keys.add(pieceKeys[i]);
            }
        }

        keys.add(playerMap.get(Color.WHITE));
        keys.add(playerMap.get(Color.BLACK));

        keys.add(castlingMap.get(CastlingRights.BLACK_QUEENSIDE));
        keys.add(castlingMap.get(CastlingRights.BLACK_KINGSIDE));
        keys.add(castlingMap.get(CastlingRights.WHITE_QUEENSIDE));
        keys.add(castlingMap.get(CastlingRights.WHITE_KINGSIDE));

        for (int i=0;i<Square.NUM_SQUARES;i++) {
            keys.add(epMap.get(Square.valueOf(i)));
        }

        return keys;
    }

    public static void setKeys(List<Long> keys) {
        String[] strPieces = {"P","R","N","B","Q","K","p","r","n","b","q","k"};

        int ind=0;

        for (String strPiece : strPieces) {
            Piece piece = PieceFactory.getPiece(strPiece);
            Long[] pieceKeys = new Long[Square.NUM_SQUARES];
            for (int i=0;i<Square.NUM_SQUARES;i++) {
                pieceKeys[i] = keys.get(ind++);
            }
            pieceMap.put(piece, pieceKeys);
        }

        playerMap.put(Color.WHITE, keys.get(ind++));
        playerMap.put(Color.BLACK, keys.get(ind++));

        castlingMap.put(CastlingRights.BLACK_QUEENSIDE, keys.get(ind++));
        castlingMap.put(CastlingRights.BLACK_KINGSIDE, keys.get(ind++));
        castlingMap.put(CastlingRights.WHITE_QUEENSIDE, keys.get(ind++));
        castlingMap.put(CastlingRights.WHITE_KINGSIDE, keys.get(ind++));

        for (int i=0;i<Square.NUM_SQUARES;i++) {
            epMap.put(Square.valueOf(i), keys.get(ind++));
        }
    }
}
