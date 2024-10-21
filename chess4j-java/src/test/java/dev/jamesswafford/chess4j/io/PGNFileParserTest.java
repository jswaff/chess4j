package dev.jamesswafford.chess4j.io;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class PGNFileParserTest {

    @Test
    public void load() throws IOException {
        // sample.pgn has three games
        File pgnFile = new File(getClass().getResource("/pgn/sample.pgn").getFile());

        // game 1 : 61 half moves ==> 62 FEN records
        // game 2 : 104 half moves ==> 105 FEN records
        // game 3 : 91 half moves ==> 92 FEN records
        List<FENRecord> fenRecords = PGNFileParser.load(pgnFile, false);
        assertEquals(259, fenRecords.size());

        // test final position of each game
        assertEquals("3r2k1/1p3p2/p7/P5Q1/6P1/8/1Pq5/R3K3 b Q - 1 31", fenRecords.get(61).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(61).getResult());

        assertEquals("7r/6b1/7k/p6p/PpBq1p2/8/1P1Q2P1/6K1 w - - 8 53", fenRecords.get(166).getFen());
        assertEquals(PGNResult.BLACK_WINS, fenRecords.get(166).getResult());

        assertEquals("8/6R1/p4kRp/P4p1P/2rpbP2/B7/3K4/8 b - - 1 46", fenRecords.get(258).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(258).getResult());

        // if we dedupe the records count should be less
        List<FENRecord> fenRecords2 = PGNFileParser.load(pgnFile, true);
        assertTrue(fenRecords2.size() < 259);
    }

    @Test
    public void toFEN() throws FileNotFoundException {
        File pgnFile = new File(getClass().getResource("/pgn/tiny.pgn").getFile());
        PGNIterator it = new PGNIterator(pgnFile);

        PGNGame game1 = it.next();
        assertNotNull(game1);
        assertEquals(85, game1.getMoves().size());
        assertEquals(PGNResult.WHITE_WINS, game1.getResult());

        List<FENRecord> fenRecords = PGNFileParser.toFEN(game1);
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
