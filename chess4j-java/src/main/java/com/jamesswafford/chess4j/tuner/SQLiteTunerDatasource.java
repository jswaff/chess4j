package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
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

    @Override
    public void initializeDatasource() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("create table tuner_pos(fen text UNIQUE, outcome integer, processed integer, " +
                    "eval_depth integer, eval_score real)");
            stmt.execute("create index idx_tuner_pos_fen on tuner_pos(fen)");
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(String fen, PGNResult pgnResult) {
        if (getFenCount(fen) == 0) {
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

    @Override
    public void update(String fen, int evalDepth, float evalScore) {
        int currentDepth = getEvalDepth(fen);
        if (evalDepth >= currentDepth) {
            String qry = "update tuner_pos set eval_depth=?, eval_score=? where fen = ?";

            try {
                PreparedStatement ps = conn.prepareStatement(qry);
                ps.setInt(1, evalDepth);
                ps.setFloat(2, evalScore);
                ps.setString(3, fen);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

    @Override
    public int getEvalDepth(String fen) {
        String sql = "select eval_depth from tuner_pos where fen = ?";

        int depth = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                depth = rs.getInt("eval_depth");
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return depth;
    }

    @Override
    public float getEvalScore(String fen) {
        String sql = "select eval_score from tuner_pos where fen = ?";

        float score = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                score = rs.getFloat("eval_score");
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return score;
    }

}
