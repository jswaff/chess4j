package com.jamesswafford.chess4j.book;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.utils.GameResult;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class AbstractOpeningBook {

    private Random r = new Random();

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

    public void dropIndexes() {
        // default impl is a no-op
    }

    public Optional<BookMove> getMoveWeightedRandomByFrequency(Board board) {

        List<BookMove> bookMoves = getMoves(board);

        if (bookMoves.size()==0) return Optional.empty();

        int totalWeight = bookMoves.stream().mapToInt(BookMove::getFrequency).sum();

        int val = r.nextInt(totalWeight)+1;  // e.g. if totalWeight is 10, then 0-9 ==> 1-10

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
