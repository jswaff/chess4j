package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jamesswafford.chess4j.board.squares.Square.E2;
import static com.jamesswafford.chess4j.board.squares.Square.E4;
import static com.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchIteratorTest {

    SearchIterator searchIterator;

    @Before
    public void setUp() {
        searchIterator = new SearchIterator();
    }
    
    @Test
    public void earlyExitOneLegalMove() throws Exception {

        MoveGenerator moveGenerator = mock(MoveGenerator.class);
        searchIterator.setMoveGenerator(moveGenerator);
        searchIterator.setEarlyExitOk(true);

        Board board = new Board();

        // set up one legal move
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        when(moveGenerator.generateLegalMoves(board)).thenReturn(Collections.singletonList(e2e4));

        // when the iterator is invoked, it should immediately return the one move without any search
        List<Move> pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
        assertEquals(pv, Collections.singletonList(e2e4));

    }

}