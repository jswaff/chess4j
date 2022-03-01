package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.io.PGNResult;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.utils.GameResult;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static com.jamesswafford.chess4j.io.FenBuilder.createFen;
import static org.junit.Assert.*;

public class SQLiteTunerDatasourceTest {

    private final static String smallPGN = "/pgn/small.pgn";
    private final static String tinyPGN = "/pgn/tiny.pgn";
    private final static String testDB = "tunertest.db";

    static SQLiteTunerDatasource tunerDatasource;
    static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + testDB);

        tunerDatasource = new SQLiteTunerDatasource(conn);
        tunerDatasource.initializeDatasource();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        conn.close();
        new File(testDB).delete();
    }

    @Before
    public void deleteData() throws Exception {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("delete from tuner_pos");
    }

    @Test
    public void addPositions() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        String fen = FenBuilder.createFen(board, false);
        tunerDatasource.insert(fen, PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());
        List<GameRecord> gameRecords = tunerDatasource.getGameRecords(false);
        assertEquals(1, gameRecords.size());
        assertEquals(fen, gameRecords.get(0).getFen());
        assertEquals(GameResult.WIN, gameRecords.get(0).getGameResult());
        assertFalse(gameRecords.get(0).getProcessed());
        assertEquals(Integer.valueOf(0), gameRecords.get(0).getEvalDepth());
        assertEquals(Float.valueOf(0), gameRecords.get(0).getEvalScore());

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        String fen2 = FenBuilder.createFen(board, false);
        tunerDatasource.insert(fen2, PGNResult.WHITE_WINS);
        assertEquals(2, tunerDatasource.getTotalPositionsCount());
        GameRecord gr2 = tunerDatasource.getGameRecords(true).stream()
                        .filter(gr -> fen2.equals(gr.getFen())).findFirst().get();
        assertEquals(GameResult.LOSS, gr2.getGameResult());

        assertEquals(2, tunerDatasource.getGameRecords(true).size());
    }

    @Test
    public void addPositionTwice() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        tunerDatasource.insert(FenBuilder.createFen(board, false), PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());

        tunerDatasource.insert(FenBuilder.createFen(board, false), PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());
    }

    @Test
    public void addPositionFromAdjurnedGame() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        try {
            tunerDatasource.insert(FenBuilder.createFen(board, false), PGNResult.ADJOURNED);
            fail();
        } catch (IllegalStateException e) { /* good */ }

        assertEquals(0, tunerDatasource.getTotalPositionsCount());
    }

    @Test
    public void updateWithScoreAndDepth() {
        Board board = new Board();
        String fen = FenBuilder.createFen(board, false);
        tunerDatasource.insert(fen, PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());

        assertEquals(0, tunerDatasource.getEvalDepth(fen));

        tunerDatasource.updateGameDepthAndScore(fen, 12, 9.30F);

        assertEquals(12, tunerDatasource.getEvalDepth(fen));
        float foundScore = tunerDatasource.getEvalScore(fen);
        assertTrue(foundScore > 9.29999);
        assertTrue(foundScore < 9.30001);
    }

    @Test
    public void addReallySmallPGN() {
        populateTunerDatasource(tinyPGN);
        assertEquals(75, tunerDatasource.getTotalPositionsCount()); // 85 - skip first 10

        Board board = new Board();

        MoveParser mp = new MoveParser();

        /*
         * 1.f4 e5 2.fxe5 d6 3.exd6 Bxd6 4.Nf3 g5 5.g3 g4 6.Nh4 Ne7 7.d4 {-2.65/14 0.12s} Ng6 8.Nxg6
            hxg6 9.Qd3 Nc6 10.c3 Bf5 11.e4 Qe7 12.Bg2 O-O-O 13.Be3 Rde8 14.Nd2 f6 15.
            O-O-O Bd7 16.Nc4 Kb8 17.e5 fxe5 18.dxe5 Nxe5 19.Qd4 b6 20.Nxe5 Qxe5 21.
            Qxe5 Rxe5 22.Bd4 Re2 23.Bxh8 Rxg2 24.Rd2 Rxd2 25.Kxd2 b5 26.Re1 a5 27.Bf6
            Bf8 28.c4 bxc4 29.Re4 Kb7 30.Kc3 Kb6 31.Kxc4 a4 32.b3 axb3 33.axb3 Bb5+
            34.Kc3 Bd7 35.b4 Kb5 36.Re5+ Kc6 37.Kc4 Kd6 38.Rd5+ Ke6 39.Rd4 Bd6 40.Bg5
            Be5 41.Rd2 Bc6 42.b5 Bf3 43.Bf4 1-0
         */

        // first ten moves skipped
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));

        board.applyMove(mp.parseMove("f4", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));

        board.applyMove(mp.parseMove("e5", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));

        board.applyMove(mp.parseMove("fxe5", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("d6", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("exd6", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("Bxd6", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("Nf3", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("g5", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("g3", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("g4", board));
        assertEquals(0, tunerDatasource.getFenCount(createFen(board, false)));

        // starting here we should find an entry
        board.applyMove(mp.parseMove("Nh4", board));
        assertEquals(1, tunerDatasource.getFenCount(createFen(board, false)));
        board.applyMove(mp.parseMove("Ne7", board));
        assertEquals(1, tunerDatasource.getFenCount(createFen(board, false)));

        board.applyMove(mp.parseMove("d4", board));
        String fen = createFen(board, false);
        assertEquals(1, tunerDatasource.getFenCount(fen));
        assertEquals(14, tunerDatasource.getEvalDepth(fen));
        float evalScore = tunerDatasource.getEvalScore(fen);
        assertTrue(evalScore < -2.64999);
        assertTrue(evalScore > -2.65001);
    }

    private void populateTunerDatasource(String pgn) {
        File pgnFile = new File(SQLiteTunerDatasourceTest.class.getResource(pgn).getFile());
        tunerDatasource.addFile(pgnFile);
    }

}
