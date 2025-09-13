package dev.jamesswafford.chess4j;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.book.OpeningBook;
import dev.jamesswafford.chess4j.eval.EvalWeights;
import dev.jamesswafford.chess4j.nn.NeuralNetwork;
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
    private static NeuralNetwork neuralNetwork;

    @Getter
    @Setter
    private static boolean pawnHashEnabled = true;

    public static Optional<OpeningBook> getOpeningBook() {
        return Optional.ofNullable(openingBook);
    }

    public static Optional<NeuralNetwork> getNeuralNetwork() { return Optional.ofNullable(neuralNetwork); }
}
