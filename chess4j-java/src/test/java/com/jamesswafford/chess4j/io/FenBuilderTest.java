package com.jamesswafford.chess4j.io;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static org.junit.Assert.*;

public class FenBuilderTest {

    @Test
    public void testCreateFen_InitialPos() {

        Board b = Board.INSTANCE;
        b.resetBoard();

        String fen = FenBuilder.createFen(b, true);

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen);
    }

    @Test
    public void testCreateFen_pos1() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        b.setPos(fen);

        assertEquals(fen, FenBuilder.createFen(b, true));
    }

    @Test
    public void testCreateFen_pos2() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -";
        b.setPos(fen);

        assertEquals(fen, FenBuilder.createFen(b, false));
    }

    @Test
    public void testCreateFen_pos3() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        b.setPos(fen);

        assertEquals(fen, FenBuilder.createFen(b, true));
    }

}
