package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalWeightsVector;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.*;

public class EvalWeightsVectorUtilTest {

    @Test
    public void toPropertiesAndToVector() {
        EvalWeightsVector weights = new EvalWeightsVector();
        // change the fields away from the defaults
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i<weights.weights.length; i++) {
            weights.weights[i] = r.nextInt();
        }

        Properties props = EvalWeightsVectorUtil.toProperties(weights);

        // load a new vector and ensure it is equivalent
        EvalWeightsVector weights2 = EvalWeightsVectorUtil.toVector(props);
        assertArrayEquals(weights.weights, weights2.weights);
        assertEquals(weights, weights2);
    }

    @Test
    public void toVector() throws IOException {
        File propsFile = new File(getClass().getResource("/eval.properties").getFile());
        try (FileInputStream fis = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            props.load(fis);
            EvalWeightsVector weights = EvalWeightsVectorUtil.toVector(props);
            assertEquals(6, weights.weights[EvalWeightsVector.MAJOR_ON_7TH_IND]);
            assertEquals(49, weights.weights[EvalWeightsVector.QUEEN_PST_IND+1]);
        }
    }

}
