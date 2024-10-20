package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.*;
import org.junit.Test;

import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;

import static org.junit.Assert.*;

public class MoveParserTest {

    private Board board = new Board();
    private MoveParser mp = new MoveParser();

    @Test
    public void moveParserTest1() throws ParseException, IllegalMoveException {
        board.resetBoard();
        Board board2 = board.deepCopy();
        assertEquals(board, board2);
        Move mv = mp.parseMove("b1c3",board);
        /// should not have changed state of board
        assertEquals(board, board2);
        board.applyMove(mv);
        board2.setPos("rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1");
        assertEquals(board, board2);
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
        Move m2 = new Move(King.WHITE_KING, Square.E1, Square.G1, true);
        assertEquals(m2, m);
    }

    @Test
    public void moveParserTest4() throws ParseException, IllegalMoveException {
        board.setPos("5k2/1P6/1K6/8/8/8/8/8 w - -");
        Move m = mp.parseMove("b7b8n",board);
        Move m2 = new Move(Pawn.WHITE_PAWN, Square.B7, Square.B8,null, Knight.WHITE_KNIGHT);
        assertEquals(m2, m);
    }

    @Test
    public void moveParserTest5() throws ParseException, IllegalMoveException {
        board.setPos("8/8/8/8/8/8/3pk3/1KR5 b - -");
        Move m = mp.parseMove("d2xc1q",board);
        Move m2 = new Move(Pawn.BLACK_PAWN, Square.D2, Square.C1, Rook.WHITE_ROOK, Queen.BLACK_QUEEN);
        assertEquals(m2, m);
    }
}
