package com.jamesswafford.chess4j.book;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.io.PGNGame;
import com.jamesswafford.chess4j.io.PGNIterator;

import static org.junit.Assert.*;

public class OpeningBookInMemoryImplTest {

    static OpeningBookInMemoryImpl book;

    @BeforeClass
    public static void setUp() throws Exception {
        book = OpeningBookInMemoryImpl.getInstance();
        FileInputStream fis = new FileInputStream(
                //new File("src/test/resources/pgn/small.pgn")
                new File(OpeningBookInMemoryImplTest.class.getResource("/pgn/small.pgn").getFile())
        );
        PGNIterator it = new PGNIterator(fis);

        PGNGame pgnGame;
        while ((pgnGame = it.next()) != null) {
            book.addToBook(pgnGame);
        }

        fis.close();
    }

    @Test
    public void movesFromInitialPos() throws Exception {
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
    }

    @Test
    public void movesAfterF4() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();
        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("f4", board));

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(3, bookMoves.size());

        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("c5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("e5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("d5", board),2)));
    }

    @Test
    public void testWeightedRandomByFrequency() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();

        // we are going to call the function 1000 times from the opening.  expect to get
        // Nc3 approximately 1000 times, g3 1000, f4 4000, Nf3 3000, e4 1000

        int nc3Cnt=0,g3Cnt=0,f4Cnt=0,nf3Cnt=0,e4Cnt=0;
        MoveParser mp = new MoveParser();
        Move nc3 = mp.parseMove("Nc3", board);
        Move g3 = mp.parseMove("g3", board);
        Move f4 = mp.parseMove("f4", board);
        Move nf3 = mp.parseMove("Nf3", board);
        Move e4 = mp.parseMove("e4", board);

        for (int i=0;i<10000;i++) {
            BookMove bm = book.getMoveWeightedRandomByFrequency(board);
            assertNotNull(bm);

            if (nc3.equals(bm.getMove())) {
                nc3Cnt++;
            } else if (g3.equals(bm.getMove())) {
                g3Cnt++;
            } else if (f4.equals(bm.getMove())) {
                f4Cnt++;
            } else if (nf3.equals(bm.getMove())) {
                nf3Cnt++;
            } else if (e4.equals(bm.getMove())) {
                e4Cnt++;
            }
        }

        // allow 10% tolerance from expected value
        assertTrue(900 <= nc3Cnt && nc3Cnt <= 1100);
        assertTrue(900 <= g3Cnt && g3Cnt <= 1100);
        assertTrue(3600 <= f4Cnt && f4Cnt <= 4400);
        assertTrue(2700 <= nf3Cnt && nf3Cnt <= 3300);
        assertTrue(900 <= e4Cnt && e4Cnt <= 1100);
    }

    @Test
    public void testWeightedRandomByFrequency2() throws Exception {
        Board board = Board.INSTANCE;
        board.resetBoard();

        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("f4", board));

        // we are going to call the function 1000 times from the opening.  expect to get
        // c5 approximately 2500 times, e5 2500, d5 5000

        int c5Cnt=0,e5Cnt=0,d5Cnt=0;
        Move c5 = mp.parseMove("c5", board);
        Move e5 = mp.parseMove("e5", board);
        Move d5 = mp.parseMove("d5", board);

        for (int i=0;i<10000;i++) {
            BookMove bm = book.getMoveWeightedRandomByFrequency(board);
            assertNotNull(bm);

            if (c5.equals(bm.getMove())) {
                c5Cnt++;
            } else if (e5.equals(bm.getMove())) {
                e5Cnt++;
            } else if (d5.equals(bm.getMove())) {
                d5Cnt++;
            }
        }

        // allow 10% tolerance from expected value
        assertTrue(2250 <= c5Cnt && c5Cnt <= 2750);
        assertTrue(2250 <= e5Cnt && e5Cnt <= 2750);
        assertTrue(4500 <= d5Cnt && d5Cnt <= 5500);
    }

    @Test
    public void testGetMoveCount() {
        assertEquals(125, book.getTotalMoveCount());
    }
}
