package dev.jamesswafford.chess4j.book;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.io.MoveParser;

import static org.junit.Assert.*;

public class InMemoryBookTest {

    static InMemoryBook book;

    @BeforeClass
    public static void setUp() throws Exception {
        book = InMemoryBook.getInstance();
        File pgnFile = new File(InMemoryBookTest.class.getResource("/pgn/small.pgn").getFile());
        book.addToBook(pgnFile);
    }

    @Test
    public void movesFromInitialPos() {
        Board board = new Board();
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
    public void movesAfterF4() {
        Board board = new Board();
        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("f4", board));

        List<BookMove> bookMoves = book.getMoves(board);
        assertEquals(3, bookMoves.size());

        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("c5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("e5", board))));
        assertTrue(bookMoves.contains(new BookMove(mp.parseMove("d5", board),2)));
    }

    @Test
    public void testWeightedRandomByFrequency() {
        Board board = new Board();

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
            BookMove bookMove = book.getMoveWeightedRandomByFrequency(board).get();

            if (nc3.equals(bookMove.getMove())) {
                nc3Cnt++;
            } else if (g3.equals(bookMove.getMove())) {
                g3Cnt++;
            } else if (f4.equals(bookMove.getMove())) {
                f4Cnt++;
            } else if (nf3.equals(bookMove.getMove())) {
                nf3Cnt++;
            } else if (e4.equals(bookMove.getMove())) {
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
    public void testWeightedRandomByFrequency2() {
        Board board = new Board();

        MoveParser mp = new MoveParser();
        board.applyMove(mp.parseMove("f4", board));

        // we are going to call the function 1000 times from the opening.  expect to get
        // c5 approximately 2500 times, e5 2500, d5 5000

        int c5Cnt=0,e5Cnt=0,d5Cnt=0;
        Move c5 = mp.parseMove("c5", board);
        Move e5 = mp.parseMove("e5", board);
        Move d5 = mp.parseMove("d5", board);

        for (int i=0;i<10000;i++) {
            BookMove bookMove = book.getMoveWeightedRandomByFrequency(board).get();

            if (c5.equals(bookMove.getMove())) {
                c5Cnt++;
            } else if (e5.equals(bookMove.getMove())) {
                e5Cnt++;
            } else if (d5.equals(bookMove.getMove())) {
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
