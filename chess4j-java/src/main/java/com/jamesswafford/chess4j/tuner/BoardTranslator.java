package com.jamesswafford.chess4j.tuner;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.jamesswafford.chess4j.board.Board;

public class BoardTranslator implements Translator<Board, Float> {

    @Override
    public NDList processInput(TranslatorContext translatorContext, Board board) {
        NDManager ndManager = translatorContext.getNDManager();
        // TODO: avoid copy
        float[] ohe = BoardToNetwork.transform1(board);
        NDArray oheArray = ndManager.create(ohe, new Shape(1, BoardToNetwork.NUM_INPUTS));
        return new NDList(oheArray);
    }

    @Override
    public Float processOutput(TranslatorContext translatorContext, NDList ndList) {
        return ndList.get(0).getFloat(0);
    }

}
