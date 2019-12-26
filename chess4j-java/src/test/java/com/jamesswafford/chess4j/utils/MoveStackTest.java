package com.jamesswafford.chess4j.utils;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class MoveStackTest {

    Move m1,m2;

    public MoveStackTest() {
        m1 = new Move(WHITE_PAWN, E2, E4);
        m2 = new Move(BLACK_PAWN, D7, D5);
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

        assertEquals(m1, MoveStack.getInstance().get(1337));
    }

    @Test
    public void testClear() {
        assertEquals(0, MoveStack.getInstance().getCurrentIndex());
        assertNull(MoveStack.getInstance().get(999));

        MoveStack.getInstance().insertAt(999,m1);
        assertEquals(m1, MoveStack.getInstance().get(999));

        MoveStack.getInstance().clear();
        assertNull(MoveStack.getInstance().get(999));
    }

}
