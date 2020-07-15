package com.jamesswafford.chess4j.hash;

import org.junit.Test;

import static org.junit.Assert.*;

public class TTHolderTest {

    @Test
    public void resizeMainTables() {

        // with 2 mb
        TTHolder.getInstance().resizeMainTable(2 * 1024 * 1024);
        assertEquals(131072, TTHolder.getInstance().getHashTable().tableCapacity());

        // with 4 mb
        TTHolder.getInstance().resizeMainTable(4 * 1024 * 1024);
        assertEquals(262144, TTHolder.getInstance().getHashTable().tableCapacity());
    }

    @Test
    public void resizePawnTable() {

        // size to 1 mb
        TTHolder.getInstance().resizePawnTable(1024 * 1024);
        assertEquals(87381, TTHolder.getInstance().getPawnHashTable().tableCapacity());

        // size to 2 mb
        TTHolder.getInstance().resizePawnTable(2 * 1024 * 1024);
        assertEquals(174762, TTHolder.getInstance().getPawnHashTable().tableCapacity());
    }

    @Test
    public void resizeAllTables() {

        // with 4 mb, each table gets 2 mb
        TTHolder.getInstance().resizeAllTables(4 * 1024 * 1024);

        assertEquals(131072, TTHolder.getInstance().getHashTable().tableCapacity());
        assertEquals(174762, TTHolder.getInstance().getPawnHashTable().tableCapacity());

        // with 6 mb, each table gets 3 mb
        TTHolder.getInstance().resizeAllTables(6 * 1024 * 1024);
        assertEquals(196608, TTHolder.getInstance().getHashTable().tableCapacity());
        assertEquals(262144, TTHolder.getInstance().getPawnHashTable().tableCapacity());
    }

}
