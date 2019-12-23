package com.jamesswafford.chess4j.board;

import org.junit.Test;

import com.jamesswafford.chess4j.io.FenParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.ZugzwangDetector.*;

public class ZugzwangDetectorTest {

    @Test
    public void initialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertFalse(isZugzwang(b));
    }

    @Test
    public void justKings() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/8/8/8/8/7K w - - ");

        assertTrue(isZugzwang(b));
    }

    @Test
    public void singleWhiteBishop() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/B7/8/8/8/7K b - - ");

        assertTrue(isZugzwang(b));
    }

    @Test
    public void whitePawnBlackKnight() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/n7/P7/8/8/7K b - - ");

        assertTrue(isZugzwang(b));
    }

    @Test
    public void whiteRookBlackKnight() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/8/n7/R7/8/8/7K b - - ");

        assertFalse(isZugzwang(b));
    }

}
