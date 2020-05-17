package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.book.BookMove;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PrintBookMoves {

    private static final Logger LOGGER = LogManager.getLogger(PrintBookMoves.class);

    public static void printBookMoves(Board board) {
        Globals.getOpeningBook().ifPresentOrElse(
                book -> {
                    List<BookMove> bookMoves = book.getMoves(board);
                    bookMoves.sort((BookMove bm1, BookMove bm2) -> bm2.getFrequency() - bm1.getFrequency());

                    LOGGER.info("# book moves:");
                    for (BookMove bookMove : bookMoves) {
                        LOGGER.info("\t" + bookMove.getMove() + " - freq: " + bookMove.getFrequency()
                                + ", w/l/d: " + bookMove.getWins() + " / " + bookMove.getLosses()
                                + " / " + bookMove.getDraws());
                    }
                },
                () -> LOGGER.info("Opening book not enabled.")
        );

        LOGGER.info(""); // blank line required by protocol
    }

}
