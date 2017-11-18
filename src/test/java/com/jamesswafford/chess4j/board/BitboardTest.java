package com.jamesswafford.chess4j.board;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.North;
import com.jamesswafford.chess4j.board.squares.NorthEast;
import com.jamesswafford.chess4j.board.squares.NorthWest;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.South;
import com.jamesswafford.chess4j.board.squares.SouthEast;
import com.jamesswafford.chess4j.board.squares.SouthWest;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.board.squares.West;

import junit.framework.Assert;

public class BitboardTest {

    @Test
    public void testLSB() {
        Square e4 = Square.valueOf(File.FILE_E, Rank.RANK_4);
        Assert.assertEquals(e4.value(),new Bitboard(e4.value()).lsb());

        for (int i=0;i<64;i++) {
            Assert.assertEquals(i, new Bitboard(i).lsb());
        }

        Assert.assertEquals(Square.valueOf(File.FILE_A, Rank.RANK_6).value(),
                new Bitboard(Bitboard.ranks[Rank.RANK_6.getValue()]).lsb());
    }

    @Test
    public void testMSB() {
        Square e4 = Square.valueOf(File.FILE_E, Rank.RANK_4);
        Assert.assertEquals(e4.value(),new Bitboard(e4.value()).msb());

        for (int i=0;i<64;i++) {
            Assert.assertEquals(i, new Bitboard(i).msb());
        }

        Assert.assertEquals(Square.valueOf(File.FILE_H, Rank.RANK_6).value(),
                new Bitboard(Bitboard.ranks[Rank.RANK_6.getValue()]).msb());
    }

    @Test
    public void testRays() {

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_6).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_7).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][North.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_4).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_3).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_2).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_1).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][South.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_C, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_B, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_A, Rank.RANK_5).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][West.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_E, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_F, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_G, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_H, Rank.RANK_5).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][East.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_E, Rank.RANK_6).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_F, Rank.RANK_7).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_G, Rank.RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][NorthEast.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_E, Rank.RANK_4).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_F, Rank.RANK_3).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_G, Rank.RANK_2).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_H, Rank.RANK_1).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][SouthEast.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_C, Rank.RANK_4).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_B, Rank.RANK_3).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_A, Rank.RANK_2).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][SouthWest.getInstance().value()]);

        Assert.assertEquals(
                (Bitboard.squares[Square.valueOf(File.FILE_C, Rank.RANK_6).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_B, Rank.RANK_7).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_A, Rank.RANK_8).value()]
                ),
                Bitboard.rays[Square.valueOf(File.FILE_D, Rank.RANK_5).value()][NorthWest.getInstance().value()]);
    }

    @Test
    public void testKnightMoves() {
        Square e4 = Square.valueOf(File.FILE_E, Rank.RANK_4);
        long moves = Bitboard.knightMoves[e4.value()];
        Assert.assertEquals(8, Long.bitCount(moves));

        Square d6 = Square.valueOf(File.FILE_D, Rank.RANK_6);
        Assert.assertEquals(d6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d6.value()];

        Square f6 = Square.valueOf(File.FILE_F, Rank.RANK_6);
        Assert.assertEquals(f6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f6.value()];

        Square c5 = Square.valueOf(File.FILE_C, Rank.RANK_5);
        Assert.assertEquals(c5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c5.value()];

        Square g5 = Square.valueOf(File.FILE_G, Rank.RANK_5);
        Assert.assertEquals(g5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[g5.value()];

        Square c3 = Square.valueOf(File.FILE_C, Rank.RANK_3);
        Assert.assertEquals(c3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c3.value()];

        Square g3 = Square.valueOf(File.FILE_G, Rank.RANK_3);
        Assert.assertEquals(g3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[g3.value()];

        Square d2 = Square.valueOf(File.FILE_D, Rank.RANK_2);
        Assert.assertEquals(d2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d2.value()];

        Square f2 = Square.valueOf(File.FILE_F, Rank.RANK_2);
        Assert.assertEquals(f2.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f2.value()];

        Assert.assertEquals(0, moves);

        Square a8 = Square.valueOf(File.FILE_A, Rank.RANK_8);
        moves = Bitboard.knightMoves[a8.value()];
        Assert.assertEquals(2, Long.bitCount(moves));

        Square c7 = Square.valueOf(File.FILE_C, Rank.RANK_7);
        Assert.assertEquals(c7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[c7.value()];

        Square b6 = Square.valueOf(File.FILE_B, Rank.RANK_6);
        Assert.assertEquals(b6.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b6.value()];

        Assert.assertEquals(0, moves);
    }

    @Test
    public void testKingMoves() {
        Square e4 = Square.valueOf(File.FILE_E, Rank.RANK_4);
        long moves = Bitboard.kingMoves[e4.value()];
        Assert.assertEquals(8, Long.bitCount(moves));

        Square d5 = Square.valueOf(File.FILE_D, Rank.RANK_5);
        Assert.assertEquals(d5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d5.value()];

        Square e5 = Square.valueOf(File.FILE_E, Rank.RANK_5);
        Assert.assertEquals(e5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[e5.value()];

        Square f5 = Square.valueOf(File.FILE_F, Rank.RANK_5);
        Assert.assertEquals(f5.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f5.value()];

        Square d4 = Square.valueOf(File.FILE_D, Rank.RANK_4);
        Assert.assertEquals(d4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d4.value()];

        Square f4 = Square.valueOf(File.FILE_F, Rank.RANK_4);
        Assert.assertEquals(f4.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f4.value()];

        Square d3 = Square.valueOf(File.FILE_D, Rank.RANK_3);
        Assert.assertEquals(d3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[d3.value()];

        Square e3 = Square.valueOf(File.FILE_E, Rank.RANK_3);
        Assert.assertEquals(e3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[e3.value()];

        Square f3 = Square.valueOf(File.FILE_F, Rank.RANK_3);
        Assert.assertEquals(f3.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[f3.value()];

        Assert.assertEquals(0, moves);

        Square a8 = Square.valueOf(File.FILE_A, Rank.RANK_8);
        moves = Bitboard.kingMoves[a8.value()];
        Assert.assertEquals(3, Long.bitCount(moves));

        Square b8 = Square.valueOf(File.FILE_B, Rank.RANK_8);
        Assert.assertEquals(b8.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b8.value()];

        Square a7 = Square.valueOf(File.FILE_A, Rank.RANK_7);
        Assert.assertEquals(a7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[a7.value()];

        Square b7 = Square.valueOf(File.FILE_B, Rank.RANK_7);
        Assert.assertEquals(b7.value(), Bitboard.lsb(moves));
        moves ^= Bitboard.squares[b7.value()];

        Assert.assertEquals(0, moves);
    }

    @Test
    public void testPawnAttacks() {
        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_5).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_F, Rank.RANK_5).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_E, Rank.RANK_4).value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_D, Rank.RANK_3).value()]
                | Bitboard.squares[Square.valueOf(File.FILE_F, Rank.RANK_3).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_E, Rank.RANK_4).value()][Color.BLACK.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_B, Rank.RANK_3).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_A, Rank.RANK_2).value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_B, Rank.RANK_1).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_A, Rank.RANK_2).value()][Color.BLACK.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_G, Rank.RANK_8).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_H, Rank.RANK_7).value()][Color.WHITE.getColor()]);

        Assert.assertEquals(Bitboard.squares[Square.valueOf(File.FILE_G, Rank.RANK_6).value()],
                Bitboard.pawnAttacks[Square.valueOf(File.FILE_H, Rank.RANK_7).value()][Color.BLACK.getColor()]);

    }
}
