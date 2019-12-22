package com.jamesswafford.chess4j.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;

public class MoveOrdererTest {

    @Test
    public void testSomeMove() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - - bm g6; id \"WAC.300\";");

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(), null, null);
        mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.CAPTURES_PROMOS, mo.getNextMoveOrderStage());
    }

    @Test
    public void testPV() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "b2b1r1k/3R1ppp/4qP2/4p1PQ/4P3/5B2/4N1K1/8 w - - bm g6; id \"WAC.300\";");
        List<Move> moves = MoveGen.genLegalMoves(b);
        List<Move> moves2 = new ArrayList<Move>(moves);
        Assert.assertEquals(moves, moves2);

        Move pvMove = moves.get(5);
        MoveOrderer mo = new MoveOrderer(b,pvMove, Optional.empty(), null, null);
        mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.HASH_MOVE, mo.getNextMoveOrderStage());

        mo = new MoveOrderer(b,null, Optional.empty(), null, null);
        mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.CAPTURES_PROMOS, mo.getNextMoveOrderStage());


        // should be PV move if ply is 0 and numMovesSearched=0 and is pv node
        mo = new MoveOrderer(b,pvMove, Optional.empty(), null, null);
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.HASH_MOVE, mo.getNextMoveOrderStage());
        Assert.assertEquals(pvMove,nextMv);
    }

    @Test
    // if no PV node but hash
    public void testHash() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "1R6/1brk2p1/4p2p/p1P1Pp2/P7/6P1/1P4P1/2R3K1 w - - bm Rxb7; id \"WAC.015\";");
        List<Move> moves = MoveGen.genLegalMoves(b);

        // randomly make the 5th move the hash move
        Move hashMove = moves.get(4);
        MoveOrderer mo = new MoveOrderer(b,null, Optional.of(hashMove), null, null);
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.GENCAPS, mo.getNextMoveOrderStage());
        Assert.assertEquals(hashMove, nextMv);
    }

    @Test
    public void testPVThenHash() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "6k1/p4p1p/1p3np1/2q5/4p3/4P1N1/PP3PPP/3Q2K1 w - - bm Qd8+; id \"WAC.032\";");
        List<Move> moves = MoveGen.genLegalMoves(b);

        // make move 4 the PV move and move 2 the hash move
        Move pvMove = moves.get(4);
        Move hashMove = moves.get(2);

        MoveOrderer mo = new MoveOrderer(b,pvMove, Optional.of(hashMove),null,null);
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.HASH_MOVE, mo.getNextMoveOrderStage());
        Assert.assertEquals(pvMove, nextMv);

        nextMv = mo.selectNextMove();
        Assert.assertEquals(MoveOrderStage.GENCAPS, mo.getNextMoveOrderStage());
        Assert.assertEquals(hashMove, nextMv);

        nextMv = mo.selectNextMove();
        // there is only one capture in the list but it a losing cap (Ng3xe4).  So, even if
        // the pv move or hash move was that capture, the next stage should be 'noncaptures'
        // UPDATE: actually by MVV/LVA they are all "winning"
