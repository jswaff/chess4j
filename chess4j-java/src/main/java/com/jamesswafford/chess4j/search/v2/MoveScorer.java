package com.jamesswafford.chess4j.search.v2;

import com.jamesswafford.chess4j.board.Move;

public interface MoveScorer  {

    int calculateStaticScore(Move mv);

}
