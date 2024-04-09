package com.jamesswafford.chess4j.io;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FENRecord {

    private String fen;
    private PGNResult result;
    private Integer eval;

}
