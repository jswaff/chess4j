package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.io.PGNResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.*;

public class SQLiteTunerDatasource implements TunerDatasource {

    private static final Logger LOGGER = LogManager.getLogger(SQLiteTunerDatasource.class);

    private final Connection conn;

    public SQLiteTunerDatasource(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addToTunerDS(Board board, PGNResult pgnResult) {
        String fen = FenBuilder.createFen(board, false);
        LOGGER.info("adding " + fen);
        if (getFenCount(fen)==0) {
            insert(fen, pgnResult);
        }
    }

    @Override
    public void initializeDatasource() {
        try {
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getTotalPositionsCount() {
        long cnt = 0;
        String sql = "select count(*) cnt from tuner_pos";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getLong("cnt");
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cnt;
    }

    public static SQLiteTunerDatasource openOrInitialize(String tunerDSPath) throws Exception {
        LOGGER.debug("# initializing tuner datasource: " + tunerDSPath);

        File tunerDSFile = new File(tunerDSPath);
        boolean initDS = !tunerDSFile.exists();

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + tunerDSPath);
        SQLiteTunerDatasource sqLiteTunerDatasource = new SQLiteTunerDatasource(conn);

        if (initDS) {
            LOGGER.info("# could not find " + tunerDSPath + ", creating...");
            sqLiteTunerDatasource.initializeDatasource();
            LOGGER.info("# ... finished.");
        }

        Globals.setTunerDatasource(sqLiteTunerDatasource);

        LOGGER.info("# tuner datasource initialization complete. ");

        return sqLiteTunerDatasource;
    }



    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("create table tuner_pos(fen text, outcome integer, processed integer, eval_depth integer, eval_score integer)");
        stmt.execute("create index idx_tuner_pos_fen on tuner_pos(fen)");
        stmt.close();
    }

    @Override
    public long getFenCount(String fen) {
        long cnt = 0;
        String sql = "select count(*) cnt from tuner_pos where fen = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getLong("cnt");
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cnt;
    }

    private void insert(String fen, PGNResult pgnResult) {
        int outcome;
        if (PGNResult.WHITE_WINS.equals(pgnResult)) {
            outcome = 1;
        } else if (PGNResult.BLACK_WINS.equals(pgnResult)) {
            outcome = -1;
        } else if (PGNResult.DRAW.equals(pgnResult)) {
            outcome = 0;
        } else {
            throw new IllegalStateException("Illegal value for argument pgnResult " + pgnResult);
        }

        try {
            String sql = "insert into tuner_pos(fen, outcome, processed) values (? ,?, 0)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fen);
            ps.setInt(2, outcome);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
