package dev.jamesswafford.chess4j;

import ai.djl.inference.Predictor;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.book.OpeningBook;
import dev.jamesswafford.chess4j.eval.EvalWeights;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Globals {

    @Getter
    private static final Board board = new Board();

    @Getter
    private static final List<Undo> gameUndos = new ArrayList<>();

    @Getter
    @Setter
    private static EvalWeights evalWeights = new EvalWeights();

    @Setter
    private static OpeningBook openingBook;

    @Setter
    private static Predictor<Board, Float> predictor;

    @Getter
    @Setter
    private static boolean pawnHashEnabled = true;

    public static Optional<OpeningBook> getOpeningBook() {
        return Optional.ofNullable(openingBook);
    }

    public static Optional<Predictor<Board, Float>> getPredictor() { return Optional.ofNullable(predictor); }

}
