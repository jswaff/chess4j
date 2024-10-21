package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.eval.EvalWeights;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvalWeightsUtil {

    public static Properties toProperties(EvalWeights weights) {
        Properties props = new Properties();
        Set<String> keys = EvalWeights.getKeys();
        keys.forEach(key -> props.put(
                key,
                weights.getVals(key).stream().map(Object::toString).collect(Collectors.joining(","))));
        return props;
    }

    public static EvalWeights toWeights(Properties props) {
        EvalWeights weights = new EvalWeights();
        Set<String> keys = EvalWeights.getKeys();

        keys.forEach(key -> {
            String propVal = props.getProperty(key);
            List<Integer> propVals = Stream.of(propVal.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            weights.setVal(key, propVals);
        });

        return weights;
    }

    public static EvalWeights load(String propertiesFileName) {
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(fis);
            return toWeights(properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void store(EvalWeights weights, String propertiesFileName, String comments) {
        Properties props = toProperties(weights);
        try {
            props.store(new FileOutputStream(propertiesFileName), comments);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
