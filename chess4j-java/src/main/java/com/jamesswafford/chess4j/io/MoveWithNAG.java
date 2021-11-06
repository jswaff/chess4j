package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Move;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MoveWithNAG {

    private final Move move;
    private final String nag;

}
