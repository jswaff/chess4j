package com.jamesswafford.chess4j.search;

import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.EvalMaterial.*;

public class PruneTest {

    @Test
    public void testInitialPosition() {

        Board b = Board.INSTANCE;
        b.resetBoard();
        List<Move> moves = MoveGen.genLegalMoves(b);

        assertFalse(Prune.prune(b,moves.get(0),false,false,0,-Constants.INFINITY,
                Constants.INFINITY,3));

        // now raise alpha to it's impossible to get to it
        assertFalse(Prune.prune(b,moves.get(0),false,false,0, QUEEN_VAL,
                Constants.INFINITY,3));

        // lower beta
        assertFalse(Prune.prune(b,moves.get(0),false,false,0, QUEEN_VAL,
                QUEEN_VAL*2,3));


        // lower depth
        assertTrue(Prune.prune(b,moves.get(0),false,false,0, QUEEN_VAL,
                QUEEN_VAL*2,2));

    }

}
