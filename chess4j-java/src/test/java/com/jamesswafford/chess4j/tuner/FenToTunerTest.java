package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.io.PGNResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FenToTunerTest {

    private final static String testDB = "tunertest.db";
    private final static String testEpd = "/samplefen.epd";

    static SQLiteTunerDatasource tunerDatasource;
    static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + testDB);

        tunerDatasource = new SQLiteTunerDatasource(conn);
        tunerDatasource.initializeDatasource();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        conn.close();
        new File(testDB).delete();
    }

    @Test
    public void addEpdFile() {
        populateTunerDatasource(testEpd);
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

        List<GameRecord> gameRecords = tunerDatasource.getGameRecords();
        assertEquals(100, gameRecords.size());

        GameRecord g1 = gameRecords.stream()
                .filter(gameRecord -> "r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g1.getResult());

        GameRecord g2 = gameRecords.stream()
                .filter(gameRecord -> "8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.DRAW, g2.getResult());

        GameRecord g3 = gameRecords.stream()
                .filter(gameRecord -> "2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g3.getResult());

        GameRecord g4 = gameRecords.stream()
                .filter(gameRecord -> "4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.BLACK_WINS, g4.getResult());
    }

    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile);
    }

}
