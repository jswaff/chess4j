package com.jamesswafford.chess4j.board;

import org.junit.Test;

import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;

import junit.framework.Assert;

public class MagicTest {

    @Test
    public void testRookMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        Square b7 = Square.valueOf(File.FILE_B,Rank.RANK_7);
        long rookMoves = Magic.getRookMoves(b,b7.value(),b.getBlackPieces());

        Square f7 = Square.valueOf(File.FILE_F, Rank.RANK_7);
        Assert.assertEquals(Bitboard.squares[f7.value()], rookMoves);
    }

    @Test
    public void testBishopMoves() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/1r3k2/8/3bB3/8/8/8/3K4 b - - 0 1");

        Square e5 = Square.valueOf(File.FILE_E,Rank.RANK_5);
        long bishopMoves = Magic.getBishopMoves(b,e5.value(),b.getBlackPieces());

        Square d5 = Square.valueOf(File.FILE_D,Rank.RANK_5);
        Square f7 = Square.valueOf(File.FILE_F, Rank.RANK_7);
        Square b7 = Square.valueOf(File.FILE_B, Rank.RANK_7);
        bishopMoves = Magic.getBishopMoves(b,d5.value(),b.getBlackPieces());
        Assert.assertEquals(Bitboard.squares[f7.value()] | Bitboard.squares[b7.value()], bishopMoves);
    }

}
