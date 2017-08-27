package com.jamesswafford.chess4j.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;

public class MoveStackTest {

	Move m1,m2;
	
	public MoveStackTest() {
		m1 = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4));
		m2 = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_D, Rank.RANK_7),Square.valueOf(File.FILE_D, Rank.RANK_5));
	}
	
	@Test
	public void testPushPop() {
		Assert.assertEquals(0, MoveStack.getInstance().getCurrentIndex());
		Assert.assertNotNull(m1);
		
		MoveStack.getInstance().push(m1);
		Assert.assertEquals(1, MoveStack.getInstance().getCurrentIndex());
		
		MoveStack.getInstance().push(m2);
		Assert.assertEquals(2, MoveStack.getInstance().getCurrentIndex());
		
		Move myMove = MoveStack.getInstance().pop();
		Assert.assertEquals(m2, myMove);
		Assert.assertEquals(1, MoveStack.getInstance().getCurrentIndex());
		
		myMove = MoveStack.getInstance().pop();
		Assert.assertEquals(m1, myMove);
		Assert.assertEquals(0, MoveStack.getInstance().getCurrentIndex());
	}
	
	@Test
	public void testInsertAtAndGet() {
		Assert.assertEquals(0, MoveStack.getInstance().getCurrentIndex());

		MoveStack.getInstance().insertAt(1337,m1);
		Assert.assertEquals(0, MoveStack.getInstance().getCurrentIndex());

		Assert.assertEquals(m1, MoveStack.getInstance().get(1337));
	}
	
	@Test
	public void testClear() {
		Assert.assertEquals(0, MoveStack.getInstance().getCurrentIndex());
		Assert.assertNull(MoveStack.getInstance().get(999));
		
		MoveStack.getInstance().insertAt(999,m1);
		Assert.assertEquals(m1, MoveStack.getInstance().get(999));
		
		MoveStack.getInstance().clear();
		Assert.assertNull(MoveStack.getInstance().get(999));		
	}

}
