package com.jamesswafford.chess4j.hash;

import org.junit.Test;

import static org.junit.Assert.*;

public class TTHolderTest {

    @Test
    public void resizeAllTables() {

        // with 3 mb, each table gets 1 mb
        TTHolder.getInstance().resizeTables(3 * 1024 * 1024);

        assertEquals(65536, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getPawnTransTable().tableCapacity());

        // with 6 mb, each table gets 2 mb
        TTHolder.getInstance().resizeTables(6 * 1024 * 1024);

        assertEquals(131072, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getPawnTransTable().tableCapacity());
    }

}
