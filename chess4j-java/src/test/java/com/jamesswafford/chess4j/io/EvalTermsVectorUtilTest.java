package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.*;

public class EvalTermsVectorUtilTest {

    @Test
    public void toPropertiesAndToVector() {
        EvalTermsVector etv = new EvalTermsVector();
        // change the fields away from the defaults
        Random r = new Random(System.currentTimeMillis());
        for (int i=0;i<etv.terms.length;i++) {
            etv.terms[i] = r.nextInt();
        }

        Properties props = EvalTermsVectorUtil.toProperties(etv);

        // load a new vector and ensure it is equivalent
        EvalTermsVector etv2 = EvalTermsVectorUtil.toVector(props);
        assertArrayEquals(etv.terms, etv2.terms);
        assertEquals(etv, etv2);
    }

    @Test
    public void propertiesFromFile() throws IOException {
        File propsFile = new File(getClass().getResource("/eval.properties").getFile());
        try (FileInputStream fis = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            props.load(fis);
            EvalTermsVector etv = EvalTermsVectorUtil.toVector(props);
            assertEquals(6, etv.terms[EvalTermsVector.MAJOR_ON_7TH_IND]);
            assertEquals(49, etv.terms[EvalTermsVector.QUEEN_PST_IND+1]);
        }
    }

}
