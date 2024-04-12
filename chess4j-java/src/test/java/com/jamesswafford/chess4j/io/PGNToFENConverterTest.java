package com.jamesswafford.chess4j.io;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PGNToFENConverterTest {

    @Test
    public void convertTest() throws FileNotFoundException {
        File pgnFile = new File(getClass().getResource("/pgn/tiny.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        PGNGame game1 = it.next();
        assertNotNull(game1);
        assertEquals(85, game1.getMoves().size());
        assertEquals(PGNResult.WHITE_WINS, game1.getResult());

        List<FENRecord> fenRecords = PGNToFENConverter.convert(game1);
        assertEquals(86, fenRecords.size());

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fenRecords.get(0).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(0).getResult());

        assertEquals("rnbqkbnr/pppppppp/8/8/5P2/8/PPPPP1PP/RNBQKBNR b KQkq f3 0 1", fenRecords.get(1).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(1).getResult());

        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/5P2/8/PPPPP1PP/RNBQKBNR w KQkq e6 0 2", fenRecords.get(2).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(2).getResult());

        assertEquals("8/2p5/4k1p1/1P2b1B1/2K3p1/5bP1/3R3P/8 w - - 1 43", fenRecords.get(84).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(84).getResult());

        assertEquals("8/2p5/4k1p1/1P2b3/2K2Bp1/5bP1/3R3P/8 b - - 2 43", fenRecords.get(85).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(85).getResult());
    }
}
