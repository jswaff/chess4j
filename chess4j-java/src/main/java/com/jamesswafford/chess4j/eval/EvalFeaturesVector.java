package com.jamesswafford.chess4j.eval;

import lombok.EqualsAndHashCode;

import java.util.Arrays;

@EqualsAndHashCode
public class EvalFeaturesVector {

    public int[] features = new int[EvalWeightsVector.NUM_WEIGHTS];

    public void reset() {
        Arrays.fill(features, 0);
    }
}
