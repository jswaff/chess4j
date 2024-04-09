package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Piece;

public final class FENBuilder {

    private FENBuilder() { }

    public static String createFen(Board b, boolean includeMoveFields) {

        StringBuilder sb = new StringBuilder();

        // get a string of characters for each rank
        for (int ri=0; ri<8; ri++) { // ri(0) = Rank8
            Rank r = Rank.rank(ri);
            int emptyCnt = 0;
            for (int fi=0;fi<8;fi++) {
                File f = File.file(fi);
                Square sq = Square.valueOf(f, r);
                Piece p = b.getPiece(sq);
                if (p != null) {
                    if (emptyCnt != 0) {
                        sb.append(emptyCnt);
                        emptyCnt = 0;
                    }
                    sb.append(p);
                } else {
                    emptyCnt++;
                }
            }
            if (emptyCnt > 0) {
                sb.append(emptyCnt);
            }
            if (ri < 7) {
                sb.append("/");
            }
        }

        // player
        sb.append(" ").append(b.getPlayerToMove() == Color.WHITE ? "w" : "b");

        // castling rights
        sb.append(" ");
        if (b.hasCastlingRight(CastlingRights.WHITE_KINGSIDE)) {
            sb.append("K");
        }
        if (b.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE)) {
            sb.append("Q");
        }
        if (b.hasCastlingRight(CastlingRights.BLACK_KINGSIDE)) {
            sb.append("k");
        }
        if (b.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE)) {
            sb.append("q");
        }
        if (!b.hasCastlingRight(CastlingRights.WHITE_KINGSIDE) &&
                !b.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE) &&
                !b.hasCastlingRight(CastlingRights.BLACK_KINGSIDE) &&
                !b.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE))
        {
            sb.append("-");
        }

        // ep square
        sb.append(" ");
        if (b.getEPSquare() != null) {
            sb.append(b.getEPSquare());
        } else {
            sb.append("-");
        }

        if (includeMoveFields) {
            // half move clock
            sb.append(" ").append(b.getFiftyCounter());

            // full move counter
            sb.append(" ");
            int fenMoves = b.getMoveCounter();
            if (b.getPlayerToMove() == Color.BLACK) {
                fenMoves--;
            }
            fenMoves /= 2;
            fenMoves++;
            sb.append(fenMoves);
        }

        return sb.toString();
    }

}
