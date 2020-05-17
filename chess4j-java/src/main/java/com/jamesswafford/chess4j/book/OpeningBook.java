package com.jamesswafford.chess4j.book;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.PgnToBookException;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.io.PGNIterator;
import com.jamesswafford.chess4j.utils.GameResult;

import java.io.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface OpeningBook {

    void addToBook(Board board, Move move);

    List<BookMove> getMoves(Board board);

    long getTotalMoveCount();

    void initializeBook();

    default void learn(List<Move> moves, Color engineColor, GameResult gameResult) {
        // default impl is a no-op
    }

    default void addIndexes() {
        // default impl is a no-op
    }

    default void addToBook(PGNGame game) {

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

    default int addToBook(File pgnFile) {
        try {
            processPGNFile(pgnFile,true);
            return processPGNFile(pgnFile,false);
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

    default void dropIndexes() {
        // default impl is a no-op
    }

    default Optional<BookMove> getMoveWeightedRandomByFrequency(Board board) {

        List<BookMove> bookMoves = getMoves(board);

        if (bookMoves.size()==0) return Optional.empty();

        int totalWeight = bookMoves.stream().mapToInt(BookMove::getFrequency).sum();

        Random random = new SecureRandom();
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
