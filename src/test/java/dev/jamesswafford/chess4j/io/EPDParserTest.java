package dev.jamesswafford.chess4j.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.exceptions.ParseException;

import static org.junit.Assert.*;

import static dev.jamesswafford.chess4j.pieces.Pawn.*;
import static dev.jamesswafford.chess4j.pieces.Rook.*;
import static dev.jamesswafford.chess4j.pieces.King.*;
import static dev.jamesswafford.chess4j.board.CastlingRights.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;

public class EPDParserTest {

    private final static String zuriEpd = "/samplefen.epd";
    private final static String etherealEpd = "/sample_ethereal_fen.epd";

    Board board = new Board();

    @Test
    public void toGameRecord_EtherealFormat() throws IOException {
        File epdFile = new File(EPDParserTest.class.getResource(etherealEpd).getFile());
        List<FENRecord> fenRecords = EPDParser.load(epdFile);

        // verify a few samples
        /*
        8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - - 6 70 [1.0] 308
        2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - - 4 34 [0.5] -217
        3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - - 0 23 [0.0] -36
         */

        assertEquals(1, fenRecords.stream().filter(gr -> "8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - -".equals(gr.getFen())).count());
        assertEquals(1, fenRecords.stream().filter(gr -> "2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - -".equals(gr.getFen())).count());
        assertEquals(1, fenRecords.stream().filter(gr -> "3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - -".equals(gr.getFen())).count());

        assertEquals(100, fenRecords.size());

        FENRecord g1 = fenRecords.stream()
                .filter(gameRecord -> "8/5p2/3BpP2/2K1Pk2/7p/3N1n1P/8/8 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g1.getResult());

        FENRecord g2 = fenRecords.stream()
                .filter(gameRecord -> "2k2r2/p7/1pp1Rn1p/5Pp1/P7/1P6/2K4P/5R2 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.DRAW, g2.getResult());

        FENRecord g3 = fenRecords.stream()
                .filter(gameRecord -> "3r1rk1/4qp1p/1p1pp1p1/p1pPb3/2P1b3/PPB1P2P/3QBPP1/2RR2K1 w - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.BLACK_WINS, g3.getResult());
    }

    @Test
    public void toGameRecords_ZuriFormat() throws IOException {
        File epdFile = new File(EPDParserTest.class.getResource(zuriEpd).getFile());
        List<FENRecord> fenRecords = EPDParser.load(epdFile);

        // verify a few samples
        /*
        r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - - c9 "1-0";
        8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - - c9 "1/2-1/2";
        2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - - c9 "1-0";
        4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - - c9 "0-1";
         */

        assertEquals(1, fenRecords.stream().filter(gr -> "r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - -".equals(gr.getFen())).count());
        assertEquals(1, fenRecords.stream().filter(gr -> "8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - -".equals(gr.getFen())).count());
        assertEquals(1, fenRecords.stream().filter(gr -> "2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - -".equals(gr.getFen())).count());
        assertEquals(1, fenRecords.stream().filter(gr -> "4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - -".equals(gr.getFen())).count());

        assertEquals(100, fenRecords.size());

        FENRecord g1 = fenRecords.stream()
                .filter(gameRecord -> "r5k1/1N3pp1/1ppb3p/3p4/1p1P4/P2P2PP/4QP2/R5K1 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g1.getResult());

        FENRecord g2 = fenRecords.stream()
                .filter(gameRecord -> "8/8/8/3p4/1P1P2k1/2NP4/7p/2b4K b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.DRAW, g2.getResult());

        FENRecord g3 = fenRecords.stream()
                .filter(gameRecord -> "2r3k1/2b2pp1/1pp4p/3p4/1P1P4/2NQ2PP/5PK1/1R6 b - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.WHITE_WINS, g3.getResult());

        FENRecord g4 = fenRecords.stream()
                .filter(gameRecord -> "4r1k1/1rpb1pp1/1p5p/3P4/p1PBn1P1/3n3P/R1N2P1K/1R6 w - -".equals(gameRecord.getFen()))
                .findFirst()
                .get();
        assertEquals(PGNResult.BLACK_WINS, g4.getResult());
    }

    @Test
    public void setPosTest1() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "7k/p7/1R5K/6r1/6p1/6P1/8/8 w - - bm Rb7; id \"WAC.006\";");

        assertEquals(BLACK_KING, b.getPiece(H8));
        assertEquals(BLACK_ROOK, b.getPiece(G5));
        assertEquals(WHITE_PAWN, b.getPiece(G3));
        assertEquals(Color.WHITE, b.getPlayerToMove());
        assertFalse(b.hasCastlingRight(BLACK_KINGSIDE));
        assertFalse(b.hasCastlingRight(BLACK_QUEENSIDE));
        assertFalse(b.hasCastlingRight(WHITE_KINGSIDE));
        assertFalse(b.hasCastlingRight(WHITE_QUEENSIDE));
        assertEquals(0, b.getMoveCounter());
        assertNull(b.getEpSquare());

        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals("Rb7", op1.getEpdOperands().get(0));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals("WAC.006", op2.getEpdOperands().get(0));
    }

    @Test
    public void setPosTest2() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "6k1/2p2pp1/1p3n2/6rp/3P3q/P3PQ1P/6PN/5R1K w - - bm Qa8+; id \"position 0117\";");
        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals("Qa8+", op1.getEpdOperands().get(0));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals("position 0117", op2.getEpdOperands().get(0));
    }

    @Test
    public void setPosTest3() throws ParseException {
        board.resetBoard();
        Board b = board.deepCopy();

        List<EPDOperation> ops = EPDParser.setPos(b, "r1bqk2r/ppp1nppp/4p3/n5N1/2BPp3/P1P5/2P2PPP/R1BQK2R w KQkq - bm Ba2 Nxf7; id \"WAC.022\";");
        assertEquals(2, ops.size());

        EPDOperation op1 = ops.get(0);
        assertEquals("bm", op1.getEpdOpcode());
        assertEquals(2, op1.getEpdOperands().size());
        assertEquals("Ba2", op1.getEpdOperands().get(0));
        assertEquals("Nxf7", op1.getEpdOperands().get(1));

        EPDOperation op2 = ops.get(1);
        assertEquals("id", op2.getEpdOpcode());
        assertEquals(1, op2.getEpdOperands().size());
        assertEquals("WAC.022", op2.getEpdOperands().get(0));
    }

}
