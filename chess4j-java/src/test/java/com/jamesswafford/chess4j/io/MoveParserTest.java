package com.jamesswafford.chess4j.io;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class MoveParserTest {

    private Board board = new Board();
    private MoveParser mp = new MoveParser();

    @Test
    public void moveParserTest1() throws ParseException, IllegalMoveException {
        board.resetBoard();
        Board b = board.deepCopy();
        assertEquals(board, b);
        Move mv = mp.parseMove("b1c3",board);
        /// should not have changed state of board
        assertEquals(board, b);
        board.applyMove(mv);
        b.setPos("rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 0 1");
        assertTrue(board.equalExceptMoveHistory(b, false));
    }

    @Test(expected=IllegalMoveException.class)
    public void moveParserTest2() throws ParseException, IllegalMoveException {
        board.resetBoard();
        mp.parseMove("O-O",board);
    }

    @Test
    public void moveParserTest3() throws ParseException, IllegalMoveException {
        board.setPos("5k2/8/8/8/8/8/8/4K2R w K - 0 1");
        Move m = mp.parseMove("O-O",board);
        Move m2 = new Move(WHITE_KING, E1, G1, true);
        assertEquals(m2, m);
    }

    @Test
    public void moveParserTest4() throws ParseException, IllegalMoveException {
        board.setPos("5k2/1P6/1K6/8/8/8/8/8 w - -");
        Move m = mp.parseMove("b7b8n",board);
        Move m2 = new Move(WHITE_PAWN, B7, B8,null, WHITE_KNIGHT);
        assertEquals(m2, m);
    }

    @Test
    public void moveParserTest5() throws ParseException, IllegalMoveException {
        board.setPos("8/8/8/8/8/8/3pk3/1KR5 b - -");
        Move m = mp.parseMove("d2xc1q",board);
        Move m2 = new Move(BLACK_PAWN, D2, C1, WHITE_ROOK, BLACK_QUEEN);
        assertEquals(m2, m);
    }
}
