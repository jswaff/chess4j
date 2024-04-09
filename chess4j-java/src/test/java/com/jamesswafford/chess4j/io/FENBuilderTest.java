package com.jamesswafford.chess4j.io;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static org.junit.Assert.*;

public class FENBuilderTest {

    @Test
    public void testCreateFen_InitialPos() {

        Board board = new Board();

        String fen = FENBuilder.createFen(board, true);

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen);
    }

    @Test
    public void testCreateFen_pos1() {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        Board board = new Board(fen);

        assertEquals(fen, FENBuilder.createFen(board, true));
    }

    @Test
    public void testCreateFen_pos2() {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -";

        Board board = new Board(fen);

        assertEquals(fen, FENBuilder.createFen(board, false));
    }

    @Test
    public void testCreateFen_pos3() {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";

        Board board = new Board(fen);

        assertEquals(fen, FENBuilder.createFen(board, true));
    }

}
