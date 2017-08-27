package com.jamesswafford.chess4j.utils;

import com.jamesswafford.chess4j.board.Move;

public class MoveStack {

	private static final MoveStack INSTANCE = new MoveStack();
	
	private final int STACK_SIZE = 10000;
	private Move[] moves = new Move[STACK_SIZE];
	private int index;
	
	private MoveStack() {
		
	}
	
	public static MoveStack getInstance() {
		return INSTANCE;
	}
	
	public void clear() {
		for (int i=0;i<STACK_SIZE;i++) {
			moves[i] = null;
		}
		index = 0;
	}
	
	public void push(Move m) {
		moves[index] = m;
		index++;
	}
	
	public int getCurrentIndex() {
		return index;
	}
	
	public Move get(int index) {
		return moves[index];
	}
	
	public void insertAt(int index,Move m) {
		moves[index] = m;
	}
	
	public Move pop() {
		index--;
		return moves[index];
	}
	
	
}
