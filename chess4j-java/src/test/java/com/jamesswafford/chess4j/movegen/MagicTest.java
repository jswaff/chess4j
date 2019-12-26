package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.Square.*;

public class MagicTest {

    @Test
    public void rookMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        long rookMoves = Magic.getRookMoves(b, B7.value(), b.getBlackPieces());
        assertEquals(Bitboard.squares[F7.value()], rookMoves);
    }

    @Test
    public void bishopMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        long bishopMoves = Magic.getBishopMoves(b, D5.value(),b.getBlackPieces());

        assertEquals(Bitboard.squares[F7.value()] | Bitboard.squares[B7.value()], bishopMoves);
    }

}
