package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.io.FENRecord;
import com.jamesswafford.chess4j.io.PGNResult;
import org.junit.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FenToTunerTest {

    private final static String testDB = "tunertest.db";
    private final static String zuriEpd = "/samplefen.epd";
    private final static String etherealEpd = "/sample_ethereal_fen.epd";

    static SQLiteTunerDatasource tunerDatasource;
    static Connection conn;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + testDB);

        tunerDatasource = new SQLiteTunerDatasource(conn);
        tunerDatasource.initializeDatasource();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
        new File(testDB).delete();
    }

    @Test
    public void addZuriEpdFile() {
        populateTunerDatasource(zuriEpd, true);
        assertEquals(100, tunerDatasource.getTotalPositionsCount());

        // verify a few samples
        /*
        r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - - c9 "1-0";
        8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - - c9 "1/2-1/2";
        2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - - c9 "1-0";
        4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - - c9 "0-1";
         */

        assertEquals(1, tunerDatasource.getFenCount("r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - -"));
        assertEquals(1, tunerDatasource.getFenCount("8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - -"));
        assertEquals(1, tunerDatasource.getFenCount("2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - -"));
        assertEquals(1, tunerDatasource.getFenCount("4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - -"));

        List<FENRecord> fenRecords = tunerDatasource.getGameRecords(false);
        assertEquals(100, fenRecords.size());

        FENRecord g1 = fenRecords.stream()
                .filter(gameRecord -> "r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g1.getResult());

        FENRecord g2 = fenRecords.stream()
                .filter(gameRecord -> "8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.DRAW, g2.getResult());

        FENRecord g3 = fenRecords.stream()
                .filter(gameRecord -> "2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g3.getResult());

        FENRecord g4 = fenRecords.stream()
                .filter(gameRecord -> "4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.BLACK_WINS, g4.getResult());
    }

    @Test
    public void addEtherealEdpFile() {
        populateTunerDatasource(etherealEpd, false);
        assertEquals(100, tunerDatasource.getTotalPositionsCount());

        /*
        8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - - 6 70 [1.0] 308
        2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - - 4 34 [0.5] -217
        3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - - 0 23 [0.0] -36
         */

        assertEquals(1, tunerDatasource.getFenCount("8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - -"));
        assertEquals(1, tunerDatasource.getFenCount("2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - -"));
        assertEquals(1, tunerDatasource.getFenCount("3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - -"));

        List<FENRecord> fenRecords = tunerDatasource.getGameRecords(false);
        assertEquals(100, fenRecords.size());

        FENRecord g1 = fenRecords.stream()
                .filter(gameRecord -> "8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g1.getResult());

        FENRecord g2 = fenRecords.stream()
                .filter(gameRecord -> "2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.DRAW, g2.getResult());

        FENRecord g3 = fenRecords.stream()
                .filter(gameRecord -> "3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.BLACK_WINS, g3.getResult());

    }
    
    private void populateTunerDatasource(String epd, boolean zuriFormat) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile, zuriFormat);
    }

}
