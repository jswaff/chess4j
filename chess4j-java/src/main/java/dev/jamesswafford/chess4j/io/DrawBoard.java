package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.CastlingRights;
import dev.jamesswafford.chess4j.board.squares.File;
import dev.jamesswafford.chess4j.board.squares.Rank;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.Piece;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DrawBoard {

    private static final  Logger LOGGER = LogManager.getLogger(DrawBoard.class);

    public static void drawBoard(Board board) {
        StringBuffer boardBuffer = new StringBuffer();

        for (Rank r : Rank.values()) {
            for (File f : File.values()) {
                Piece p = board.getPiece(Square.valueOf(f, r));
                boardBuffer.append(p==null?"-":p.toString());
                if (f.equals(File.FILE_H)) {
                    if (r.equals(Rank.RANK_7)) {
                        if (board.getPlayerToMove().isWhite()) {
                            boardBuffer.append("\twhite to move");
                        } else {
                            boardBuffer.append("\tblack to move");
                        }
                    } else if (r.equals(Rank.RANK_6)) {
                        boardBuffer.append("\tcastling rights: ");
                        CastlingRights[] crs = CastlingRights.values();
                        for (CastlingRights cr : crs) {
                            if (board.hasCastlingRight(cr)) {
                                boardBuffer.append(cr.getLabel());
                            }
                        }
                    } else if (r.equals(Rank.RANK_5)) {
                        boardBuffer.append("\t");
                        boardBuffer.append(board.getEPSquare()==null?"no ep":("ep=" + board.getEPSquare()));
                    } else if (r.equals(Rank.RANK_4)) {
                        boardBuffer.append("\tfifty=").append(board.getFiftyCounter());
                    } else if (r.equals(Rank.RANK_3)) {
                        boardBuffer.append("\tmove counter=").append(board.getMoveCounter());
                    }
                }
            }
            boardBuffer.append("\n");
        }

        LOGGER.info(boardBuffer);
    }

}
