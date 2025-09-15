package dev.jamesswafford.chess4j.utils;

import org.junit.Test;

import dev.jamesswafford.chess4j.pieces.Bishop;
import dev.jamesswafford.chess4j.pieces.Pawn;
import dev.jamesswafford.chess4j.pieces.Queen;
import dev.jamesswafford.chess4j.pieces.Rook;

import static org.junit.Assert.*;

public class PieceFactoryTest {

    @Test
    public void testGetPiece() {
        assertEquals(Pawn.WHITE_PAWN, PieceFactory.getPiece('P'));
        assertEquals(Pawn.BLACK_PAWN, PieceFactory.getPiece('p'));

        assertEquals(Rook.WHITE_ROOK, PieceFactory.getPiece('R'));
        assertEquals(Bishop.BLACK_BISHOP, PieceFactory.getPiece('b'));

        assertEquals(Queen.BLACK_QUEEN, PieceFactory.getPiece('Q',false));
    }
}
