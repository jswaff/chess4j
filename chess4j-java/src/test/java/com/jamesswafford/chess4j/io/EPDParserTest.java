package com.jamesswafford.chess4j.io;

import java.util.List;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.ParseException;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class EPDParserTest {

    Board board = new Board();

    @Test
    public void epdParserTest1() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "7k/p7/1R5K/6r1/6p1/6P1/8/8 w - - bm Rb7; id \"WAC.006\";");

        assertEquals(BLACK_KING, b.getPiece(H8));
        assertEquals(BLACK_ROOK, b.getPiece(G5));
        assertEquals(WHITE_PAWN, b.getPiece(G3));
        assertEquals(Color.WHITE, b.getPlayerToMove());
        assertFalse(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));
        assertFalse(b.hasCastlingRight(WHITE_KINGSIDE));
        assertFalse(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertEquals(0, b.getMoveCounter());
        assertNull(b.getEPSquare());

        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals("Rb7", op1.getEpdOperands().get(0));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals("WAC.006", op2.getEpdOperands().get(0));
    }

    @Test
    public void epdParserTest2() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "6k1/2p2pp1/1p3n2/6rp/3P3q/P3PQ1P/6PN/5R1K w - - bm Qa8+; id \"position 0117\";");
        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals("Qa8+", op1.getEpdOperands().get(0));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals("position 0117", op2.getEpdOperands().get(0));
    }

    @Test
    public void epdParserTest3() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "r1bqk2r/ppp1nppp/4p3/n5N1/2BPp3/P1P5/2P2PPP/R1BQK2R w KQkq - bm Ba2 Nxf7; id \"WAC.022\";");
        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals(2, op1.getEpdOperands().size());
        assertEquals("Ba2", op1.getEpdOperands().get(0));
        assertEquals("Nxf7", op1.getEpdOperands().get(1));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals(1, op2.getEpdOperands().size());
        assertEquals("WAC.022", op2.getEpdOperands().get(0));
    }
}
