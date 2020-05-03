package com.jamesswafford.chess4j.book;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.PgnToBookException;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.io.PGNIterator;
import com.jamesswafford.chess4j.utils.GameResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class AbstractOpeningBook {

    private static final Logger LOGGER = LogManager.getLogger(AbstractOpeningBook.class);

    private final Random random = new Random();

    public abstract void addToBook(Board board, Move move);

    public abstract List<BookMove> getMoves(Board board);

    public abstract long getTotalMoveCount();

    public abstract void initializeBook();

    public void learn(List<Move> moves, Color engineColor, GameResult gameResult) {
        // default impl is a no-op
    }

    public void addIndexes() {
        // default impl is a no-op
    }

    public void addToBook(PGNGame game) {

        Board board = new Board();

        List<Move> gameMoves = game.getMoves();
        int i=0;
        while (i<15 && i<gameMoves.size()) {
            Move gameMove = gameMoves.get(i);
            addToBook(board, gameMove);
            board.applyMove(gameMove);
            i++;
        }
    }

    public void addToBook(File pgnFile) {

        LOGGER.info("processing pgn: " + pgnFile.getName() + " ...");

        try {
            long startTime = System.currentTimeMillis();
            LOGGER.info("starting dry run...");
            int n = processPGNFile(pgnFile,true);
            LOGGER.info("\ndry run complete.  adding " + n + " games to book.");
            processPGNFile(pgnFile,false);
            DecimalFormat df = new DecimalFormat("0.00");
            long elapsed = System.currentTimeMillis() - startTime;
            LOGGER.info("\nfinished in " + df.format((double) elapsed /1000.0) + " seconds.");
        } catch (IOException e) {
            throw new PgnToBookException("Error adding " + pgnFile.getName() + " to opening book", e);
        }
    }

    private int processPGNFile(File pgnFile, boolean dryRun) throws IOException {
        int n = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(pgnFile))) {
            if (!dryRun) {
                dropIndexes();
            }

            PGNIterator it = new PGNIterator(br);
            PGNGame pgnGame;
            while ((pgnGame = it.next()) != null) {
                if ((n % 1000)==0) {
                    LOGGER.info(".");
                }
                if (!dryRun) {
                    addToBook(pgnGame);
                }
                n++;
            }
        } finally {
            if (!dryRun) {
                addIndexes();
            }
        }

        return n;
    }

    public void dropIndexes() {
        // default impl is a no-op
    }

    public Optional<BookMove> getMoveWeightedRandomByFrequency(Board board) {

        List<BookMove> bookMoves = getMoves(board);

        if (bookMoves.size()==0) return Optional.empty();

        int totalWeight = bookMoves.stream().mapToInt(BookMove::getFrequency).sum();

        int val = random.nextInt(totalWeight)+1;  // e.g. if totalWeight is 10, then 0-9 ==> 1-10

        int countWeight = 0;
        for (BookMove bookMove : bookMoves) {
            countWeight += bookMove.getFrequency();
            if (countWeight >= val) {
                return Optional.of(bookMove);
            }
        }

        throw new RuntimeException("Error in getMoveWeightedRandomByFrequency().  totalWeight=" + totalWeight +
                ", val=" + val);
    }

}
