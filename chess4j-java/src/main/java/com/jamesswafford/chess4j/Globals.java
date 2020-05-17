package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.OpeningBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Globals {

    private static Board board = new Board();
    private static List<Undo> gameUndos = new ArrayList<>();
    private static OpeningBook openingBook;

    public static Board getBoard() {
        return board;
    }

    public static List<Undo> getGameUndos() {
        return gameUndos;
    }

    public static Optional<OpeningBook> getOpeningBook() {
        return Optional.ofNullable(openingBook);
    }

    public static void setOpeningBook(OpeningBook openingBook) {
        Globals.openingBook = openingBook;
    }
}
