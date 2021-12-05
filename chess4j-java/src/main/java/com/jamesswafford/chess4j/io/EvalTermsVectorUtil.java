package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.eval.EvalTermsVector;

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
}
