package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.board.squares.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BitboardTest {

    @Test
    public void toBitboard_single() {

        Assert.assertEquals(Bitboard.squares[Square.D6.value()], Bitboard.toBitboard(Square.D6));
        Assert.assertEquals(Bitboard.squares[Square.F3.value()], Bitboard.toBitboard(Square.F3));
        Assert.assertEquals(Bitboard.squares[Square.C8.value()], Bitboard.toBitboard(Square.C8));
    }

    @Test
    public void toBitboard_list() {

        Assert.assertEquals(
                Bitboard.squares[Square.D6.value()] | Bitboard.squares[Square.D7.value()] | Bitboard.squares[Square.D8.value()],
                Bitboard.toBitboard(Arrays.asList(Square.D6, Square.D7, Square.D8)));

        Assert.assertEquals(0, Bitboard.toBitboard(new ArrayList<>()));

        Assert.assertEquals(-1L, Bitboard.toBitboard(Square.allSquares()));

        //System.out.println("all squares:\n" + drawBitboard(toBitboard(Square.allSquares())));
    }

    @Test
    public void lsb() {
        Square.allSquares()
                .forEach(square -> assertEquals(square.value(), Bitboard.lsb(square)));

        Assert.assertEquals(Square.A6.value(), Bitboard.lsb(Bitboard.ranks[Rank.RANK_6.getValue()]));
    }

    @Test
    public void msb() {
        Square.allSquares()
                .forEach(square -> assertEquals(square.value(), Bitboard.msb(square)));

        Assert.assertEquals(Square.H6.value(), Bitboard.msb(Bitboard.ranks[Rank.RANK_6.getValue()]));
    }

    @Test
    public void rays() {

        Assert.assertEquals(
                Bitboard.squares[Square.D6.value()] | Bitboard.squares[Square.D7.value()] | Bitboard.squares[Square.D8.value()],
                Bitboard.rays[Square.D5.value()][North.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.D4.value()] | Bitboard.squares[Square.D3.value()] | Bitboard.squares[Square.D2.value()] | Bitboard.squares[Square.D1.value()],
                Bitboard.rays[Square.D5.value()][South.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.C5.value()] | Bitboard.squares[Square.B5.value()] | Bitboard.squares[Square.A5.value()],
                Bitboard.rays[Square.D5.value()][West.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.E5.value()] | Bitboard.squares[Square.F5.value()] | Bitboard.squares[Square.G5.value()] | Bitboard.squares[Square.H5.value()],
                Bitboard.rays[Square.D5.value()][East.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.E6.value()] | Bitboard.squares[Square.F7.value()] | Bitboard.squares[Square.G8.value()],
                Bitboard.rays[Square.D5.value()][NorthEast.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.E4.value()] | Bitboard.squares[Square.F3.value()] | Bitboard.squares[Square.G2.value()] | Bitboard.squares[Square.H1.value()],
                Bitboard.rays[Square.D5.value()][SouthEast.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.C4.value()] | Bitboard.squares[Square.B3.value()] | Bitboard.squares[Square.A2.value()],
                Bitboard.rays[Square.D5.value()][SouthWest.getInstance().value()]);

        Assert.assertEquals(
                Bitboard.squares[Square.C6.value()] | Bitboard.squares[Square.B7.value()] | Bitboard.squares[Square.A8.value()],
                Bitboard.rays[Square.D5.value()][NorthWest.getInstance().value()]);
    }

    @Test
    public void knightMoves() {
        long moves = Bitboard.knightMoves[Square.E4.value()];
        assertEquals(8, Long.bitCount(moves));

        Assert.assertEquals(Square.D6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.D6.value()];

        Assert.assertEquals(Square.F6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.F6.value()];

        Assert.assertEquals(Square.C5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.C5.value()];

        Assert.assertEquals(Square.G5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.G5.value()];

        Assert.assertEquals(Square.C3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.C3.value()];

        Assert.assertEquals(Square.G3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.G3.value()];

        Assert.assertEquals(Square.D2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.D2.value()];

        Assert.assertEquals(Square.F2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.F2.value()];

        assertEquals(0, moves);

        moves = Bitboard.knightMoves[Square.A8.value()];
        assertEquals(2, Long.bitCount(moves));

        Assert.assertEquals(Square.C7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.C7.value()];

        Assert.assertEquals(Square.B6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.B6.value()];

        assertEquals(0, moves);
    }

    @Test
    public void kingMoves() {
        long moves = Bitboard.kingMoves[Square.E4.value()];
        assertEquals(8, Long.bitCount(moves));

        Assert.assertEquals(Square.D5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.D5.value()];

        Assert.assertEquals(Square.E5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.E5.value()];

        Assert.assertEquals(Square.F5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.F5.value()];

        Assert.assertEquals(Square.D4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.D4.value()];

        Assert.assertEquals(Square.F4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.F4.value()];

        Assert.assertEquals(Square.D3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.D3.value()];

        Assert.assertEquals(Square.E3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.E3.value()];

        Assert.assertEquals(Square.F3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.F3.value()];

        assertEquals(0, moves);

        moves = Bitboard.kingMoves[Square.A8.value()];
        assertEquals(3, Long.bitCount(moves));

        Assert.assertEquals(Square.B8.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.B8.value()];

        Assert.assertEquals(Square.A7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.A7.value()];

        Assert.assertEquals(Square.B7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[Square.B7.value()];

        assertEquals(0, moves);
    }

    @Test
    public void pawnAttacks() {
        Assert.assertEquals(Bitboard.squares[Square.D5.value()] | Bitboard.squares[Square.F5.value()],
                Bitboard.pawnAttacks[Square.E4.value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.D3.value()] | Bitboard.squares[Square.F3.value()],
                Bitboard.pawnAttacks[Square.E4.value()][Color.BLACK.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.B3.value()],
                Bitboard.pawnAttacks[Square.A2.value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.B1.value()],
                Bitboard.pawnAttacks[Square.A2.value()][Color.BLACK.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.G8.value()],
                Bitboard.pawnAttacks[Square.H7.value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.G6.value()],
                Bitboard.pawnAttacks[Square.H7.value()][Color.BLACK.getColor()]);
    }

}
