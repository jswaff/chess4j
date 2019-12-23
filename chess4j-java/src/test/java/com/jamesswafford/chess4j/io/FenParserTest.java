package com.jamesswafford.chess4j.io;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;

import static com.jamesswafford.chess4j.board.CastlingRights.*;

import static org.junit.Assert.*;

public class FenParserTest {

    Board board = Board.INSTANCE;

    @Test
    public void setPosTest1() throws Exception {
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        FenParser.setPos(board, fen);

        assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_3)));
        assertFalse(board.canCastle(BLACK_KINGSIDE));
        assertFalse(board.canCastle(BLACK_QUEENSIDE));
        assertFalse(board.canCastle(WHITE_KINGSIDE));
        assertFalse(board.canCastle(WHITE_QUEENSIDE));
        assertEquals(3,board.getMoveCounter());
        assertEquals(1, board.getFiftyCounter());
        assertEquals(null, board.getEPSquare());
    }

    @Test
    public void setPosTest2() throws Exception {
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -";
        FenParser.setPos(board, fen);

        assertEquals(Knight.WHITE_KNIGHT, board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_3)));
        assertFalse(board.canCastle(BLACK_KINGSIDE));
        assertFalse(board.canCastle(BLACK_QUEENSIDE));
        assertFalse(board.canCastle(WHITE_KINGSIDE));
        assertFalse(board.canCastle(WHITE_QUEENSIDE));
        assertEquals(1,board.getMoveCounter());
        assertEquals(0, board.getFiftyCounter());
        assertNull(board.getEPSquare());
    }

    @Test
    public void setPosTest3() throws Exception {
        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        FenParser.setPos(board, fen);

        assertEquals(Pawn.BLACK_PAWN, board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_5)));
        assertFalse(board.canCastle(BLACK_KINGSIDE));
        assertFalse(board.canCastle(BLACK_QUEENSIDE));
        assertFalse(board.canCastle(WHITE_KINGSIDE));
        assertFalse(board.canCastle(WHITE_QUEENSIDE));
        assertEquals(2,board.getMoveCounter());
        assertEquals(0, board.getFiftyCounter());
        assertEquals(Square.valueOf(File.FILE_C, Rank.RANK_6), board.getEPSquare());
    }

    @Test
    public void getFen_InitialPos() {

        Board b = Board.INSTANCE;
        b.resetBoard();

        String fen = FenParser.getFen(b, true);

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen);
    }

    @Test
    public void getFenTest1() throws Exception {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        FenParser.setPos(board, fen);

        assertEquals(fen, FenParser.getFen(board, true));
    }

    @Test
    public void getFenTest2() throws Exception {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -";
        FenParser.setPos(board, fen);

        assertEquals(fen, FenParser.getFen(board, false));
    }

    @Test
    public void getFenTest3() throws Exception {

        String fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        FenParser.setPos(board, fen);

        assertEquals(fen, FenParser.getFen(board, true));
    }

}
