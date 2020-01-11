package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Evaluator;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.utils.BoardUtils;

import java.util.List;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;

public class AlphaBetaSearch {

    private final Board board;
    private final SearchParameters searchParameters;
    private final Evaluator evaluator;

    public AlphaBetaSearch(Board board, SearchParameters searchParameters, Evaluator evaluator) {
        this.board = board;
        this.searchParameters = searchParameters;
        this.evaluator = evaluator;
    }

    public int search() {
        return search(0, searchParameters.getDepth(), searchParameters.getAlpha(), searchParameters.getBeta());
    }

    private int search(int ply, int depth, int alpha, int beta) {

        if (depth == 0) {
            return evaluator.evaluateBoard(board);
        }

        List<Move> moves = MoveGen.genPseudoLegalMoves(board);

        int numMovesSearched = 0;
        for (Move move : moves) {
            Undo undo = board.applyMove(move);
            // check if move was legal
            if (BoardUtils.isOpponentInCheck(board)) {
                board.undoMove(undo);
                continue;
            }

            // TODO: undo should be passed through for draw checks
            int val = -search(ply+1, depth-1, -beta, -alpha);
            ++numMovesSearched;
            board.undoMove(undo);
            if (val >= beta) {
                return beta;
            }
            if (val > alpha) {
                alpha = val;
            }
        }

        alpha = adjustFinalScoreForMates(alpha, numMovesSearched, ply);

        return alpha;
    }

    private int adjustFinalScoreForMates(int score, int numMovesSearched, int ply) {
        int adjScore = score;

        if (numMovesSearched==0) {
            if (BoardUtils.isPlayerInCheck(board)) {
                adjScore = -CHECKMATE + ply;
            } else {
                // draw score
                adjScore = 0;
            }
        }

        return adjScore;
    }

}
