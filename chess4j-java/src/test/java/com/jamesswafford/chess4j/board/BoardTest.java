package com.jamesswafford.chess4j.board;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.movegen.MoveGen;

import com.jamesswafford.chess4j.utils.BoardUtils;
import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

public class BoardTest {

    @Test
    public void testReset() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(WHITE_ROOK, b.getPiece(A1));
        assertEquals(WHITE_KNIGHT, b.getPiece(B1));
        assertEquals(WHITE_BISHOP, b.getPiece(C1));
        assertEquals(WHITE_QUEEN, b.getPiece(D1));
        assertEquals(WHITE_KING, b.getPiece(E1));
        assertEquals(WHITE_BISHOP, b.getPiece(F1));
        assertEquals(WHITE_KNIGHT, b.getPiece(G1));
        assertEquals(WHITE_ROOK, b.getPiece(H1));
        for (Square sq : Square.rankSquares(RANK_2)) {
            assertEquals(WHITE_PAWN, b.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(RANK_3)) {
            assertNull(b.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(RANK_4)) {
            assertNull(b.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(RANK_5)) {
            assertNull(b.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(RANK_6)) {
            assertNull(b.getPiece(sq));
        }
        for (Square sq : Square.rankSquares(RANK_7)) {
            assertEquals(BLACK_PAWN,b.getPiece(sq));
        }
        assertEquals(BLACK_ROOK, b.getPiece(A8));
        assertEquals(BLACK_KNIGHT, b.getPiece(B8));
        assertEquals(BLACK_BISHOP, b.getPiece(C8));
        assertEquals(BLACK_QUEEN, b.getPiece(D8));
        assertEquals(BLACK_KING, b.getPiece(E8));
        assertEquals(BLACK_BISHOP, b.getPiece(F8));
        assertEquals(BLACK_KNIGHT, b.getPiece(G8));
        assertEquals(BLACK_ROOK, b.getPiece(H8));

        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertTrue(b.hasCastlingRight(BLACK_QUEENSIDE));

        assertEquals(Color.WHITE, b.getPlayerToMove());
        assertNull(b.getEPSquare());
        assertEquals(0, b.getFiftyCounter());
        assertEquals(0, b.getMoveCounter());

        assertEquals(E1, b.getKingSquare(Color.WHITE));
        assertEquals(E8, b.getKingSquare(Color.BLACK));
    }

    @Test
    public void testSetPos_moveCounters() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        b.setPos(fen);

        assertEquals(WHITE_KNIGHT,  b.getPiece(F3));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertTrue(b.hasCastlingRight(BLACK_QUEENSIDE));
        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertEquals(3, b.getMoveCounter());
        assertEquals(1, b.getFiftyCounter());
        assertNull(b.getEPSquare());
    }

    @Test
    public void testSetPos_castling() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQ -";
        b.setPos(fen);

        assertEquals(WHITE_KNIGHT, b.getPiece(F3));
        assertFalse(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));
        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertEquals(1, b.getMoveCounter());
        assertEquals(0, b.getFiftyCounter());
        assertNull(b.getEPSquare());
    }

    @Test
    public void testSetPos_ep() {

        Board b = Board.INSTANCE;
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        b.setPos(fen);

        assertEquals(BLACK_PAWN, b.getPiece(C5));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertTrue(b.hasCastlingRight(BLACK_QUEENSIDE));
        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertEquals(2, b.getMoveCounter());
        assertEquals(0, b.getFiftyCounter());
        assertEquals(C6, b.getEPSquare());
    }

    @Test
    public void testKingSquares() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        assertEquals(E1, b.getKingSquare(Color.WHITE));
        assertEquals(E8, b.getKingSquare(Color.BLACK));

        b.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        assertEquals(G1, b.getKingSquare(Color.WHITE));
        assertEquals(E8, b.getKingSquare(Color.BLACK));

