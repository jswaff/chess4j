package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalMajorOn7th.*;

public class EvalMajorOn7thTest {

    private final Board board = new Board();

    @Test
    public void testEvalMajorOn7th() {

        board.setPos("7k/2Q2R2/8/8/8/8/r7/7K w - - 0 1");
        /*
            -------k
            --Q--R--	white to move
            --------	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        assertEquals(MAJOR_ON_7TH + CONNECTED_MAJORS_ON_7TH,
                evalMajorOn7th(board, true, C7));

        assertEquals(MAJOR_ON_7TH, evalMajorOn7th(board, true, F7));

        assertEquals(MAJOR_ON_7TH, evalMajorOn7th(board, false, A2));

        // move the black king out from the back rank
        board.setPos("8/2Q2R2/7k/8/8/8/r7/7K w - - 0 1");

        /*
            --------
            --Q--R--	white to move
            -------k	castling rights:
            --------	no ep
            --------	fifty=0
            --------	move counter=0
            r-------
            -------K
         */

        assertEquals(0, evalMajorOn7th(board, true, C7));

        assertEquals(0, evalMajorOn7th(board, true, F7));

    }

}
