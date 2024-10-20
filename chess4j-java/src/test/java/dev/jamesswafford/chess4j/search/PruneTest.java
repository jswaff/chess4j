package dev.jamesswafford.chess4j.search;

import java.util.List;

import dev.jamesswafford.chess4j.Constants;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import org.junit.Test;

import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import static org.junit.Assert.*;

public class PruneTest {

    @Test
    public void testInitialPosition() {

        Board board = new Board();
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);

        assertFalse(Prune.prune(board,moves.get(0),false,false,0,-Constants.CHECKMATE,
                Constants.CHECKMATE,3));

        // now raise alpha to it's impossible to get to it
        assertFalse(Prune.prune(board,moves.get(0),false,false,0, SEE.QUEEN_VAL,
                Constants.CHECKMATE,3));

        // lower beta
        assertFalse(Prune.prune(board,moves.get(0),false,false,0, SEE.QUEEN_VAL,
                SEE.QUEEN_VAL*2,3));


        // lower depth
        assertTrue(Prune.prune(board,moves.get(0),false,false,0, SEE.QUEEN_VAL,
                SEE.QUEEN_VAL*2,2));
    }
}