        b.applyMove(new Move(BLACK_KING, E8, D8));
        assertEquals(G1, b.getKingSquare(Color.WHITE));
        assertEquals(D8, b.getKingSquare(Color.BLACK));
    }

    @Test
    public void testApplyMoveSequence1() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();

        Move m = new Move(WHITE_PAWN,E2, E4);
        b.applyMove(m);
        b2.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,C7, C5));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KNIGHT,G1, F3));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_QUEEN,D8, A5));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_BISHOP, F1, E2));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPPBPPP/RNBQK2R b KQkq - 3 3");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_QUEEN, A5, D2, WHITE_BISHOP));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPqBPPP/RNBQK2R w KQkq - 0 4");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KNIGHT, B1, D2,BLACK_QUEEN));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPNBPPP/R1BQK2R b KQkq - 0 4");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN, C5, C4));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQK2R w KQkq - 0 5");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KING, E1, G1,true));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_KING, E8, D8));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 w - - 1 6");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_PAWN, B2, B4));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN, C4, B3,WHITE_PAWN,true));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQ1RK1 w - - 0 7");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_ROOK, F1, E1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQR1K1 b - - 1 7");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN, B3, B2));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR1K1 w - - 0 8");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KING, G1, H1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR2K b - - 1 8");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN, B2, A1, WHITE_ROOK, BLACK_KNIGHT));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/P1PNBPPP/n1BQR2K w - - 0 9");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

    }

    @Test
    public void testApplyMoveSequence_castling() {
        Board b = Board.INSTANCE;
        b.setPos("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertTrue(b.hasCastlingRight(BLACK_QUEENSIDE));

        // queenside black rook takes white rook removed qside castling options
        b.applyMove(new Move(BLACK_ROOK, A8, A1, WHITE_ROOK));
        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertFalse(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));

        // moving the black king removes bk castling option
        b.applyMove(new Move(BLACK_KING, E8, E7));
        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertFalse(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertFalse(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));

        // moving the wk rook removes the wk castling option
        b.applyMove(new Move(WHITE_ROOK, H1, H7));
        assertFalse(b.hasCastlingRight(WHITE_KINGSIDE));
        assertFalse(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertFalse(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));
    }

    @Test
    public void testUndoDoublePawnPush() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(WHITE_PAWN, E2, E4)));

        assertTrue(b.equalExceptMoveHistory(b2, true));
    }

    @Test
    public void testUndoCapturingPromotion() {
        Board b = Board.INSTANCE;
        b.setPos("r7/1PK5/8/8/k7/8/8/8 w - -");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(WHITE_PAWN, B7, A8, BLACK_ROOK, WHITE_QUEEN)));

        assertTrue(b.equalExceptMoveHistory(b2, true));
    }

    @Test
    public void testUndoEp() {
        Board b = Board.INSTANCE;
        b.setPos("k7/8/8/8/pP6/8/K7/8 b - b3");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(BLACK_PAWN, A4, B3, WHITE_PAWN, true)));

        assertTrue(b.equalExceptMoveHistory(b2, true));
    }

    @Test
    public void testUndoCastle() {
        Board b = Board.INSTANCE;
        b.setPos("k7/8/8/8/8/8/8/4K2R w K -");
        Board b2 = b.deepCopy();

        b.undoMove(b.applyMove(new Move(WHITE_KING, E1, G1, true)));

        assertTrue(b.equalExceptMoveHistory(b2, true));
    }

    @Test
    public void testSwapPlayer() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        b.swapPlayer();
        assertEquals(Color.BLACK, b.getPlayerToMove());
        b.swapPlayer();
        assertEquals(Color.WHITE, b.getPlayerToMove());
    }

    @Test
    public void testDeepCopy() {
        Board b = Board.INSTANCE;
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
        Board b = Board.INSTANCE;
        b.resetBoard();
        Undo u = b.applyMove(new Move(WHITE_PAWN, E2, E4));
        Board b2 = b.deepCopy();
        assertNotSame(b, b2);
        assertEquals(b, b2);

        b.undoMove(u);
        b2.undoMove(u);
        assertTrue(b.equalExceptMoveHistory(b2, false));
        assertEquals(b, b2);
    }

    @Test
    public void testDeepCopy3() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();
        assertNotSame(b, b2);
        b.undoMove(b.applyMove(new Move(WHITE_PAWN, E2, E4)));
        assertEquals(b, b2);
    }

    @Test
    public void testFlipVertical() {
        Board b = Board.INSTANCE;

        b.resetBoard();
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
    public void testHash() {
        List<Integer> hashCodes1 = new ArrayList<>();
        List<Integer> hashCodes2 = new ArrayList<>();

        Board b1 = Board.INSTANCE;
        b1.resetBoard();
        Board b2 = b1.deepCopy();

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // step through French Defense with b1
        b1.applyMove(new Move(WHITE_PAWN, E2, E4));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN, E7, E6));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_PAWN, D2, D4));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN, D7, D5));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_PAWN, E4, D5,BLACK_PAWN));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN, E6, D5,WHITE_PAWN));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));


        // step through the Petrov Defense with b2
        b2.applyMove(new Move(WHITE_PAWN, E2, E4));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN, E7, E5));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT, G1, F3));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT, F3, E5,BLACK_PAWN));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN, D7, D6));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT, E5, F3));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT, F6, E4, WHITE_PAWN));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_PAWN, D2, D3));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT, E4, F6));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_PAWN, D3, D4));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN, D6, D5));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));


        // Positions would be equal at this point, except for move history and fifty counter
        assertNotEquals(b1, b2);
        assertTrue(b1.equalExceptMoveHistory(b2, false));
        assertFalse(b1.equalExceptMoveHistory(b2, true));

        // by adding a pawn move we should be equal except move history and number of moves
        b1.applyMove(new Move(WHITE_PAWN, G2, G3));
        b2.applyMove(new Move(WHITE_PAWN, G2, G3));

        assertNotEquals(b1, b2);
        assertFalse(b1.equalExceptMoveHistory(b2, true));
        assertTrue(b1.equalExceptMoveHistory(b2, false));
        assertNotEquals(b1.hashCode(), b2.hashCode());

        assertEquals(b1.hashCodeWithoutMoveHistory(false), b2.hashCodeWithoutMoveHistory(false));
        assertNotEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // hash codes should be equal at beginning, move 1, move 7 and end only.
        for (int i=0;i<hashCodes1.size();i++) {
            int hc1 = hashCodes1.get(i);
            if (i==0) {
                assertEquals((int) hashCodes2.get(0), hc1);
                assertFalse(hashCodes2.subList(1, hashCodes2.size()).contains(hc1));
            } else if (i==7) {
                assertEquals(hc1, (int) hashCodes2.get(11));
                assertFalse(hashCodes2.subList(0, hashCodes2.size()-1).contains(hc1));
            } else {
                assertFalse(hashCodes2.contains(hc1));
            }
        }
    }

    @Test
    /*
     * Should be able to obtain an equal position using the Queen's Gambit (d4,d5,c4,e6,Nc3,Nf6) and
     * the English Opening (c4,Nf6,Nc3,e6,d4,d5).
     */
    public void testHash2() {
        List<Integer> hashCodes1 = new ArrayList<>();
        List<Integer> hashCodes2 = new ArrayList<>();

        Board b1 = Board.INSTANCE;
        b1.resetBoard();
        Board b2 = b1.deepCopy();

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // Go through Queen's Gambit with b1
        b1.applyMove(new Move(WHITE_PAWN, D2, D4));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_PAWN, D7, D5));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(WHITE_PAWN, C2, C4));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_PAWN, E7, E6));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(WHITE_KNIGHT, B1, C3));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));

        // Step through English Opening with b2
        b2.applyMove(new Move(WHITE_PAWN, C2, C4));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_KNIGHT, G8, F6));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(WHITE_KNIGHT, B1, C3));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_PAWN, E7, E6));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(WHITE_PAWN, D2, D4));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_PAWN, D7, D5));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));

        // Positions would be equal at this point, except for move history, fifty counter and ep square
        assertNotEquals(b1, b2);
        assertFalse(b1.equalExceptMoveHistory(b2, false));

        // by adding a pawn move we should be equal except move history
        b1.applyMove(new Move(WHITE_PAWN, G2, G3));
        b2.applyMove(new Move(WHITE_PAWN, G2, G3));
        assertNotEquals(b1, b2);
        assertTrue(b1.equalExceptMoveHistory(b2, true));
        assertNotEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // hash codes should be equal at beginning and end only.  Neither were
        // saved in list so lists should contain completely different codes
        for (int hc1 : hashCodes1) {
            assertFalse(hashCodes2.contains(hc1));
        }
    }

    @Test
    public void testEqualityBeforeAndAfterCastle() {
        Board b1 = Board.INSTANCE;
        b1.setPos("4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(BLACK_KING, E8, G8,true);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
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
        Board b1 = Board.INSTANCE;
        b1.setPos("rnbqkbnr/pp1ppppp/8/2p5/3P4/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN, D4, C5, BLACK_PAWN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
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
        Board b1 = Board.INSTANCE;
        b1.setPos("rnbqkbnr/pp1ppppp/8/2pP4/8/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        Move m = new Move(WHITE_PAWN, D5, C6, BLACK_PAWN,true);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
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
        Board b1 = Board.INSTANCE;
        b1.setPos("8/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN, A7, A8,null,WHITE_QUEEN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
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
        Board b1 = Board.INSTANCE;
        b1.setPos("1n6/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN, A7, B8, BLACK_KNIGHT, WHITE_QUEEN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
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
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());

        MoveParser mp = new MoveParser();
        b.applyMove(mp.parseMove("e4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("e5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("d4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("exd4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("c4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("dxc3", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nf3", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("cxb2", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bc4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        Undo u = b.applyMove(mp.parseMove("bxc1=q", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(u);
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("bxa1=n", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("O-O", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("b5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bxb5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nc6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nc3", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("d5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bd2", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bh3", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Qxa1", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Qd7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Ng5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("O-O-O", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("g2xh3", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kb7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h4", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Be7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nxh7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rxh7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h5", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rh6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kh1", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rg6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rg1", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kxg1", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nf6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h7", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("a6", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h8=r", b));
        assertEquals(Zobrist.calculateBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
    }

    @Test
    public void testPawnKey() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        MoveParser mp = new MoveParser();
        List<Undo> undos = new ArrayList<>();
        undos.add(b.applyMove(mp.parseMove("e4", b)));
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        undos.add(b.applyMove(mp.parseMove("e5", b)));
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        undos.add(b.applyMove(mp.parseMove("d4", b)));
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        undos.add(b.applyMove(mp.parseMove("exd4", b)));
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        undos.add(b.applyMove(mp.parseMove("c4", b)));
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        undos.add(b.applyMove(mp.parseMove("dxc3", b))); // ep
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());

        b.undoMove(undos.get(undos.size()-1)); // dxc3
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(undos.get(undos.size()-2)); // c4
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(undos.get(undos.size()-3)); // exd4
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(undos.get(undos.size()-4)); // d4
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(undos.get(undos.size()-5)); // e5
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(undos.get(undos.size()-6)); // e4
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
    }

    @Test
    public void testPawnKeyPromotion() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("7k/P7/K7/8/8/8/8/8 w - - 0 1");
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=Q", b);
        List<Move> legalMoves = MoveGen.genLegalMoves(b);
        assertTrue(legalMoves.contains(m));
        Undo u = b.applyMove(m);
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
        b.undoMove(u);
        assertEquals(Zobrist.calculatePawnKey(b), b.getPawnKey());
    }

    @Test
    public void testGetNumPieces_initialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(8, b.getNumPieces(WHITE_PAWN));
        assertEquals(8, b.getNumPieces(BLACK_PAWN));
        assertEquals(1, b.getNumPieces(WHITE_QUEEN));
        assertEquals(1, b.getNumPieces(BLACK_QUEEN));
        assertEquals(2, b.getNumPieces(WHITE_ROOK));
        assertEquals(2, b.getNumPieces(BLACK_ROOK));
        assertEquals(2, b.getNumPieces(WHITE_KNIGHT));
        assertEquals(2, b.getNumPieces(BLACK_KNIGHT));
        assertEquals(2, b.getNumPieces(WHITE_BISHOP));
        assertEquals(2, b.getNumPieces(BLACK_BISHOP));
    }

    @Test
    public void testGetNumPieces_pos1() {
        Board b = Board.INSTANCE;
        b.setPos("7k/br6/8/8/8/8/Q7/7K w - -");

        assertEquals(0, b.getNumPieces(WHITE_PAWN));
        assertEquals(0, b.getNumPieces(BLACK_PAWN));
        assertEquals(1, b.getNumPieces(WHITE_QUEEN));
        assertEquals(0, b.getNumPieces(BLACK_QUEEN));
        assertEquals(0, b.getNumPieces(WHITE_ROOK));
        assertEquals(1, b.getNumPieces(BLACK_ROOK));
        assertEquals(0, b.getNumPieces(WHITE_KNIGHT));
        assertEquals(0, b.getNumPieces(BLACK_KNIGHT));
        assertEquals(0, b.getNumPieces(WHITE_BISHOP));
        assertEquals(1, b.getNumPieces(BLACK_BISHOP));
    }

    @Test
    public void testPieceCountsPromotion() throws Exception {
        Board b = Board.INSTANCE;
        b.setPos("7k/P7/8/8/8/8/8/7K w - -");

        assertEquals(1, b.getNumPieces(WHITE_PAWN));
        assertEquals(0, b.getNumPieces(WHITE_QUEEN));

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=q", b);

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertTrue(moves.contains(m));
        Undo u = b.applyMove(m);

        assertEquals(0, b.getNumPieces(WHITE_PAWN));
        assertEquals(1, b.getNumPieces(WHITE_QUEEN));

        b.undoMove(u);
        assertEquals(1, b.getNumPieces(WHITE_PAWN));
        assertEquals(0, b.getNumPieces(WHITE_QUEEN));
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
        Board b = Board.INSTANCE;
        b.setPos(fen);

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove(mv, Board.INSTANCE);
        b.applyMove(m);

        assertEquals(inCheck, BoardUtils.isPlayerInCheck(b));
    }
}
