package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import org.apache.logging.log4j.LogManager;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class InputParserTest {

    InputParser inputParser = InputParser.getInstance();

    @Rule
    public LogAppenderResource appender = new LogAppenderResource(LogManager.getLogger(InputParser.class));

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

        // ensure pong 1337 was printed to the console
        assertTrue(appender.getOutput().contains("pong 1337"));

    }

}
