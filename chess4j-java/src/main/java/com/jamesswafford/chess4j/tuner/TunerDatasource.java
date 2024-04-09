package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.io.*;

import java.util.List;

@Deprecated
public interface TunerDatasource {

    void initializeDatasource();

    void insert(String fen, PGNResult pgnResult);

    void updateEval(String fen, int eval);

    long getTotalPositionsCount();

    long getFenCount(String fen);

    List<FENRecord> getGameRecords(boolean justUnprocessed);

}
