package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

public class EvalMajorOn7thTest {

    private Board board = new Board();

    @Test
    public void testEvalMajorOn7th() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");

        assertEquals(MAJOR_ON_7TH + CONNECTED_MAJORS_ON_7TH,
                evalMajorOn7th(board, true, C7));
    }

}
