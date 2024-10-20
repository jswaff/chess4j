package dev.jamesswafford.chess4j.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class ZugzwangDetectorTest {

    @Test
    public void initialPos() {
        Board board = new Board();

        assertFalse(ZugzwangDetector.isZugzwang(board));
    }

    @Test
    public void justKings() {
        Board board = new Board("7k/8/8/8/8/8/8/7K w - - ");

        assertTrue(ZugzwangDetector.isZugzwang(board));
    }

    @Test
    public void singleWhiteBishop() {
        Board board = new Board("7k/8/8/B7/8/8/8/7K b - - ");

        assertTrue(ZugzwangDetector.isZugzwang(board));
    }

    @Test
    public void whitePawnBlackKnight() {
        Board board = new Board("7k/8/8/n7/P7/8/8/7K b - - ");

        assertTrue(ZugzwangDetector.isZugzwang(board));
    }

    @Test
    public void whiteRookBlackKnight() {
        Board board = new Board("7k/8/8/n7/R7/8/8/7K b - - ");

        assertFalse(ZugzwangDetector.isZugzwang(board));
    }

}
