package com.jamesswafford.chess4j.search;

import java.util.List;
import java.util.Optional;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.MoveGen;
import com.jamesswafford.chess4j.io.DrawBoard;
import com.jamesswafford.chess4j.utils.BoardUtils;
import com.jamesswafford.chess4j.utils.MoveUtils;

public class MoveOrderer {

    private Board board;
    private MoveOrderStage nextMoveOrderStage = MoveOrderStage.PV;
    private Move[] captures;
    private int captureIndex;
    private Integer[] captureScores;
    private Move[] noncaptures;
    private int noncaptureIndex;
    private Move pvMove;
    private Optional<Move> hashMove;
    private Move killer1,killer2;

    public MoveOrderer(Board board,Move pvMove,Optional<Move> hashMove,Move killer1,Move killer2) {
        this.board = board;
        this.pvMove = pvMove;
        this.hashMove = hashMove;
        this.killer1 = killer1;
        this.killer2 = killer2;

        assert(hashMove!=null);
    }

    public Move selectNextMove() {
        return selectNextMove(false);
    }

    public Move selectNextMove(boolean capturesOnly) {

        // try PV move
        if (nextMoveOrderStage==MoveOrderStage.PV) {
            nextMoveOrderStage = MoveOrderStage.HASH_MOVE;
            if (pvMove != null) {
                assert(BoardUtils.isGoodMove(board, pvMove));
                assert(testGoodMove(board,pvMove));
                return pvMove;
            }
        }

        // try hash move
        if (nextMoveOrderStage == MoveOrderStage.HASH_MOVE) {
            nextMoveOrderStage = MoveOrderStage.GENCAPS;
            // TODO: try to express using filter and isPresent
            if (hashMove.isPresent() && !hashMove.get().equals(pvMove)
                    && BoardUtils.isGoodMove(board, hashMove.get())) {
                assert(testGoodMove(board,hashMove.get()));
                return hashMove.get();
            }
        }

        // generate and score captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.GENCAPS) {
            nextMoveOrderStage = MoveOrderStage.CAPTURES_PROMOS;
            List<Move> myCaptures = MoveGen.genPseudoLegalMoves(board,true,false);
            captures =  myCaptures.toArray(new Move[myCaptures.size()]);
            captureIndex = 0;
            captureScores = new Integer[myCaptures.size()];
            for (int i=0;i<captures.length;i++) {
                final int finalI = i;
                if (captures[i].equals(pvMove) || hashMove.filter(hm -> hm.equals(captures[finalI])).isPresent()) {
                    captures[i] = null;
                } else {
                    captureScores[i] = MVVLVA.score(board, captures[i]);
                }
            }
        }

        // captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.CAPTURES_PROMOS) {
            int bestInd = getIndexOfBestCapture(captureIndex);
            if (bestInd != -1 && captureScores[bestInd] >= 0) {
                MoveUtils.swap(captures, captureIndex,bestInd);
                swapScores(captureIndex,bestInd);
                return captures[captureIndex++];
            }
            nextMoveOrderStage = MoveOrderStage.KILLER1;
        }

        if (!capturesOnly) {
            // killer1
            if (nextMoveOrderStage == MoveOrderStage.KILLER1) {
                nextMoveOrderStage = MoveOrderStage.KILLER2;
                if (killer1 != null && !killer1.equals(pvMove)
                        && !hashMove.filter(hm -> hm.equals(killer1)).isPresent()
                        && BoardUtils.isGoodMove(board, killer1)) {

                    assert(killer1.captured()==null);
                    assert(testGoodMove(board,killer1));
                    return killer1;
                }
            }

            // killer2
            if (nextMoveOrderStage == MoveOrderStage.KILLER2) {
                nextMoveOrderStage = MoveOrderStage.GENNONCAPS;
                if (killer2 != null
                    && !killer2.equals(pvMove) && !hashMove.filter(hm -> hm.equals(killer2)).isPresent()
                    && BoardUtils.isGoodMove(board, killer2)) {

                    assert(killer2.captured()==null);
                    assert(testGoodMove(board,killer2));
                    return killer2;
                }
            }

            // generate noncaptures
            if (nextMoveOrderStage == MoveOrderStage.GENNONCAPS) {
                nextMoveOrderStage = MoveOrderStage.REMAINING;
                List<Move> myNoncaps = MoveGen.genPseudoLegalMoves(board,false,true);
                noncaptures =  myNoncaps.toArray(new Move[myNoncaps.size()]);
                for (int i=0;i<noncaptures.length;i++) {
                    final int finalI = i;
                    if (noncaptures[i].equals(pvMove) || hashMove.filter(hm -> hm.equals(noncaptures[finalI])).isPresent()
                            || noncaptures[i].equals(killer1) || noncaptures[i].equals(killer2)) {
                        noncaptures[i] = null;
                    }
                }

                noncaptureIndex = 0;
            }

            // just play them as they come
            if (noncaptureIndex < noncaptures.length) {
                int ind = getIndexOfFirstNonCapture(noncaptureIndex);
                if (ind != -1) {
                    MoveUtils.swap(noncaptures, noncaptureIndex,ind);
                    return noncaptures[noncaptureIndex++];
                }
            }
        }

        return null;
    }

    private int getIndexOfBestCapture(int startIndex) {
        int index=-1;
        int bestScore=-9999;

        for (int i=startIndex;i<captures.length;i++) {
            Move m = captures[i];
            if (m!=null && (m.captured()!=null || m.promotion()!=null)) {
                int myScore = captureScores[i];
                if (myScore > bestScore) {
                    index = i;
                    bestScore = myScore;
                }
            }
        }
        return index;
    }

    private int getIndexOfFirstNonCapture(int startIndex) {
        int index = -1;

        for (int i=startIndex;i<noncaptures.length;i++) {
            Move m = noncaptures[i];
            if (m!=null && m.captured()==null) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void swapScores(int ind1,int ind2) {
        Integer tmp = captureScores[ind1];
        captureScores[ind1] = captureScores[ind2];
        captureScores[ind2] = tmp;
    }

    private boolean testGoodMove(Board b,Move m) {
        List<Move> moves = MoveGen.genPseudoLegalMoves(b);
        if (!moves.contains(m)) {
            DrawBoard.drawBoard(b);
            System.out.println("not good!: " + m.toString2());
            for (Move mv : moves) {
                System.out.println(mv.toString2());
            }
        }
        return moves.contains(m);
    }

    public MoveOrderStage getNextMoveOrderStage() {
        return nextMoveOrderStage;
    }


}
