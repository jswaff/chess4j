package dev.jamesswafford.chess4j.board.squares;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionTest {

    @Test
    public void directionTo() {
        Assert.assertEquals(North.getInstance(), Direction.getDirectionTo(Square.E4, Square.E6).get());
        Assert.assertEquals(South.getInstance(), Direction.getDirectionTo(Square.E6, Square.E4).get());
        Assert.assertEquals(West.getInstance(), Direction.getDirectionTo(Square.H4, Square.E4).get());
        Assert.assertEquals(East.getInstance(), Direction.getDirectionTo(Square.A4, Square.E4).get());
        Assert.assertEquals(SouthWest.getInstance(), Direction.getDirectionTo(Square.H7, Square.E4).get());
        Assert.assertEquals(SouthEast.getInstance(), Direction.getDirectionTo(Square.C6, Square.E4).get());
        Assert.assertEquals(NorthWest.getInstance(), Direction.getDirectionTo(Square.H1, Square.E4).get());
        Assert.assertEquals(NorthEast.getInstance(), Direction.getDirectionTo(Square.B1, Square.E4).get());
        assertFalse(Direction.getDirectionTo(Square.E4, Square.A1).isPresent());
    }

}
