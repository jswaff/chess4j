package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.exceptions.GameRecordNotFoundException;
import com.jamesswafford.chess4j.exceptions.UncheckedSqlException;
import com.jamesswafford.chess4j.io.PGNResult;
import com.jamesswafford.chess4j.utils.GameResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            stmt.execute("create table tuner_pos(fen text UNIQUE, outcome integer, " +
                    "eval_depth integer, eval_score real)");
            stmt.execute("create index idx_tuner_pos_fen on tuner_pos(fen)");
            stmt.close();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
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
                String sql = "insert into tuner_pos(fen, outcome) values (? ,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, fen);
                ps.setInt(2, outcome);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new UncheckedSqlException(e);
            }
        }
    }

    @Override
    public void updateGameDepthAndScore(String fen, int evalDepth, float evalScore) {
        GameRecord gameRecord = getGameRecord(fen);
        if (evalDepth >= gameRecord.getEvalDepth()) {

            String qry = "update tuner_pos set eval_depth=?, eval_score=? where fen = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(qry);
                ps.setInt(1, evalDepth);
                ps.setFloat(2, evalScore);
                ps.setString(3, fen);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new UncheckedSqlException(e);
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
            throw new UncheckedSqlException(e);
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
            throw new UncheckedSqlException(e);
        }

        return cnt;
    }

    @Override
    public GameRecord getGameRecord(String fen) {

        String sql = "select fen, outcome, eval_depth, eval_score from tuner_pos where fen = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Board board = new Board(fen);
                return GameRecord.builder()
                        .fen(fen)
                        .gameResult(mapOutcomeToGameResult(rs.getInt("outcome"), board.getPlayerToMove()))
                        .evalDepth(rs.getInt("eval_depth"))
                        .evalScore(rs.getFloat("eval_score"))
                        .build();
            } else {
                throw new GameRecordNotFoundException("Game record not found for fen " + fen);
            }
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    @Override
    public List<GameRecord> getGameRecords() {
        List<GameRecord> gameRecords = new ArrayList<>();

        String sql = "select fen, outcome, eval_depth, eval_score from tuner_pos ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fen = rs.getString("fen");
                Board board = new Board(fen);
                GameRecord gameRecord = GameRecord.builder()
                        .fen(fen)
                        .gameResult(mapOutcomeToGameResult(rs.getInt("outcome"), board.getPlayerToMove()))
                        .evalDepth(rs.getInt("eval_depth"))
                        .evalScore(rs.getFloat("eval_score"))
                        .build();
                gameRecords.add(gameRecord);
            }
            ps.close();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }

        return gameRecords;
    }

    private GameResult mapOutcomeToGameResult(int outcome, Color ptm) {
        GameResult gameResult;
        if (outcome==-1) {
            gameResult = ptm.isBlack() ? GameResult.WIN : GameResult.LOSS;
        } else if (outcome==0) {
            gameResult = GameResult.DRAW;
        } else if (outcome==1) {
            gameResult = ptm.isWhite() ? GameResult.WIN : GameResult.LOSS;
        } else {
            throw new IllegalStateException("Outcome is invalid: " + outcome);
        }
        return gameResult;
    }
}
