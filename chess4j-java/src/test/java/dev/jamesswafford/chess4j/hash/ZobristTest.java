package dev.jamesswafford.chess4j.hash;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.CastlingRights;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.io.MoveParser;
import dev.jamesswafford.chess4j.pieces.*;
import org.junit.Test;

import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;
import dev.jamesswafford.chess4j.pieces.Piece;

import static org.junit.Assert.*;


public class ZobristTest {

    @Test
    public void hammingDistances_noDistance() {
        long key=Zobrist.getPlayerKey(Color.WHITE);
        String strKey=DecimalToBinaryString.longToBinary(key);
        Hamming h = new Hamming(strKey, strKey);
        int hd=h.getHammingDistance();
        assertEquals(0, hd);
    }

    @Test
    public void hammingDistances_maxDistance() {
        String key1="0000000000000000000000000000000000000000000000000000000000000000";
        String key2="1111111111111111111111111111111111111111111111111111111111111111";
        assertEquals(64, key1.length());
        assertEquals(64, key2.length());
        Hamming h = new Hamming(key1, key2);
        int hd=h.getHammingDistance();
        assertEquals(64, hd);
    }

    @Test
    public void hammingDistances_meanDistance() {
        List<String> keys = getStringKeys();
        // should have 12x64 for pieces + 2 for colors + 64 for EP + 4 for CR
        assertEquals((12*64)+2+64+4, keys.size());

        List<Integer> hammingDistances = new ArrayList<>();
        for (int i=0; i<keys.size(); i++) {
            String key = keys.get(i);
            for (int j = i+1; j<keys.size(); j++) {
                String key2 = keys.get(j);
                int hd = new Hamming(key, key2).getHammingDistance();
                hammingDistances.add(hd);
            }
        }

        // should have sum of 1 to numKeys-1 hamming distances to test
        int numHDs = ((keys.size()-1) * keys.size()) / 2;
        assertEquals(numHDs, hammingDistances.size());

        double[] hds = new double[hammingDistances.size()];
        for (int i=0; i<hammingDistances.size(); i++) {
            hds[i] = Double.valueOf(hammingDistances.get(i));
        }
        double minHD = StdStats.min(hds);
        double meanHD = StdStats.mean(hds);
        double stdDevHD = StdStats.stddev(hds);

        assertTrue(minHD >= 3);
        assertTrue(meanHD >= 31.0 && meanHD <= 33.0);
        assertTrue(stdDevHD > 3.0);
    }

