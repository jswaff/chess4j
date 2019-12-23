package com.jamesswafford.chess4j.book;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.io.PGNIterator;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.utils.GameResult;

public class OpeningBookSQLiteImplTest {

    private final static String smallPGN = "pgn/small.pgn";
    private final static String tinyPGN = "pgn/tiny.pgn";
    private final static String testDB = "test.db";

    static OpeningBookSQLiteImpl book;
    static Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + testDB);
        book = new OpeningBookSQLiteImpl(conn);
        book.initializeBook();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        conn.close();
        new java.io.File(testDB).delete();
    }

    @Before
    public void deleteData() throws Exception {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("delete from book_moves");
    }

    @Test
    public void addMove() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(0,bookMoves.size());

        MoveParser mp = new MoveParser();
        Move e2e4 = mp.parseMove("e2e4", board);
        book.addToBook(board, e2e4);

        bookMoves = book.getMoves(board);
        assertEquals(1,bookMoves.size());

        BookMove bookMove = bookMoves.get(0);
        assertEquals(e2e4,bookMove.getMove());
        assertEquals(1,bookMove.getFrequency());
    }

    @Test
    public void addMoveTwice() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(0,bookMoves.size());

        MoveParser mp = new MoveParser();
        Move d2d4 = mp.parseMove("d2d4", board);
        book.addToBook(board, d2d4);
        book.addToBook(board, d2d4);

        bookMoves = book.getMoves(board);
        assertEquals(1,bookMoves.size());

        BookMove bookMove = bookMoves.get(0);
        assertEquals(d2d4,bookMove.getMove());
        assertEquals(2,bookMove.getFrequency());
    }

    @Test
    public void addMultipleMoves() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(0,bookMoves.size());

        MoveParser mp = new MoveParser();
        Move d2d4 = mp.parseMove("d2d4", board);
        Move e2e4 = mp.parseMove("e2e4", board);
        Move g1f3 = mp.parseMove("g1f3", board);
        book.addToBook(board, d2d4);
        book.addToBook(board, d2d4);
        book.addToBook(board, e2e4);
        book.addToBook(board, g1f3);

        bookMoves = book.getMoves(board);
        assertEquals(3,bookMoves.size());

        assertTrue(bookMoves.contains(new BookMove(d2d4,2)));
        assertTrue(bookMoves.contains(new BookMove(e2e4,1)));
        assertTrue(bookMoves.contains(new BookMove(g1f3,1)));
    }

    @Test
    public void addIllegalMove() {
        Board board = Board.INSTANCE;
        board.resetBoard();

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(0,bookMoves.size());

        Move illegal = new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),
                Square.valueOf(File.FILE_E, Rank.RANK_5));
        book.addToBook(board, illegal);

        assertEquals(0,book.getMoves(board).size());
    }

    @Test
    public void addSmallPGN() throws Exception {
        populateBook(smallPGN);

        assertEquals(125, book.getTotalMoveCount()); // 850 for entire PGN

        Board board = Board.INSTANCE;
        board.resetBoard();
        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(5, bookMoves.size());

        MoveParser mp = new MoveParser();
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("Nc3", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("g3", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("f4", board),4)));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("Nf3", board),3)));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("e4", board))));

        // now apply a move
        board.applyMove(mp.parseMove("f4", board));
        bookMoves = book.getMoves(board);
        assertEquals(3, bookMoves.size());

        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("c5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("e5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("d5", board),2)));
    }

    @Test
    public void addReallySmallPGN() throws Exception {
        populateBook(tinyPGN);
        assertEquals(15, book.getTotalMoveCount()); // 85 for entire game

        Board board = Board.INSTANCE;
        board.resetBoard();

        MoveParser mp = new MoveParser();

        /*
         * 1.f4 e5 2.fxe5 d6 3.exd6 Bxd6 4.Nf3 g5 5.g3 g4 6.Nh4 Ne7 7.d4 Ng6 8.Nxg6
hxg6 9.Qd3 Nc6 10.c3 Bf5 11.e4 Qe7 12.Bg2 O-O-O 13.Be3 Rde8 14.Nd2 f6 15.
O-O-O Bd7 16.Nc4 Kb8 17.e5 fxe5 18.dxe5 Nxe5 19.Qd4 b6 20.Nxe5 Qxe5 21.
Qxe5 Rxe5 22.Bd4 Re2 23.Bxh8 Rxg2 24.Rd2 Rxd2 25.Kxd2 b5 26.Re1 a5 27.Bf6 
Bf8 28.c4 bxc4 29.Re4 Kb7 30.Kc3 Kb6 31.Kxc4 a4 32.b3 axb3 33.axb3 Bb5+ 
34.Kc3 Bd7 35.b4 Kb5 36.Re5+ Kc6 37.Kc4 Kd6 38.Rd5+ Ke6 39.Rd4 Bd6 40.Bg5 
Be5 41.Rd2 Bc6 42.b5 Bf3 43.Bf4 1-0
         */

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(mp.parseMove("f4", board),bookMoves.get(0).getMove());
        board.applyMove(bookMoves.get(0).getMove());

        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(mp.parseMove("e5", board),bookMoves.get(0).getMove());
        board.applyMove(bookMoves.get(0).getMove());

        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(mp.parseMove("fxe5", board),bookMoves.get(0).getMove());
        board.applyMove(bookMoves.get(0).getMove());

        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(mp.parseMove("d6", board),bookMoves.get(0).getMove());
        board.applyMove(bookMoves.get(0).getMove());

        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(mp.parseMove("exd6", board),bookMoves.get(0).getMove());
        board.applyMove(bookMoves.get(0).getMove());
    }

    @Test
    public void writeAndLoadZobristKeys() throws Exception {
        List<Long> keys1 = Zobrist.getAllKeys();

        book.writeZobristKeys();
        book.loadZobristKeys();

        List<Long> keys2 = Zobrist.getAllKeys();

        assertEquals(keys1,keys2);
    }

    @Test
    public void learnMultipleTimes() throws Exception {
        populateBook(tinyPGN);

        Board board = Board.INSTANCE;
        board.resetBoard();

        MoveParser mp = new MoveParser();
        Move f4 = mp.parseMove("f4", board);
        board.applyMove(f4);

        Move e5 = mp.parseMove("e5", board);
        board.applyMove(e5);

        List<Move> moves = Arrays.asList(f4,e5);
        book.learn(moves, Color.BLACK, GameResult.LOSS);
        book.learn(moves, Color.BLACK, GameResult.LOSS);
        book.learn(moves, Color.BLACK, GameResult.DRAW);

        board.resetBoard();
        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(f4, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(0, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());

        board.applyMove(f4);
        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(e5, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(0, bookMoves.get(0).getWins());
        assertEquals(2, bookMoves.get(0).getLosses());
        assertEquals(1, bookMoves.get(0).getDraws());
    }

    @Test
    public void learn() throws Exception {
        populateBook(tinyPGN);

        Board board = Board.INSTANCE;
        board.resetBoard();

        MoveParser mp = new MoveParser();
        Move f4 = mp.parseMove("f4", board);
        board.applyMove(f4);

        Move e5 = mp.parseMove("e5", board);
        board.applyMove(e5);

        Move fe5 = mp.parseMove("fxe5", board);
        board.applyMove(fe5);

        Move d5 = mp.parseMove("d5", board); // this one not in the book
        board.applyMove(d5);

        Move a3 = mp.parseMove("a3", board);
        board.applyMove(a3);

        List<Move> moves = Arrays.asList(f4,e5,fe5,d5,a3);

        book.learn(moves, Color.WHITE, GameResult.WIN);

        // now assert that f4,fxe5 have been learned as a win
        // ... and that the other moves have not been learned
        board.resetBoard();
        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(f4, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(1, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());

        board.applyMove(f4);
        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(e5, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(0, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());

        board.applyMove(e5);
        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        assertEquals(fe5, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(1, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());

        board.applyMove(fe5);
        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        Move d6 = mp.parseMove("d6", board);
        assertEquals(d6, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(0, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());

        board.applyMove(d6);
        bookMoves = book.getMoves(board);
        assertEquals(1, bookMoves.size());
        Move ed6 = mp.parseMove("exd6", board);
        assertEquals(ed6, bookMoves.get(0).getMove());
        assertEquals(1, bookMoves.get(0).getFrequency());
        assertEquals(0, bookMoves.get(0).getWins());
        assertEquals(0, bookMoves.get(0).getLosses());
        assertEquals(0, bookMoves.get(0).getDraws());
    }

    private void populateBook(String pgn) throws Exception {
        System.out.println("populating book using " + pgn);

        book.dropIndexes();
        long start = System.currentTimeMillis();

        ClassLoader classLoader = getClass().getClassLoader();
        java.io.File pgnFile = new java.io.File(classLoader.getResource(pgn).getFile());

        FileInputStream fis = new FileInputStream(pgnFile);
        PGNIterator it = new PGNIterator(fis);

        PGNGame pgnGame;
        while ((pgnGame = it.next()) != null) {
            book.addToBook(pgnGame);
        }

        fis.close();
        long end = System.currentTimeMillis();
        book.addIndexes();
        System.out.println("populated book in " + (end-start) + " ms");
    }


}
