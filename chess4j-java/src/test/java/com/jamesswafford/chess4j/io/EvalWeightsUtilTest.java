package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalWeights;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.*;

public class EvalWeightsUtilTest {

    @Test
    public void toPropertiesAndToVector() {
        EvalWeights weights = new EvalWeights();
        // change the fields away from the defaults
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i<weights.vals.length; i++) {
            weights.vals[i] = r.nextInt();
        }

        Properties props = EvalWeightsUtil.toProperties(weights);

        // load a new vector and ensure it is equivalent
        EvalWeights weights2 = EvalWeightsUtil.toWeights(props);
        assertArrayEquals(weights.vals, weights2.vals);
        assertEquals(weights, weights2);
    }

    @Test
    public void toVector() throws IOException {
        File propsFile = new File(getClass().getResource("/eval.properties").getFile());
        try (FileInputStream fis = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            props.load(fis);
            EvalWeights weights = EvalWeightsUtil.toWeights(props);
            assertEquals(6, weights.vals[EvalWeights.MAJOR_ON_7TH_MG_IND]);
            assertEquals(49, weights.vals[EvalWeights.QUEEN_PST_MG_IND +1]);
        }
    }

}
