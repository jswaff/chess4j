package com.jamesswafford.chess4j.board;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.NorthEast;
import com.jamesswafford.chess4j.board.squares.NorthWest;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.SouthEast;
import com.jamesswafford.chess4j.board.squares.SouthWest;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.board.squares.West;

import static junit.framework.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class BitboardTest {

    @Test
    public void lsb() {
        Square e4 = Square.valueOf(FILE_E, RANK_4);
        assertEquals(e4.value(), new Bitboard(e4.value()).lsb());

        for (int i=0; i<64; i++) {
            assertEquals(i, new Bitboard(i).lsb());
        }

        assertEquals(Square.valueOf(FILE_A, RANK_6).value(),
                new Bitboard(Bitboard.ranks[RANK_6.getValue()]).lsb());
    }

    @Test
    public void msb() {
        Square e4 = Square.valueOf(FILE_E, RANK_4);
        assertEquals(e4.value(), new Bitboard(e4.value()).msb());

        for (int i=0; i<64; i++) {
            assertEquals(i, new Bitboard(i).msb());
        }

        assertEquals(Square.valueOf(FILE_H, RANK_6).value(),
                new Bitboard(Bitboard.ranks[RANK_6.getValue()]).msb());
    }

    @Test
    public void rays() {

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_D, RANK_6).value()]
                | Bitboard.squares[Square.valueOf(FILE_D, RANK_7).value()]
                | Bitboard.squares[Square.valueOf(FILE_D, RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][North.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_D, RANK_4).value()]
                | Bitboard.squares[Square.valueOf(FILE_D, RANK_3).value()]
                | Bitboard.squares[Square.valueOf(FILE_D, RANK_2).value()]
                | Bitboard.squares[Square.valueOf(FILE_D, RANK_1).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][South.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_C, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_B, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_A, RANK_5).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][West.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_E, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_F, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_G, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_H, RANK_5).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][East.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_E, RANK_6).value()]
                | Bitboard.squares[Square.valueOf(FILE_F, RANK_7).value()]
                | Bitboard.squares[Square.valueOf(FILE_G, RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][NorthEast.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_E, RANK_4).value()]
                | Bitboard.squares[Square.valueOf(FILE_F, RANK_3).value()]
                | Bitboard.squares[Square.valueOf(FILE_G, RANK_2).value()]
                | Bitboard.squares[Square.valueOf(FILE_H, RANK_1).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][SouthEast.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_C, RANK_4).value()]
                | Bitboard.squares[Square.valueOf(FILE_B, RANK_3).value()]
                | Bitboard.squares[Square.valueOf(FILE_A, RANK_2).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][SouthWest.getInstance().value()]);

        assertEquals(
                (Bitboard.squares[Square.valueOf(FILE_C, RANK_6).value()]
                | Bitboard.squares[Square.valueOf(FILE_B, RANK_7).value()]
                | Bitboard.squares[Square.valueOf(FILE_A, RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(FILE_D, RANK_5).value()][NorthWest.getInstance().value()]);
    }

    @Test
    public void knightMoves() {
        Square e4 = Square.valueOf(FILE_E, RANK_4);
        long moves = Bitboard.knightMoves[e4.value()];
        assertEquals(8, Long.bitCount(moves));

        Square d6 = Square.valueOf(FILE_D, RANK_6);
        assertEquals(d6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d6.value()];

        Square f6 = Square.valueOf(FILE_F, RANK_6);
        assertEquals(f6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f6.value()];

        Square c5 = Square.valueOf(FILE_C, RANK_5);
        assertEquals(c5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c5.value()];

        Square g5 = Square.valueOf(FILE_G, RANK_5);
        assertEquals(g5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[g5.value()];

        Square c3 = Square.valueOf(FILE_C, RANK_3);
        assertEquals(c3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c3.value()];

        Square g3 = Square.valueOf(FILE_G, RANK_3);
        assertEquals(g3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[g3.value()];

        Square d2 = Square.valueOf(FILE_D, RANK_2);
        assertEquals(d2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d2.value()];

        Square f2 = Square.valueOf(FILE_F, RANK_2);
        assertEquals(f2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f2.value()];

        assertEquals(0, moves);

        Square a8 = Square.valueOf(FILE_A, RANK_8);
        moves = Bitboard.knightMoves[a8.value()];
        assertEquals(2, Long.bitCount(moves));

        Square c7 = Square.valueOf(FILE_C, RANK_7);
        assertEquals(c7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c7.value()];

        Square b6 = Square.valueOf(FILE_B, RANK_6);
        assertEquals(b6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b6.value()];

        assertEquals(0, moves);
    }

    @Test
    public void kingMoves() {
        Square e4 = Square.valueOf(FILE_E, RANK_4);
        long moves = Bitboard.kingMoves[e4.value()];
        assertEquals(8, Long.bitCount(moves));

        Square d5 = Square.valueOf(FILE_D, RANK_5);
        assertEquals(d5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d5.value()];

        Square e5 = Square.valueOf(FILE_E, RANK_5);
        assertEquals(e5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[e5.value()];

        Square f5 = Square.valueOf(FILE_F, RANK_5);
        assertEquals(f5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f5.value()];

        Square d4 = Square.valueOf(FILE_D, RANK_4);
        assertEquals(d4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d4.value()];

        Square f4 = Square.valueOf(FILE_F, RANK_4);
        assertEquals(f4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f4.value()];

        Square d3 = Square.valueOf(FILE_D, RANK_3);
        assertEquals(d3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d3.value()];

        Square e3 = Square.valueOf(FILE_E, RANK_3);
        assertEquals(e3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[e3.value()];

        Square f3 = Square.valueOf(FILE_F, RANK_3);
        assertEquals(f3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f3.value()];

        assertEquals(0, moves);

        Square a8 = Square.valueOf(FILE_A, RANK_8);
        moves = Bitboard.kingMoves[a8.value()];
        assertEquals(3, Long.bitCount(moves));

        Square b8 = Square.valueOf(FILE_B, RANK_8);
        assertEquals(b8.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b8.value()];

        Square a7 = Square.valueOf(FILE_A, RANK_7);
        assertEquals(a7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[a7.value()];

        Square b7 = Square.valueOf(FILE_B, RANK_7);
        assertEquals(b7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b7.value()];

        assertEquals(0, moves);
    }

    @Test
    public void pawnAttacks() {
        assertEquals(Bitboard.squares[Square.valueOf(FILE_D, RANK_5).value()]
                | Bitboard.squares[Square.valueOf(FILE_F, RANK_5).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_E, RANK_4).value()][Color.WHITE.getColor()]);

        assertEquals(Bitboard.squares[Square.valueOf(FILE_D, RANK_3).value()]
                | Bitboard.squares[Square.valueOf(FILE_F, RANK_3).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_E, RANK_4).value()][Color.BLACK.getColor()]);

        assertEquals(Bitboard.squares[Square.valueOf(FILE_B, RANK_3).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_A, RANK_2).value()][Color.WHITE.getColor()]);

        assertEquals(Bitboard.squares[Square.valueOf(FILE_B, RANK_1).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_A, RANK_2).value()][Color.BLACK.getColor()]);

        assertEquals(Bitboard.squares[Square.valueOf(FILE_G, RANK_8).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_H, RANK_7).value()][Color.WHITE.getColor()]);

        assertEquals(Bitboard.squares[Square.valueOf(FILE_G, RANK_6).value()],
                Bitboard.pawnAttacks[Square.valueOf(FILE_H, RANK_7).value()][Color.BLACK.getColor()]);

    }
}
