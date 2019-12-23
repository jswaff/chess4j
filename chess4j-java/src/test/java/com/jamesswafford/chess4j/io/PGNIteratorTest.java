package com.jamesswafford.chess4j.io;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class PGNIteratorTest {

    @Test
    public void sample() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/sample.pgn").getFile())
        );
        PGNIterator it = new PGNIterator(fis);

        PGNGame game1 = it.next();
        assertNotNull(game1);
        assertEquals(PGNResult.WHITE_WINS, game1.getResult());
        assertEquals(61, game1.getMoves().size());
        assertEquals(15, game1.getTags().size());
        assertEquals(new PGNTag("TimeControl","300+3"), game1.getTags().get(14));

        PGNGame game2 = it.next();
        assertNotNull(game2);
        assertEquals(PGNResult.BLACK_WINS, game2.getResult());
        assertEquals(104, game2.getMoves().size());
        assertEquals(15, game2.getTags().size());
        assertEquals(new PGNTag("Opening","KP: Nimzovich defense"),game2.getTags().get(10));

        PGNGame game3 = it.next();
        assertNotNull(game3);

        PGNGame game4 = it.next();
        assertNull(game4);

        fis.close();
    }

    @Test
    public void small() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/small.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(10, c);

        fis.close();
    }

    @Ignore
    @Test
    public void normbk03() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/normbk03.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(50110,c);

        fis.close();
    }

    @Ignore
    @Test
    public void kasporov() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/Kasparov.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(544,c);

        fis.close();
    }

}
