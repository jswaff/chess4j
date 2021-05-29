package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.eval.EvalMaterial;
import com.jamesswafford.chess4j.movegen.MoveGenerator;

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

    private Move[] captures;
    private int capturesIndex;
    private Integer[] captureMvvLvaScores;
    private Move[] noncaptures;
    private int noncapturesIndex;
    private Move[] badcaptures;
    private Integer[] badCaptureSeeScores;
    private int badCapturesIndex;
    private int numBadCaptures;

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
            capturesIndex = 0;
            badcaptures = new Move[captures.length];
            badCapturesIndex = 0;
            numBadCaptures = 0;
            captureMvvLvaScores = new Integer[myCaptures.size()];
            for (int i=0;i<captures.length;i++) {
                if (specialMovesPlayed.contains(captures[i])) {
                    captures[i] = null;
                } else {
                    captureMvvLvaScores[i] = MVVLVA.score(captures[i]);
                }
            }
        }

        // good captures and promotions
        if (nextMoveOrderStage == MoveOrderStage.GOOD_CAPTURES_PROMOS) {
            int bestInd = getIndexOfBestCaptureByMvvLva(capturesIndex);
            while (bestInd != -1) {
                // the best by MVV/LVA doesn't mean the move is good.  A move is "good" if:
                // 1) it's a promotion
                // 2) the value of the captured piece is >= the value of the capturing piece
                // 3) SEE analysis gives a non-negative score
                // only do SEE if necessary, but if we do, keep the score for sorting bad captures later on.
                Move mv = captures[bestInd];
                boolean goodCap = mv.promotion() != null ||
                        EvalMaterial.evalPiece(mv.captured()) >= EvalMaterial.evalPiece(mv.piece());
                if (!goodCap) {
                    int seeScore = SEE.see(board, mv);
                    goodCap = seeScore >= 0;
                }

                if (goodCap) {
                    swap(captures, capturesIndex, bestInd);
                    swapMvvLvaScores(capturesIndex, bestInd);
                    return captures[capturesIndex++];
                } else {
                    // add to "deferred" list, then go to the next item
                    badcaptures[numBadCaptures] = mv;
                    ++numBadCaptures;
                    captures[bestInd] = null;
                    bestInd = getIndexOfBestCaptureByMvvLva(capturesIndex);
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
            nextMoveOrderStage = generateNonCaptures ? MoveOrderStage.GENNONCAPS : MoveOrderStage.BAD_CAPTURES;
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

                noncapturesIndex = 0;
            }

            if (nextMoveOrderStage == MoveOrderStage.NONCAPS) {
                if (noncapturesIndex < noncaptures.length) {
                    int ind = getIndexOfFirstNonCapture(noncapturesIndex);
                    if (ind != -1) {
                        swap(noncaptures, noncapturesIndex, ind);
                        return noncaptures[noncapturesIndex++];
                    }
                }
                nextMoveOrderStage = MoveOrderStage.BAD_CAPTURES;
            }
        }

        if (badCapturesIndex < numBadCaptures) {
            Move mv = badcaptures[badCapturesIndex];
            ++badCapturesIndex;
            return mv;
        }

        return null;
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
        Integer tmp = captureMvvLvaScores[ind1];
        captureMvvLvaScores[ind1] = captureMvvLvaScores[ind2];
        captureMvvLvaScores[ind2] = tmp;
    }

    private int getIndexOfBestCaptureByMvvLva(int startIndex) {
        int bestIndex = -1;
        int bestScore = -9999;

        for (int i=startIndex;i<captures.length;i++) {
            Move m = captures[i];
            if (m != null && (m.captured() != null || m.promotion() != null)) {
                if (captureMvvLvaScores[i] > bestScore) {
                    bestIndex = i;
                    bestScore = captureMvvLvaScores[i];
                }
            }
        }
        return bestIndex;
    }

}
