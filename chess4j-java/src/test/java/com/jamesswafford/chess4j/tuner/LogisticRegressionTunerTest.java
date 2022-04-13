package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.io.PGNResult;
import io.vavr.Tuple2;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogisticRegressionTunerTest {

    LogisticRegressionTuner tuner = new LogisticRegressionTuner();

    private final static String testDB = "tunertest.db";
    private final static String testEpd = "/samplefen1000.epd";

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
    public void kqk() {

        EvalWeights weights = new EvalWeights();
        Arrays.fill(weights.vals, 0);
        weights.vals[EvalWeights.QUEEN_VAL_IND] = 100;

        Tuple2<EvalWeights, Double> tunedWeights = tuner.optimize(
                weights,
                List.of(new GameRecord("3k4/3Q4/3K4/8/8/8/8/8 w - -", PGNResult.WHITE_WINS)),
                100.0,
                10);

        assertTrue(tunedWeights._1.vals[EvalWeights.QUEEN_VAL_IND] > 100);
    }

    @Test
    public void optimize() {

        // get a list of game records
        populateTunerDatasource(testEpd);
        assertEquals(1000, tunerDatasource.getTotalPositionsCount());
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords();

        EvalWeights weights = new EvalWeights();

        tuner.optimize(weights, gameRecords, 100.0, 3);
    }

    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile);
    }

}
