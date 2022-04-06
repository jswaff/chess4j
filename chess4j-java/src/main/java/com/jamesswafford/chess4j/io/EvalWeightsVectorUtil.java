package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalWeightsVector;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvalWeightsVectorUtil {

    public static Properties toProperties(EvalWeightsVector theta) {
        Properties props = new Properties();
        Set<String> keys = EvalWeightsVector.getKeys();
        keys.forEach(key -> props.put(
                key,
                theta.getVals(key).stream().map(Object::toString).collect(Collectors.joining(","))));
        return props;
    }

    public static EvalWeightsVector toVector(Properties props) {
        EvalWeightsVector etv = new EvalWeightsVector();
        Set<String> keys = EvalWeightsVector.getKeys();

        keys.forEach(key -> {
            String propVal = props.getProperty(key);
            List<Integer> propVals = Stream.of(propVal.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            etv.setVal(key, propVals);
        });

        return etv;
    }

    public static EvalWeightsVector load(String propertiesFileName) {
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(fis);
            return toVector(properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void store(EvalWeightsVector theta, String propertiesFileName, String comments) {
        Properties props = toProperties(theta);
        try {
            props.store(new FileOutputStream(propertiesFileName), comments);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
