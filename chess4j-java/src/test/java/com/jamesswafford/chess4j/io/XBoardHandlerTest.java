package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.BookMove;
import com.jamesswafford.chess4j.book.OpeningBook;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.search.SearchIterator;
import com.jamesswafford.chess4j.search.SearchIteratorImpl;
import com.jamesswafford.chess4j.utils.GameResult;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class XBoardHandlerTest {

    XBoardHandler xboardHandler;
    OpeningBook openingBook;
    SearchIterator searchIterator;

    private static Logger inputParserLogger;
    private static Logger printGameResultLogger;
    private static TestLogAppender testAppender;

    @BeforeClass
    public static void setUpClass() {
        inputParserLogger = (Logger) LogManager.getLogger(XBoardHandler.class);
        printGameResultLogger = (Logger) LogManager.getLogger(PrintGameResult.class);
        testAppender = TestLogAppender.createAppender("TestAppender");
        assertNotNull(testAppender);
        testAppender.start();
    }

    @Before
    public void setUp() {
        xboardHandler = new XBoardHandler();
        openingBook = mock(OpeningBook.class);
        xboardHandler.setOpeningBook(openingBook);
        searchIterator = mock(SearchIterator.class);
        xboardHandler.setSearchIterator(searchIterator);

        inputParserLogger.addAppender(testAppender);
        inputParserLogger.setAdditive(false);

        printGameResultLogger.addAppender(testAppender);
        printGameResultLogger.setAdditive(false);
    }

    @After
    public void tearDown() {
        testAppender.clearMessages();

        printGameResultLogger.setAdditive(true);
        printGameResultLogger.removeAppender(testAppender);

        inputParserLogger.setAdditive(true);
        inputParserLogger.removeAppender(testAppender);
    }

    @Test
    public void easyCmd() {
        xboardHandler.parseAndDispatch("easy");
        assertEquals(0, testAppender.getNonDebugMessages().size());
        // TODO: ensure pondering was turned off
    }

    @Test
    public void forceCmd() {
        xboardHandler.parseAndDispatch("new");
        assertFalse(xboardHandler.isForceMode());
        xboardHandler.parseAndDispatch("force");
        assertTrue(xboardHandler.isForceMode());

        xboardHandler.parseAndDispatch("usermove e2e4");

        // the global board should have been updated
        Board board = new Board();
        Move move = new Move(WHITE_PAWN, E2, E4);
        Undo undo = board.applyMove(move);
        assertEquals(board, Globals.getBoard());
        assertEquals(Collections.singletonList(undo), Globals.getGameUndos());

        // the search should NOT have been called
        verify(searchIterator, never()).findPvFuture(any(), any());
    }

    @Test
    public void goCmd() {
        Move move = new Move(WHITE_PAWN, E2, E4);
        when(searchIterator.findPvFuture(new Board(), new ArrayList<>()))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(move)));

        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("go");

        List<String> output = testAppender.getNonDebugMessages();
        assertEquals(1, output.size());
        assertEquals("move e2e4", output.get(0));

        // the global board should have been updated
        Board board = new Board();
        Undo undo = board.applyMove(move);
        assertEquals(board, Globals.getBoard());
        assertEquals(Collections.singletonList(undo), Globals.getGameUndos());

        // the search should have been called
        verify(searchIterator, times(1)).setMaxDepth(eq(0)); // by NEW command
        verify(searchIterator, times(1)).findPvFuture(
                eq(new Board()), eq(new ArrayList<>()));
    }

    @Test
    public void goCmd_EndOfGame() {

        xboardHandler.parseAndDispatch("new");

        // set up Fool's Mate
        String fen = "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2";
        xboardHandler.parseAndDispatch("setboard " + fen);

        Move move = new Move(BLACK_QUEEN, D8, H4);
        when(searchIterator.findPvFuture(Globals.getBoard().deepCopy(), new ArrayList<>()))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(move)));

        xboardHandler.parseAndDispatch("go");
        assertEquals(GameStatus.CHECKMATED,
                GameStatusChecker.getGameStatus(Globals.getBoard(), Globals.getGameUndos()));

        List<String> output = testAppender.getNonDebugMessages();
        assertEquals(2, output.size());
        assertEquals("move d8h4", output.get(0));
        assertEquals("RESULT 0-1 {Black mates}\n", output.get(1));
    }

    @Test
    public void hardCmd() {
        xboardHandler.parseAndDispatch("hard");
        assertEquals(0, testAppender.getNonDebugMessages().size());
        // TODO: ensure pondering turned on
    }

    @Test
    public void memoryCmd() {
        xboardHandler.parseAndDispatch("memory 6");
        assertEquals(131072, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getPawnTransTable().tableCapacity());

        xboardHandler.parseAndDispatch("memory 3");
        assertEquals(65536, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getPawnTransTable().tableCapacity());
    }

    @Test
    public void newCmd() {

        // put the board in a position other than the initial position
        Globals.getBoard().applyMove(new Move(WHITE_PAWN, E2, E4));

        xboardHandler.parseAndDispatch("new");

        assertEquals(new Board(), Globals.getBoard());
        assertEquals(0, Globals.getGameUndos().size());
    }

    @Test
    public void nopostCmd() {
        xboardHandler.parseAndDispatch("nopost");
        verify(searchIterator).setPost(false);
    }

    @Test
    public void pgn2bookCmd() {
        xboardHandler.parseAndDispatch("pgn2book foo.pgn");
        verify(openingBook).addToBook(new File("foo.pgn"));
    }

    @Test
    public void pingCmd() {
        xboardHandler.parseAndDispatch("ping 1337");

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("pong 1337", output.get(0));
    }

    @Test
    public void postCmd() {
        xboardHandler.parseAndDispatch("post");
        verify(searchIterator).setPost(true);
    }

    @Test
    public void protoverCmd() {
        xboardHandler.parseAndDispatch("protover 2");

        // ensure we sent some 'feature' lines, ending with 'done'
        List<String> featureStatements = testAppender.getNonDebugMessages();
        assertTrue(featureStatements.size() > 0);
        assertEquals("feature done=1", featureStatements.get(featureStatements.size()-1));
    }

    @Test
    public void randomCmd() {
        xboardHandler.parseAndDispatch("random");
        assertEquals(0, testAppender.getNonDebugMessages().size());
    }

    @Test
    public void ratingCmd() {
        xboardHandler.parseAndDispatch("rating");
        assertEquals(0, testAppender.getNonDebugMessages().size());
    }

    @Test
    public void removeCmd() {
        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove e2e4");
        xboardHandler.parseAndDispatch("usermove e7e5");
        xboardHandler.parseAndDispatch("remove");

        assertEquals(new Board(), Globals.getBoard());
        assertEquals(0, Globals.getGameUndos().size());
    }

    @Test
    public void resultCmd() {
        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove e2e4");
        xboardHandler.parseAndDispatch("usermove e7e5");
        xboardHandler.parseAndDispatch("result 1-0 {Black resigns in fear}");

        List<String> messages = testAppender.getMessages();
        assertTrue(messages.contains("# result: 1-0 - LOSS"));
        assertTrue(messages.contains("# game moves: e2e4 e7e5 "));

        verify(openingBook).learn(
                List.of(new Move(WHITE_PAWN, E2, E4), new Move(BLACK_PAWN, E7, E5)),
                Color.BLACK,
                GameResult.LOSS);

        /// take 2
        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove c2c4");
        xboardHandler.parseAndDispatch("result 1-0 {Black resigns in fear}");

        messages = testAppender.getMessages();
        assertTrue(messages.contains("# result: 1-0 - LOSS"));
        assertTrue(messages.contains("# game moves: c2c4 "));

        verify(openingBook).learn(
                List.of(new Move(WHITE_PAWN, C2, C4)),
                Color.BLACK,
                GameResult.LOSS);
    }

    @Test
    public void sdCmd() {
        xboardHandler.parseAndDispatch("sd 12");
        verify(searchIterator).setMaxDepth(12);
    }

    @Test
    public void stCmd() {
        xboardHandler.parseAndDispatch("st 5");
        verify(searchIterator).setMaxTime(5000);
    }

    @Test
    public void setboardCmd() {

        xboardHandler.parseAndDispatch("new");
        // play one move just to put an undo in the list
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove a2a3");

        // set up board to compare final result to
        Board board = new Board();
        board.applyMove(new Move(WHITE_PAWN, F2, F3));
        board.applyMove(new Move(BLACK_PAWN, E7, E5));
        board.applyMove(new Move(WHITE_PAWN, G2, G4));

        String fen = "rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq g3 0 2";
        xboardHandler.parseAndDispatch("setboard " + fen);

        assertEquals(board, Globals.getBoard());
        assertEquals(0, Globals.getGameUndos().size());
    }

    @Test
    public void setboardCmd_IllegalPos() {

        // set the board to something that is not the initial position
        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove a2a3");

        // create a copy of the position ,we should revert to it later
        Board expected = Globals.getBoard().deepCopy();

        // attempt to set the board to something illegal
        xboardHandler.parseAndDispatch("setboard bla bla bla");
        List<String> output = testAppender.getNonDebugMessages();
        assertEquals(1, output.size());
        assertEquals("tellusererror Illegal position", output.get(0));

        // the board should be unchanged
        assertEquals(expected, Globals.getBoard());

        // something more subtle - the last rank is missing a square
        xboardHandler.parseAndDispatch("setboard rnbqkbnr/pppp1ppp/8/4p3/6P1/5P2/PPPPP2P/RNBQKBN b KQkq g3 0 2");
        output = testAppender.getNonDebugMessages();
        assertEquals(2, output.size());
        assertEquals("tellusererror Illegal position", output.get(1));
    }

    @Test
    public void timeCmd() {
        xboardHandler.parseAndDispatch("level 0 5 0");
        xboardHandler.parseAndDispatch("time 2500");
        verify(searchIterator, times(1)).setMaxTime(1000);

        xboardHandler.parseAndDispatch("level 0 5 3");
        xboardHandler.parseAndDispatch("time 2500");
        verify(searchIterator, times(1)).setMaxTime(4000);

        // fixed time
        xboardHandler.parseAndDispatch("st 30");
        xboardHandler.parseAndDispatch("time 300");
        verify(searchIterator, times(1)).setMaxTime(2900);
    }

    @Test
    public void undoCmd() {
        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove e2e4");
        xboardHandler.parseAndDispatch("usermove e7e5");
        xboardHandler.parseAndDispatch("undo");

        Board board = new Board();
        board.applyMove(new Move(WHITE_PAWN, E2, E4));
        assertEquals(board, Globals.getBoard());
        assertEquals(1, Globals.getGameUndos().size());
    }

    @Test
    public void userMoveCmd() {

        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("force");
        xboardHandler.parseAndDispatch("usermove e2e4");

        assertEquals(0, testAppender.getNonDebugMessages().size());
        assertEquals(WHITE_PAWN, Globals.getBoard().getPiece(E4));
        assertEquals(1, Globals.getGameUndos().size());
    }

    @Test
    public void xboardCmd() {
        xboardHandler.parseAndDispatch("xboard");
        assertEquals(0, testAppender.getNonDebugMessages().size());
    }

    @Test
    public void moveNowCmd() {

        // use a real search iterator
        SearchIterator searchIterator = new SearchIteratorImpl();
        xboardHandler.setSearchIterator(searchIterator);
        xboardHandler.parseAndDispatch("new");
        Board origBoard = Globals.getBoard().deepCopy();
        xboardHandler.parseAndDispatch("sd 20");
        xboardHandler.parseAndDispatch("usermove e2e4");
        xboardHandler.parseAndDispatch("?");

        // wait for the board to change state
        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .with()
                .pollInterval(new Duration(50, TimeUnit.MILLISECONDS))
                .until(() -> !origBoard.equals(Globals.getBoard()));

        // ensure the move command was sent
        List<String> output = testAppender.getNonDebugMessages();
        assertEquals(1, output.size());
        assertTrue(output.get(0).startsWith("move "));
    }

    /**
     * The xboard documentation says you should never see the ping command when it is your move, but if you do...
     * you must not send the "pong" reply to xboard until after you send your move.
     */
    @Test
    public void moveNow_pingSequence() {

        // use a real search iterator
        SearchIterator searchIterator = new SearchIteratorImpl();
        xboardHandler.setSearchIterator(searchIterator);
        xboardHandler.parseAndDispatch("new");
        Board origBoard = Globals.getBoard().deepCopy();
        xboardHandler.parseAndDispatch("sd 20");
        xboardHandler.parseAndDispatch("usermove e2e4");
        xboardHandler.parseAndDispatch("?");
        xboardHandler.parseAndDispatch("ping 1337");

        // wait for the board to change state
        Awaitility.await()
                .atMost(Duration.ONE_SECOND)
                .with()
                .pollInterval(new Duration(50, TimeUnit.MILLISECONDS))
                .until(() -> !origBoard.equals(Globals.getBoard()));

        // ensure the move command was sent
        List<String> output = testAppender.getNonDebugMessages();
        assertEquals(2, output.size());
        assertTrue(output.get(0).startsWith("move "));
        assertTrue(output.get(1).startsWith("pong "));
    }

    @Test
    public void illegalMove() {

        xboardHandler.parseAndDispatch("new");
        xboardHandler.parseAndDispatch("usermove e2e5");

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Illegal move: e2e5", output.get(0));

        // try another non-sense move
        testAppender.clearMessages();
        xboardHandler.parseAndDispatch("usermove bla");

        output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Illegal move: bla", output.get(0));
    }

    @Test
    public void errorMessage() {

        xboardHandler.parseAndDispatch("foo");

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Error (unknown command): foo", output.get(0));
    }

}
