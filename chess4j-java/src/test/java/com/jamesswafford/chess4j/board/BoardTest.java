package com.jamesswafford.chess4j.board;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.movegen.MoveGen;

import org.junit.Test;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.io.MoveParser;

import static junit.framework.Assert.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public class BoardTest {

    @Test
    public void reset() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        testResetBoard(b);
    }

    private void testResetBoard(Board b) {
        assertEquals(WHITE_ROOK,b.getPiece(Square.valueOf(FILE_A, RANK_1)));
        assertEquals(WHITE_KNIGHT,b.getPiece(Square.valueOf(FILE_B, RANK_1)));
        assertEquals(WHITE_BISHOP,b.getPiece(Square.valueOf(FILE_C, RANK_1)));
        assertEquals(WHITE_QUEEN,b.getPiece(Square.valueOf(FILE_D, RANK_1)));
        assertEquals(WHITE_KING,b.getPiece(Square.valueOf(FILE_E, RANK_1)));
        assertEquals(WHITE_BISHOP,b.getPiece(Square.valueOf(FILE_F, RANK_1)));
        assertEquals(WHITE_KNIGHT,b.getPiece(Square.valueOf(FILE_G, RANK_1)));
        assertEquals(WHITE_ROOK,b.getPiece(Square.valueOf(FILE_H, RANK_1)));
        for (Square sq : Square.rankSquares(RANK_2)) {
            assertEquals(WHITE_PAWN,b.getPiece(sq));
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
        assertEquals(BLACK_ROOK,b.getPiece(Square.valueOf(FILE_A, RANK_8)));
        assertEquals(BLACK_KNIGHT,b.getPiece(Square.valueOf(FILE_B, RANK_8)));
        assertEquals(BLACK_BISHOP,b.getPiece(Square.valueOf(FILE_C, RANK_8)));
        assertEquals(BLACK_QUEEN,b.getPiece(Square.valueOf(FILE_D, RANK_8)));
        assertEquals(BLACK_KING,b.getPiece(Square.valueOf(FILE_E, RANK_8)));
        assertEquals(BLACK_BISHOP,b.getPiece(Square.valueOf(FILE_F, RANK_8)));
        assertEquals(BLACK_KNIGHT,b.getPiece(Square.valueOf(FILE_G, RANK_8)));
        assertEquals(BLACK_ROOK,b.getPiece(Square.valueOf(FILE_H, RANK_8)));

        assertTrue(b.hasCastlingRight(WHITE_KINGSIDE));
        assertTrue(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertTrue(b.hasCastlingRight(BLACK_KINGSIDE));
        assertTrue(b.hasCastlingRight(BLACK_QUEENSIDE));

        assertEquals(Color.WHITE, b.getPlayerToMove());
        assertNull(b.getEPSquare());
        assertEquals(0, b.getFiftyCounter());
        assertEquals(0, b.getMoveCounter());

        assertEquals(Square.valueOf(FILE_E, RANK_1), b.getKingSquare(Color.WHITE));
        assertEquals(Square.valueOf(FILE_E, RANK_8), b.getKingSquare(Color.BLACK));
    }

    @Test
    public void kingSquares() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();
        assertEquals(Square.valueOf(FILE_E, RANK_1), b.getKingSquare(Color.WHITE));
        assertEquals(Square.valueOf(FILE_E, RANK_8), b.getKingSquare(Color.BLACK));

        FenParser.setPos(b, "rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        assertEquals(Square.valueOf(FILE_G, RANK_1), b.getKingSquare(Color.WHITE));
        assertEquals(Square.valueOf(FILE_E, RANK_8), b.getKingSquare(Color.BLACK));

        b.applyMove(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_D, RANK_8)));
        assertEquals(Square.valueOf(FILE_G, RANK_1), b.getKingSquare(Color.WHITE));
        assertEquals(Square.valueOf(FILE_D, RANK_8), b.getKingSquare(Color.BLACK));
    }

    @Test
    public void applyMoves1() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_4));
        b.applyMove(m);
        assertNull(b.getPiece(Square.valueOf(FILE_E, RANK_2)));
        assertEquals(WHITE_PAWN,b.getPiece(Square.valueOf(FILE_E, RANK_4)));
        assertEquals(Square.valueOf(FILE_E, RANK_3), b.getEPSquare());
        assertEquals(Color.BLACK, b.getPlayerToMove());
        assertEquals(1, b.getMoveCounter());
        assertEquals(0, b.getFiftyCounter());

        Move m2 = new Move(BLACK_KNIGHT,Square.valueOf(FILE_B, RANK_8), Square.valueOf(FILE_C, RANK_6));
        b.applyMove(m2);
        assertNull(b.getPiece(Square.valueOf(FILE_B, RANK_8)));
        assertTrue(b.isEmpty(Square.valueOf(FILE_B, RANK_8)));
        assertEquals(BLACK_KNIGHT,b.getPiece(Square.valueOf(FILE_C, RANK_6)));
        assertNull(b.getEPSquare());
        assertEquals(2, b.getMoveCounter());
        assertEquals(1, b.getFiftyCounter());

        b.undoLastMove();
        b.undoLastMove();
        testResetBoard(b);
    }

    @Test
    public void applyMoves2() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();

        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2), Square.valueOf(FILE_E, RANK_4));
        b.applyMove(m);
        FenParser.setPos(b2, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_C, RANK_7), Square.valueOf(FILE_C, RANK_5)));
        FenParser.setPos(b2, "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1), Square.valueOf(FILE_F, RANK_3)));
        FenParser.setPos(b2, "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_QUEEN,Square.valueOf(FILE_D, RANK_8), Square.valueOf(FILE_A, RANK_5)));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_BISHOP,Square.valueOf(FILE_F, RANK_1), Square.valueOf(FILE_E, RANK_2)));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPPBPPP/RNBQK2R b KQkq - 3 3");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_QUEEN,Square.valueOf(FILE_A, RANK_5), Square.valueOf(FILE_D, RANK_2),WHITE_BISHOP));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPqBPPP/RNBQK2R w KQkq - 0 4");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1), Square.valueOf(FILE_D, RANK_2),BLACK_QUEEN));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPNBPPP/R1BQK2R b KQkq - 0 4");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_C, RANK_5), Square.valueOf(FILE_C, RANK_4)));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQK2R w KQkq - 0 5");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KING,Square.valueOf(FILE_E, RANK_1), Square.valueOf(FILE_G, RANK_1),true));
        FenParser.setPos(b2, "rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8), Square.valueOf(FILE_D, RANK_8)));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 w - - 1 6");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_B, RANK_2), Square.valueOf(FILE_B, RANK_4)));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_C, RANK_4), Square.valueOf(FILE_B, RANK_3),WHITE_PAWN,true));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQ1RK1 w - - 0 7");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_ROOK,Square.valueOf(FILE_F, RANK_1), Square.valueOf(FILE_E, RANK_1)));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQR1K1 b - - 1 7");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_B, RANK_3), Square.valueOf(FILE_B, RANK_2)));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR1K1 w - - 0 8");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(WHITE_KING,Square.valueOf(FILE_G, RANK_1), Square.valueOf(FILE_H, RANK_1)));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR2K b - - 1 8");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

        b.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_B, RANK_2),
                Square.valueOf(FILE_A, RANK_1),
                WHITE_ROOK,
                BLACK_KNIGHT));
        FenParser.setPos(b2, "rnbk1bnr/pp1ppppp/8/8/4P3/5N2/P1PNBPPP/n1BQR2K w - - 0 9");
        assertTrue(b.equalExceptMoveHistory(b2,true));
        assertTrue(b2.equalExceptMoveHistory(b,true));

    }

    @Test
    public void swapPlayer() {
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

        assertEquals(b, b2);

        b.swapPlayer();
        assertFalse(b.equals(b2));
        b.swapPlayer();
        assertEquals(b, b2);
    }

    @Test
    public void deepCopy2() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        b.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4)));
        Board b2 = b.deepCopy();
        assertEquals(b, b2);

        b.undoLastMove();
        b2.undoLastMove();
        assertTrue(b.equalExceptMoveHistory(b2, false));
        assertEquals(b, b2);
    }

    @Test
    public void deepCopy3() {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();
        b.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4)));
        b.undoLastMove();
        assertEquals(b, b2);
    }

    @Test
    public void flipVertical() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();
        Board b2 = b.deepCopy();

        b.flipVertical();
        b.flipVertical();
        assertTrue(b.equals(b2));

        FenParser.setPos(b, "7r/R6p/2K4P/5k1P/2p4n/5p2/8/8 w - - 0 1");
        FenParser.setPos(b2, "8/8/5P2/2P4N/5K1p/2k4p/r6P/7R b - - 0 1");
        b.flipVertical();
        // move counter doesn't need to be part of this test
        b.setMoveCounter(b2.getMoveCounter());
        assertTrue(b.equals(b2));

        // test EP
        FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        FenParser.setPos(b2, "rnbqkbnr/pppp1ppp/8/4p3/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1");

        b.flipVertical();
        b.setMoveCounter(b2.getMoveCounter());
        assertTrue(b.equals(b2));

        // test castling
        FenParser.setPos(b, "4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        FenParser.setPos(b2, "r3k3/8/8/8/8/8/8/4K2R w qK - 0 1");
        b.flipVertical();
        b.setMoveCounter(b2.getMoveCounter());
        assertTrue(b.equals(b2));
    }

    @Test
    /*
     * Should be able to obtain an equal position using the French Defense and Petrov Defense
     */
    public void testHash() throws Exception {
        List<Integer> hashCodes1 = new ArrayList<Integer>();
        List<Integer> hashCodes2 = new ArrayList<Integer>();

        Board b1 = Board.INSTANCE;
        b1.resetBoard();
        Board b2 = b1.deepCopy();

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // step through French Defense with b1
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_E, RANK_7),Square.valueOf(FILE_E, RANK_6)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_4)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_5)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_D, RANK_5),BLACK_PAWN));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_E, RANK_6),Square.valueOf(FILE_D, RANK_5),WHITE_PAWN));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));
        b1.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_G, RANK_8),Square.valueOf(FILE_F, RANK_6)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(false));


        // step through the Petrov Defense with b2
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_E, RANK_2),Square.valueOf(FILE_E, RANK_4)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_E, RANK_7),Square.valueOf(FILE_E, RANK_5)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_G, RANK_1),Square.valueOf(FILE_F, RANK_3)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_G, RANK_8),Square.valueOf(FILE_F, RANK_6)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_F, RANK_3),Square.valueOf(FILE_E, RANK_5),BLACK_PAWN));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_6)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_E, RANK_5),Square.valueOf(FILE_F, RANK_3)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_F, RANK_6),Square.valueOf(FILE_E, RANK_4),WHITE_PAWN));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_3)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_E, RANK_4),Square.valueOf(FILE_F, RANK_6)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_3),Square.valueOf(FILE_D, RANK_4)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));
        b2.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_6),Square.valueOf(FILE_D, RANK_5)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(false));


        // Positions would be equal at this point, except for move history and fifty counter
        assertFalse(b1.equals(b2));
        assertTrue(b1.equalExceptMoveHistory(b2, false));
        assertFalse(b1.equalExceptMoveHistory(b2, true));

        // by adding a pawn move we should be equal except move history and number of moves
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3)));
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3)));

        assertFalse(b1.equals(b2));
        assertFalse(b1.equalExceptMoveHistory(b2, true));
        assertTrue(b1.equalExceptMoveHistory(b2, false));
        assertFalse(b1.hashCode()==b2.hashCode());

        assertTrue(b1.hashCodeWithoutMoveHistory(false)==b2.hashCodeWithoutMoveHistory(false));
        assertFalse(b1.hashCodeWithoutMoveHistory(true)==b2.hashCodeWithoutMoveHistory(true));

        // hash codes should be equal at beginning, move 1, move 7 and end only.
        for (int i=0;i<hashCodes1.size();i++) {
            int hc1 = hashCodes1.get(i);
            if (i==0) {
                assertTrue(hashCodes2.get(0).equals(hc1));
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
    public void testHash2() throws Exception {
        List<Integer> hashCodes1 = new ArrayList<Integer>();
        List<Integer> hashCodes2 = new ArrayList<Integer>();

        Board b1 = Board.INSTANCE;
        b1.resetBoard();
        Board b2 = b1.deepCopy();

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1.hashCodeWithoutMoveHistory(true), b2.hashCodeWithoutMoveHistory(true));

        // Go through Queen's Gambit with b1
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_4)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_5)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_4)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_E, RANK_7),Square.valueOf(FILE_E, RANK_6)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_C, RANK_3)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));
        b1.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_G, RANK_8),Square.valueOf(FILE_F, RANK_6)));
        hashCodes1.add(b1.hashCodeWithoutMoveHistory(true));

        // Step through English Opening with b2
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_C, RANK_2),Square.valueOf(FILE_C, RANK_4)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_KNIGHT,Square.valueOf(FILE_G, RANK_8),Square.valueOf(FILE_F, RANK_6)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(WHITE_KNIGHT,Square.valueOf(FILE_B, RANK_1),Square.valueOf(FILE_C, RANK_3)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_E, RANK_7),Square.valueOf(FILE_E, RANK_6)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_2),Square.valueOf(FILE_D, RANK_4)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));
        b2.applyMove(new Move(BLACK_PAWN,Square.valueOf(FILE_D, RANK_7),Square.valueOf(FILE_D, RANK_5)));
        hashCodes2.add(b2.hashCodeWithoutMoveHistory(true));

        // Positions would be equal at this point, except for move history, fifty counter and ep square
        assertFalse(b1.equals(b2));
        assertFalse(b1.equalExceptMoveHistory(b2, false));

        // by adding a pawn move we should be equal except move history
        b1.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3)));
        b2.applyMove(new Move(WHITE_PAWN,Square.valueOf(FILE_G, RANK_2),Square.valueOf(FILE_G, RANK_3)));
        assertFalse(b1.equals(b2));
        assertTrue(b1.equalExceptMoveHistory(b2, true));
        assertFalse(b1.hashCode()==b2.hashCode());
        assertTrue(b1.hashCodeWithoutMoveHistory(true)==b2.hashCodeWithoutMoveHistory(true));

        // hash codes should be equal at beginning and end only.  Neither were
        // saved in list so lists should contain completely different codes
        for (int hc1 : hashCodes1) {
            assertFalse(hashCodes2.contains(hc1));
        }

    }

    @Test
    public void equalityBeforeAndAfterCastle() throws ParseException {
        Board b1 = Board.INSTANCE;
        FenParser.setPos(b1, "4k2r/8/8/8/8/8/8/R3K3 b Qk - 0 1");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(BLACK_KING,Square.valueOf(FILE_E, RANK_8),Square.valueOf(FILE_G,RANK_8),true);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        b1.applyMove(m);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode()==b2.hashCode());

        b1.undoLastMove();
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equalityBeforeAndAfterCapture() throws ParseException {
        Board b1 = Board.INSTANCE;
        FenParser.setPos(b1, "rnbqkbnr/pp1ppppp/8/2p5/3P4/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_4),Square.valueOf(FILE_C,RANK_5),BLACK_PAWN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        b1.applyMove(m);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode()==b2.hashCode());

        b1.undoLastMove();
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equalityBeforeAndAfterEPCapture() throws ParseException {
        Board b1 = Board.INSTANCE;
        FenParser.setPos(b1, "rnbqkbnr/pp1ppppp/8/2pP4/8/8/PPP1PPPP/RNBQKBNR w KQkq c6 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_D, RANK_5),Square.valueOf(FILE_C,RANK_6),BLACK_PAWN,true);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        b1.applyMove(m);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode()==b2.hashCode());

        b1.undoLastMove();
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equalityBeforeAndAfterPromotion() throws ParseException {
        Board b1 = Board.INSTANCE;
        FenParser.setPos(b1, "8/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_A,RANK_8),null,WHITE_QUEEN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        b1.applyMove(m);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode()==b2.hashCode());

        b1.undoLastMove();
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void equalityBeforeAndAfterCapturingPromotion() throws ParseException {
        Board b1 = Board.INSTANCE;
        FenParser.setPos(b1, "1n6/PK6/8/8/8/8/k7/8 w - - 0 2");
        Board b2 = b1.deepCopy();

        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());

        Move m = new Move(WHITE_PAWN,Square.valueOf(FILE_A, RANK_7),Square.valueOf(FILE_B,RANK_8),BLACK_KNIGHT,WHITE_QUEEN);
        List<Move> legalMoves = MoveGen.genLegalMoves(b1);
        assertTrue(legalMoves.contains(m));
        b1.applyMove(m);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode()==b2.hashCode());

        b1.undoLastMove();
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void zobristKey() throws ParseException, IllegalMoveException {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());

        MoveParser mp = new MoveParser();
        b.applyMove(mp.parseMove("e4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("e5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("d4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("exd4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("c4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("dxc3", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nf3", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("cxb2", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bc4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("bxc1=q", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove();
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("bxa1=n", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("O-O", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("b5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bxb5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nc6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nc3", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("d5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bd2", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Bh3", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Qxa1", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Qd7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Ng5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("O-O-O", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("g2xh3", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kb7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h4", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Be7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nxh7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rxh7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h5", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rh6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kh1", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rg6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Rg1", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Kxg1", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("Nf6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h7", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("a6", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("h8=r", b));
        assertEquals(Zobrist.getBoardKey(b), b.getZobristKey());
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
    }

    @Test
    public void pawnKey() throws Exception {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        MoveParser mp = new MoveParser();
        b.applyMove(mp.parseMove("e4", b));
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("e5", b));
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("d4", b));
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("exd4", b));
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("c4", b));
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.applyMove(mp.parseMove("dxc3", b)); // ep
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());

        b.undoLastMove(); // dxc3
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove(); // c4
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove(); // exd4
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove(); // d4
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove(); // e5
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove(); // e4
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
    }

    @Test
    public void pawnKeyPromotion() throws Exception {
        Board b = Board.INSTANCE;
        FenParser.setPos(b, "7k/P7/K7/8/8/8/8/8 w - - 0 1");
        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=Q", b);
        List<Move> legalMoves = MoveGen.genLegalMoves(b);
        assertTrue(legalMoves.contains(m));
        b.applyMove(m);
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
        b.undoLastMove();
        assertEquals(Zobrist.getPawnKey(b), b.getPawnKey());
    }

    @Test
    public void numPawnsInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

        assertEquals(8, b.getNumPieces(WHITE_PAWN));

        assertEquals(8, b.getNumPieces(BLACK_PAWN));
    }

    @Test
    public void numPawns() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "7k/pp6/8/8/8/8/7P/7K w - - ");

        assertEquals(1, b.getNumPieces(WHITE_PAWN));

        assertEquals(2, b.getNumPieces(BLACK_PAWN));
    }

    @Test
    public void numNonPawnsInitialPos() {
        Board b = Board.INSTANCE;
        b.resetBoard();

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
    public void numNonPawns() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "7k/br6/8/8/8/8/Q7/7K w - -");

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
    public void pieceCountsPromotion() throws Exception {
        Board b = Board.INSTANCE;

        FenParser.setPos(b, "7k/P7/8/8/8/8/8/7K w - -");

        assertEquals(1, b.getNumPieces(WHITE_PAWN));
        assertEquals(0, b.getNumPieces(WHITE_QUEEN));

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove("a8=q", b);

        List<Move> moves = MoveGen.genLegalMoves(b);
        assertTrue(moves.contains(m));
        b.applyMove(m);

        assertEquals(0, b.getNumPieces(WHITE_PAWN));
        assertEquals(1, b.getNumPieces(WHITE_QUEEN));

        b.undoLastMove();
        assertEquals(1, b.getNumPieces(WHITE_PAWN));
        assertEquals(0, b.getNumPieces(WHITE_QUEEN));
    }

    // these taken from Arasan
    @Test
    public void playerInCheck() throws Exception {
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
        FenParser.setPos(b, fen);

        MoveParser mp = new MoveParser();
        Move m = mp.parseMove(mv, Board.INSTANCE);
        b.applyMove(m);

        assertEquals(inCheck, b.isPlayerInCheck());
    }
}
