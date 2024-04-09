package com.jamesswafford.chess4j.io;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GameRecord {

    private String fen;
    private PGNResult result; // TODO: why PGNResult?

    @Deprecated
    private Integer eval;

}
