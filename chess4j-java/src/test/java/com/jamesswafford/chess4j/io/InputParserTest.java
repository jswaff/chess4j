package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class InputParserTest {

    InputParser inputParser = InputParser.getInstance();

    private static Logger inputParserLogger;
    private static TestLogAppender testAppender;

    @BeforeClass
    public static void setUpClass() {
        inputParserLogger = (Logger) LogManager.getLogger(InputParser.class);
        testAppender = TestLogAppender.createAppender("TestAppender");
        assertNotNull(testAppender);
        testAppender.start();
    }

    @Before
    public void setUp() {
        inputParserLogger.addAppender(testAppender);
        inputParserLogger.setAdditive(false);
    }

    @After
    public void tearDown() {
        testAppender.clearMessages();
        inputParserLogger.setAdditive(true);
        inputParserLogger.removeAppender(testAppender);
    }

    @Test
    public void newCmd() {

        // put the board in a position other than the initial position
        Globals.getBoard().applyMove(new Move(WHITE_PAWN, E2, E4));

        inputParser.parseCommand("new");

        assertEquals(new Board(), Globals.getBoard());
        assertEquals(0, Globals.getGameUndos().size());
    }

    @Test
    public void pingCmd() {

        inputParser.parseCommand("ping 1337");

        testAppender.printMessages();

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("pong 1337", output.get(0));
    }

    @Test
    public void userMoveCmd() {

        inputParser.parseCommand("new");
        inputParser.parseCommand("force");
        inputParser.parseCommand("usermove e2e4");

        assertEquals(0, testAppender.getNonDebugMessages().size());
        assertEquals(WHITE_PAWN, Globals.getBoard().getPiece(E4));
        assertEquals(1, Globals.getGameUndos().size());
    }

    @Test
    public void illegalMove() {

        inputParser.parseCommand("new");
        inputParser.parseCommand("usermove e2e5");

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Illegal move: e2e5", output.get(0));

        // try another non-sense move
        testAppender.clearMessages();
        inputParser.parseCommand("usermove bla");

        output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Illegal move: bla", output.get(0));
    }

    @Test
    public void errorMessage() {

        inputParser.parseCommand("foo");

        List<String> output = testAppender.getNonDebugMessages();

        assertEquals(1, output.size());
        assertEquals("Error (unknown command): foo", output.get(0));
    }
}
