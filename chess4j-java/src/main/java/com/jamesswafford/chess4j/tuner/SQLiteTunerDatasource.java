package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.exceptions.UncheckedSqlException;
import com.jamesswafford.chess4j.io.PGNResult;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteTunerDatasource implements TunerDatasource {

    private static final Logger LOGGER = LogManager.getLogger(SQLiteTunerDatasource.class);

    private final Connection conn;

    public SQLiteTunerDatasource(Connection conn) {
        this.conn = conn;
    }

    @SneakyThrows
    public static SQLiteTunerDatasource openOrInitialize(String tunerDSPath) {
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
            stmt.execute("create table tuner_pos(fen text UNIQUE, outcome integer, eval integer, eval_processed integer)");
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
                String sql = "insert into tuner_pos(fen, outcome, eval_processed) values (? ,?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, fen);
                ps.setInt(2, outcome);
                ps.setInt(3, 0);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                throw new UncheckedSqlException(e);
            }
        }
    }

    @Override
    public void updateEval(String fen, int eval) {
        try {
            String sql = "update tuner_pos set eval=?, eval_processed=1 where fen=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eval);
            ps.setString(2, fen);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
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
    public List<GameRecord> getGameRecords(boolean justUnprocessed) {
        List<GameRecord> gameRecords = new ArrayList<>();

        String sql = "select fen, outcome, eval, eval_processed from tuner_pos ";
        if (justUnprocessed) {
            sql += "where eval_processed = 0 or eval_processed is null ";
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GameRecord gameRecord = GameRecord.builder()
                        .fen(rs.getString("fen"))
                        .result(mapOutcomeToResult(rs.getInt("outcome")))
                        .eval(rs.getInt("eval_processed")==1 ? rs.getInt("eval") : null)
                        .build();
                gameRecords.add(gameRecord);
            }
            ps.close();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }

        return gameRecords;
    }

    @Override
    public void exportToCSV(String file) {

        BufferedWriter out = null;
        List<GameRecord> gameRecords = getGameRecords(false);

        try {
            FileWriter fstream = new FileWriter(file);
            out = new BufferedWriter(fstream);

            for (GameRecord gameRecord : gameRecords) {
                Board board = new Board(gameRecord.getFen());
                double[][] features = BoardToNetwork.transform(board);
                StringBuilder sample = new StringBuilder();
                for (double[] feature : features) {
                    sample.append((int)feature[0]);
                }
                sample.append(",").append(gameRecord.getEval()).append("\n");
                out.write(sample.toString());
                System.out.println(sample);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) { /* ignore */  }
            }
        }
    }

    private PGNResult mapOutcomeToResult(int outcome) {
        PGNResult result;
        if (outcome==-1) {
            result = PGNResult.BLACK_WINS;
        } else if (outcome==0) {
            result = PGNResult.DRAW;
        } else if (outcome==1) {
            result = PGNResult.WHITE_WINS;
        } else {
            throw new IllegalStateException("Outcome is invalid: " + outcome);
        }
        return result;
    }
}
