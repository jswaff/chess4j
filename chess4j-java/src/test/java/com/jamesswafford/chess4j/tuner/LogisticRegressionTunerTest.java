package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LogisticRegressionTunerTest {

    LogisticRegressionTuner tuner;

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
    public void errorShouldDecrease() {

        // get a list of game records
        populateTunerDatasource(testEpd);
        assertEquals(100, tunerDatasource.getTotalPositionsCount());

        // get a sample theta vector
        EvalTermsVector initialTheta = new EvalTermsVector();
        Arrays.fill(initialTheta.terms, 0);

        // call tuner with 1..10 iterations and verify error decreases.

        tuner = new LogisticRegressionTuner();
        tuner.optimize(initialTheta, tunerDatasource.getGameRecords(), 3);
    }

    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile);
    }

}
