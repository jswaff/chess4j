package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalQueen.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

public class EvalQueenTest {

    private Board board = new Board();

    @Test
    public void testEvalQueen() {

        board.resetBoard();

        assertEquals(QUEEN_PST[D1.value()], evalQueen(board, D1));

        // test symmetry
        assertEquals(evalQueen(board, D1), evalQueen(board, D8));
    }

    @Test
    public void testEvalQueen_bankRankMate() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(QUEEN_PST[C7.value()] + MAJOR_ON_7TH + CONNECTED_MAJORS_ON_7TH,
                evalQueen(board, C7));
    }
}
