package com.jamesswafford.chess4j.movegen;

import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.Square;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class MagicTest {

    @Test
    public void rookMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        Square b7 = Square.valueOf(FILE_B, RANK_7);
        long rookMoves = Magic.getRookMoves(b,b7.value(),b.getBlackPieces());

        Square f7 = Square.valueOf(FILE_F, RANK_7);
        assertEquals(Bitboard.squares[f7.value()], rookMoves);
    }

    @Test
    public void bishopMoves() {
        Board b = Board.INSTANCE;
        b.setPos("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        Square d5 = Square.valueOf(FILE_D, RANK_5);
        Square f7 = Square.valueOf(FILE_F, RANK_7);
        Square b7 = Square.valueOf(FILE_B, RANK_7);

        long bishopMoves = Magic.getBishopMoves(b,d5.value(),b.getBlackPieces());

        assertEquals(Bitboard.squares[f7.value()] | Bitboard.squares[b7.value()], bishopMoves);
    }

}
