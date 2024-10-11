package dev.jamesswafford.chess4j.board.squares;

import org.junit.Test;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.board.squares.File.*;

public class FileTest {

    @Test
    public void value() {
        assertEquals(0, FILE_A.getValue());
        assertEquals(1, FILE_B.getValue());
        assertEquals(2, FILE_C.getValue());
        assertEquals(3, FILE_D.getValue());
        assertEquals(4, FILE_E.getValue());
        assertEquals(5, FILE_F.getValue());
        assertEquals(6, FILE_G.getValue());
        assertEquals(7, FILE_H.getValue());

        assertEquals(FILE_A, file(0));
        assertEquals(FILE_B, file(1));
        assertEquals(FILE_C, file(2));
        assertEquals(FILE_D, file(3));
        assertEquals(FILE_E, file(4));
        assertEquals(FILE_F, file(5));
        assertEquals(FILE_G, file(6));
        assertEquals(FILE_H, file(7));
    }

    @Test
    public void label() {
        assertEquals("a", FILE_A.getLabel());
        assertEquals("b", FILE_B.getLabel());
        assertEquals("c", FILE_C.getLabel());
        assertEquals("d", FILE_D.getLabel());
        assertEquals("e", FILE_E.getLabel());
        assertEquals("f", FILE_F.getLabel());
        assertEquals("g", FILE_G.getLabel());
        assertEquals("h", FILE_H.getLabel());

        assertEquals(FILE_A, file("a"));
        assertEquals(FILE_B, file("B"));
        assertEquals(FILE_C, file("c"));
        assertEquals(FILE_D, file("D"));
        assertEquals(FILE_E, file("e"));
        assertEquals(FILE_F, file("F"));
        assertEquals(FILE_G, file("G"));
        assertEquals(FILE_H, file("h"));
    }

    @Test
    public void west() {
        assertFalse(FILE_A.west().isPresent());
        assertEquals(FILE_A, FILE_B.west().get());
        assertEquals(FILE_B, FILE_C.west().get());
        assertEquals(FILE_C, FILE_D.west().get());
        assertEquals(FILE_D, FILE_E.west().get());
        assertEquals(FILE_E, FILE_F.west().get());
        assertEquals(FILE_F, FILE_G.west().get());
        assertEquals(FILE_G, FILE_H.west().get());
    }

    @Test
    public void east() {
        assertEquals(FILE_B, FILE_A.east().get());
        assertEquals(FILE_C, FILE_B.east().get());
        assertEquals(FILE_D, FILE_C.east().get());
        assertEquals(FILE_E, FILE_D.east().get());
        assertEquals(FILE_F, FILE_E.east().get());
        assertEquals(FILE_G, FILE_F.east().get());
        assertEquals(FILE_H, FILE_G.east().get());
        assertFalse(FILE_H.east().isPresent());
    }

    @Test
    public void westOf() {
        assertTrue(FILE_A.westOf(FILE_B));
        assertTrue(FILE_C.westOf(FILE_G));
        assertFalse(FILE_D.westOf(FILE_D));
        assertFalse(FILE_E.westOf(FILE_B));
    }

    @Test
    public void eastOf() {
        assertTrue(FILE_B.eastOf(FILE_A));
        assertTrue(FILE_G.eastOf(FILE_C));
        assertFalse(FILE_D.eastOf(FILE_D));
        assertFalse(FILE_B.eastOf(FILE_E));
    }

    @Test
    public void distance() {
        assertEquals(0, FILE_A.distance(FILE_A));
        assertEquals(7, FILE_A.distance(FILE_H));
        assertEquals(7, FILE_H.distance(FILE_A));
        assertEquals(2, FILE_C.distance(FILE_E));
    }

    @Test
    public void flip() {
        assertEquals(FILE_A, FILE_H.flip());
        assertEquals(FILE_B, FILE_G.flip());
        assertEquals(FILE_C, FILE_F.flip());
        assertEquals(FILE_D, FILE_E.flip());
        assertEquals(FILE_E, FILE_D.flip());
        assertEquals(FILE_F, FILE_C.flip());
        assertEquals(FILE_G, FILE_B.flip());
        assertEquals(FILE_H, FILE_A.flip());
    }
}
