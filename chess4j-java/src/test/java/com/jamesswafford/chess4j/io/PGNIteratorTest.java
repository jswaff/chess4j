package com.jamesswafford.chess4j.io;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class PGNIteratorTest {

    @Test
    public void test1() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/sample.pgn").getFile())
        );
        PGNIterator it = new PGNIterator(fis);

        PGNGame game1 = it.next();
        Assert.assertNotNull(game1);
        Assert.assertEquals(PGNResult.WHITE_WINS, game1.getResult());
        Assert.assertEquals(61, game1.getMoves().size());
        Assert.assertEquals(15, game1.getTags().size());
        Assert.assertEquals(new PGNTag("TimeControl","300+3"), game1.getTags().get(14));

        PGNGame game2 = it.next();
        Assert.assertNotNull(game2);
        Assert.assertEquals(PGNResult.BLACK_WINS, game2.getResult());
        Assert.assertEquals(104, game2.getMoves().size());
        Assert.assertEquals(15, game2.getTags().size());
        Assert.assertEquals(new PGNTag("Opening","KP: Nimzovich defense"),game2.getTags().get(10));

        PGNGame game3 = it.next();
        Assert.assertNotNull(game3);

        PGNGame game4 = it.next();
        Assert.assertNull(game4);


        fis.close();
    }

    @Test
    public void test2() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/small.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        Assert.assertEquals(10,c);

        fis.close();
    }

    @Ignore
    @Test
    public void test3() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/normbk03.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        Assert.assertEquals(50110,c);

        fis.close();
    }

    @Test
    public void testKasporov() throws Exception {
        FileInputStream fis = new FileInputStream(
                new File(PGNIteratorTest.class.getResource("/pgn/Kasparov.pgn").getFile()));
        PGNIterator it = new PGNIterator(fis);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        Assert.assertEquals(544,c);

        fis.close();
    }

}
