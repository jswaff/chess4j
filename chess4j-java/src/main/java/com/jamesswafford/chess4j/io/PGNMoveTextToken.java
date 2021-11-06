package com.jamesswafford.chess4j.io;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PGNMoveTextToken {

    private final PGNMoveTextTokenType tokenType;
    private final String value;

}
