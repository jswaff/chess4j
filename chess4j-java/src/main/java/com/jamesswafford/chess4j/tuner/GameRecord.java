package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.io.PGNResult;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GameRecord {

    private String fen;
    private PGNResult result;

    private Integer eval;

}
