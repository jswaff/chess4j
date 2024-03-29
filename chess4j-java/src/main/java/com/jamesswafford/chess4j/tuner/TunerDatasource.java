package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.io.*;

import java.util.List;

public interface TunerDatasource {

    void initializeDatasource();

    void insert(String fen, PGNResult pgnResult);

    long getTotalPositionsCount();

    long getFenCount(String fen);

    List<GameRecord> getGameRecords();

}
