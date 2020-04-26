package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Piece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DrawBoard {
    private static final  Logger LOGGER = LogManager.getLogger(DrawBoard.class);

    public static void drawBoard(Board b) {
        StringBuffer board = new StringBuffer();

        for (Rank r : Rank.values()) {
            for (File f : File.values()) {
                Piece p = b.getPiece(Square.valueOf(f, r));
                board.append(p==null?"-":p.toString());
                if (f.equals(File.FILE_H)) {
                    if (r.equals(Rank.RANK_7)) {
                        if (b.getPlayerToMove().isWhite()) {
                            board.append("\twhite to move");
                        } else {
                            board.append("\tblack to move");
                        }
                    } else if (r.equals(Rank.RANK_6)) {
                        board.append("\tcastling rights: ");
                        CastlingRights[] crs = CastlingRights.values();
                        for (CastlingRights cr : crs) {
                            if (b.hasCastlingRight(cr)) {
                                board.append(cr.getLabel());
                            }
                        }
                    } else if (r.equals(Rank.RANK_5)) {
                        board.append("\t");
                        board.append(b.getEPSquare()==null?"no ep":("ep=" + b.getEPSquare()));
                    } else if (r.equals(Rank.RANK_4)) {
                        board.append("\tfifty=" + b.getFiftyCounter());
                    } else if (r.equals(Rank.RANK_3)) {
                        board.append("\tmove counter=" + b.getMoveCounter());
                    }
                }
            }
            board.append("\n");
        }

        LOGGER.info(board);
    }

}
