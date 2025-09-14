package dev.jamesswafford.chess4j.io;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class PGNIteratorTest {

    @Test
    public void sample() throws Exception {
        File pgnFile = new File(getClass().getResource("/pgn/sample.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

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
    }

    @Test
    public void sampleWithAnnotations() throws Exception {
        File pgnFile = new File(getClass().getResource("/pgn/sample_with_annotations.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        PGNGame game1 = it.next();
        assertNotNull(game1);
        assertEquals(PGNResult.BLACK_WINS, game1.getResult());
        assertEquals(113, game1.getMoves().size());
        assertEquals(15, game1.getTags().size());
        assertEquals(new PGNTag("TimeControl","1+0.1"), game1.getTags().get(14));

        PGNGame game2 = it.next();
        assertNotNull(game2);
        assertEquals(PGNResult.BLACK_WINS, game2.getResult());
        assertEquals(193, game2.getMoves().size());
        assertEquals(15, game2.getTags().size());
        assertEquals(new PGNTag("Opening","Bird's Opening"),game2.getTags().get(11));

        PGNGame game3 = it.next();
        assertNull(game3);
    }

    @Test
    public void small() throws Exception {
        File pgnFile = new File(getClass().getResource("/pgn/small.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(10, c);
    }

    @Ignore
    @Test
    public void normbk03() throws Exception {
        File pgnFile = new File(getClass().getResource("/pgn/normbk03.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(50110, c);
    }

    @Ignore
    @Test
    public void kasporov() throws Exception {
        File pgnFile = new File(getClass().getResource("/pgn/Kasparov.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        int c=0;
        while (it.next() != null) {
            c++;
        }
        assertEquals(544, c);
    }

}
