package com.jamesswafford.chess4j.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;

public class OpeningBookInMemoryImpl extends AbstractOpeningBook {

	private static final OpeningBookInMemoryImpl INSTANCE = new OpeningBookInMemoryImpl();
	
	private Map<Long,List<BookMove>> movesMap = new HashMap<Long,List<BookMove>>();
	
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
			bms = new ArrayList<BookMove>();
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
		List<BookMove> legalMoves = new ArrayList<BookMove>();
		
		List<BookMove> bookMoves = movesMap.get(board.getZobristKey());
		
		if (bookMoves != null) {
			List<Move> lms = MoveGen.genLegalMoves(board);
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
		long cnt = 0;
		
		Set<Long> keys = movesMap.keySet();
		for (Long key : keys) {
			cnt += movesMap.get(key).size();
		}
		
		return cnt;
	}

	@Override
	public void initializeBook() {
	}

}
