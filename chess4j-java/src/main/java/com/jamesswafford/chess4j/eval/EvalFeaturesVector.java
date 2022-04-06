package com.jamesswafford.chess4j.eval;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EvalFeaturesVector {

    public int[] features = new int[EvalWeightsVector.NUM_WEIGHTS];

}
