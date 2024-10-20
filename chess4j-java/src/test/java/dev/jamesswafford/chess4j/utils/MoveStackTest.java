package dev.jamesswafford.chess4j.utils;

import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.Pawn;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MoveStackTest {

    Move m1,m2;

    public MoveStackTest() {
        m1 = new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4);
        m2 = new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5);
    }

    @Test
    public void testPushPop() {
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());
        assertNotNull(m1);

        MoveStack.getInstance().push(m1);
        assertEquals(1, MoveStack.getInstance().getCurrentIndex());

        MoveStack.getInstance().push(m2);
        assertEquals(2, MoveStack.getInstance().getCurrentIndex());

        Move myMove = MoveStack.getInstance().pop();
        assertEquals(m2, myMove);
        assertEquals(1, MoveStack.getInstance().getCurrentIndex());

        myMove = MoveStack.getInstance().pop();
        assertEquals(m1, myMove);
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());
    }

    @Test
    public void testInsertAtAndGet() {
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());

        MoveStack.getInstance().insertAt(1337,m1);
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());

        Assert.assertEquals(m1, MoveStack.getInstance().get(1337));
    }

    @Test
    public void testClear() {
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());
        assertNull(MoveStack.getInstance().get(999));

        MoveStack.getInstance().insertAt(999,m1);
        Assert.assertEquals(m1, MoveStack.getInstance().get(999));

        MoveStack.getInstance().clear();
        assertNull(MoveStack.getInstance().get(999));
    }

}
