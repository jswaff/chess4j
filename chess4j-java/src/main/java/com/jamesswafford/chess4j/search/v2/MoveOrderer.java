package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import com.jamesswafford.chess4j.search.MoveOrderStage;
import com.jamesswafford.chess4j.utils.MoveUtils;

import java.util.List;

public class MoveOrderer {

    private final Board board;
    private final MoveGenerator moveGenerator;
    private final MoveScorer moveScorer;

    private Move[] captures;
    private int captureIndex;
    private Integer[] captureScores;
    private Move[] noncaptures;
    private int noncaptureIndex;

    private MoveOrderStage nextMoveOrderStage = MoveOrderStage.GENCAPS;

    public MoveOrderer(Board board, MoveGenerator moveGenerator, MoveScorer moveScorer) {
        this.board = board;
        this.moveGenerator = moveGenerator;
        this.moveScorer = moveScorer;
    }

    public Move selectNextMove() {

        // generate and score captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.GENCAPS) {
            nextMoveOrderStage = MoveOrderStage.CAPTURES_PROMOS;
            List<Move> myCaptures = moveGenerator.generatePseudoLegalCaptures(board);
            captures =  myCaptures.toArray(new Move[0]);
            captureIndex = 0;
            captureScores = new Integer[myCaptures.size()];
            for (int i=0;i<captures.length;i++) {
                captureScores[i] = moveScorer.calculateStaticScore(captures[i]);
            }
        }

        // captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.CAPTURES_PROMOS) {
            int bestInd = getIndexOfBestCapture(captureIndex);
            if (bestInd != -1) {
                MoveUtils.swap(captures, captureIndex, bestInd);
                swapScores(captureIndex, bestInd);
                return captures[captureIndex++];
            }
            nextMoveOrderStage = MoveOrderStage.GENNONCAPS;
        }

        // generate non-captures
        if (nextMoveOrderStage == MoveOrderStage.GENNONCAPS) {
            nextMoveOrderStage = MoveOrderStage.REMAINING;
            List<Move> myNoncaps = moveGenerator.generatePseudoLegalNonCaptures(board);
            noncaptures =  myNoncaps.toArray(new Move[0]);
            noncaptureIndex = 0;
        }

        // just play them as they come
        if (noncaptureIndex < noncaptures.length) {
            return noncaptures[noncaptureIndex++];
        }

        return null;
    }

    public void swapScores(int ind1, int ind2) {
        Integer tmp = captureScores[ind1];
        captureScores[ind1] = captureScores[ind2];
        captureScores[ind2] = tmp;
    }

    private int getIndexOfBestCapture(int startIndex) {
        int bestIndex = -1;
        int bestScore = -9999;

        for (int i=startIndex;i<captures.length;i++) {
            if (captureScores[i] > bestScore) {
                bestIndex = i;
                bestScore = captureScores[i];
            }
        }
        return bestIndex;
    }

    public MoveOrderStage getNextMoveOrderStage() {
        return nextMoveOrderStage;
    }

}
