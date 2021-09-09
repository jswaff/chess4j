package com.jamesswafford.chess4j.eval;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.eval.EvalDraw.*;

public class EvalDrawTest {

    @Test
    public void initialPos() {
        testWithSymmetry(Board.INITIAL_POS, false);
    }

    @Test
    public void kk() {
        testWithSymmetry("k7/8/8/8/8/8/8/K7 w - -", true);
    }

    @Test
    public void knk() {
        testWithSymmetry("kn6/8/8/8/8/8/8/K7 w - -", true);
    }

    @Test
    public void kbk() {
        testWithSymmetry("kb6/8/8/8/8/8/8/K7 w - -", true);
    }

    @Test
    public void kpk() {
        testWithSymmetry("k7/p7/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kqk() {
        testWithSymmetry("k7/q7/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void krk() {
        testWithSymmetry("k7/r7/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kbbk() {
        testWithSymmetry("kbb5/8/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kbnk() {
        testWithSymmetry("kbn5/8/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void knnk() {
        testWithSymmetry("knn5/8/8/8/8/8/8/K7 w - -", true);
    }

    @Test
    public void kbkb() {
        testWithSymmetry("kb6/8/8/8/8/8/8/KB6 w - -", true);
    }

    @Test
    public void kbkn() {
        testWithSymmetry("kb6/8/8/8/8/8/8/KN6 w - -", true);
    }

    @Test
    public void knkn() {
        testWithSymmetry("kn6/8/8/8/8/8/8/KN6 w - -", true);
    }

    @Test
    // this generally is a draw but there are many exceptions that are hard to recognize.
    public void kbkp() {
        testWithSymmetry("kb6/8/8/8/8/8/P7/K7 w - -", false);
    }

    @Test
    // this generally is a draw but there are many exceptions that are hard to recognize.
    public void knkp() {
        testWithSymmetry("kn6/8/8/8/8/8/P7/K7 w - -", false);
    }

    @Test
    public void kbpk() {
        testWithSymmetry("kb6/p7/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void knpk() {
        testWithSymmetry("kn6/p7/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kppk() {
        testWithSymmetry("k7/pp6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kpkp() {
        testWithSymmetry("k7/p7/8/8/8/8/P7/K7 w - -", false);
    }

    @Test
    public void kqkb() {
        testWithSymmetry("k7/q7/8/8/8/8/B7/K7 w - -", false);
    }

    @Test
    public void kqkn() {
        testWithSymmetry("k7/q7/8/8/8/8/N7/K7 w - -", false);
    }

    @Test
    public void kqkp() {
        testWithSymmetry("k7/q7/8/8/8/8/P7/K7 w - -", false);
    }

    @Test
    public void kqkq() {
        testWithSymmetry("k7/q7/8/8/8/8/Q7/K7 w - -", false);
    }

    @Test
    public void kqkr() {
        testWithSymmetry("k7/q7/8/8/8/8/R7/K7 w - -", false);
    }

    @Test
    public void krkp() {
        testWithSymmetry("k7/r7/8/8/8/8/P7/K7 w - -", false);
    }

    @Test
    public void krkr() {
        testWithSymmetry("k7/r7/8/8/8/8/R7/K7 w - -", false);
    }

    @Test
    public void kqbk() {
        testWithSymmetry("k7/qb6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kqnk() {
        testWithSymmetry("k7/qn6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kqpk() {
        testWithSymmetry("k7/qp6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kqqk() {
        testWithSymmetry("k7/qq6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void kqrk() {
        testWithSymmetry("k7/qr6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void krbk() {
        testWithSymmetry("k7/rb6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void krnk() {
        testWithSymmetry("k7/rn6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void krpk() {
        testWithSymmetry("k7/rp6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    public void krrk() {
        testWithSymmetry("k7/rr6/8/8/8/8/8/K7 w - -", false);
    }

    @Test
    // mostly drawn but still some winning chances
    public void krkb() {
        testWithSymmetry("k7/r7/8/8/8/8/B7/K7 w - -", false);
    }

    @Test
    // mostly drawn but still some winning chances
    public void krkn() {
        testWithSymmetry("k7/r7/8/8/8/8/N7/K7 w - -", false);
    }

    private void testWithSymmetry(String fen, boolean draw) {
        Board board = new Board(fen);
        assertEquals(draw, evalDraw(board));
        board.flipVertical();
        assertEquals(draw, evalDraw(board));
    }
}
