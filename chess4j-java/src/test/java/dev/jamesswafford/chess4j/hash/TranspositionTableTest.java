package dev.jamesswafford.chess4j.hash;

import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import dev.jamesswafford.chess4j.Constants;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.pieces.King;
import dev.jamesswafford.chess4j.pieces.Knight;
import dev.jamesswafford.chess4j.pieces.Pawn;
import dev.jamesswafford.chess4j.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TranspositionTableTest {

    private final TranspositionTable ttable = new TranspositionTable();
    private final Board board = new Board();

    @Test
    public void capacity() {
        TranspositionTable tt = new TranspositionTable();
        assertEquals(TranspositionTable.getDefaultSizeBytes() / 16, tt.tableCapacity());

        tt = new TranspositionTable(1000000);
        assertEquals(1000000 / 16, tt.tableCapacity());

        tt = new TranspositionTable(32000);
        assertEquals(32000 / 16, tt.tableCapacity());

        tt = new TranspositionTable(65536);
        assertEquals(65536 / 16, tt.tableCapacity());
    }

    @Test
    public void storeAndProbeWithNegativeScore() {
        ttable.clear();
        board.resetBoard();
        long key = Zobrist.calculateBoardKey(board);
        // shouldn't be anything
        assertNull(ttable.probe(key));

        // now store and reload
        Move m = new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4);
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND, -100, 3, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals( TranspositionTableEntryType.LOWER_BOUND, tte.getType());
        assertEquals(-100, tte.getScore());
        assertEquals(3, tte.getDepth());
        Assert.assertEquals(m, tte.getMove());

        TranspositionTableEntry lbe = new TranspositionTableEntry(key,  TranspositionTableEntryType.LOWER_BOUND,-100,3, m);
        assertEquals(tte, lbe);

        // now make move and reprobe
        Undo u = board.applyMove(m);
        key = Zobrist.calculateBoardKey(board);
        assertNull(ttable.probe(key));

        // finally undo move and reprobe again
        board.undoMove(u);
        key = Zobrist.calculateBoardKey(board);
        tte = ttable.probe(key);
        assertEquals(lbe, tte);
    }

    @Test
    public void storeCapture() {
        ttable.clear();
        board.setPos("5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - -");
        Move capture = MagicBitboardMoveGenerator.genLegalMoves(board).stream()
                .filter(m -> m.captured() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected capture"));

        long key = Zobrist.calculateBoardKey(board);
        assertNull(ttable.probe(key));

        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE,100,3,capture);
        Move capture2 = ttable.probe(key).getMove();
        assertNotNull(capture2.captured());
        assertEquals(capture, capture2);
    }

    @Test
    public void storePromotion() {
        ttable.clear();
        board.setPos("8/4Pk1p/6p1/1r6/8/5N2/2B2PPP/b5K1 w - -");
        Move promotion = MagicBitboardMoveGenerator.genLegalMoves(board).stream()
                .filter(m -> m.promotion() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected promotion"));

        long key = Zobrist.calculateBoardKey(board);
        assertNull(ttable.probe(key));

        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE,100,3,promotion);
        Assert.assertEquals(promotion, ttable.probe(key).getMove());
    }

    @Test
    public void storeCastle() {
        ttable.clear();
        board.setPos("4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        Move castle = MagicBitboardMoveGenerator.genLegalMoves(board).stream()
                .filter(Move::isCastle)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected castle"));

        long key = Zobrist.calculateBoardKey(board);
        assertNull(ttable.probe(key));

        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE,100,3,castle);
        Assert.assertEquals(castle, ttable.probe(key).getMove());
    }

    @Test
    public void storeEPCapture() {
        ttable.clear();
        board.setPos("rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        Move epCapture = MagicBitboardMoveGenerator.genLegalMoves(board).stream()
                .filter(Move::isEpCapture)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("expected ep capture"));

        long key = Zobrist.calculateBoardKey(board);
        assertNull(ttable.probe(key));

        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE,100,3,epCapture);
        Assert.assertEquals(epCapture, ttable.probe(key).getMove());
    }

    @Test
    public void storeMateScore() {
        ttable.clear();
        board.setPos("5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN, Square.C6, Square.C4);
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND, Constants.CHECKMATE-3, 5, m);

        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(tte.getMove(), m);

        // the idea here is to not deal with mate score adjustments!  Just say "it's at least a mate"
        Assert.assertEquals(tte.getScore(), Constants.CHECKMATE-500);
    }

    @Test
    public void transformUpperBoundMate() {
        ttable.clear();
        board.setPos("r4rk1/ppp2ppp/2n5/2bqp3/8/P2PB3/1PP1NPPP/R2Q1RK1 w - -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(Rook.WHITE_ROOK, Square.F1, Square.E1);
        ttable.store(key, TranspositionTableEntryType.UPPER_BOUND, Constants.CHECKMATE-3, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.MOVE_ONLY, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        assertEquals(5, tte.getDepth());
    }

    @Test
    public void transformExactScoreMate() {
        ttable.clear();
        board.setPos("3r2k1/p6p/2Q3p1/4q3/2P1p3/P3Pb2/1P3P1P/2K2BR1 b - -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(King.BLACK_KING, Square.G8, Square.F8);
        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE, Constants.CHECKMATE-7, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.LOWER_BOUND, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        Assert.assertEquals(Constants.CHECKMATE-500, tte.getScore());
        assertEquals(5, tte.getDepth());
    }

    @Test
    public void transformLowerBoundMated() {
        ttable.clear();
        board.setPos("2rq1bk1/p4p1p/1p4p1/3b4/3B1Q2/8/P4PpP/3RR1K1 w - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(Rook.WHITE_ROOK, Square.D1, Square.A1);
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND,-Constants.CHECKMATE+10, 8, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.MOVE_ONLY, tte.getType());
        assertEquals(8, tte.getDepth());
        Assert.assertEquals(-Constants.CHECKMATE+10, tte.getScore()); // note the negative score
    }

    @Test
    public void transformExactScoreMated() {
        ttable.clear();
        board.setPos("4r1k1/5bpp/2p5/3pr3/8/1B3pPq/PPR2P2/2R2QK1 b - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(King.BLACK_KING, Square.G7, Square.G6);
        ttable.store(key, TranspositionTableEntryType.EXACT_SCORE,-Constants.CHECKMATE+12, 10, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.UPPER_BOUND, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        assertEquals(10, tte.getDepth());
        Assert.assertEquals(-(Constants.CHECKMATE-500), tte.getScore());
    }

    @Test
    public void transformUpperBoundMated() {
        ttable.clear();
        board.setPos("r1b1k2r/1pp1q2p/p1n3p1/3QPp2/8/1BP3B1/P5PP/3R1RK1 w kq -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(King.WHITE_KING, Square.G1, Square.H1);
        ttable.store(key, TranspositionTableEntryType.UPPER_BOUND,-Constants.CHECKMATE+8, 7, m);
        TranspositionTableEntry tte = ttable.probe(key);
        Assert.assertEquals(TranspositionTableEntryType.UPPER_BOUND, tte.getType());
        Assert.assertEquals(m, tte.getMove());
        assertEquals(7, tte.getDepth());
        Assert.assertEquals(-(Constants.CHECKMATE-500), tte.getScore());
    }

    @Test
    public void clearTable() {
        ttable.clear();
        board.setPos("3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(Knight.WHITE_KNIGHT, Square.F5, Square.D4);
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND,-93, 4, m);
        ttable.probe(key);

        ttable.clear();
        assertNull(ttable.probe(key));
    }

    @Test
    public void overwrite() {
        ttable.clear();
        board.setPos("8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(Pawn.BLACK_PAWN, Square.E5, Square.E4);
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND,1001, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        TranspositionTableEntry lbe = new TranspositionTableEntry(key, TranspositionTableEntryType.LOWER_BOUND,1001,5, m);
        assertEquals(lbe, tte);

        // overwrite with different score/depth
        ttable.store(key, TranspositionTableEntryType.LOWER_BOUND,900, 6, m);
        tte = ttable.probe(key);
        assertNotEquals(lbe, tte);
        lbe = new TranspositionTableEntry(key, TranspositionTableEntryType.LOWER_BOUND,900,6, m);
        assertEquals(lbe, tte);

        // use different key to store new entry
        long key2 = ~key;
        assertNull(ttable.probe(key2));
        ttable.store(key2, TranspositionTableEntryType.LOWER_BOUND, 800, 7, m);

        // now make sure we didn't overwrite lbe
        tte = ttable.probe(key);
        assertEquals(lbe, tte);

        TranspositionTableEntry lbe2 = new TranspositionTableEntry(key2, TranspositionTableEntryType.LOWER_BOUND,800,7, m);
        tte = ttable.probe(key2);
        assertEquals(lbe2, tte);
    }

    @Test
    public void resize() {

        TranspositionTable tt = new TranspositionTable(1024);
        assertEquals(1024 / 16, tt.tableCapacity());

        int fourMb = 4 * 1024 * 1024;
        tt.resizeTable(fourMb);

        assertEquals(fourMb / 16, tt.tableCapacity());
    }

}
