package dev.jamesswafford.chess4j.nn;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Pair;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.*;

import java.util.List;
import java.util.Map;

public class BoardTranslator implements Translator<Board, Float> {

    private static final int NUM_INPUTS = 768;

    private static final Map<String,Integer> offsets = Map.of(
            "R",  0,
            "N", 128,
            "B", 256,
            "Q", 384,
            "K", 512,
            "P", 640
    );

    @Override
    public NDList processInput(TranslatorContext translatorContext, Board board) {
        NDManager ndManager = translatorContext.getNDManager();
        Pair<float[],float[]> ohe = transform(board);
        NDArray oheArray1 = ndManager.create(ohe.getKey(), new Shape(NUM_INPUTS));
        NDArray oheArray2 = ndManager.create(ohe.getValue(), new Shape(NUM_INPUTS));
        return new NDList(oheArray1, oheArray2);
    }

    @Override
    public Float processOutput(TranslatorContext translatorContext, NDList ndList) {
        return ndList.get(0).getFloat(0);
    }


    private static Pair<float[],float[]> transform(Board board) {
        float[] data1 = new float[NUM_INPUTS];
        float[] data2 = new float[NUM_INPUTS];

        List<Square> squares = Square.allSquares();
        squares.forEach(sq -> {
            int i = sq.value();
            int flipped_i = sq.flipVertical().value();
            Piece p = board.getPiece(i);
            if (p != null) {
                int offset = offsets.get(p.toString().toUpperCase());
                if (p.isBlack()) offset += 64;
                data1[offset + i] = 1;
                int flipped_offset = p.isWhite() ? offset + 64 : offset - 64;
                data2[flipped_offset + flipped_i] = 1;
            }
        });

        return new Pair<>(data1, data2);
    }

}
