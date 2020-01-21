package com.jamesswafford.chess4j.book;

import java.util.List;
import java.util.Random;

import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.utils.GameResult;

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

    public BookMove getMoveWeightedRandomByFrequency(Board board) {
        List<BookMove> bms = getMoves(board);

        //LOGGER.debug("# choosing book move from list of " + bms.size() + " candidate moves.");

        if (bms.size()==0) return null;

        int totalWeight = bms.stream().mapToInt(bm -> bm.getFrequency()).sum();

        int val = r.nextInt(totalWeight)+1;  // e.g. if totalWeight is 10, then 0-9 ==> 1-10

        int countWeight = 0;
        for (BookMove bm : bms) {
            countWeight += bm.getFrequency();
            if (countWeight >= val) {
                return bm;
            }
        }

        throw new RuntimeException("Error in getMoveWeightedRandomByFrequency().  totalWeight=" + totalWeight + ", val=" + val);
    }

}
