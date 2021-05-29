package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.eval.EvalMaterial;
import com.jamesswafford.chess4j.movegen.MoveGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jamesswafford.chess4j.utils.BoardUtils.*;
import static com.jamesswafford.chess4j.utils.MoveUtils.*;

public class MoveOrderer {

    private final Board board;
    private final MoveGenerator moveGenerator;

    private final Move pvMove, hashMove, killer1, killer2;
    private final boolean generateNonCaptures;
    private final Set<Move> specialMovesPlayed;
    private final List<Move> deferredCaptures;

    private Move[] captures;
    private int captureIndex;
    private Integer[] mvvlvaScores;
    private Move[] noncaptures;
    private int noncaptureIndex;

    private MoveOrderStage nextMoveOrderStage = MoveOrderStage.PV;

    public MoveOrderer(Board board, MoveGenerator moveGenerator, Move pvMove, Move hashMove, Move killer1,
                       Move killer2, boolean generateNonCaptures)
    {
        this.board = board;
        this.moveGenerator = moveGenerator;

        this.pvMove = pvMove;
        this.hashMove = hashMove;
        this.killer1 = killer1;
        this.killer2 = killer2;
        this.generateNonCaptures = generateNonCaptures;
        this.specialMovesPlayed = new HashSet<>();
        this.deferredCaptures = new ArrayList<>();
    }

    public MoveOrderStage getNextMoveOrderStage() {
        return nextMoveOrderStage;
    }

    public Move selectNextMove() {

        // pv move
        if (nextMoveOrderStage == MoveOrderStage.PV) {
            nextMoveOrderStage = MoveOrderStage.HASH_MOVE;
            if (pvMove != null) {
                assert(moveGenerator.generateLegalMoves(board).contains(pvMove));
                specialMovesPlayed.add(pvMove);
                return pvMove;
            }
        }

        // hash move
        if (nextMoveOrderStage == MoveOrderStage.HASH_MOVE) {
            nextMoveOrderStage = MoveOrderStage.GENCAPS;
            if (hashMove != null && !specialMovesPlayed.contains(hashMove) && isPseudoLegalMove(board, hashMove)) {
                assert(moveGenerator.generateLegalMoves(board).contains(hashMove));
                specialMovesPlayed.add(hashMove);
                return hashMove;
            }
        }

        // generate and score captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.GENCAPS) {
            nextMoveOrderStage = MoveOrderStage.GOOD_CAPTURES_PROMOS;
            List<Move> myCaptures = moveGenerator.generatePseudoLegalCaptures(board);
            captures =  myCaptures.toArray(new Move[0]);
            captureIndex = 0;
            mvvlvaScores = new Integer[myCaptures.size()];
            for (int i=0;i<captures.length;i++) {
                if (specialMovesPlayed.contains(captures[i])) {
                    captures[i] = null;
                } else {
                    mvvlvaScores[i] = MVVLVA.score(captures[i]);
                }
            }
        }

        // good captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.GOOD_CAPTURES_PROMOS) {
            int bestInd = getIndexOfBestCaptureByMvvLva(captureIndex);
            while (bestInd != -1) {
                // the best by MVV/LVA doesn't mean the move is good
                if (isPromotionOrGoodCapture(captures[bestInd])) {
                    swap(captures, captureIndex, bestInd);
                    swapMvvLvaScores(captureIndex, bestInd);
                    return captures[captureIndex++];
                } else {
                    // add to "deferred" list, then go to the next item
                    deferredCaptures.add(captures[bestInd]);
                    captures[bestInd] = null;
                    bestInd = getIndexOfBestCaptureByMvvLva(captureIndex);
                }
            }
            nextMoveOrderStage = MoveOrderStage.KILLER1;
        }

        if (nextMoveOrderStage == MoveOrderStage.KILLER1) {
            nextMoveOrderStage = MoveOrderStage.KILLER2;
            if (killer1 != null && !specialMovesPlayed.contains(killer1) && isPseudoLegalMove(board, killer1)) {
                assert(killer1.captured()==null);
                specialMovesPlayed.add(killer1);
                return killer1;
            }
        }

        if (nextMoveOrderStage == MoveOrderStage.KILLER2) {
            nextMoveOrderStage = generateNonCaptures ? MoveOrderStage.GENNONCAPS : MoveOrderStage.SORT_BAD_CAPTURES;
            if (killer2 != null && !specialMovesPlayed.contains(killer2) && isPseudoLegalMove(board, killer2)) {
                assert(killer2.captured()==null);
                specialMovesPlayed.add(killer2);
                return killer2;
            }
        }

        // generate non-captures
        if (generateNonCaptures) {
            if (nextMoveOrderStage == MoveOrderStage.GENNONCAPS) {
                nextMoveOrderStage = MoveOrderStage.NONCAPS;
                List<Move> myNoncaps = moveGenerator.generatePseudoLegalNonCaptures(board);
                noncaptures = myNoncaps.toArray(new Move[0]);
                // avoid playing special moves again
                for (int i = 0; i < noncaptures.length; i++) {
                    if (specialMovesPlayed.contains(noncaptures[i])) {
                        noncaptures[i] = null;
                    }
                }

                noncaptureIndex = 0;
            }

            if (nextMoveOrderStage == MoveOrderStage.NONCAPS) {
                if (noncaptureIndex < noncaptures.length) {
                    int ind = getIndexOfFirstNonCapture(noncaptureIndex);
                    if (ind != -1) {
                        swap(noncaptures, noncaptureIndex, ind);
                        return noncaptures[noncaptureIndex++];
                    }
                }
                nextMoveOrderStage = MoveOrderStage.SORT_BAD_CAPTURES;
            }
        }

        if (nextMoveOrderStage == MoveOrderStage.SORT_BAD_CAPTURES) {
            nextMoveOrderStage = MoveOrderStage.BAD_CAPTURES;
            // TODO: sort using SEE
        }

        if (deferredCaptures.size() > 0) {
            deferredCaptures.forEach(System.out::println);
            Move mv = deferredCaptures.get(0);
            deferredCaptures.remove(0);
            return mv;
        }

        return null;
    }

    private boolean isPromotionOrGoodCapture(Move mv) {
        assert (mv.promotion() != null || mv.captured() != null);

        if (mv.promotion() != null) return true;

        // if the value of the captured piece is equal to or greater than the capturing piece, it can't be bad
        if (EvalMaterial.evalPiece(mv.captured()) >= EvalMaterial.evalPiece(mv.piece())) return true;

        // otherwise, fall back to SEE to figure it out
        return SEE.see(board, mv) >= 0;
    }

    private int getIndexOfFirstNonCapture(int startIndex) {
        int index = -1;

        for (int i=startIndex;i<noncaptures.length;i++) {
            Move m = noncaptures[i];
            if (m != null) {
                assert(m.captured()==null);
                assert(m.promotion()==null);
                index = i;
                break;
            }
        }

        return index;
    }

    private void swapMvvLvaScores(int ind1, int ind2) {
        Integer tmp = mvvlvaScores[ind1];
        mvvlvaScores[ind1] = mvvlvaScores[ind2];
        mvvlvaScores[ind2] = tmp;
    }

    private int getIndexOfBestCaptureByMvvLva(int startIndex) {
        int bestIndex = -1;
        int bestScore = -9999;

        for (int i=startIndex;i<captures.length;i++) {
            Move m = captures[i];
            if (m != null && (m.captured() != null || m.promotion() != null)) {
                if (mvvlvaScores[i] > bestScore) {
                    bestIndex = i;
                    bestScore = mvvlvaScores[i];
                }
            }
        }
        return bestIndex;
    }

}
