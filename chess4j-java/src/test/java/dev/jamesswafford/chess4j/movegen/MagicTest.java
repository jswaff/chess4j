package dev.jamesswafford.chess4j.movegen;

import dev.jamesswafford.chess4j.board.Bitboard;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.squares.Square;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MagicTest {

    @Test
    public void rookMoves() {
        Board board = new Board("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        long rookMoves = Magic.getRookMoves(board, Square.B7.value(), board.getBlackPieces());
        Assert.assertEquals(Bitboard.squares[Square.F7.value()], rookMoves);
    }

    @Test
    public void bishopMoves() {
        Board board = new Board("8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        long bishopMoves = Magic.getBishopMoves(board, Square.D5.value(),board.getBlackPieces());

        assertEquals(Bitboard.squares[Square.F7.value()] | Bitboard.squares[Square.B7.value()], bishopMoves);
    }

}
