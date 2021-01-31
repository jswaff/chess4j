package com.jamesswafford.chess4j.hash;

import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import org.junit.Ignore;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

import static com.jamesswafford.chess4j.Constants.*;
import static com.jamesswafford.chess4j.hash.TranspositionTableEntryType.*;

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
        Move m = new Move(WHITE_PAWN, E2, E4);
        ttable.store(key, LOWER_BOUND, -100, 3, m);
        TranspositionTableEntry tte = ttable.probe(key);
        assertEquals( LOWER_BOUND, tte.getType());
        assertEquals(-100, tte.getScore());
        assertEquals(3, tte.getDepth());
        assertEquals(m, tte.getMove());

        TranspositionTableEntry lbe = new TranspositionTableEntry(key,  LOWER_BOUND,-100,3,m);
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

        ttable.store(key, EXACT_SCORE,100,3,capture);
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

        ttable.store(key, EXACT_SCORE,100,3,promotion);
        assertEquals(promotion, ttable.probe(key).getMove());
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

        ttable.store(key, EXACT_SCORE,100,3,castle);
        assertEquals(castle, ttable.probe(key).getMove());
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

        ttable.store(key, EXACT_SCORE,100,3,epCapture);
        assertEquals(epCapture, ttable.probe(key).getMove());
    }

    @Ignore
    @Test
    public void storeMateScore() {
        ttable.clear();
        board.setPos("5k2/6pp/p1qN4/1p1p4/3P4/2PKP2Q/PP3r2/3R4 b - -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(BLACK_PAWN, C6, C4);
        // store a mate in 10 (half) moves 7 ply from the root
        // it should be stored as mate in 3 from the current position
        ttable.store(key, LOWER_BOUND,CHECKMATE-10, 5, m, 7);

        TranspositionTableEntry tte = ttable.probe(key, 0);
        assertEquals(m, tte.getMove());
        assertEquals(CHECKMATE-3, tte.getScore());

        // when probed from 3 moves down, the returned score should reflect being 6 moves from the root
        assertEquals(CHECKMATE-6, ttable.probe(key, 3).getScore());
    }

    @Ignore
    @Test
    public void storeMatedScore() {
        ttable.clear();
        board.setPos("r1b1k2r/1pp1q2p/p1n3p1/3QPp2/8/1BP3B1/P5PP/3R1RK1 w kq -");
        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(WHITE_KING, G1, H1);
        // store as mated-in-12 from ply 4, which should get translated into mated-in-8
        ttable.store(key,UPPER_BOUND,-CHECKMATE+12, 7, m, 4);

        TranspositionTableEntry tte = ttable.probe(key, 0);
        assertEquals(UPPER_BOUND, tte.getType());
        assertEquals(m, tte.getMove());
        assertEquals(7, tte.getDepth());
        assertEquals(-(CHECKMATE-8), tte.getScore());

        // from ply 2, the mated score should be mated-in-10 from root
        assertEquals(-(CHECKMATE-10), ttable.probe(key, 2).getScore());
    }

    @Test
    public void clearTable() {
        ttable.clear();
        board.setPos("3qrrk1/1pp2pp1/1p2bn1p/5N2/2P5/P1P3B1/1P4PP/2Q1RRK1 w - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(WHITE_KNIGHT, F5, D4);
        ttable.store(key,LOWER_BOUND,-93, 4, m);
        ttable.probe(key);

        ttable.clear();
        assertNull(ttable.probe(key));
    }

    @Test
    public void overwrite() {
        ttable.clear();
        board.setPos("8/k7/p7/3Qp2P/n1P5/3KP3/1q6/8 b - -");

        long key = Zobrist.calculateBoardKey(board);
        Move m = new Move(BLACK_PAWN, E5, E4);
        ttable.store(key, LOWER_BOUND,1001, 5, m);
        TranspositionTableEntry tte = ttable.probe(key);
        TranspositionTableEntry lbe = new TranspositionTableEntry(key, LOWER_BOUND,1001,5,m);
        assertEquals(lbe, tte);

        // overwrite with different score/depth
        ttable.store(key, LOWER_BOUND,900, 6, m);
        tte = ttable.probe(key);
        assertNotEquals(lbe, tte);
        lbe = new TranspositionTableEntry(key, LOWER_BOUND,900,6,m);
        assertEquals(lbe, tte);

        // use different key to store new entry
        long key2 = ~key;
        assertNull(ttable.probe(key2));
        ttable.store(key2, LOWER_BOUND, 800, 7, m);

        // now make sure we didn't overwrite lbe
        tte = ttable.probe(key);
        assertEquals(lbe, tte);

        TranspositionTableEntry lbe2 = new TranspositionTableEntry(key2, LOWER_BOUND,800,7,m);
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
