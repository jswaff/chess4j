package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.eval.EvalTermsVector;
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
    public void errorShouldDecrease() {

        // get a list of game records
        populateTunerDatasource(testEpd);
        assertEquals(1000, tunerDatasource.getTotalPositionsCount());
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords();

        // get a sample theta vector
        EvalTermsVector theta = new EvalTermsVector();
        Arrays.fill(theta.terms, 0);

        // verify error continues to decrease
        double lastError = 999999;
        for (int i=1;i<=5;i++) {
            Tuple2<EvalTermsVector, Double> retVal = tuner.optimize(theta, gameRecords, i);
            assertTrue(retVal._2 < lastError);
            lastError = retVal._2;
            theta = new EvalTermsVector(retVal._1);
        }
    }

    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile);
    }

}
