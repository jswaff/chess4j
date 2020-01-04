package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Undo;

import java.util.ArrayList;
import java.util.List;

public class Globals {

    private static Board board = new Board();

    // TODO - make private
    public static List<Undo> gameUndos = new ArrayList<>();

    public static Board getBoard() {
        return board;
    }
}
