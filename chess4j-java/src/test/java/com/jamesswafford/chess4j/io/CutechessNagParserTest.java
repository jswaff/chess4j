package com.jamesswafford.chess4j.io;

import org.junit.Test;

import static org.junit.Assert.*;

public class CutechessNagParserTest {

    @Test
    public void test1() {
        CutechessNagParser parser = new CutechessNagParser("{+0.30/7 0.065s}");
        assertTrue(parser.isValid());
        assertTrue(parser.score() > 0.29999);
        assertTrue(parser.score() < 0.30001);
        assertEquals(7, parser.depth());
    }

    @Test
    public void test2() {
        CutechessNagParser parser = new CutechessNagParser("{-0.96/14 0.12s}");
        assertTrue(parser.isValid());
        assertTrue(parser.score() > -0.96001);
        assertTrue(parser.score() < -0.95999);
        assertEquals(14, parser.depth());
    }

    @Test
    public void test3() {
        CutechessNagParser parser = new CutechessNagParser("{-19.59/12 0.12s, Black wins by adjudication}");
        assertTrue(parser.isValid());
        assertTrue(parser.score() > -19.59001);
        assertTrue(parser.score() < -19.58999);
        assertEquals(12, parser.depth());
    }

}
