package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.eval.EvalBishop.*;

public class EvalBishopTest {

    @Test
    public void testEvalBishop() {

        assertEquals(BISHOP_PST[C1.value()], evalBishop(true, C1));

        // test the symmetry
        assertEquals(evalBishop(true, C1), evalBishop(false, C8));
    }


}
