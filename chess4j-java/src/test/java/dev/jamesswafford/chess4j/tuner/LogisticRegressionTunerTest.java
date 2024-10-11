package dev.jamesswafford.chess4j.tuner;

import dev.jamesswafford.chess4j.eval.EvalWeights;
import dev.jamesswafford.chess4j.io.EPDParser;
import dev.jamesswafford.chess4j.io.FENRecord;
import dev.jamesswafford.chess4j.io.PGNResult;
import io.vavr.Tuple2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogisticRegressionTunerTest {

    LogisticRegressionTuner tuner = new LogisticRegressionTuner();

    private final static String testEpd = "/samplefen1000.epd";

    private static List<FENRecord> fenRecords;

    @BeforeClass
    public static void setUp() throws Exception {
        File epdFile = new File(LogisticRegressionTunerTest.class.getResource(testEpd).getFile());
        fenRecords = EPDParser.load(epdFile, true);
    }

    @Test
    public void kqk() {

        EvalWeights weights = new EvalWeights();
        Arrays.fill(weights.vals, 0);
        weights.vals[EvalWeights.QUEEN_VAL_IND] = 100;

        FENRecord kqk = FENRecord.builder().fen("3k4/3Q4/3K4/8/8/8/8/8 w - -").result(PGNResult.WHITE_WINS).build();
        Tuple2<EvalWeights, Double> tunedWeights = tuner.optimize(
                weights,
                Arrays.asList(kqk, kqk),
                100.0,
                1);

        assertTrue(tunedWeights._1.vals[EvalWeights.QUEEN_VAL_IND] > 100);
    }

    @Test
    public void optimize() {

        // get a list of game records
        assertEquals(1000, fenRecords.size());

        EvalWeights weights = new EvalWeights();

        tuner.optimize(weights, fenRecords, 100.0, 3);
    }

}
