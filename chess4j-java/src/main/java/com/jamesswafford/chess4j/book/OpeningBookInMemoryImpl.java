package com.jamesswafford.chess4j.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGeneratorImpl;

public class OpeningBookInMemoryImpl extends AbstractOpeningBook {

    private static final OpeningBookInMemoryImpl INSTANCE = new OpeningBookInMemoryImpl();

    private Map<Long,List<BookMove>> movesMap = new HashMap<>();

    private OpeningBookInMemoryImpl() {
    }

    public static OpeningBookInMemoryImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void addToBook(Board board,Move move) {
        addToMap(board.getZobristKey(),move);
    }

    private void addToMap(Long key,Move move) {
        List<BookMove> bms = movesMap.get(key);
        if (bms==null) {
            bms = new ArrayList<>();
            movesMap.put(key, bms);
        }

        // is this move already in the list?
        for (BookMove bm : bms) {
            if (bm.getMove().equals(move)) {
                bm.setFrequency(bm.getFrequency() + 1);
                return;
            }
        }

        // not in list, add it now
        bms.add(new BookMove(move));
    }

    @Override
    public List<BookMove> getMoves(Board board) {
        List<BookMove> legalMoves = new ArrayList<>();

        List<BookMove> bookMoves = movesMap.get(board.getZobristKey());

        if (bookMoves != null) {
            List<Move> lms = MoveGeneratorImpl.genLegalMoves(board);
            for (BookMove bm : bookMoves) {
                if (lms.contains(bm.getMove())) {
                    legalMoves.add(bm);
                }
            }
        }

        return legalMoves;
    }

    @Override
    public long getTotalMoveCount() {
        return movesMap.keySet().stream().mapToLong(key -> movesMap.get(key).size()).sum();
    }

    @Override
    public void initializeBook() {
    }

}
