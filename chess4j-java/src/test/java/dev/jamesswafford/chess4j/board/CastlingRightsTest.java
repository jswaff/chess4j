package dev.jamesswafford.chess4j.board;

import org.junit.Test;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.CastlingRights.*;

public class CastlingRightsTest {

    @Test
    public void testLabel() {
        assertEquals("K", WHITE_KINGSIDE.getLabel());
        assertEquals("Q", WHITE_QUEENSIDE.getLabel());
        assertEquals("k", BLACK_KINGSIDE.getLabel());
        assertEquals("q", BLACK_QUEENSIDE.getLabel());
    }
}
