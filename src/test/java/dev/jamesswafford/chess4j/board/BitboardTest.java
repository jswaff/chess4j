package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.board.squares.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.Bitboard.*;
import static dev.jamesswafford.chess4j.board.squares.Rank.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;

public class BitboardTest {

    @Test
    public void toBitboard_single() {

        assertEquals(squares[D6.value()], toBitboard(D6));
        assertEquals(squares[F3.value()], toBitboard(F3));
        assertEquals(squares[C8.value()], toBitboard(C8));
    }

    @Test
    public void toBitboard_list() {

        assertEquals(
                squares[D6.value()] | squares[D7.value()] | squares[D8.value()],
                toBitboard(Arrays.asList(D6, D7, D8)));

        assertEquals(0, toBitboard(new ArrayList<>()));

        assertEquals(-1L, toBitboard(Square.allSquares()));

        //System.out.println("all squares:\n" + drawBitboard(toBitboard(Square.allSquares())));
    }

    @Test
    public void lsb() {
        Square.allSquares()
                .forEach(square -> assertEquals(square.value(), Bitboard.lsb(square)));

        assertEquals(A6.value(), Bitboard.lsb(ranks[RANK_6.getValue()]));
    }

    @Test
    public void msb() {
        Square.allSquares()
                .forEach(square -> assertEquals(square.value(), Bitboard.msb(square)));

        assertEquals(H6.value(), Bitboard.msb(ranks[RANK_6.getValue()]));
    }

    @Test
    public void rays() {

        assertEquals(
                squares[D6.value()] | squares[D7.value()] | squares[D8.value()],
                rays[D5.value()][North.getInstance().value()]);

        assertEquals(
                squares[D4.value()] | squares[D3.value()] | squares[D2.value()] | squares[D1.value()],
                rays[D5.value()][South.getInstance().value()]);

        assertEquals(
                squares[C5.value()] | squares[B5.value()] | squares[A5.value()],
                rays[D5.value()][West.getInstance().value()]);

        assertEquals(
                squares[E5.value()] | squares[F5.value()] | squares[G5.value()] | squares[H5.value()],
                rays[D5.value()][East.getInstance().value()]);

        assertEquals(
                squares[E6.value()] | squares[F7.value()] | squares[G8.value()],
                rays[D5.value()][NorthEast.getInstance().value()]);

        assertEquals(
                squares[E4.value()] | squares[F3.value()] | squares[G2.value()] | squares[H1.value()],
                rays[D5.value()][SouthEast.getInstance().value()]);

        assertEquals(
                squares[C4.value()] | squares[B3.value()] | squares[A2.value()],
                rays[D5.value()][SouthWest.getInstance().value()]);

        assertEquals(
                squares[C6.value()] | squares[B7.value()] | squares[A8.value()],
                rays[D5.value()][NorthWest.getInstance().value()]);
    }

    @Test
    public void knightMoves() {
        long moves = knightMoves[E4.value()];
        assertEquals(8, Long.bitCount(moves));

        assertEquals(D6.value(), Bitboard.lsb(moves));
        moves ^= squares[D6.value()];

        assertEquals(F6.value(), Bitboard.lsb(moves));
        moves ^= squares[F6.value()];

        assertEquals(C5.value(), Bitboard.lsb(moves));
        moves ^= squares[C5.value()];

        assertEquals(G5.value(), Bitboard.lsb(moves));
        moves ^= squares[G5.value()];

        assertEquals(C3.value(), Bitboard.lsb(moves));
        moves ^= squares[C3.value()];

        assertEquals(G3.value(), Bitboard.lsb(moves));
        moves ^= squares[G3.value()];

        assertEquals(D2.value(), Bitboard.lsb(moves));
        moves ^= squares[D2.value()];

        assertEquals(F2.value(), Bitboard.lsb(moves));
        moves ^= squares[F2.value()];

        assertEquals(0, moves);

        moves = knightMoves[A8.value()];
        assertEquals(2, Long.bitCount(moves));

        assertEquals(C7.value(), Bitboard.lsb(moves));
        moves ^= squares[C7.value()];

        assertEquals(B6.value(), Bitboard.lsb(moves));
        moves ^= squares[B6.value()];

        assertEquals(0, moves);
    }

    @Test
    public void kingMoves() {
        long moves = kingMoves[E4.value()];
        assertEquals(8, Long.bitCount(moves));

        assertEquals(D5.value(), Bitboard.lsb(moves));
        moves ^= squares[D5.value()];

        assertEquals(E5.value(), Bitboard.lsb(moves));
        moves ^= squares[E5.value()];

        assertEquals(F5.value(), Bitboard.lsb(moves));
        moves ^= squares[F5.value()];

        assertEquals(D4.value(), Bitboard.lsb(moves));
        moves ^= squares[D4.value()];

        assertEquals(F4.value(), Bitboard.lsb(moves));
        moves ^= squares[F4.value()];

        assertEquals(D3.value(), Bitboard.lsb(moves));
        moves ^= squares[D3.value()];

        assertEquals(E3.value(), Bitboard.lsb(moves));
        moves ^= squares[E3.value()];

        assertEquals(F3.value(), Bitboard.lsb(moves));
        moves ^= squares[F3.value()];

        assertEquals(0, moves);

        moves = kingMoves[A8.value()];
        assertEquals(3, Long.bitCount(moves));

        assertEquals(B8.value(), Bitboard.lsb(moves));
        moves ^= squares[B8.value()];

        assertEquals(A7.value(), Bitboard.lsb(moves));
        moves ^= squares[A7.value()];

        assertEquals(B7.value(), Bitboard.lsb(moves));
        moves ^= squares[B7.value()];

        assertEquals(0, moves);
    }

    @Test
    public void pawnAttacks() {
        assertEquals(squares[D5.value()] | squares[F5.value()],
                pawnAttacks[E4.value()][Color.WHITE.getColor()]);

        assertEquals(squares[D3.value()] | squares[F3.value()],
                pawnAttacks[E4.value()][Color.BLACK.getColor()]);

        assertEquals(squares[B3.value()],
                pawnAttacks[A2.value()][Color.WHITE.getColor()]);

        assertEquals(squares[B1.value()],
                pawnAttacks[A2.value()][Color.BLACK.getColor()]);

        assertEquals(squares[G8.value()],
                pawnAttacks[H7.value()][Color.WHITE.getColor()]);

        assertEquals(squares[G6.value()],
                pawnAttacks[H7.value()][Color.BLACK.getColor()]);
    }

}
