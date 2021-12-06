package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.OpeningBook;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import com.jamesswafford.chess4j.tuner.TunerDatasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Globals {

    private static final Board board = new Board();
    private static final List<Undo> gameUndos = new ArrayList<>();
    private static EvalTermsVector evalTermsVector = new EvalTermsVector();
    private static OpeningBook openingBook;
    private static TunerDatasource tunerDatasource;

    public static Board getBoard() {
        return board;
    }

    public static List<Undo> getGameUndos() {
        return gameUndos;
    }

    public static EvalTermsVector getEvalTermsVector() {
        return evalTermsVector;
    }

    public static void setEvalTermsVector(EvalTermsVector evalTermsVector) {
        Globals.evalTermsVector = evalTermsVector;
    }

    public static Optional<OpeningBook> getOpeningBook() {
        return Optional.ofNullable(openingBook);
    }

    public static void setOpeningBook(OpeningBook openingBook) {
        Globals.openingBook = openingBook;
    }

    public static Optional<TunerDatasource> getTunerDatasource() {
        return Optional.ofNullable(tunerDatasource);
    }

    public static void setTunerDatasource(TunerDatasource tunerDatasource) {
        Globals.tunerDatasource = tunerDatasource;
    }
}
