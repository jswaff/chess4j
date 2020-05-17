package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;

import java.util.List;

public interface MoveGenerator {

    List<Move> generateLegalMoves(Board board);

    List<Move> generatePseudoLegalMoves(Board board);

    List<Move> generatePseudoLegalCaptures(Board board);

    List<Move> generatePseudoLegalNonCaptures(Board board);

}
