package com.jamesswafford.chess4j.hash;

import org.junit.Test;

import static org.junit.Assert.*;

public class TTHolderTest {

    @Test
    public void resizeMainTables() {

        // with 2 mb, each table gets 1 mb
        TTHolder.getInstance().resizeMainTables(2 * 1024 * 1024);

        assertEquals(65536, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());

        // with 4 mb, each table gets 2 mb
        TTHolder.getInstance().resizeMainTables(4 * 1024 * 1024);

        assertEquals(131072, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
    }

    @Test
    public void resizePawnTable() {

        // size to 1 mb
        TTHolder.getInstance().resizePawnTable(1024 * 1024);
        assertEquals(65536, TTHolder.getInstance().getPawnTransTable().tableCapacity());

        // size to 2 mb
        TTHolder.getInstance().resizePawnTable(2 * 1024 * 1024);
        assertEquals(131072, TTHolder.getInstance().getPawnTransTable().tableCapacity());
    }

    @Test
    public void resizeAllTables() {

        // with 3 mb, each table gets 1 mb
        TTHolder.getInstance().resizeAllTables(3 * 1024 * 1024);

        assertEquals(65536, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(65536, TTHolder.getInstance().getPawnTransTable().tableCapacity());

        // with 6 mb, each table gets 2 mb
        TTHolder.getInstance().resizeAllTables(6 * 1024 * 1024);

        assertEquals(131072, TTHolder.getInstance().getAlwaysReplaceTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getDepthPreferredTransTable().tableCapacity());
        assertEquals(131072, TTHolder.getInstance().getPawnTransTable().tableCapacity());
    }

}
