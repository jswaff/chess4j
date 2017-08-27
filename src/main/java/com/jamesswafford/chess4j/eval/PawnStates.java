package com.jamesswafford.chess4j.eval;

public class PawnStates {

	private static final int PASSED = 0x01;
	
	private int pawnState;
	
	public void setPassed() {
		pawnState |= PASSED;
	}
	
	public boolean isPassed() {
		return (pawnState & PASSED) > 0;
	}
	
}
