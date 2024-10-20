package dev.jamesswafford.chess4j.board;

import java.util.ArrayList;
import java.util.List;

import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;

import dev.jamesswafford.chess4j.utils.BoardUtils;
import dev.jamesswafford.chess4j.board.squares.Rank;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.hash.Zobrist;
import dev.jamesswafford.chess4j.io.MoveParser;
import dev.jamesswafford.chess4j.pieces.*;
import org.junit.Assert;
import org.junit.Test;

import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testReset() {
        Board board = new Board();

        Assert.assertEquals(Rook.WHITE_ROOK, board.getPiece(Square.A1));
        Assert.assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.B1));
        Assert.assertEquals(Bishop.WHITE_BISHOP, board.getPiece(Square.C1));
        Assert.assertEquals(Queen.WHITE_QUEEN, board.getPiece(Square.D1));
        Assert.assertEquals(King.WHITE_KING, board.getPiece(Square.E1));
        Assert.assertEquals(Bishop.WHITE_BISHOP, board.getPiece(Square.F1));
        Assert.assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.G1));
        Assert.assertEquals(Rook.WHITE_ROOK, board.getPiece(Square.H1));
        for (Square sq : Square.rankSquares(Rank.RANK_2)) {
            Assert.assertEquals(Pawn.WHITE_PAWN, board.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(Rank.RANK_3)) {
            assertNull(board.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(Rank.RANK_4)) {
            assertNull(board.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(Rank.RANK_5)) {
            assertNull(board.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(Rank.RANK_6)) {
            assertNull(board.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(Rank.RANK_7)) {
            Assert.assertEquals(Pawn.BLACK_PAWN,board.getPiece(sq));
        }
        Assert.assertEquals(Rook.BLACK_ROOK, board.getPiece(Square.A8));
        Assert.assertEquals(Knight.BLACK_KNIGHT, board.getPiece(Square.B8));
        Assert.assertEquals(Bishop.BLACK_BISHOP, board.getPiece(Square.C8));
        Assert.assertEquals(Queen.BLACK_QUEEN, board.getPiece(Square.D8));
        Assert.assertEquals(King.BLACK_KING, board.getPiece(Square.E8));
        Assert.assertEquals(Bishop.BLACK_BISHOP, board.getPiece(Square.F8));
        Assert.assertEquals(Knight.BLACK_KNIGHT, board.getPiece(Square.G8));
        Assert.assertEquals(Rook.BLACK_ROOK, board.getPiece(Square.H8));

        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));

        assertEquals(Color.WHITE, board.getPlayerToMove());
        assertNull(board.getEPSquare());
        assertEquals(0, board.getFiftyCounter());
        assertEquals(0, board.getMoveCounter());

        Assert.assertEquals(Square.E1, board.getKingSquare(Color.WHITE));
        Assert.assertEquals(Square.E8, board.getKingSquare(Color.BLACK));
    }

    @Test
    public void testSetPos_moveCounters() {

        Board board = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");

        Assert.assertEquals(Knight.WHITE_KNIGHT,  board.getPiece(Square.F3));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertEquals(3, board.getMoveCounter());
        assertEquals(1, board.getFiftyCounter());
        assertNull(board.getEPSquare());
    }

    @Test
    public void testSetPos_castling() {

        Board board = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQ -");

        Assert.assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.F3));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertEquals(1, board.getMoveCounter());
        assertEquals(0, board.getFiftyCounter());
        assertNull(board.getEPSquare());
    }

    @Test
    public void testSetPos_ep() {

        Board board = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");

        Assert.assertEquals(Pawn.BLACK_PAWN, board.getPiece(Square.C5));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertEquals(2, board.getMoveCounter());
        assertEquals(0, board.getFiftyCounter());
        Assert.assertEquals(Square.C6, board.getEPSquare());
    }

    @Test
    public void testKingSquares() {

        Board board = new Board();
        Assert.assertEquals(Square.E1, board.getKingSquare(Color.WHITE));
        Assert.assertEquals(Square.E8, board.getKingSquare(Color.BLACK));

        board.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        Assert.assertEquals(Square.G1, board.getKingSquare(Color.WHITE));
        Assert.assertEquals(Square.E8, board.getKingSquare(Color.BLACK));

        board.applyMove(new Move(King.BLACK_KING, Square.E8, Square.D8));
        Assert.assertEquals(Square.G1, board.getKingSquare(Color.WHITE));
        Assert.assertEquals(Square.D8, board.getKingSquare(Color.BLACK));
    }

    @Test
    public void testApplyMoveSequence1() {
        Board b = new Board();
        Board b2 = b.deepCopy();

        Move m = new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4);
        b.applyMove(m);
        b2.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C7, Square.C5));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        assertEquals(b2, b);

        b.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        assertEquals(b2, b);

        b.applyMove(new Move(Queen.BLACK_QUEEN, Square.D8, Square.A5));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        assertEquals(b2, b);

        b.applyMove(new Move(Bishop.WHITE_BISHOP, Square.F1, Square.E2));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPPBPPP/RNBQK2R b KQkq - 3 3");
        assertEquals(b2, b);

        b.applyMove(new Move(Queen.BLACK_QUEEN, Square.A5, Square.D2, Bishop.WHITE_BISHOP));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPqBPPP/RNBQK2R w KQkq - 0 4");
        assertEquals(b2, b);

        b.applyMove(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.D2, Queen.BLACK_QUEEN));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPNBPPP/R1BQK2R b KQkq - 0 4");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C5, Square.C4));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQK2R w KQkq - 0 5");
        assertEquals(b2, b);

        b.applyMove(new Move(King.WHITE_KING, Square.E1, Square.G1,true));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        assertEquals(b2, b);

        b.applyMove(new Move(King.BLACK_KING, Square.E8, Square.D8));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 w - - 1 6");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.B2, Square.B4));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C4, Square.B3, Pawn.WHITE_PAWN,true));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQ1RK1 w - - 0 7");
        assertEquals(b2, b);

        b.applyMove(new Move(Rook.WHITE_ROOK, Square.F1, Square.E1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQR1K1 b - - 1 7");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.B3, Square.B2));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR1K1 w - - 0 8");
        assertEquals(b2, b);

        b.applyMove(new Move(King.WHITE_KING, Square.G1, Square.H1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR2K b - - 1 8");
        assertEquals(b2, b);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.B2, Square.A1, Rook.WHITE_ROOK, Knight.BLACK_KNIGHT));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/P1PNBPPP/n1BQR2K w - - 0 9");
        assertEquals(b2, b);
    }

    @Test
    public void testApplyMoveSequence_castling() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));

        // queenside black rook takes white rook removed qside castling options
        board.applyMove(new Move(Rook.BLACK_ROOK, Square.A8, Square.A1, Rook.WHITE_ROOK));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertTrue(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));

        // moving the black king removes bk castling option
        board.applyMove(new Move(King.BLACK_KING, Square.E8, Square.E7));
        assertTrue(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));

        // moving the wk rook removes the wk castling option
        board.applyMove(new Move(Rook.WHITE_ROOK, Square.H1, Square.H7));
        assertFalse(board.hasCastlingRight(CastlingRights.WHITE_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_KINGSIDE));
        assertFalse(board.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE));
    }

    @Test
    public void testUndoDoublePawnPush() {
        Board b = new Board();
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4)));

        assertEquals(b2, b);
    }

    @Test
    public void testUndoCapturingPromotion() {
        Board b = new Board("r7/1PK5/8/8/k7/8/8/8 w - -");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(Pawn.WHITE_PAWN, Square.B7, Square.A8, Rook.BLACK_ROOK, Queen.WHITE_QUEEN)));

        assertEquals(b2, b);
    }

    @Test
    public void testUndoEp() {
        Board b = new Board("k7/8/8/8/pP6/8/K7/8 b - b3");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(Pawn.BLACK_PAWN, Square.A4, Square.B3, Pawn.WHITE_PAWN, true)));

        assertEquals(b2, b);
    }

    @Test
    public void testUndoCastle() {
        Board b = new Board("k7/8/8/8/8/8/8/4K2R w K -");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(King.WHITE_KING, Square.E1, Square.G1, true)));

        assertEquals(b2, b);
    }

    @Test
    public void testSwapPlayer() {
        Board board = new Board();
        board.swapPlayer();
        assertEquals(Color.BLACK, board.getPlayerToMove());
        board.swapPlayer();
        assertEquals(Color.WHITE, board.getPlayerToMove());
    }

    @Test
    public void testDeepCopy() {
        Board b = new Board();
        Board b2 = b.deepCopy();

        assertNotSame(b, b2);
        assertEquals(b, b2);

        b.swapPlayer();
        assertNotEquals(b, b2);

        b.swapPlayer();
        assertEquals(b, b2);
    }

    @Test
    public void testDeepCopy2() {
        Board b = new Board();
        Undo u = b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        Board b2 = b.deepCopy();

        assertNotSame(b, b2);
        assertEquals(b, b2);

        b.undoMove(u);
        b2.undoMove(u);
        assertEquals(b, b2);
    }

    @Test
    public void testFlipVertical() {
        Board b = new Board();
        Board b2 = b.deepCopy();
        b.flipVertical();
        b.flipVertical();
        assertEquals(b2, b);

        b.setPos("7r/R6p/2K4P/5k1P/2p4n/5p2/8/8 w - - 0 1");
        b2 = b.deepCopy();
        b.flipVertical();
        b.flipVertical();
        assertEquals(b2, b);

        // test EP
        b.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        b2 = b.deepCopy();
        b.flipVertical();
        b.flipVertical();
        assertEquals(b2, b);

        // test castling
        b.setPos("4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        b2 = b.deepCopy();
        b.flipVertical();
        b.flipVertical();
        assertEquals(b2, b);
    }

    @Test
    /*
     * Should be able to obtain an equal position using the French Defense and Petrov Defense
     */
    public void testEqualsAndHash() {

        Board b1 = new Board();
        Board b2 = b1.deepCopy();

        // step through French Defense with b1
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E6));
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.E6, Square.D5, Pawn.WHITE_PAWN));
        b1.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        b1.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));

        // step through the Petrov Defense with b2
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E5));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.F3, Square.E5, Pawn.BLACK_PAWN));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D6));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.E5, Square.F3));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.F6, Square.E4, Pawn.WHITE_PAWN));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D3));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.E4, Square.F6));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.D3, Square.D4));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.D6, Square.D5));

        // Positions would be equal at this point, except for move and fifty counters
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        // make a couple of moves in b1 to catch the move counter up
        b1.applyMove(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.A3));
        b1.applyMove(new Move(Knight.BLACK_KNIGHT, Square.B8, Square.A6));
        b1.applyMove(new Move(Knight.WHITE_KNIGHT, Square.A3, Square.B1));
        b1.applyMove(new Move(Knight.BLACK_KNIGHT, Square.A6, Square.B8));

        assertEquals(b2.getMoveCounter(), b1.getMoveCounter());
        assertNotEquals(b2.getFiftyCounter(), b1.getFiftyCounter());

        // by adding a pawn move the fifty counters are re-aligned
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));

        assertEquals(b2.getFiftyCounter(), b1.getFiftyCounter());

        // and now the positions should be equal
        assertEquals(b2, b1);
        assertEquals(b2.hashCode(), b1.hashCode());
    }

    @Test
    public void testEqualityBeforeAndAfterCastle() {
        Board b1 = new Board("4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(King.BLACK_KING, Square.E8, Square.G8,true);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        Undo u = b1.applyMove(m);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b1.undoMove(u);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testEqualityBeforeAndAfterCapture() {
        Board b1 = new Board("rnbqkbnr/pp1ppppp/8/2p5/3P4/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(Pawn.WHITE_PAWN, Square.D4, Square.C5, Pawn.BLACK_PAWN);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        Undo u = b1.applyMove(m);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b1.undoMove(u);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testEqualityBeforeAndAfterEPCapture() {
        Board b1 = new Board("rnbqkbnr/pp1ppppp/8/2pP4/8/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        Move m = new Move(Pawn.WHITE_PAWN, Square.D5, Square.C6, Pawn.BLACK_PAWN,true);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        Undo u = b1.applyMove(m);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b1.undoMove(u);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testEqualityBeforeAndAfterPromotion() throws ParseException {
        Board b1 = new Board("8/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(Pawn.WHITE_PAWN, Square.A7, Square.A8,null, Queen.WHITE_QUEEN);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        Undo u = b1.applyMove(m);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b1.undoMove(u);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testEqualityBeforeAndAfterCapturingPromotion() throws ParseException {
        Board b1 = new Board("1n6/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(Pawn.WHITE_PAWN, Square.A7, Square.B8, Knight.BLACK_KNIGHT, Queen.WHITE_QUEEN);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        Undo u = b1.applyMove(m);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());

        b1.undoMove(u);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testZobristKey() throws ParseException, IllegalMoveException {
        Board board = new Board();

        Assert.assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());

        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("e4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("e5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("d4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("exd4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("c4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("dxc3", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Nf3", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("cxb2", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Bc4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        Undo u = board.applyMove(mp.parseMove("bxc1=q", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(u);
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("bxa1=n", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("O-O", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("b5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Bxb5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Nc6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Nc3", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("d5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Bd2", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Bh3", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Qxa1", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Qd7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Ng5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("O-O-O", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("g2xh3", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Kb7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("h4", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Be7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Nxh7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Rxh7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("h5", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Rh6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Kh1", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Rg6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("h6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Rg1", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Kxg1", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("Nf6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("h7", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("a6", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.applyMove(mp.parseMove("h8=r", board));
        assertEquals(Zobrist.calculateBoardKey(board), board.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
    }

    @Test
    public void testPawnKey() throws Exception {
        Board board = new Board();

        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        MoveParser mp = new MoveParser();
        List<Undo> undos = new ArrayList<>();
        undos.add(board.applyMove(mp.parseMove("e4", board)));
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        undos.add(board.applyMove(mp.parseMove("e5", board)));
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        undos.add(board.applyMove(mp.parseMove("d4", board)));
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        undos.add(board.applyMove(mp.parseMove("exd4", board)));
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        undos.add(board.applyMove(mp.parseMove("c4", board)));
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        undos.add(board.applyMove(mp.parseMove("dxc3", board))); // ep
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());

        board.undoMove(undos.get(undos.size()-1)); // dxc3
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(undos.get(undos.size()-2)); // c4
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(undos.get(undos.size()-3)); // exd4
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(undos.get(undos.size()-4)); // d4
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(undos.get(undos.size()-5)); // e5
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(undos.get(undos.size()-6)); // e4
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
    }

    @Test
    public void testPawnKeyPromotion() throws Exception {
        Board board = new Board("7k/P7/K7/8/8/8/8/8 w - - 0 1");
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=Q", board);
        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(board);
        assertTrue(legalMoves.contains(m));
        Undo u = board.applyMove(m);
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
        board.undoMove(u);
        assertEquals(Zobrist.calculatePawnKey(board), board.getPawnKey());
    }

    @Test
    public void testGetNumPieces_initialPos() {
        Board board = new Board();

        assertEquals(8, board.getNumPieces(Pawn.WHITE_PAWN));
        assertEquals(8, board.getNumPieces(Pawn.BLACK_PAWN));
        assertEquals(1, board.getNumPieces(Queen.WHITE_QUEEN));
        assertEquals(1, board.getNumPieces(Queen.BLACK_QUEEN));
        assertEquals(2, board.getNumPieces(Rook.WHITE_ROOK));
        assertEquals(2, board.getNumPieces(Rook.BLACK_ROOK));
        assertEquals(2, board.getNumPieces(Knight.WHITE_KNIGHT));
        assertEquals(2, board.getNumPieces(Knight.BLACK_KNIGHT));
        assertEquals(2, board.getNumPieces(Bishop.WHITE_BISHOP));
        assertEquals(2, board.getNumPieces(Bishop.BLACK_BISHOP));
    }

    @Test
    public void testGetNumPieces_pos1() {
        Board board = new Board("7k/br6/8/8/8/8/Q7/7K w - -");

        assertEquals(0, board.getNumPieces(Pawn.WHITE_PAWN));
        assertEquals(0, board.getNumPieces(Pawn.BLACK_PAWN));
        assertEquals(1, board.getNumPieces(Queen.WHITE_QUEEN));
        assertEquals(0, board.getNumPieces(Queen.BLACK_QUEEN));
        assertEquals(0, board.getNumPieces(Rook.WHITE_ROOK));
        assertEquals(1, board.getNumPieces(Rook.BLACK_ROOK));
        assertEquals(0, board.getNumPieces(Knight.WHITE_KNIGHT));
        assertEquals(0, board.getNumPieces(Knight.BLACK_KNIGHT));
        assertEquals(0, board.getNumPieces(Bishop.WHITE_BISHOP));
        assertEquals(1, board.getNumPieces(Bishop.BLACK_BISHOP));
    }

    @Test
    public void testPieceCountsPromotion() throws Exception {
        Board b = new Board("7k/P7/8/8/8/8/8/7K w - -");

        assertEquals(1, b.getNumPieces(Pawn.WHITE_PAWN));
        assertEquals(0, b.getNumPieces(Queen.WHITE_QUEEN));

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=q", b);

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(b);
        assertTrue(moves.contains(m));
        Undo u = b.applyMove(m);

        assertEquals(0, b.getNumPieces(Pawn.WHITE_PAWN));
        assertEquals(1, b.getNumPieces(Queen.WHITE_QUEEN));

        b.undoMove(u);
        assertEquals(1, b.getNumPieces(Pawn.WHITE_PAWN));
        assertEquals(0, b.getNumPieces(Queen.WHITE_QUEEN));
    }

    // these taken from Arasan
    @Test
    public void testPlayerInCheck() throws Exception {
        testCasePlayerInCheck("5r1k/pp4pp/2p5/2b1P3/4P3/1PB1p3/P5PP/3N1QK1 b - -","e2+",true);
        testCasePlayerInCheck("8/1n3ppk/7p/3n1P1P/1P4K1/1r6/2N5/3B4 w - -","Ne3",false);
        testCasePlayerInCheck("8/5ppb/3k3p/1p3P1P/1PrN1PK1/3R4/8/8 w - -","Nf3+",true);
        testCasePlayerInCheck("8/5ppb/3k3p/1p1r1P1P/1P1N2K1/3R4/8/8 w - -","Nxb5+",true);
        testCasePlayerInCheck("8/5ppb/7p/5P1P/k2BR1K1/8/8/8 w - -","Bxg7+",true);
        testCasePlayerInCheck("7R/5kp1/4n1pp/2r1p3/4P1P1/3R3P/r4P2/5BK1 w - -","Rd7+",true);
        testCasePlayerInCheck("7R/4nkp1/6pp/2r1p3/4P1P1/3R3P/r4P2/5BK1 w - -","Rd7",false);
        testCasePlayerInCheck("r1bq1bkr/ppn2p2/2n4p/2p1p3/4B2p/2NP2P1/PP1NPP1P/R1B3QK w - -","gxh4+",true);
        testCasePlayerInCheck("4B3/1n3ppb/2P4p/5P1P/kPrN2K1/3R4/8/8 w - -","cxb7+",true);
        testCasePlayerInCheck("8/4kp2/6pp/3P4/5P1n/P2R3P/7r/5K2 w - -","d6+",true);

        testCasePlayerInCheck("7k/1p4p1/pPp4p/P1P1b2q/4Q1n1/2N3P1/5BK1/7R b - -","Qh1+",true);
        testCasePlayerInCheck("7k/1p4p1/pPp4p/P1P1b2q/4Q1n1/2N2KP1/5BR1/8 b - -","Qh1",false);
        testCasePlayerInCheck("8/5ppb/7p/5P1P/3B1RK1/8/4k3/8 w - -","Re4+",true);
        testCasePlayerInCheck("5k2/5ppb/7p/7P/3BrP2/8/2K5/8 b - -","Rd4+",true);
        testCasePlayerInCheck("6k1/5ppb/7p/7P/4rP2/3B4/2K5/8 b - -","Rd4",false);
        testCasePlayerInCheck("8/1R3P1k/8/5r2/2P1p1pP/8/1p5K/8 w - -","f8=q",true);
        testCasePlayerInCheck("2k2Nn1/2r5/q2p4/p2Np1P1/1pPpP1K1/1P1Pb2Q/P6R/8 w - -","Kh5+",true);

        // The move here is actually illegal... my move parser doesn't allow it
        //testCasePlayerInCheck("8/1P3ppb/2k4p/1p3P1P/1Pr3K1/3RN3/8/8 w - -","b8=N",true);

        testCasePlayerInCheck("1r6/P4ppb/7p/1p2kP1P/1P1N2K1/3R4/8/8 w - -","axb8=Q",true);
        testCasePlayerInCheck("1r6/P1N2ppb/7p/1p2kP1P/1P4K1/3R4/8/8 w - -","axb8=Q",false);
    }

    private void testCasePlayerInCheck(String fen,String mv,boolean inCheck) throws Exception {
        Board board = new Board(fen);

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove(mv, board);
        board.applyMove(m);

        Assert.assertEquals(inCheck, BoardUtils.isPlayerInCheck(board));
    }
}
