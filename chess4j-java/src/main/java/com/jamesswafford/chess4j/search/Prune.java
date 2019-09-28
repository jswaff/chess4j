package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.pieces.Pawn;

public class Prune {

    public static boolean prune(Board b,Move lastMove,boolean inCheck,boolean givesCheck,
            int extensions,int alpha,int beta,int depth) {

        if (depth < 3
                && alpha < (Constants.CHECKMATE-500) && beta < (Constants.CHECKMATE-500)
                && alpha > (-Constants.CHECKMATE+500) && beta > (-Constants.CHECKMATE+500)
                && extensions==0 && !inCheck && !givesCheck
                && lastMove.captured()==null && lastMove.promotion()==null
                && !(lastMove.piece()==Pawn.WHITE_PAWN && lastMove.to().rank()==Rank.RANK_7)
                && !(lastMove.piece()==Pawn.BLACK_PAWN && lastMove.to().rank()==Rank.RANK_2))
        {
            int evalMat = -Eval.eval(b,true);

            return (depth < 2 && (evalMat + Eval.PAWN_VAL*2 <= alpha))   // futility pruning
                || (depth < 3 && (evalMat + Eval.PAWN_VAL*5 <= alpha)) ;  // extended futility pruning
        }

        return false;
    }
}
