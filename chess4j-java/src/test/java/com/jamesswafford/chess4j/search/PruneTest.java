package com.jamesswafford.chess4j.search;

import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.Constants.*;
import static com.jamesswafford.chess4j.eval.EvalMaterial.*;

public class PruneTest {

    @Test
    public void testInitialPosition() {

        Board board = new Board();
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);

        assertFalse(Prune.prune(board,moves.get(0),false,false,0,-INFINITY,
                INFINITY,3));

        // now raise alpha to it's impossible to get to it
        assertFalse(Prune.prune(board,moves.get(0),false,false,0, QUEEN_VAL,
                INFINITY,3));

        // lower beta
        assertFalse(Prune.prune(board,moves.get(0),false,false,0, QUEEN_VAL,
                QUEEN_VAL*2,3));


        // lower depth
        assertTrue(Prune.prune(board,moves.get(0),false,false,0, QUEEN_VAL,
                QUEEN_VAL*2,2));

    }

}
