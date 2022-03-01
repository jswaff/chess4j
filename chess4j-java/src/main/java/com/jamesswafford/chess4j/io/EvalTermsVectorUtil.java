package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvalTermsVectorUtil {

    public static Properties toProperties(EvalTermsVector etv) {
        Properties props = new Properties();
        Set<String> keys = EvalTermsVector.getKeys();
        keys.forEach(key -> props.put(
                key,
                etv.getVals(key).stream().map(Object::toString).collect(Collectors.joining(","))));
        return props;
    }

    public static EvalTermsVector toVector(Properties props) {
        EvalTermsVector etv = new EvalTermsVector();
        Set<String> keys = EvalTermsVector.getKeys();

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

    public static EvalTermsVector load(String propertiesFileName) {
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            Properties properties = new Properties();
            properties.load(fis);
            return toVector(properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void store(EvalTermsVector etv, String propertiesFileName) {
        Properties props = toProperties(etv);
        try {
            props.store(new FileOutputStream(propertiesFileName), null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