    @Test
    public void hammingDistances_InGame() throws ParseException, IllegalMoveException {
        Board board = new Board();

        List<String> keys = new ArrayList<>();
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("Nf3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nf6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("c4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("g6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nc3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bg7", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("d4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("O-O", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bf4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("d5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qb3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("dxc4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qxc4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("c6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("e4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nbd7", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Rd1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nb6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qc5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bg4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bg5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Na4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qa3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxc3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("bxc3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxe4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bxe7", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qb6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bc4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxc3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bc5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Rfe8+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kf1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Be6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bxb6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bxc4+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kg1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ne2+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kf1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxd4+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kg1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ne2+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kf1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nc3+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kg1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("axb6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qb4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ra4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qxb6", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxd1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("h3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Rxa2", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kh2", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxf2", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Re1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Rxe1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qd8+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bf8", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nxe1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bd5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nf3", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ne4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Qb8", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("b5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("h4", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("h5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ne5", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kg7", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kg1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bc5+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kf1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ng3+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ke1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bb4+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kd1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Bb3+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kc1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Ne2+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kb1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Nc3+", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Kc1", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));
        board.applyMove(mp.parseMove("Rc2#", board));
        keys.add(DecimalToBinaryString.longToBinary(Zobrist.calculateBoardKey(board)));

        assertEquals(83, keys.size());

        List<Integer> hammingDistances = new ArrayList<>();
        for (int i=0; i<keys.size(); i++) {
            String key = keys.get(i);
            for (int j=i+1; j<keys.size(); j++) {
                String key2 = keys.get(j);
                int hd = new Hamming(key, key2).getHammingDistance();
                hammingDistances.add(hd);
            }
        }

        // should have sum of 1 to numKeys-1 hamming distances to test
        int numHDs = ((keys.size()-1) * keys.size()) / 2;
        assertEquals(numHDs, hammingDistances.size());

        double[] hds = new double[hammingDistances.size()];
        for (int i=0; i<hammingDistances.size(); i++) {
            hds[i] = Double.valueOf(hammingDistances.get(i));
        }
        double minHD = StdStats.min(hds);
        double meanHD = StdStats.mean(hds);
        double stdDevHD = StdStats.stddev(hds);

        assertTrue(minHD >= 3);
        assertTrue(meanHD >= 31.0 && meanHD <= 33.0);
        assertTrue(stdDevHD > 3.0);
    }

    private List<String> getStringKeys() {
        List<String> skeys = new ArrayList<>();
        List<Long> ikeys = getKeys();
        for (Long ikey : ikeys) {
            skeys.add(DecimalToBinaryString.longToBinary(ikey));
        }
        return skeys;
    }

    private List<Long> getKeys() {
        List<Long> keys = new ArrayList<>();

        // add colors
        keys.add(Zobrist.getPlayerKey(Color.WHITE));
        keys.add(Zobrist.getPlayerKey(Color.BLACK));

        // add ep squares
        List<Square> sqs = Square.allSquares();
        for (Square sq : sqs) {
            keys.add(Zobrist.getEnPassantKey(sq));
        }

        // add castling rights
        Set<CastlingRights> crs = EnumSet.allOf(CastlingRights.class);
        for (CastlingRights cr : crs) {
            keys.add(Zobrist.getCastlingKey(cr));
        }

        // add piece/square keys
        addToKeys(keys, Pawn.BLACK_PAWN);
        addToKeys(keys, Pawn.WHITE_PAWN);
        addToKeys(keys, Rook.BLACK_ROOK);
        addToKeys(keys, Rook.WHITE_ROOK);
        addToKeys(keys, Knight.BLACK_KNIGHT);
        addToKeys(keys, Knight.WHITE_KNIGHT);
        addToKeys(keys, Bishop.BLACK_BISHOP);
        addToKeys(keys, Bishop.WHITE_BISHOP);
        addToKeys(keys, Queen.BLACK_QUEEN);
        addToKeys(keys, Queen.WHITE_QUEEN);
        addToKeys(keys, King.BLACK_KING);
        addToKeys(keys, King.WHITE_KING);

        return keys;
    }

    private void addToKeys(List<Long> keys,Piece p) {
        List<Square> sqs = Square.allSquares();
        for (Square sq : sqs) {
            keys.add(Zobrist.getPieceKey(sq, p));
        }
    }

    @Test
    // the idea here is to progress through a series of moves, and for each one set up an equivalent board using
    // FEN notation, and make sure we compute the same zobrist keys.  Also keep track of a set of these
    // keys as we go an make sure they are all unique.
    public void testGetBoardKey() {
        Board b = new Board();
        Board b2 = b.deepCopy();

        Set<Long> keys = new HashSet<>();

        Move m = new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4);
        b.applyMove(m);
        b2.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        long key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        keys.add(key);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C7, Square.C5));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        b2.setPos("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Queen.BLACK_QUEEN, Square.D8, Square.A5));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Bishop.WHITE_BISHOP, Square.F1, Square.E2));
        b2.setPos("rnb1kbnr/pp1ppppp/8/q1p5/4P3/5N2/PPPPBPPP/RNBQK2R b KQkq - 3 3");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Queen.BLACK_QUEEN, Square.A5, Square.D2, Bishop.WHITE_BISHOP));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPqBPPP/RNBQK2R w KQkq - 0 4");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.D2, Queen.BLACK_QUEEN));
        b2.setPos("rnb1kbnr/pp1ppppp/8/2p5/4P3/5N2/PPPNBPPP/R1BQK2R b KQkq - 0 4");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C5, Square.C4));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQK2R w KQkq - 0 5");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(King.WHITE_KING, Square.E1, Square.G1,true));
        b2.setPos("rnb1kbnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 b kq - 0 5");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(King.BLACK_KING, Square.E8, Square.D8));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/2p1P3/5N2/PPPNBPPP/R1BQ1RK1 w - - 1 6");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.B2, Square.B4));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/1Pp1P3/5N2/P1PNBPPP/R1BQ1RK1 b - b3 0 6");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);
        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C4, Square.B3, Pawn.BLACK_PAWN,true));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQ1RK1 w - - 0 7");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Rook.WHITE_ROOK, Square.F1, Square.E1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/1p3N2/P1PNBPPP/R1BQR1K1 b - - 1 7");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.B3, Square.B2));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR1K1 w - - 0 8");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(King.WHITE_KING, Square.G1, Square.H1));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/PpPNBPPP/R1BQR2K b - - 1 8");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.B2, Square.A1, Rook.WHITE_ROOK, Knight.BLACK_KNIGHT));
        b2.setPos("rnbk1bnr/pp1ppppp/8/8/4P3/5N2/P1PNBPPP/n1BQR2K w - - 0 9");
        key = Zobrist.calculateBoardKey(b);
        assertEquals(Zobrist.calculateBoardKey(b2), key);
        assertFalse(keys.contains(key));
        keys.add(key);
    }

    @Test
    /*
     * Should be able to obtain an equal position using the French Defense and Petrov Defense
     */
    public void testGetBoardKey2() {
        List<Long> keys1 = new ArrayList<>();
        List<Long> keys2 = new ArrayList<>();

        Board b1 = new Board();
        Board b2 = b1.deepCopy();

        assertEquals(Zobrist.calculateBoardKey(b1), Zobrist.calculateBoardKey(b2));

        // step through French Defense with b1
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E6));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.E6, Square.D5, Pawn.WHITE_PAWN));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        keys1.add(Zobrist.calculateBoardKey(b1));


        // step through the Petrov Defense with b2
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E5));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.G1, Square.F3));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.F3, Square.E5, Pawn.BLACK_PAWN));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D6));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.E5, Square.F3));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.F6, Square.E4, Pawn.WHITE_PAWN));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D3));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.E4, Square.F6));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.D3, Square.D4));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.D6, Square.D5));
        keys2.add(Zobrist.calculateBoardKey(b2));


        // Positions would be equal at this point, except for move history and fifty counter
        assertNotEquals(b1, b2);
        assertEquals(Zobrist.calculateBoardKey(b1), Zobrist.calculateBoardKey(b2));

        // by adding a pawn move we should be equal except move history and number of moves
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));

        assertNotEquals(b1, b2);
        assertEquals(Zobrist.calculateBoardKey(b1), Zobrist.calculateBoardKey(b2));

        // hash codes should be equal at beginning, move 1, move 7 and end only.
        for (int i=0;i<keys1.size();i++) {
            long key1 = keys1.get(i);
            if (i==0) {
                assertEquals((long) keys2.get(i), key1);
                assertFalse(keys2.subList(1, keys2.size()).contains(key1));
            } else if (i==7) {
                assertEquals((long) keys2.get(11), key1);
                assertFalse(keys2.subList(0, keys2.size()-1).contains(key1));
            } else {
                assertFalse(keys2.contains(key1));
            }
        }
    }

    @Test
    /*
     * Should be able to obtain an equal position using the Queen's Gambit (d4,d5,c4,e6,Nc3,Nf6) and
     * the English Opening (c4,Nf6,Nc3,e6,d4,d5).
     */
    public void testGetBoardKey3() {
        List<Long> keys1 = new ArrayList<>();
        List<Long> keys2 = new ArrayList<>();

        Board b1 = new Board();
        b1.resetBoard();
        Board b2 = b1.deepCopy();

        assertEquals(b1.hashCode(), b2.hashCode());

        // Go through Queen's Gambit with b1
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.C2, Square.C4));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E6));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.C3));
        keys1.add(Zobrist.calculateBoardKey(b1));
        b1.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        keys1.add(Zobrist.calculateBoardKey(b1));

        // Step through English Opening with b2
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.C2, Square.C4));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Knight.WHITE_KNIGHT, Square.B1, Square.C3));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.E7, Square.E6));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.D2, Square.D4));
        keys2.add(Zobrist.calculateBoardKey(b2));
        b2.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        keys2.add(Zobrist.calculateBoardKey(b2));

        // Positions would be equal at this point, except for move history, fifty counter and ep square
        assertNotEquals(b1, b2);
        assertNotEquals(Zobrist.calculateBoardKey(b1), Zobrist.calculateBoardKey(b2));

        // by adding a pawn move we should be equal
        b1.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));
        b2.applyMove(new Move(Pawn.WHITE_PAWN, Square.G2, Square.G3));
        assertEquals(b1, b2);
        assertEquals(Zobrist.calculateBoardKey(b1), Zobrist.calculateBoardKey(b2));

        // keys should be equal at beginning and end only.  Neither were
        // saved in list so lists should contain completely different codes
        for (long key1 : keys1) {
            assertFalse(keys2.contains(key1));
        }
    }
}
