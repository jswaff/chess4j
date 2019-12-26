package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jamesswafford.chess4j.board.Draw;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.board.ZugzwangDetector;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TranspositionTableEntryType;
import com.jamesswafford.chess4j.io.PrintLine;
import com.jamesswafford.chess4j.utils.BoardUtils;

public final class Search {

    private static final Log LOGGER = LogFactory.getLog(Search.class);

    public static long startTime=0;
    public static long stopTime=0;
    private static long lastTimeCheck=0;

    public static boolean analysisMode=false;
    public static boolean abortSearch=false;

    private Search() {}

    public static int search(List<Move> parentPV,int alpha,int beta,Board board,
            int depth,SearchStats stats,boolean showThinking) {

        assert(depth>0);
        assert(beta>alpha);

        stats.incNodes();
        stats.getFirstLine().clear();

        // TODO: think we want Java9 "or" here
        Optional<TranspositionTableEntry> te = TTHolder.getDepthPreferredTransTable().probe(board.getZobristKey());
        if (!te.isPresent()) {
            te = TTHolder.getAlwaysReplaceTransTable().probe(board.getZobristKey());
        }

        List<Move> pv = new ArrayList<>();
        int numMovesSearched = 0;
        Move pvMove = stats.getLastPV().size()>0 ? stats.getLastPV().get(0) : null;
        MoveOrderer mo = new MoveOrderer(board,pvMove,
                te.map(TranspositionTableEntry::getMove),
                null,null);

        Move move;
        while ((move = mo.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));
            recordFirstLine(true,numMovesSearched,stats,move);
            board.applyMove(move);
            if (BoardUtils.isOpponentInCheck(board)) {
                // illegal
                board.undoLastMove();
                continue;
            }

            int score;
            boolean givesCheck = BoardUtils.isPlayerInCheck(board);

            int extend = Extend.extendDepth(board,move,givesCheck);
            assert(extend >= 0 && extend <= 1);

            if (numMovesSearched==0) {
                score = -searchHelper(pv,-beta,-alpha,board,depth-1+extend,1,true,givesCheck,false,stats);
            } else {
                //score = -searchHelper(pv,-(alpha+1),-alpha,board,depth-1+extend,1,false,givesCheck,(extend==0),stats);
                //if (score > alpha && score < beta) {
                    score = -searchHelper(pv,-beta,-alpha,board,depth-1+extend,1,false,givesCheck,(extend==0),stats);
                //}
            }
            numMovesSearched++;
            board.undoLastMove();

            // if depth==1, we need to continue to search to ensure we have something to play
            if (depth > 1 && abortSearch) {
                return 0;
            }

            if (score > alpha) {
                if (score >= beta) {
                    return beta;
                }
                alpha = score;
                setParentPV(parentPV,move,pv);
                if (showThinking) {
                    PrintLine.printLine(parentPV,depth,score,startTime,stats.getNodes());
                }
            }
        }

        alpha = adjustFinalScoreForMates(board,alpha,numMovesSearched,0);

