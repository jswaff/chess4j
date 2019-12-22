package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.movegen.MoveGen;
import com.jamesswafford.chess4j.io.FenParser;
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

    TranspositionTable ttable = new TranspositionTable(false);
    TranspositionTable ttDPtable = new TranspositionTable(true);
    Board board = Board.INSTANCE;

    @Test
    public void testNumEntriesIsPowerOf2() {
        TranspositionTable tt = new TranspositionTable(false);
        assertIsPowerOf2(tt.getNumEntries());

        tt = new TranspositionTable(false,1000000);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(524288, tt.getNumEntries());


        tt = new TranspositionTable(false,32000);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(16384, tt.getNumEntries());

        tt = new TranspositionTable(false,65536);
        assertIsPowerOf2(tt.getNumEntries());
        Assert.assertEquals(65536, tt.getNumEntries());
    }

    private void assertIsPowerOf2(long val) {
        Assert.assertTrue((val & (val - 1))==0);
    }

    @Test
    public void testStoreAndProbeWithNegativeScore() {
        ttable.clear();
        board.resetBoard();
        long key = Zobrist.getBoardKey(board);
        // shouldn't be anything
        Assert.assertFalse(ttable.probe(key).isPresent());

        // now store and reload
        Move m = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E,Rank.RANK_4));
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND, -100, 3, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
        Assert.assertEquals(TranspositionTableEntryType.LOWER_BOUND,tte.getType());
        Assert.assertEquals(-100,tte.getScore());
        Assert.assertEquals(3,tte.getDepth());
        Assert.assertEquals(m,tte.getMove());

        TranspositionTableEntry lbe = new TranspositionTableEntry(key,TranspositionTableEntryType.LOWER_BOUND,-100,3,m);
        Assert.assertEquals(lbe, tte);

        // now make move and reprobe
        board.applyMove(m);
        key = Zobrist.getBoardKey(board);
        Assert.assertFalse(ttable.probe(key).isPresent());

        // finally undo move and reprobe again
        board.undoLastMove();
        key = Zobrist.getBoardKey(board);
        tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
        Assert.assertEquals(lbe, tte);
    }

    @Test
    public void testStoreCapture() throws Exception {
        ttable.clear();
        EPDParser.setPos(board,"5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - - bm Qc4+; id \"WAC.005\";");
        Move capture = MoveGen.genLegalMoves(board).stream()
                .filter(m -> m.captured() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected capture"));

        long key = Zobrist.getBoardKey(board);
        Assert.assertFalse(ttable.probe(key).isPresent());

        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,100,3,capture);
        Move capture2 = ttable.probe(key).get().getMove();
        Assert.assertTrue(capture2.captured() != null);
        Assert.assertEquals(capture,capture2);
    }

    @Test
    public void testStorePromotion() throws Exception {
        ttable.clear();
        EPDParser.setPos(board,"8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - - bm e8=Q+; id \"position 0631\";");
        Move promotion = MoveGen.genLegalMoves(board).stream()
                .filter(m -> m.promotion() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected promotion"));

        long key = Zobrist.getBoardKey(board);
        Assert.assertFalse(ttable.probe(key).isPresent());

        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,100,3,promotion);
        Assert.assertEquals(promotion,ttable.probe(key).get().getMove());
    }

    @Test
    public void testStoreCastle() throws Exception {
        ttable.clear();
        FenParser.setPos(board, "4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        Move castle = MoveGen.genLegalMoves(board).stream()
                .filter(m -> m.isCastle())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected castle"));

        long key = Zobrist.getBoardKey(board);
        Assert.assertFalse(ttable.probe(key).isPresent());

        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,100,3,castle);
        Assert.assertEquals(castle,ttable.probe(key).get().getMove());
    }

    @Test
    public void testStoreEPCapture() throws Exception {
        ttable.clear();
        FenParser.setPos(board, "rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        Move epCapture = MoveGen.genLegalMoves(board).stream()
                .filter(m -> m.isEpCapture())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected ep capture"));

        long key = Zobrist.getBoardKey(board);
        Assert.assertFalse(ttable.probe(key).isPresent());

        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,100,3,epCapture);
        Assert.assertEquals(epCapture,ttable.probe(key).get().getMove());
    }

    @Test
    public void testWithMateScore() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - - bm Qc4+; id \"WAC.005\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_C, Rank.RANK_6),Square.valueOf(File.FILE_C,Rank.RANK_4));
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND,29997, 5, m);

        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected move"));
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
        ttable.store(key,TranspositionTableEntryType.UPPER_BOUND, 29997, 5, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
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
        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,29993, 5, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
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
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND,-29990, 8, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
        Assert.assertEquals(TranspositionTableEntryType.MOVE_ONLY, tte.getType());
        Assert.assertEquals(8, tte.getDepth());
        Assert.assertEquals(-29990,tte.getScore()); // note the negative score
    }

    @Test
    public void testTransformExactScoreMated() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "4r1k1/5bpp/2p5/3pr3/8/1B3pPq/PPR2P2/2R2QK1 b - - bm Re1; id \"WAC.132\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(King.BLACK_KING,Square.valueOf(File.FILE_G, Rank.RANK_7),Square.valueOf(File.FILE_G,Rank.RANK_6));
        ttable.store(key,TranspositionTableEntryType.EXACT_MATCH,-29988, 10, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
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
        ttable.store(key,TranspositionTableEntryType.UPPER_BOUND,-29992, 7, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
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
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND,-93, 4, m);
        TranspositionTableEntry tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));

        ttable.clear();
        Assert.assertFalse(ttable.probe(key).isPresent());
    }

    @Test
    public void testOverwrite() throws Exception {
        ttable.clear();
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_E,Rank.RANK_4));
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND,1001, 5, m);
        TranspositionTableEntry tte = ttable.probe(key).get();
        TranspositionTableEntry lbe = new TranspositionTableEntry(key,TranspositionTableEntryType.LOWER_BOUND,1001,5,m);
        Assert.assertEquals(lbe, tte);

        // overwrite with different score/depth
        ttable.store(key,TranspositionTableEntryType.LOWER_BOUND,900, 6, m);
        tte = ttable.probe(key).get();
        Assert.assertFalse(lbe.equals(tte));
        lbe = new TranspositionTableEntry(key,TranspositionTableEntryType.LOWER_BOUND,900,6,m);
        Assert.assertTrue(lbe.equals(tte));

        // use different key to store new entry
        long key2 = ~key;
        Assert.assertFalse(ttable.probe(key2).isPresent());
        ttable.store(key2,TranspositionTableEntryType.LOWER_BOUND, 800, 7, m);

        // now make sure we didn't overwrite lbe
        tte = ttable.probe(key).orElseThrow(() -> new RuntimeException("Expected Move"));
        Assert.assertEquals(lbe, tte);

        TranspositionTableEntry lbe2 = new TranspositionTableEntry(key2,TranspositionTableEntryType.LOWER_BOUND,800,7,m);
        tte = ttable.probe(key2).get();
        Assert.assertEquals(lbe2, tte);
    }

    @Test
    public void testOverwriteDepthPreferred() throws Exception {
        ttDPtable.clear();
        EPDParser.setPos(board, "8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - - bm e4+; id \"WAC.094\";");
        long key = Zobrist.getBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_5),Square.valueOf(File.FILE_E,Rank.RANK_4));
        ttDPtable.store(key,TranspositionTableEntryType.LOWER_BOUND,1001, 5, m);
        TranspositionTableEntry tte = ttDPtable.probe(key).get();
        TranspositionTableEntry origEntry = new TranspositionTableEntry(key,TranspositionTableEntryType.LOWER_BOUND,1001,5,m);
        Assert.assertEquals(origEntry, tte);

        // overwrite with a different score and shallower depth
        ttDPtable.store(key,TranspositionTableEntryType.LOWER_BOUND,900, 4, m);
        tte = ttDPtable.probe(key).get();
        Assert.assertEquals(origEntry, tte);

        // overwrite with yet another score but a deeper depth
        ttDPtable.store(key,TranspositionTableEntryType.LOWER_BOUND,800, 6, m);
        tte = ttDPtable.probe(key).get();
        Assert.assertEquals(6,tte.getDepth());
    }

}