//		Assert.assertEquals(MoveOrderStage.NONCAPTURES, mo.getLastMoveOrderStage());
    }

    @Test
    public void testPVAndHashSameMove() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        Collections.shuffle(moves);

        Move pv = moves.get(9);
        MoveOrderer mo = new MoveOrderer(b,pv,Optional.of(pv),null,null);
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(pv, nextMv);

        for (int i=1;i<20;i++) {
            nextMv = mo.selectNextMove();
            Assert.assertTrue(! pv.equals(nextMv));
        }
    }

    @Test
    public void testPromotions() throws Exception {
        Board b = Board.INSTANCE;
        EPDParser.setPos(b, "8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - - bm e8=Q+; id \"position 0631\";");

        List<Move> moves = MoveGen.genLegalMoves(b);

        MoveParser mp = new MoveParser();
        Move e7e8q = mp.parseMove("e7e8=q", b);
        Move e7e8r = mp.parseMove("e7e8=r", b);
        Move e7e8b = mp.parseMove("e7e8=b", b);
        Move e7e8n = mp.parseMove("e7e8=n", b);
        Move c2g6 = mp.parseMove("c2g6", b);
        Assert.assertTrue(moves.contains(e7e8q));
        Assert.assertTrue(moves.contains(e7e8r));
        Assert.assertTrue(moves.contains(e7e8b));
        Assert.assertTrue(moves.contains(e7e8n));
        Assert.assertTrue(moves.contains(c2g6));

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(), null, null);
        Assert.assertTrue(mo.selectNextMove().equals(e7e8q));
        Assert.assertTrue(mo.selectNextMove().equals(e7e8r));
        Assert.assertTrue(mo.selectNextMove().equals(e7e8b));
        Assert.assertTrue(mo.selectNextMove().equals(e7e8n));

        // bonus: make sure c2g6 (only capture and losing capture) is last on list
        /*for (int i=5;i<24;i++) {
            mo.selectNextMove(i);
        }
        Assert.assertTrue(mo.selectNextMove(23).equals(c2g6));
        */

        Assert.assertTrue(mo.selectNextMove().equals(c2g6));
    }

    @Test
    public void testInitialPosition() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();
        List<Move> moves = MoveGen.genLegalMoves(b);
        Assert.assertEquals(20, moves.size());

        // without a PV or hash the order shouldn't change, since there are no captures
        List<Move> moves2 = new ArrayList<Move>();
        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),null,null);
        for (int i=0;i<20;i++) {
            moves2.add(mo.selectNextMove());
        }

        Assert.assertEquals(moves2, moves);

        List<Move> moves3 = new ArrayList<Move>();
        Move pvMove = moves.get(18);
        Move hashMove = moves.get(19);
        mo = new MoveOrderer(b,pvMove,Optional.of(hashMove),null,null);
        for (int i=0;i<20;i++) {
            moves3.add(mo.selectNextMove());
        }

        Assert.assertFalse(moves3.equals(moves2));
        Assert.assertEquals(pvMove, moves3.get(0));
        Assert.assertEquals(hashMove, moves3.get(1));
    }

    @Test
    public void testPVThenHashThenCaptures() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "6R1/kp6/8/1KpP4/8/8/8/6B1 w - c6");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move d5c6 = mp.parseMove("d5c6", b);
        Move b5c5 = mp.parseMove("b5c5", b);
        Move g1c5 = mp.parseMove("g1c5", b);
        Move g8g7 = mp.parseMove("g8g7", b);

        Assert.assertTrue(moves.contains(d5c6));
        Assert.assertTrue(moves.contains(b5c5));
        Assert.assertTrue(moves.contains(g1c5));
        Assert.assertTrue(moves.contains(g8g7));

        MoveOrderer mo = new MoveOrderer(b,g8g7,Optional.of(d5c6),null,null);
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(g8g7, nextMv);
        nextMv = mo.selectNextMove();
        Assert.assertEquals(d5c6, nextMv);
        nextMv = mo.selectNextMove();
        Assert.assertTrue(g1c5.equals(nextMv) || b5c5.equals(nextMv));
        nextMv = mo.selectNextMove();
        Assert.assertTrue(g1c5.equals(nextMv) || b5c5.equals(nextMv));
    }

    /*
        -----b--
        p----PPk	white to move
        -p------	castling rights:
        --------	no ep
        K-------	fifty=0
        -R------	move counter=0
        -p------
        --------
     */
    @Test
    public void testWinningCapsBeforeNonCaps() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "5b2/p4PPk/1p6/8/K7/1R6/1p6/8 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);

        Assert.assertEquals(20, moves.size());
        Collections.shuffle(moves);

        MoveParser mp = new MoveParser();
        Move b3f3 = mp.parseMove("b3f3", b);
        Move b3a3 = mp.parseMove("b3a3", b);
        Move b3c3 = mp.parseMove("b3c3", b);

        Assert.assertTrue(moves.contains(b3f3));
        Assert.assertTrue(moves.contains(b3a3));
        Assert.assertTrue(moves.contains(b3c3));

        MoveOrderer mo = new MoveOrderer(b,b3f3,Optional.of(b3a3),b3f3,b3c3);

        // pv move
        Move nextMv = mo.selectNextMove();
        Assert.assertEquals(b3f3, nextMv);

        // hash move
        nextMv = mo.selectNextMove();
        Assert.assertEquals(b3a3, nextMv);

        // capturing promotions
        Assert.assertEquals(mp.parseMove("g7f8=q", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7f8=r", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7f8=b", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7f8=n", b), mo.selectNextMove());

        // non-capturing promotions
        Assert.assertEquals(mp.parseMove("g7g8=q", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7g8=r", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7g8=b", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g7g8=n", b), mo.selectNextMove());

        // the next two come could come in either order
        Move m1 = mo.selectNextMove();
        Move m2 = mo.selectNextMove();
        Move b3b2 = mp.parseMove("b3b2", b);
        Move b3b6 = mp.parseMove("b3b6", b);

        Assert.assertTrue(m1.equals(b3b2) || m1.equals(b3b6));
        Assert.assertTrue(m2.equals(b3b2) || m2.equals(b3b6));
        Assert.assertFalse(m1.equals(m2));
    }

    @Test
    public void testPVandHashSame() throws Exception {
        Board b = Board.INSTANCE;

        // this position has
        FenParser.setPos(b, "5b2/p4PPk/1p6/8/K7/1R6/1p6/8 w - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        Assert.assertEquals(20, moves.size());
        Collections.shuffle(moves);

        MoveParser mp = new MoveParser();
        Move b3f3 = mp.parseMove("b3f3", b);

        Assert.assertTrue(moves.contains(b3f3));

        MoveOrderer mo = new MoveOrderer(b,b3f3,Optional.of(b3f3),null,null);

        // pv move
        Assert.assertEquals(b3f3, mo.selectNextMove());

        // capturing move (not hash move)
        Assert.assertEquals(mp.parseMove("g7f8=q", b), mo.selectNextMove());
    }

    @Test
    public void testCapsInOrderWhite() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/4p3/R2p2Q1/4P3/1B6/8/7K w - - ");

        List<Move> moves = MoveGen.genLegalMoves(b);
        List<Move> caps = new ArrayList<Move>();
        for (Move mv : moves) {
            if (mv.captured() != null) {
                caps.add(mv);
            }
        }

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),null,null);
        MoveParser mp = new MoveParser();

        Assert.assertEquals(mp.parseMove("e4d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("b3d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("a5d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g5d5", b), mo.selectNextMove());
    }

    @Test
    public void testCapsInOrderBlack() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/8/4p3/r2P2q1/4P3/1b6/8/7K b - - ");

        List<Move> moves = MoveGen.genLegalMoves(b);
        List<Move> caps = new ArrayList<Move>();
        for (Move mv : moves) {
            if (mv.captured() != null) {
                caps.add(mv);
            }
        }

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),null,null);
        MoveParser mp = new MoveParser();

        Assert.assertEquals(mp.parseMove("e6d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("b3d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("a5d5", b), mo.selectNextMove());
        Assert.assertEquals(mp.parseMove("g5d5", b), mo.selectNextMove());
    }

    @Test
    public void testEPOrderedAsWinningCapture() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "8/3p4/8/4P3/8/8/K6k/8 b - -");

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Assert.assertTrue(moves.contains(mp.parseMove("d7d5", b)));
        b.applyMove(mp.parseMove("d7d5", b));

        moves = MoveGen.genLegalMoves(b);
        Move epCap = mp.parseMove("e5d6", b);
        Assert.assertTrue(moves.contains(epCap));

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),null,null);
        Assert.assertEquals(epCap, mo.selectNextMove());
    }

    @Test
    public void testKiller1() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("h2h3", b);
        Assert.assertTrue(moves.contains(m));

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),m,null);
        Assert.assertEquals(m, mo.selectNextMove());
    }

    @Test
    public void testKiller2() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("h2h3", b);
        Assert.assertTrue(moves.contains(m));

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),null,m);
        Assert.assertEquals(m, mo.selectNextMove());
    }


    @Test
    public void testKiller1ThenKiller2() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        List<Move> moves = MoveGen.genLegalMoves(b);
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("h2h3", b);
        Assert.assertTrue(moves.contains(m));

        Move m2 = mp.parseMove("h2h4", b);
        Assert.assertTrue(moves.contains(m));

        MoveOrderer mo = new MoveOrderer(b,null,Optional.empty(),m,m2);
        Assert.assertEquals(m, mo.selectNextMove());
        Assert.assertEquals(m2, mo.selectNextMove());
    }
}