        return alpha;
    }

    private static void setParentPV(List<Move> parentPV,Move head,List<Move> tail) {
        parentPV.clear();
        parentPV.add(head);
        parentPV.addAll(tail);
    }

    private static int searchHelper(List<Move> parentPV,int alpha,int beta,Board board,
            int depth,int ply,boolean pvNode,boolean inCheck,boolean isNullMoveOK,SearchStats stats) {

        assert(ply > 0);
        assert(alpha < beta);
        assert(inCheck==BoardUtils.isPlayerInCheck(board));

        int origAlpha = alpha;
        parentPV.clear();
        stats.incNodes();

        if (depth < 1) {
            assert(!inCheck);
            return Search.quiescenceSearch(alpha,beta,inCheck,board,stats);
        }

        // Time check
        if (!analysisMode && lastTimeCheck > 10000) {
            if (System.currentTimeMillis() > stopTime) {
                abortSearch = true;
                LOGGER.debug("# Aborting search depth=" + depth + ", ply=" + ply + " on time...");
                return 0;
            }
            lastTimeCheck=0;
        } else {
            lastTimeCheck++;
        }

        // Draw check
        if (Draw.isDraw(board)) {
            return 0;
        }

        // probe the hash table.  maybe we won't have to do any work at all!
        // TODO: think we want Java9 "or" here
        Optional<TranspositionTableEntry> te = TTHolder.getDepthPreferredTransTable().probe(board.getZobristKey());
        if (!te.isPresent()) {
            te = TTHolder.getAlwaysReplaceTransTable().probe(board.getZobristKey());
        }
        // TODO: is there a more functional approach to this?
        if (te.isPresent() && te.get().getDepth() >= depth) {
            if (te.get().getType()==TranspositionTableEntryType.LOWER_BOUND) {
                if (te.get().getScore() >= beta) {
                    stats.incFailHighs();
                    return beta;
                }
            } else if (te.get().getType()==TranspositionTableEntryType.UPPER_BOUND) {
                if (te.get().getScore() <= alpha) {
                    stats.incFailLows();
                    return alpha;
                }
            } else if (te.get().getType()==TranspositionTableEntryType.EXACT_MATCH) {
                stats.incHashExactScores();
                return te.get().getScore();
            }
        }

        ////////////////////// Null Move
        // The idea here is that if my position is so good that, if by giving the opponent an extra turn they
        // can't raise their score to alpha, then when I actually do make a move I will almost surely fail high.
        if (!pvNode && !inCheck && isNullMoveOK && depth >= 3 && beta < Constants.INFINITY
                && !ZugzwangDetector.isZugzwang(board)) {

            Square nullEP = board.clearEPSquare();
            board.swapPlayer();

            int nullDepth = depth - 4; // R=3
            if (nullDepth < 1) { nullDepth = 1; } // don't drop into q-search
            int nullScore = -searchHelper(parentPV,0-beta,1-beta,board,nullDepth,ply,false,false,false,stats);

            board.swapPlayer();
            if (nullEP != null) {
                board.setEP(nullEP);
            }

            if (abortSearch) {
                return 0;
            }

            if (nullScore >= beta) {
                return beta;
            }
        }
        ////////////////////// End Null Move

        // Generate moves
        List<Move> pv = new ArrayList<>(50);
        int numMovesSearched = 0;
        int numMovesPruned = 0;
        Move pvMove = (pvNode && stats.getLastPV().size()>ply) ? stats.getLastPV().get(ply) : null;
        MoveOrderer mo = new MoveOrderer(board,pvMove,
                te.map(TranspositionTableEntry::getMove),
                KillerMoves.getInstance().getKiller1(ply),
                KillerMoves.getInstance().getKiller2(ply));
        Move bestMove = null;

        Move move;
        while ((move = mo.selectNextMove()) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, move));
            recordFirstLine(pvNode,numMovesSearched,stats,move);
            board.applyMove(move);
            if (BoardUtils.isOpponentInCheck(board)) {
                // illegal
                board.undoLastMove();
                continue;
            }

            boolean givesCheck = BoardUtils.isPlayerInCheck(board);

            int extend = Extend.extendDepth(board,move,givesCheck);
            assert(extend >= 0 && extend <= 1);

            /// LMR + PVS
            int score;
            if (numMovesSearched == 0) {
                // this is a PV node.
                score = -searchHelper(pv,-beta,-alpha,board,depth-1+extend,ply+1,pvNode,givesCheck,!pvNode,stats);
            } else {

                // if this position is looking unpromising, just skip it
                if (Prune.prune(board, move, inCheck, givesCheck, extend, alpha, beta, depth)) {
                    board.undoLastMove();
                    stats.incPrunes();
                    numMovesPruned++;
                    continue;
                }

                // if we've searched enough moves and haven't gotten a fail high yet, this is likely
                // a fail-low node.  reduce the remaining moves.  if the score comes back above alpha,
                // then our assumption was wrong and we need to research at full depth.
                if (numMovesSearched >= 4 && depth >= 3 &&
                        !pvNode && !inCheck && !givesCheck && extend==0
                        && move.captured()==null
                        && move.promotion()==null
                        && !move.equals(KillerMoves.getInstance().getKiller1(ply))
                        && !move.equals(KillerMoves.getInstance().getKiller2(ply)))
                {
                    score = -searchHelper(pv,-(alpha+1),-alpha,board,depth-2,ply+1,false,false,true,stats);
                } else {
                    score = alpha + 1; // ensure a full depth search
                }

                if (score > alpha) {
                    // try a PVS search.  since this is not the PV node, our expectation is that it will not improve
                    // alpha.  If it does, then we need to research with a full window to establish the real score.
                    //score = -searchHelper(pv,-(alpha+1),-alpha,board,depth-1+extend,ply+1,false,givesCheck,(extend==0),stats);
                    //if (score > alpha && score < beta) {
                        score = -searchHelper(pv,-beta,-alpha,board,depth-1+extend,ply+1,false,givesCheck,(extend==0),stats);
                    //}
                }
            }
            //////

            numMovesSearched++;
            board.undoLastMove();

            if (abortSearch) {
                return 0;
            }

            if (score > alpha) {
                if (score >= beta) {
                    boolean stored =  TTHolder.getDepthPreferredTransTable().store(board.getZobristKey(),
                            TranspositionTableEntryType.LOWER_BOUND,beta,depth,move);
                    if (!stored) {
                        TTHolder.getAlwaysReplaceTransTable().store(board.getZobristKey(),
                                TranspositionTableEntryType.LOWER_BOUND,beta,depth,move);
                    }
                    if (move.captured()==null && move.promotion()==null) {
                        KillerMoves.getInstance().addKiller(ply, move);
                    }
                    return beta;
                }
                alpha = score;
                bestMove = move;
                setParentPV(parentPV,move,pv);
            }
        }
        assert(numMovesSearched+numMovesPruned==MoveGen.genLegalMoves(board).size());
        assert(alpha >= origAlpha);

        alpha = adjustFinalScoreForMates(board,alpha,numMovesSearched,ply);

        TranspositionTableEntryType tet = bestMove == null ?
                TranspositionTableEntryType.UPPER_BOUND : // fail low node - we didn't find a move > alpha
                TranspositionTableEntryType.EXACT_MATCH;

        boolean stored = TTHolder.getDepthPreferredTransTable().store(board.getZobristKey(),tet, alpha, depth, bestMove);
        if (!stored) {
            TTHolder.getAlwaysReplaceTransTable().store(board.getZobristKey(), tet, alpha, depth, bestMove);
        }

        return alpha;
    }

    public static int quiescenceSearch(int alpha,int beta,boolean inCheck,Board board,SearchStats stats) {
        assert(alpha < beta);

        // Time check
        if (!analysisMode && lastTimeCheck > 10000) {
            if (System.currentTimeMillis() > stopTime) {
                LOGGER.debug("# Aborting qsearch on time...");
                abortSearch = true;
                return 0;
            }
            lastTimeCheck = 0;
        } else {
            lastTimeCheck++;
        }

        int standPat = Eval.eval(board);
        if (standPat > alpha) {
            if (standPat >= beta) {
                return beta;
            }
            // our static evaluation will be the lower bound
            alpha = standPat;
        }

        MoveOrderer mo = new MoveOrderer(board,null,Optional.empty(),null,null);

        Move mv;
        while ((mv = mo.selectNextMove(true)) != null) {
            assert(BoardUtils.isPseudoLegalMove(board, mv));
            assert(mv.captured()!=null || mv.promotion()!=null);

            board.applyMove(mv);
            if (BoardUtils.isOpponentInCheck(board)) {
                // illegal
                board.undoLastMove();
                continue;
            }

            // can this capture possibly raise alpha?
            if (!inCheck && mv.promotion()==null
                    && Eval.getPieceValue(mv.captured()) + Eval.PAWN_VAL*2 + standPat < alpha) {
                board.undoLastMove();
                continue;
            }

            // if not a promising capture just skip
            if (!inCheck && mv.promotion()==null
                    && Eval.getPieceValue(board.getPiece(mv.to())) > Eval.getPieceValue(mv.captured())
                    && SEE.see(board, mv) < 0) {
                board.undoLastMove();
                continue;
            }

            boolean givesCheck = false; // board.isPlayerInCheck();
            stats.incQNodes();
            int score = -quiescenceSearch(-beta,-alpha,givesCheck,board,stats);

            board.undoLastMove();

            if (abortSearch) {
                return 0;
            }

            if (score > alpha) {
                if (score >= beta) {
                    return beta;
                }
                alpha = score;
            }
        }

        return alpha;
    }

    private static void recordFirstLine(boolean isPVNode,int numMovesSearched,SearchStats stats,Move move) {
        if (isPVNode && numMovesSearched==0) {
            stats.getFirstLine().add(move);
        }
    }

    private static int adjustFinalScoreForMates(Board board,int score,int numMovesSearched,int ply) {
        int adjScore=score;

        if (numMovesSearched==0) {
            if (BoardUtils.isPlayerInCheck(board)) {
                adjScore = -(Constants.CHECKMATE-ply);
            } else {
                // draw score
                adjScore = 0;
            }
        }

        return adjScore;
    }

}
