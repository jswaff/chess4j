package com.jamesswafford.chess4j.book;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import com.jamesswafford.chess4j.utils.GameResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jamesswafford.chess4j.utils.GameResult.*;

public class SQLiteBook implements OpeningBook {

    private static final Logger LOGGER = LogManager.getLogger(SQLiteBook.class);

    private final Connection conn;

    public SQLiteBook(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addIndexes() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("create index idx_book_moves_key on book_moves(key)");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void addToBook(Board board, Move move) {
        try {
            int n = getMoveCount(board,move);
            if (n==0) {
                insert(board,move);
            } else {
                update(board,move,n+1,0,0,0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void dropIndexes() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("drop index idx_book_moves_key");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<BookMove> getMoves(Board board) {

        List<BookMove> bookMoves = new ArrayList<>();

        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(board);

        String qry = "select fromsq,tosq,frequency,wins,losses,draws from book_moves where key=?";
        try {
            PreparedStatement ps = conn.prepareStatement(qry);
            ps.setLong(1, board.getZobristKey());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int fromsq = rs.getInt("fromsq");
                int tosq = rs.getInt("tosq");
                int freq = rs.getInt("frequency");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int draws = rs.getInt("draws");

                for (Move legalMove : legalMoves) {
                    if (legalMove.from().value()==fromsq && legalMove.to().value()==tosq) {
                        bookMoves.add(new BookMove(legalMove,freq,wins,losses,draws));
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return bookMoves;
    }

    @Override
    public long getTotalMoveCount() {
        long cnt = 0;
        String sql = "select count(*) cnt from book_moves";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getLong("cnt");
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        return cnt;
    }

    @Override
    public void initializeBook() {
        try {
            createTables();
            writeZobristKeys();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void learn(List<Move> moves, Color engineColor, GameResult gameResult) {

        if (!Arrays.asList(WIN, LOSS, DRAW).contains(gameResult)) {
            return;
        }

        Board board = new Board();

        try {
            for (Move move : moves) {
                if (board.getPlayerToMove().equals(engineColor)) {
                    learn(move, board, gameResult);
                }
                board.applyMove(move);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void learn(Move move, Board board, GameResult gameResult) throws SQLException {
        List<BookMove> bookMoves = getMoves(board);

        for (BookMove bookMove : bookMoves) {
            if (bookMove.getMove().equals(move)) {
                update(board, move, bookMove.getFrequency(),
                        bookMove.getWins() + (WIN.equals(gameResult) ? 1 : 0),
                        bookMove.getLosses() + (LOSS.equals(gameResult) ? 1 : 0),
                        bookMove.getDraws() + (DRAW.equals(gameResult) ? 1 : 0)
                        );
            }
        }
    }

    public void loadZobristKeys() throws SQLException {
        List<Long> keys = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select key from zobrist_keys order by id");
        while (rs.next()) {
            keys.add(rs.getLong("key"));
        }
        stmt.close();

        Zobrist.setKeys(keys);
    }

    void writeZobristKeys() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("delete from zobrist_keys");
        stmt.close();

        PreparedStatement ps = conn.prepareStatement("insert into zobrist_keys (key) values (?)");

        List<Long> zobristKeys = Zobrist.getAllKeys();

        for (Long zobristKey : zobristKeys) {
            ps.setLong(1, zobristKey);
            ps.executeUpdate();
        }
        ps.close();
    }

    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("create table book_moves (key int not null,fromsq int not null,tosq int not null,frequency int default 1,wins int,losses int,draws int)");
        stmt.execute("create index idx_book_moves_key on book_moves(key)");
        stmt.execute("create table zobrist_keys (id integer primary key autoincrement,key int not null)");
        stmt.close();
    }

    private int getMoveCount(Board board, Move move) throws SQLException {
        int freq = 0;
        String sql = "select frequency from book_moves where key=? and fromsq=? and tosq=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, board.getZobristKey());
        ps.setInt(2, move.from().value());
        ps.setInt(3, move.to().value());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            freq = rs.getInt("frequency");
        }
        ps.close();

        return freq;
    }

    private void insert(Board board,Move move) throws SQLException {
        String sql = "insert into book_moves (key,fromsq,tosq,frequency) values (?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, board.getZobristKey());
        ps.setInt(2, move.from().value());
        ps.setInt(3, move.to().value());
        ps.setInt(4, 1);
        ps.executeUpdate();
        ps.close();
    }

    private void update(Board board,Move move,int frequency,int wins,int losses,int draws) throws SQLException {
        String sql = "update book_moves set frequency=?,wins=?,losses=?,draws=? where key=? and fromsq=? and tosq=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, frequency);
        ps.setInt(2, wins);
        ps.setInt(3, losses);
        ps.setInt(4, draws);
        ps.setLong(5, board.getZobristKey());
        ps.setInt(6, move.from().value());
        ps.setInt(7, move.to().value());
        ps.executeUpdate();
        ps.close();
    }

    public static SQLiteBook openOrInitialize(String bookPath) throws Exception {
        LOGGER.debug("# initializing book: " + bookPath);

        File bookFile = new File(bookPath);
        boolean initBook = !bookFile.exists();

        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + bookPath);
        SQLiteBook sqlOpeningBook = new SQLiteBook(conn);

        if (initBook) {
            LOGGER.info("# could not find " + bookPath + ", creating...");
            sqlOpeningBook.initializeBook();
            LOGGER.info("# ... finished.");
        } else {
            sqlOpeningBook.loadZobristKeys();
        }

        Globals.setOpeningBook(sqlOpeningBook);

        LOGGER.info("# book initialization complete. " +
                sqlOpeningBook.getTotalMoveCount() + " moves in book file.");

        return sqlOpeningBook;
    }

}
