package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;

public class Globals {

    private static Board board = new Board();

    public static Board getBoard() {
        return board;
    }
}
