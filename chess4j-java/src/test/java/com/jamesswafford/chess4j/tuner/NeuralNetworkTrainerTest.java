package com.jamesswafford.chess4j.tuner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NeuralNetworkTrainerTest {

    NeuralNetworkTrainer trainer = new NeuralNetworkTrainer();

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
    public void trainTest() {
        populateTunerDatasource(testEpd);
        assertEquals(1000, tunerDatasource.getTotalPositionsCount());
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords();
        trainer.train(gameRecords, 1.0, 100);
    }

    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile, true);
    }

}
