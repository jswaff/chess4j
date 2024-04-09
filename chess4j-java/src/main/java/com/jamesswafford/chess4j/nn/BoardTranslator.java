package com.jamesswafford.chess4j.nn;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.pieces.*;

public class BoardTranslator implements Translator<Board, Float> {

    private static final int NUM_INPUTS = 768;

    @Override
    public NDList processInput(TranslatorContext translatorContext, Board board) {
        NDManager ndManager = translatorContext.getNDManager();
        float[] ohe = transform(board);
        NDArray oheArray = ndManager.create(ohe, new Shape(1, NUM_INPUTS));
        return new NDList(oheArray);
    }

    @Override
    public Float processOutput(TranslatorContext translatorContext, NDList ndList) {
        return ndList.get(0).getFloat(0);
    }


    private static float[] transform(Board board) {
        float[] data = new float[NUM_INPUTS];

        for (int i=0;i<64;i++) {
            Piece p = board.getPiece(i);
            if (Rook.WHITE_ROOK.equals(p)) data[i] = 1;
            else if (Rook.BLACK_ROOK.equals(p)) data[64+i] = 1;
            else if (Knight.WHITE_KNIGHT.equals(p)) data[128+i] = 1;
            else if (Knight.BLACK_KNIGHT.equals(p)) data[192+i] = 1;
            else if (Bishop.WHITE_BISHOP.equals(p)) data[256+i] = 1;
            else if (Bishop.BLACK_BISHOP.equals(p)) data[320+i] = 1;
            else if (Queen.WHITE_QUEEN.equals(p)) data[384+i] = 1;
            else if (Queen.BLACK_QUEEN.equals(p)) data[448+i] = 1;
            else if (King.WHITE_KING.equals(p)) data[512+i] = 1;
            else if (King.BLACK_KING.equals(p)) data[576+i] = 1;
            else if (Pawn.WHITE_PAWN.equals(p)) data[640+i] = 1;
            else if (Pawn.BLACK_PAWN.equals(p)) data[704+i] = 1;
        }

        return data;
    }

}
