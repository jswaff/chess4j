package com.jamesswafford.chess4j.exceptions;

public class IllegalMoveException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7181833070688650739L;

	private String illegalMove;
	public IllegalMoveException(String illegalMove) {
		this.illegalMove = illegalMove;
	}
	
	@Override
	public String getMessage() {
		return "Illegal move: " + illegalMove;
	}
}
