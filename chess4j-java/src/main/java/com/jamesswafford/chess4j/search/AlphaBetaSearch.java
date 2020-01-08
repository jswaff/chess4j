package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.movegen.MoveGen;

import java.util.List;

public class AlphaBetaSearch {

    private final Board board;
    private final SearchParameters searchParameters;

    public AlphaBetaSearch(Board board, SearchParameters searchParameters) {
        this.board = board;
        this.searchParameters = searchParameters;
    }

    public int search() {
        return search(searchParameters.getDepth(), searchParameters.getAlpha(), searchParameters.getBeta());
    }

    private int search(int depth, int alpha, int beta) {

        if (depth == 0) {
            return Eval.eval(board);
        }

        List<Move> moves = MoveGen.genLegalMoves(board);

        for (Move move : moves) {
            Undo undo = board.applyMove(move);
            int val = -search(depth-1, -beta, -alpha);
            board.undoMove(undo);
            if (val >= beta) {
                return beta;
            }
            if (val > alpha) {
                alpha = val;
            }
        }

        return alpha;
    }
}
