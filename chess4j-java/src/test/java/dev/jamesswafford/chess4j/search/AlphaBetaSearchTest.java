package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.eval.Evaluator;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.movegen.MoveGenerator;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static dev.jamesswafford.chess4j.Constants.CHECKMATE;
import static dev.jamesswafford.chess4j.board.squares.Square.*;
import static dev.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static dev.jamesswafford.chess4j.pieces.King.BLACK_KING;
import static dev.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static dev.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static dev.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static dev.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AlphaBetaSearchTest {

    AlphaBetaSearch search;

    @Before
    public void setUp() {
        search = new AlphaBetaSearch();
        TTHolder.getInstance().clearTables();
    }

    @Test
    public void search_initialPos_depth1() {

        // given a board in the initial position
        Board board = new Board();
        Evaluator evaluator = mock(Evaluator.class);
        SearchParameters params = new SearchParameters(1, -CHECKMATE, CHECKMATE);

        // set up the return scores for a couple of moves.  all others default to score=0.
        // the returned scores are from black's point-of-view so the lower the better for white.
        Board board1 = new Board();
        board1.applyMove(new Move(WHITE_PAWN, E2, E4));
        when(evaluator.evaluateBoard(board1)).thenReturn(3);

        Board board2 = new Board();
        board2.applyMove(new Move(WHITE_KNIGHT, B1, C3));
        when(evaluator.evaluateBoard(board2)).thenReturn(-5);

        // when the search is invoked
        search.setEvaluator(evaluator);

        int score = search.search(board, params,
                SearchOptions.builder().startTime(System.currentTimeMillis())
                        .avoidResearches(true)
                        .build());

        // then the evaluator should have been invoked for each move
        verify(evaluator, times(20)).evaluateBoard(any(Board.class));

        // and the score should be the highest returned score
        assertEquals(5, score);

        // and the PV should have Nc3
        assertEquals(1, search.getPv().size());
        assertEquals(new Move(WHITE_KNIGHT, B1, C3), search.getPv().getFirst());
    }

    @Test
    public void mateIn1() {
        Board board = new Board("4k3/8/3Q4/2B5/8/8/1K6/8 w - -");

        SearchParameters params = new SearchParameters(2, -CHECKMATE, CHECKMATE);

        int score = search.search(board, params);
        assertEquals(CHECKMATE-1, score);

        List<Move> pv = search.getPv();
        assertEquals(1, pv.size());
        assertEquals(new Move(WHITE_QUEEN, D6, E7), pv.getFirst());
    }

    @Test
    public void mateIn1b() {
        Board board = new Board("4K3/8/8/3n2q1/8/8/3k4/8 b - -");

        SearchParameters params = new SearchParameters(2, -CHECKMATE, CHECKMATE);

        int score = search.search(board, params);
        assertEquals(CHECKMATE-1, score);

        List<Move> pv = search.getPv();
        assertEquals(1, pv.size());
        assertEquals(new Move(BLACK_QUEEN, G5, E7), pv.getFirst());
    }

    @Test
    public void mateIn2() {
        Board board = new Board("r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - -");

        SearchParameters params = new SearchParameters(4, -CHECKMATE, CHECKMATE);

        int score = search.search(board, params);
        assertEquals(CHECKMATE-3, score);

        List<Move> pv = search.getPv();
        assertEquals(3, pv.size());
        assertEquals(new Move(WHITE_QUEEN, D2, H6), pv.get(0));
        assertEquals(new Move(BLACK_KING, G7, H6, WHITE_QUEEN), pv.get(1));
        assertEquals(new Move(WHITE_BISHOP, H4, F6, BLACK_PAWN), pv.get(2));
    }

    @Test
    public void mateIn3() {
        Board board = new Board("r5rk/5p1p/5R2/4B3/8/8/7P/7K w - -");

        SearchParameters params = new SearchParameters(6, -CHECKMATE, CHECKMATE);

        int score = search.search(board, params);
        assertEquals(CHECKMATE-5, score);

        List<Move> pv = search.getPv();
        assertEquals(5, pv.size());
        assertEquals(new Move(WHITE_ROOK, F6, A6), pv.get(0));
        assertEquals(new Move(BLACK_PAWN, F7, F6), pv.get(1));
        assertEquals(new Move(WHITE_BISHOP, E5, F6, BLACK_PAWN), pv.get(2));
        assertEquals(new Move(BLACK_ROOK, G8, G7), pv.get(3));
        assertEquals(new Move(WHITE_ROOK, A6, A8, BLACK_ROOK), pv.get(4));
    }

    @Test
    public void staleMate() {
        Board board = new Board("8/6p1/5p2/5k1K/7P/8/8/8 w - -");

        SearchParameters params = new SearchParameters(1, -CHECKMATE, CHECKMATE);

        int score = search.search(board, params);
        assertEquals(0, score);

        assertEquals(0, search.getPv().size());
    }

    /**
     * Test that the search is making the correct cutoffs using the alpha/beta algorithm.  This example
     * follows the example on page 6 of my Master's project paper.
     */
    @Test
    public void alphaBetaCutoffs() {

        Board boardA = new Board();

        Evaluator evaluator = mock(Evaluator.class);
        MoveGenerator moveGenerator = mock(MoveGenerator.class);
        SearchParameters params = new SearchParameters(3, -CHECKMATE, CHECKMATE);
        ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);

        // from position A we need three moves.  It doesn't matter what the moves
        // are as long as they are legal.
        Move e2e3 = new Move(WHITE_PAWN, E2, E3);
        Move e2e4 = new Move(WHITE_PAWN, E2, E4);
        Move d2d4 = new Move(WHITE_PAWN, D2, D4);

        when(moveGenerator.generatePseudoLegalNonCaptures(boardA)).thenReturn(Arrays.asList(e2e3, e2e4, d2d4));
        Board boardB = boardA.deepCopy();
        boardB.applyMove(e2e3);
        Board boardI = boardA.deepCopy();
        boardI.applyMove(e2e4);
        Board boardO = boardA.deepCopy();
        boardO.applyMove(d2d4);

        // from position B (1. e3) we need two moves.
        Move d7d5 = new Move(BLACK_PAWN, D7, D5);
        Move d7d6 = new Move(BLACK_PAWN, D7, D6);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardB)).thenReturn(Arrays.asList(d7d5, d7d6));
        Board boardC = boardB.deepCopy();
        boardC.applyMove(d7d5);
        Board boardF = boardB.deepCopy();
        boardF.applyMove(d7d6);

        // from position C (1. e3 d5) we need two moves.
        Move b2b3 = new Move(WHITE_PAWN, B2, B3);
        Move b2b4 = new Move(WHITE_PAWN, B2, B4);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardC)).thenReturn(Arrays.asList(b2b3, b2b4));
        Board boardD = boardC.deepCopy();
        boardD.applyMove(b2b3);
        Board boardE = boardC.deepCopy();
        boardE.applyMove(b2b4);

        // position D (1. e3 d5 2. b3) is a leaf node
        when(evaluator.evaluateBoard(boardD)).thenReturn(-1);

        // position E (1. e3 d5 2. b4) is a leaf node
        when(evaluator.evaluateBoard(boardE)).thenReturn(-3);

        // from position F (1. e3 d6) we need two moves.  One will end up being cutoff.
        Move c2c3 = new Move(WHITE_PAWN, C2, C3);
        Move c2c4 = new Move(WHITE_PAWN, C2, C4);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardF)).thenReturn(Arrays.asList(c2c3, c2c4));
        Board boardG = boardF.deepCopy();
        boardG.applyMove(c2c3);
        Board boardH = boardF.deepCopy();
        boardH.applyMove(c2c4);

        // position  G (1. e3 d6 2. c3) is a leaf node
        when(evaluator.evaluateBoard(boardG)).thenReturn(-5);

        // from position I  (1. e4) we need two moves.
        Move c7c5 = new Move(BLACK_PAWN, C7, C5);
        Move c7c6 = new Move(BLACK_PAWN, C7, C6);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardI)).thenReturn(Arrays.asList(c7c5, c7c6));
        Board boardJ = boardI.deepCopy();
        boardJ.applyMove(c7c5);
        Board boardL = boardI.deepCopy();
        boardL.applyMove(c7c6);

        // from position J (1. e4 c5) we need one move.
        Move a2a3 = new Move(WHITE_PAWN, A2, A3);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardJ)).thenReturn(Collections.singletonList(a2a3));
        Board boardK = boardJ.deepCopy();
        boardK.applyMove(a2a3);

        // position K (1. e4 c5 2. a3) is a leaf node.
        when(evaluator.evaluateBoard(boardK)).thenReturn(9);

        // from position O (1. d4)  we need two moves.
        Move g7g5 = new Move(BLACK_PAWN, G7, G5);
        Move g7g6 = new Move(BLACK_PAWN, G7, G6);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardO)).thenReturn(Arrays.asList(g7g5, g7g6));
        Board boardP = boardO.deepCopy();
        boardP.applyMove(g7g5);
        Board boardS = boardO.deepCopy();
        boardS.applyMove(g7g6);

        // from position P (1. d4 g5) we need two moves
        Move h2h4 = new Move(WHITE_PAWN, H2, H4);
        Move h2h3 = new Move(WHITE_PAWN, H2, H3);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardP)).thenReturn(Arrays.asList(h2h4, h2h3));
        Board boardQ = boardP.deepCopy();
        boardQ.applyMove(h2h4);
        Board boardR = boardP.deepCopy();
        boardR.applyMove(h2h3);

        // position Q is a leaf node.
        when(evaluator.evaluateBoard(boardQ)).thenReturn(0);

        // position R is a leaf node
        when(evaluator.evaluateBoard(boardR)).thenReturn(4);

        // from position S (1. d4 g6) we need two moves
        Move b1c3 = new Move(WHITE_KNIGHT, B1, C3);
        Move b1a3 = new Move(WHITE_KNIGHT, B1, A3);
        when(moveGenerator.generatePseudoLegalNonCaptures(boardS)).thenReturn(Arrays.asList(b1c3, b1a3));
        Board boardT = boardS.deepCopy();
        boardT.applyMove(b1c3);
        Board boardU = boardS.deepCopy();
        boardU.applyMove(b1a3);

        // start the search!
        search.setEvaluator(evaluator);
        search.setMoveGenerator(moveGenerator);
        search.setKillerMovesStore(mock(KillerMovesStore.class));

        int score = search.search(boardA, params,
                SearchOptions.builder().startTime(System.currentTimeMillis())
                    .avoidResearches(true)
                    .build());

        assertEquals(3, score);

        // ensure the proper nodes were evaluated
        verify(evaluator, times(6)).evaluateBoard(boardCaptor.capture());
        // it would be nice to verify the actual boards that were evaluated but they are
        // all board A since we don't copy the board when evaluating.
        assertEquals(boardA, boardCaptor.getAllValues().getFirst());

        // verify 14 nodes visited and 3 fail highs
        // 8 of those nodes are "interior" nodes and 6 are leaf nodes
        assertEquals(8L, search.getSearchStats().nodes);
        assertEquals(6L, search.getSearchStats().qnodes);
        assertEquals(3L, search.getSearchStats().failHighs);

        // verify the PV follows A -> B -> C -> E
        assertEquals(3, search.getPv().size());
        assertEquals(e2e3, search.getPv().get(0));
        assertEquals(d7d5, search.getPv().get(1));
        assertEquals(b2b4, search.getPv().get(2));
    }

    // this test is highly sensitive to the JVM being "warmed up"
    @Test
    public void stopSearch() {

        // start what would be a long running search in a separate thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> search.search(new Board(),
                new SearchParameters(8, -CHECKMATE, CHECKMATE)));

        long start = System.currentTimeMillis();
        search.stop();

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(future::isDone);

        long duration = System.currentTimeMillis() - start;
        System.out.println("duration: " + duration);
        assertTrue(duration < 250);
    }

    @Test
    public void stoppedSearchDoesNotReturnPV() {

        // first a search that has NOT been stopped
        search.search(new Board(), new SearchParameters(1, -CHECKMATE, CHECKMATE));
        assertEquals(1, search.getPv().size());

        search.stop();
        search.search(new Board(), new SearchParameters(1, -CHECKMATE, CHECKMATE));
        assertEquals(0, search.getPv().size());
    }

    @Test
    public void lastPvIsTriedFirst() {

        // initialize the search
        Board board = new Board();
        search.search(board, new SearchParameters(1, -CHECKMATE, CHECKMATE));
        List<Move> lastPv = new ArrayList<>(search.getPv());
        assertEquals(1, lastPv.size());

        // now try deeper searches.
        final Map<Integer, Boolean> visited = new HashMap<>();
        Consumer<PvCallbackDTO> pvCallback = pvUpdate -> {
            // we expect the first N-1 moves to match the previous PV
            int ply = pvUpdate.ply;
            if (ply < lastPv.size()) {
                Move rootMv = pvUpdate.pv.getFirst();
                if (visited.get(ply) == null) {
                    assertEquals(lastPv.get(ply), rootMv);
                    visited.put(ply, true);
                }
            }
        };

        SearchOptions opts = SearchOptions.builder().pvCallback(pvCallback).startTime(System.currentTimeMillis())
                .build();
        for (int depth=2; depth <= 6; depth++) {
            search.search(board, new SearchParameters(depth, -CHECKMATE, CHECKMATE), opts);
            assertTrue(search.getPv().size() >= depth);
            assertEquals(depth-1, visited.keySet().size());

            // prepare for next iteration
            visited.clear();
            lastPv.clear();
            lastPv.addAll(search.getPv());
        }
    }

}
