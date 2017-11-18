package com.jamesswafford.chess4j.hash;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.Constants;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Rook;

public class TranspositionTableTest {

    TranspositionTable ttable = new TranspositionTable();
    Board board = Board.INSTANCE;

    @Test
    public void testNumEntriesIsPowerOf2() {
        TranspositionTable tt = new TranspositionTable();
        assertIsPowerOf2(tt.getNumEntries());

        tt = new TranspositionTable(1000000);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(524288, tt.getNumEntries());


        tt = new TranspositionTable(32000);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(16384, tt.getNumEntries());

        tt = new TranspositionTable(65536);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(65536, tt.getNumEntries());

    }

    private void assertIsPowerOf2(long val) {
        Assert.assertTrue((val & (val - 1))==0);
    }

    @Test
    public void test1() {
        ttable.clear();
        board.resetBoard();
        long key = Zobrist.getBoardKey(board);
        // shouldn't be anything
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertNull(tte);

        // now store and reload
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E,Rank.RANK_4));
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, 100, 3, m);
        tte = ttable.probe(key);
        Assert.assertNotNull(tte);

        TranspositionTableEntry lbe = new TranspositionTableEntry(TranspositionTableEntryType.LOWER_BOUND,key,100,3,m);
        Assert.assertEquals(lbe, tte);

        // now make move and reprobe
        board.applyMove(m);
        key = Zobrist.getBoardKey(board);
        tte = ttable.probe(key);
        Assert.assertNull(tte);

        // finally undo move and reprobe again
        board.undoLastMove();
        key = Zobrist.getBoardKey(board);
        tte = ttable.probe(key);
        Assert.assertNotNull(tte);
        Assert.assertEquals(lbe, tte);
    }

    @Test
    public void testWithMateScore() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - - bm Qc4+; id \"WAC.005\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_6),Square.valueOf(File.FILE_C,Rank.RANK_4));
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, 29997, 5, m);

        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertNotNull(tte);
        Assert.assertEquals(tte.getMove(),m);
        // the idea here is to not deal with mate score adjustments!  Just say "it's at least a mate"
        Assert.assertEquals(tte.getScore(),Constants.CHECKMATE-500);
    }

    @Test
    public void testTransformUpperBoundMate() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "r4rk1/ppp2ppp/2n5/2bqp3/8/P2PB3/1PP1NPPP/R2Q1RK1 w - - bm Nc3; id \"WAC.016\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Rook.WHITE_ROOK,Square.valueOf(File.FILE_F, Rank.RANK_1),Square.valueOf(File.FILE_E,Rank.RANK_1));
        ttable.store(TranspositionTableEntryType.UPPER_BOUND,key, 29997, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.MOVE_ONLY, tte.getType());
        Assert.assertEquals(m,tte.getMove());
        Assert.assertEquals(5, tte.getDepth());
    }

    @Test
    public void testTransformExactScoreMate() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "3r2k1/p6p/2Q3p1/4q3/2P1p3/P3Pb2/1P3P1P/2K2BR1 b - - bm Rd1+; id \"WAC.134\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(King.BLACK_KING,Square.valueOf(File.FILE_G, Rank.RANK_8),Square.valueOf(File.FILE_F,Rank.RANK_8));
        ttable.store(TranspositionTableEntryType.EXACT_MATCH,key, 29993, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.LOWER_BOUND, tte.getType());
        Assert.assertEquals(m,tte.getMove());
        Assert.assertEquals(Constants.CHECKMATE-500, tte.getScore());
        Assert.assertEquals(5, tte.getDepth());
    }

    @Test
    public void testTransformLowerBoundMated() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "2rq1bk1/p4p1p/1p4p1/3b4/3B1Q2/8/P4PpP/3RR1K1 w - - bm Re8; id \"WAC.131\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Rook.WHITE_ROOK,Square.valueOf(File.FILE_D, Rank.RANK_1),Square.valueOf(File.FILE_A,Rank.RANK_1));
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, -29990, 8, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.MOVE_ONLY, tte.getType());
        Assert.assertEquals(8, tte.getDepth());
    }

    @Test
    public void testTransformExactScoreMated() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "4r1k1/5bpp/2p5/3pr3/8/1B3pPq/PPR2P2/2R2QK1 b - - bm Re1; id \"WAC.132\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(King.BLACK_KING,Square.valueOf(File.FILE_G, Rank.RANK_7),Square.valueOf(File.FILE_G,Rank.RANK_6));
        ttable.store(TranspositionTableEntryType.EXACT_MATCH,key, -29988, 10, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.UPPER_BOUND, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        Assert.assertEquals(10, tte.getDepth());
        Assert.assertEquals(-(Constants.CHECKMATE-500), tte.getScore());
    }

    @Test
    public void testTransformUpperBoundMated() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "r1b1k2r/1pp1q2p/p1n3p1/3QPp2/8/1BP3B1/P5PP/3R1RK1 w kq - bm Bh4; id \"WAC.133\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(King.WHITE_KING,Square.valueOf(File.FILE_G, Rank.RANK_1),Square.valueOf(File.FILE_H,Rank.RANK_1));
        ttable.store(TranspositionTableEntryType.UPPER_BOUND,key, -29992, 7, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.UPPER_BOUND, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        Assert.assertEquals(7, tte.getDepth());
        Assert.assertEquals(-(Constants.CHECKMATE-500), tte.getScore());
    }

    @Test
    public void testClear() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - - bm Nxg7; id \"WAC.090\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Knight.WHITE_KNIGHT,Square.valueOf(File.FILE_F, Rank.RANK_5),Square.valueOf(File.FILE_D,Rank.RANK_4));
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, -93, 4, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertNotNull(tte);

        ttable.clear();
        tte = ttable.probe(key);
        Assert.assertNull(tte);
    }

    @Test
    public void testOverwrite() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_E,Rank.RANK_4));
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, 1001, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        TranspositionTableEntry lbe = new TranspositionTableEntry(TranspositionTableEntryType.LOWER_BOUND,key,1001,5,m);
        Assert.assertEquals(lbe, tte);

        // overwrite with different score/depth
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key, 900, 6, m);
        tte = ttable.probe(key);
        Assert.assertFalse(lbe.equals(tte));
        lbe = new TranspositionTableEntry(TranspositionTableEntryType.LOWER_BOUND,key,900,6,m);
        Assert.assertTrue(lbe.equals(tte));

        // use different key to store new entry
        long key2 = ~key;
        tte = ttable.probe(key2);
        Assert.assertNull(tte);
        ttable.store(TranspositionTableEntryType.LOWER_BOUND,key2, 800, 7, m);

        // now make sure we didn't overwrite lbe
        tte = ttable.probe(key);
        Assert.assertEquals(lbe, tte);

        TranspositionTableEntry lbe2 = new TranspositionTableEntry(TranspositionTableEntryType.LOWER_BOUND,key2,800,7,m);
        tte = ttable.probe(key2);
        Assert.assertEquals(lbe2, tte);
    }
}
