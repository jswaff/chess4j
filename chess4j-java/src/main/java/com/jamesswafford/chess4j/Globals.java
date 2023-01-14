package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.OpeningBook;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.tuner.TunerDatasource;
import com.jamesswafford.ml.nn.Network;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Globals {

    private static final Board board = new Board();
    private static final List<Undo> gameUndos = new ArrayList<>();
    private static EvalWeights evalWeights = new EvalWeights();

    @Getter
    @Setter
    private static Network network;
    private static OpeningBook openingBook;
    private static TunerDatasource tunerDatasource;

    private static boolean pawnHashEnabled = true;

    public static Board getBoard() {
        return board;
    }

    public static List<Undo> getGameUndos() {
        return gameUndos;
    }

    public static EvalWeights getEvalWeights() {
        return evalWeights;
    }

    public static void setEvalWeights(EvalWeights evalWeights) {
        Globals.evalWeights = evalWeights;
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

    public static boolean isPawnHashEnabled() {
        return pawnHashEnabled;
    }

    public static void setPawnHashEnabled(boolean pawnHashEnabled) {
        Globals.pawnHashEnabled = pawnHashEnabled;
    }


}
