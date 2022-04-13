package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.eval.EvalWeights;
import io.vavr.Tuple2;
import org.ejml.simple.SimpleMatrix;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GradientTest {

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
    public void gradientTest() {
        populateTunerDatasource(testEpd);

        assertEquals(1000, tunerDatasource.getTotalPositionsCount());
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords();

        EvalWeights weights = new EvalWeights();
        int n = weights.vals.length;
        SimpleMatrix theta = MatrixUtils.weightsToMatrix(weights);

        Tuple2<SimpleMatrix, SimpleMatrix> xy = MatrixUtils.loadXY(gameRecords, 1000, n);
        SimpleMatrix x = xy._1;
        SimpleMatrix y = xy._2;

        SimpleMatrix g = Gradient.gradient(x, y, theta);

        // This is too crude
        /*int epsilon = 2;
        for (int i=0;i<n;i++) {
            EvalWeights wPlus = new EvalWeights(weights);
            wPlus.vals[i] += epsilon;

            EvalWeights wMinus = new EvalWeights(weights);
            wMinus.vals[i] -= epsilon;

            double gradApprox = (CostFunction.cost(gameRecords, wPlus) - CostFunction.cost(gameRecords, wMinus)) /
                    (2 * epsilon);

            System.out.println("n=" + i + ", g: " + g.get(i, 0) + ", gApprox: " + gradApprox);
        }*/


    }


    private void populateTunerDatasource(String epd) {
        File epdFile = new File(SQLiteTunerDatasourceTest.class.getResource(epd).getFile());
        FenToTuner fenToTuner = new FenToTuner(tunerDatasource);
        fenToTuner.addFile(epdFile);
    }

}
