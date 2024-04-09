package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FENBuilder;
import com.jamesswafford.chess4j.io.FENRecord;
import com.jamesswafford.chess4j.io.PGNResult;
import com.jamesswafford.chess4j.pieces.Pawn;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class SQLiteTunerDatasourceTest {

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
        String fen = FENBuilder.createFen(board, false);
        tunerDatasource.insert(fen, PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());
        List<FENRecord> fenRecords = tunerDatasource.getGameRecords(false);
        assertEquals(1, fenRecords.size());
        assertEquals(fen, fenRecords.get(0).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(0).getResult());
        assertNull(fenRecords.get(0).getEval());

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        String fen2 = FENBuilder.createFen(board, false);
        tunerDatasource.insert(fen2, PGNResult.WHITE_WINS);
        assertEquals(2, tunerDatasource.getTotalPositionsCount());
        FENRecord gr2 = tunerDatasource.getGameRecords(false).stream()
                        .filter(gr -> fen2.equals(gr.getFen())).findFirst().get();
        assertEquals(PGNResult.WHITE_WINS, gr2.getResult());
        assertNull(gr2.getEval());

        assertEquals(2, tunerDatasource.getGameRecords(false).size());
    }

    @Test
    public void addPositionTwice() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        tunerDatasource.insert(FENBuilder.createFen(board, false), PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());

        tunerDatasource.insert(FENBuilder.createFen(board, false), PGNResult.WHITE_WINS);
        assertEquals(1, tunerDatasource.getTotalPositionsCount());
    }

    @Test
    public void addPositionFromAdjurnedGame() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        try {
            tunerDatasource.insert(FENBuilder.createFen(board, false), PGNResult.ADJOURNED);
            fail();
        } catch (IllegalStateException e) { /* good */ }

        assertEquals(0, tunerDatasource.getTotalPositionsCount());
    }

    @Test
    public void setEval() {
        assertEquals(0, tunerDatasource.getTotalPositionsCount());

        Board board = new Board();
        String fen = FENBuilder.createFen(board, false);
        tunerDatasource.insert(fen, PGNResult.WHITE_WINS);
        List<FENRecord> fenRecords = tunerDatasource.getGameRecords(false);
        assertEquals(1L, tunerDatasource.getGameRecords(true).size());

        assertEquals(1, fenRecords.size());
        assertEquals(fen, fenRecords.get(0).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(0).getResult());
        assertNull(fenRecords.get(0).getEval());

        tunerDatasource.updateEval(fen, -325);
        fenRecords = tunerDatasource.getGameRecords(false);

        assertEquals(1, fenRecords.size());
        assertEquals(fen, fenRecords.get(0).getFen());
        assertEquals(PGNResult.WHITE_WINS, fenRecords.get(0).getResult());
        assertEquals(Integer.valueOf(-325), fenRecords.get(0).getEval());

        assertEquals(0L, tunerDatasource.getGameRecords(true).size());
    }

}
