package dev.jamesswafford.chess4j.board;

import org.junit.Assert;
import org.junit.Test;

public class CastlingRightsTest {

    @Test
    public void testLabel() {
        Assert.assertEquals("K", CastlingRights.WHITE_KINGSIDE.getLabel());
        Assert.assertEquals("Q", CastlingRights.WHITE_QUEENSIDE.getLabel());
        Assert.assertEquals("k", CastlingRights.BLACK_KINGSIDE.getLabel());
        Assert.assertEquals("q", CastlingRights.BLACK_QUEENSIDE.getLabel());
    }
}
