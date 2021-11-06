package com.jamesswafford.chess4j.io;

import org.junit.Test;

import java.util.List;

import static com.jamesswafford.chess4j.io.PGNMoveTextTokenType.*;

import static org.junit.Assert.*;

public class PGNMoveTextTokenizerTest {

    @Test
    public void test1() {
        String moveText = "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 {This opening is called the Ruy Lopez.}";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(10, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "e4"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(MOVE, "e5"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "2."), tokens.get(3));
        assertEquals(new PGNMoveTextToken(MOVE, "Nf3"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE, "Nc6"), tokens.get(5));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "3."), tokens.get(6));
        assertEquals(new PGNMoveTextToken(MOVE, "Bb5"), tokens.get(7));
        assertEquals(new PGNMoveTextToken(MOVE, "a6"), tokens.get(8));
        assertEquals(new PGNMoveTextToken(NAG, "{This opening is called the Ruy Lopez.}"), tokens.get(9));
    }

    @Test
    public void test2() {
        String moveText = "10. Bxf6 {book} gxf6 {book} 11. Nh4 {+0.10/7 0.066s} d4 {0.00/8 0.091s}";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(10, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "10."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "Bxf6"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(NAG, "{book}"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE, "gxf6"), tokens.get(3));
        assertEquals(new PGNMoveTextToken(NAG, "{book}"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "11."), tokens.get(5));
        assertEquals(new PGNMoveTextToken(MOVE, "Nh4"), tokens.get(6));
        assertEquals(new PGNMoveTextToken(NAG, "{+0.10/7 0.066s}"), tokens.get(7));
        assertEquals(new PGNMoveTextToken(MOVE, "d4"), tokens.get(8));
        assertEquals(new PGNMoveTextToken(NAG, "{0.00/8 0.091s}"), tokens.get(9));
    }

    @Test
    public void test3() {
        String moveText = "Ke8 {+2.32/7 0.11s} 48. Qd5 {-1.27/7 0.072s} Qf1+ {+1.35/7 0.059s}";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(7, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE, "Ke8"), tokens.get(0));
        assertEquals(new PGNMoveTextToken(NAG, "{+2.32/7 0.11s}"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "48."), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE, "Qd5"), tokens.get(3));
        assertEquals(new PGNMoveTextToken(NAG, "{-1.27/7 0.072s}"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE, "Qf1+"), tokens.get(5));
        assertEquals(new PGNMoveTextToken(NAG, "{+1.35/7 0.059s}"), tokens.get(6));
    }

    @Test
    public void test4() {
        String moveText = "97. Kd3 {-19.59/12 0.12s, Black wins by adjudication} 0-1";

        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(4, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "97."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "Kd3"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(NAG, "{-19.59/12 0.12s, Black wins by adjudication}"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(GAME_RESULT, "0-1"), tokens.get(3));
    }

    @Test
    public void test5() {
        String moveText = "16.Bg5 Qc7 17.Qd2 Reb8 18.Nf5 *";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(9, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "16."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "Bg5"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(MOVE, "Qc7"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "17."), tokens.get(3));
        assertEquals(new PGNMoveTextToken(MOVE, "Qd2"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE, "Reb8"), tokens.get(5));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "18."), tokens.get(6));
        assertEquals(new PGNMoveTextToken(MOVE, "Nf5"), tokens.get(7));
        assertEquals(new PGNMoveTextToken(GAME_RESULT, "*"), tokens.get(8));
    }

    @Test
    public void test6() {
        String moveText = "1.e4 { comment } 1...e5 2.Nf3";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(7, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "e4"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(NAG, "{ comment }"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1..."), tokens.get(3));
        assertEquals(new PGNMoveTextToken(MOVE, "e5"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "2."), tokens.get(5));
        assertEquals(new PGNMoveTextToken(MOVE, "Nf3"), tokens.get(6));
    }

    @Test
    public void test7() {
        String moveText = "1.e4 { comment { nested comment }} 1...e5 2.Nf3";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(7, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "e4"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(NAG, "{ comment { nested comment }}"), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1..."), tokens.get(3));
        assertEquals(new PGNMoveTextToken(MOVE, "e5"), tokens.get(4));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "2."), tokens.get(5));
        assertEquals(new PGNMoveTextToken(MOVE, "Nf3"), tokens.get(6));
    }

    @Test
    public void test8() {
        String moveText = "1. ... e5 2.f3";
        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        assertEquals(4, tokens.size());
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "1."), tokens.get(0));
        assertEquals(new PGNMoveTextToken(MOVE, "e5"), tokens.get(1));
        assertEquals(new PGNMoveTextToken(MOVE_NUMBER, "2."), tokens.get(2));
        assertEquals(new PGNMoveTextToken(MOVE, "f3"), tokens.get(3));
    }

}
