package dev.jamesswafford.chess4j.eval;

import dev.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvalTaperTest {

    @Test
    public void testTaper() {

        Board board = new Board();

        int mgScore = 100;
        int egScore = 200;

        assertEquals(24, EvalTaper.phase(board));
        assertEquals(100, EvalTaper.taper(board, mgScore, egScore));

        board.setPos("k7/8/8/8/8/8/8/K7 w - -");
        assertEquals(0, EvalTaper.phase(board));
        assertEquals(200, EvalTaper.taper(board, mgScore, egScore));

        board.setPos("kq6/8/8/8/8/8/8/KR6 b - -");
        assertEquals(6, EvalTaper.phase(board));
        assertEquals(175, EvalTaper.taper(board, mgScore, egScore));
    }

}
