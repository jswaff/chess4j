package com.jamesswafford.chess4j.search;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.movegen.MoveGenerator;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.jamesswafford.chess4j.Constants.CHECKMATE;
import static com.jamesswafford.chess4j.Constants.INFINITY;
import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static com.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SearchIteratorImplTest {

    SearchIteratorImpl searchIterator;

    @Before
    public void setUp() {
        searchIterator = new SearchIteratorImpl();
    }

    @Test
    public void earlyExitOneLegalMove() throws Exception {

        MoveGenerator moveGenerator = mock(MoveGenerator.class);
        searchIterator.setMoveGenerator(moveGenerator);
        searchIterator.setEarlyExitOk(true);
        searchIterator.setPost(false);

        Board board = new Board();

        // set up one legal move
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        when(moveGenerator.generateLegalMoves(board)).thenReturn(Collections.singletonList(e2e4));

        // when the iterator is invoked, it should immediately return the one move without any search
        List<Move> pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();
        assertEquals(pv, Collections.singletonList(e2e4));
    }

    @Test
    public void iterateUntilMaxDepth() throws Exception {

        // given an iterator and a board to search
        Search search = mock(Search.class);
        searchIterator.setSearch(search);
        searchIterator.setEarlyExitOk(true);
        searchIterator.setMaxDepth(3);
        searchIterator.setPost(false);
        
        Board board = new Board();
        List<Undo> undos = new ArrayList<>();

        // set up search PV
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        List<Move> expectedPV = Collections.singletonList(e2e4);
        when(search.getPv()).thenReturn(expectedPV);

        // when the iterator is invoked
        List<Move> pv = searchIterator.findPvFuture(board, undos).get();

        // then the PV will be the PV returned from the last search
        assertEquals(expectedPV, pv);

        // then the search will have been invoked three times
        // getLastPV is called after each search in an assert statement
        verify(search, times(1)).initialize();

        verify(search, times(3)).getPv();

        verify(search, times(4)).isStopped();

        verify(search, times(1))
                .search(eq(board), eq(undos), eq(new SearchParameters(1, -INFINITY, INFINITY)), any());

        verify(search, times(1))
                .search(eq(board), eq(undos), eq(new SearchParameters(2, -INFINITY, INFINITY)), any());

        verify(search, times(1))
                .search(eq(board), eq(undos), eq(new SearchParameters(3, -INFINITY, INFINITY)), any());

        verifyNoMoreInteractions(search);
    }


    @Test
    public void iteratorStopsWhenMateIsFound() throws Exception {

        // given an iterator and a board to search
        Search search = mock(Search.class);
        searchIterator.setSearch(search);
        searchIterator.setEarlyExitOk(true);
        searchIterator.setMaxDepth(3);
        searchIterator.setPost(false);

        Board board = new Board();
        List<Undo> undos = new ArrayList<>();

        // return a mate score on the depth 2 search
        when(search.search(any(), any(), eq(new SearchParameters(2, -INFINITY, INFINITY)), any()))
                .thenReturn(-CHECKMATE+2);

        // set up search PV
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        List<Move> expectedPV = Collections.singletonList(e2e4);
        when(search.getPv()).thenReturn(expectedPV);

        // when the iterator is invoked
        List<Move> pv = searchIterator.findPvFuture(board, undos).get();

        // then the PV will be the PV returned from the last search
        assertEquals(expectedPV, pv);

        verify(search, times(1)).initialize();

        verify(search, times(2)).getPv();

        verify(search, times(3)).isStopped();

        verify(search, times(1))
                .search(eq(board), eq(undos), eq(new SearchParameters(1, -INFINITY, INFINITY)), any());

        verify(search, times(1))
                .search(eq(board), eq(undos), eq(new SearchParameters(2, -INFINITY, INFINITY)), any());

        verify(search, times(0))
                .search(eq(board), eq(undos), eq(new SearchParameters(3, -INFINITY, INFINITY)), any());

    }

    @Test
    public void stopIterator() {

        // start a search with no depth or time limits
        searchIterator.setMaxDepth(0);
        searchIterator.setMaxTime(0);

        CompletableFuture<List<Move>> future = searchIterator.findPvFuture(new Board(), new ArrayList<>());

        searchIterator.stop();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);
    }

    @Test
    public void iteratorStopsOnTime() {

        searchIterator.setMaxDepth(0); // no limit
        searchIterator.setMaxTime(2000);

        CompletableFuture<List<Move>> future = searchIterator.findPvFuture(new Board(), new ArrayList<>());

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);
    }


    @Test
    public void stoppedIteratorProducesValidLine() {

        searchIterator.stop();

        Board board = new Board("8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - -");

        CompletableFuture<List<Move>> future = searchIterator.findPvFuture(board, new ArrayList<>());

        List<Move> pv = future.join();
        assertEquals(1, pv.size());

        // ensure the highest scoring move by static analysis was selected
        assertEquals(new Move(WHITE_PAWN, E7, E8, null, WHITE_QUEEN), pv.get(0));
    }

}