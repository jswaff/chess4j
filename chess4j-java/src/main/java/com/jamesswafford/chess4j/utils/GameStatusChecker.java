package com.jamesswafford.chess4j.utils;

import java.util.List;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Draw;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.movegen.MoveGen;

public final class GameStatusChecker {

    private GameStatusChecker() { }

    public static GameStatus getGameStatus(Board board, List<Undo> undos) {

        List<Move> moves = MoveGen.genLegalMoves(board);
        if (moves.size()==0) {
            if (BoardUtils.isPlayerInCheck(board)) {
                return GameStatus.CHECKMATED;
            } else {
                return GameStatus.STALEMATED;
            }
        }

        if (Draw.isDrawLackOfMaterial(board)) {
            return GameStatus.DRAW_MATERIAL;
        }

        if (Draw.isDrawByRep(board, undos)) {
            return GameStatus.DRAW_REP;
        }

        if (Draw.isDrawLackOfMaterial(board)) {
            return GameStatus.DRAW_BY_50;
        }

        return GameStatus.INPROGRESS;
    }
}
