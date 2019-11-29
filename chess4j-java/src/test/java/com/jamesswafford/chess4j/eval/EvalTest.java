package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Move;
import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.io.FenParser;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.utils.OrderedPair;


public class EvalTest {

    Board board = Board.INSTANCE;

    @Test
    public void testAlways5() {
        Assert.assertEquals(5, Eval.get5());
    }

    @Test
    public void evalPstEquality() {
        for (Square sq : Square.allSquares()) {
            Assert.assertEquals(Eval.KNIGHT_PST[sq.value()],
                    Eval.evalKnightPstNative(sq.value()));
        }
    }

    @Test
    public void evalEquality() {
        board.resetBoard();

        Assert.assertEquals(
                Eval.eval(board), Eval.evalNative(board));

        board.applyMove(new Move(Pawn.WHITE_PAWN, Square.valueOf(File.FILE_E, Rank.RANK_2),
                Square.valueOf(File.FILE_E, Rank.RANK_4)));

        Assert.assertEquals(
                Eval.eval(board), Eval.evalNative(board));
    }

    @Test
    public void testStartPosIs0() {
        board.resetBoard();
        int eval = Eval.eval(board);
        Assert.assertEquals(0, eval);
    }

    @Test
    public void testScore1() throws Exception {
        FenParser.setPos(board, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        int eval = Eval.eval(board);
        Assert.assertEquals(-(Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_4).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_2).value()]), eval);
    }

    @Test
    public void testScore2() throws Exception {
        FenParser.setPos(board, "rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        int eval = Eval.eval(board);
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_4).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_2).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_6).flipVertical().value()]
                +Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_7).flipVertical().value()]
                , eval);
    }

    @Test
    public void testSymmetry() throws Exception {
        testCaseSymmetry("7r/R6p/2K4P/5k1P/2p4n/5p2/8/8 w - - 0 1");
        testCaseSymmetry("8/k3Nr2/2rR4/1P1n4/6p1/1K6/8/6n1 w - - 0 1");

        // these positions from Arasan
        String[] positions = new String[] {
            "8/4K3/8/1NR5/8/4k1r1/8/8 w - -",
            "8/4K3/8/1N6/6p1/4k2p/8/8 w - -",
            "8/4K3/8/1r6/6B1/4k2N/8/8 w - -",
            "3b4/1n3n2/1pk3Np/p7/P4P1p/1P6/5BK1/3R4 b - -",
            "8/3r1ppk/8/P6P/3n4/2K5/R2B4/8 b - -",
            "1rb1r1k1/2q2pb1/pp1p4/2n1pPPQ/Pn1BP3/1NN4R/1PP4P/R5K1 b - -",
            "6k1/1b4p1/5p1p/pq3P2/1p1BP3/1P2QR1P/P1r3PK/8 w - -",
            "8/5pk1/7p/3p1R2/p1p3P1/2P2K1P/1P1r4/8 w - -",
            "6k1/p3pp2/6p1/7P/R7/b1q2P2/B1P1K2P/7R b - -",
            "r7/1b4k1/pp1np1p1/3pq1NN/7P/4P3/PP4P1/1Q3RK1 b - -",
            "4b3/2p4p/pp1bk3/2p3p1/2P5/PPB2PP1/7P/3K1N2 w - -",
            "r1bqr1k1/ppp2ppp/3p4/4n3/2PN4/P1Q1P3/1PB2PPP/R4RK1 b - -",
            "r4rk1/1ppqbppp/p2p1n2/8/1n1PP3/1Q3N2/PP1N1PPP/R1B1R1K1 b - -",
            "r6k/1p4bp/1p1n1pp1/1B6/8/P4NP1/1P3P1P/2R3K1 w - -",
            "r1b2r1k/pp3n1p/2p1p3/3Pnppq/3PP3/1P1N1PP1/P5BP/R1Q2RK1 w - -",
            "2kr3r/1bpnqp2/1p2p3/p2p3p/P1PPBPp1/2P1P1P1/2QN2P1/1R2K2R w K -",
            "8/1R6/3k4/2p5/2p1B3/5K2/8/8 w - -",
            "1BR2rk1/pP1nbpp1/B2P2p1/8/8/8/1P4P1/3n2K1 b - -",
            "r1b1k2r/1p1n1pp1/p6p/2p5/4Nb2/5NP1/PPP2P1P/1K1R1B1R b kq -",
            "r1b2rk1/1p1n1pp1/p6p/2p5/4Nb2/3R1NP1/PPP2P1P/1K1R1B2 b - -",
            "1kr5/1p1b2R1/p3p2Q/2bp3P/8/P1PB1P2/1P1K1P2/R6q b - -",
            "rb3rk1/1p1RRpp1/p6p/r1p5/4Nb2/5NP1/PPP2P1P/1K3B2 b - -",
            "5rk1/1pqn2pp/4pn2/p7/2P5/4PP2/1B2BP1P/3Q1RK1 w - -",
            "3k1q2/p3p1p1/1p1nQ3/3P4/P2P4/B2P4/6KP/8 b - -",
            "6k1/4R1P1/5P2/5K1p/7r/8/8/8 w - -",
            "1n1q1rk1/4ppbp/3p1np1/1PpP4/4P3/2N2N2/3B1PPP/Q3K2R b K -",
            "3q1rk1/4ppbp/1n1p1np1/1PpP4/2N1P3/5N2/3B1PPP/Q3K2R b K -",
            "3q1rk1/4ppbp/1n1p1np1/1P1P4/4P3/2p1BN2/2N2PPP/Q3K2R b K -",
            "N5r1/pQ6/3b1nkp/2q5/2Pp1p2/4nP2/PP1B2PP/1RR3K1 b - -",
            "8/2kn2q1/B1p2pP1/P1P2p1p/3P2bP/3P1B2/1K1P1Q2/8 b - -",
            "5nk1/3b1r2/2p1p3/1pPpP1qp/1P1Q4/6P1/4BN1P/R5K1 w - - 0 1"
        };

        for (String position : positions) {
            testCaseSymmetry(position);
        }
    }

    private void testCaseSymmetry(String fen) throws Exception {
        FenParser.setPos(board, fen);
        int eval = Eval.eval(board);
        board.flipVertical();
        int eval2 = Eval.eval(board);
        Assert.assertEquals(eval, eval2);
    }

    @Test
    public void testPieceVals() {
        Assert.assertEquals(900, Eval.getPieceValue(Queen.WHITE_QUEEN));
        Assert.assertEquals(900, Eval.getPieceValue(Queen.BLACK_QUEEN));

        Assert.assertEquals(500, Eval.getPieceValue(Rook.WHITE_ROOK));
        Assert.assertEquals(500, Eval.getPieceValue(Rook.BLACK_ROOK));

        Assert.assertEquals(320, Eval.getPieceValue(Bishop.WHITE_BISHOP));
        Assert.assertEquals(320, Eval.getPieceValue(Bishop.BLACK_BISHOP));

        Assert.assertEquals(300, Eval.getPieceValue(Knight.WHITE_KNIGHT));
        Assert.assertEquals(300, Eval.getPieceValue(Knight.BLACK_KNIGHT));

        Assert.assertEquals(100, Eval.getPieceValue(Pawn.WHITE_PAWN));
        Assert.assertEquals(100, Eval.getPieceValue(Pawn.BLACK_PAWN));
    }

    @Test
    public void testRookPST() throws Exception {
        FenParser.setPos(board, "6k1/3R4/8/8/8/8/8/3K4 w - - 0 1");
        int eval = Eval.eval(board);
        Assert.assertEquals(Eval.ROOK_VAL + Eval.ROOK_ON_7TH + Eval.ROOK_OPEN_FILE, eval);

        FenParser.setPos(board, "6k1/3RR3/8/8/8/8/8/3K4 w - - 0 1");
        eval = Eval.eval(board);
        Assert.assertEquals(Eval.ROOK_VAL*2 + Eval.ROOK_ON_7TH*2
                + Eval.CONNECTED_MAJORS_ON_7TH
                + Eval.ROOK_OPEN_FILE * 2, eval);

        FenParser.setPos(board, "6k1/3RRr2/8/8/8/8/8/3K4 w - - 0 1");
        eval = Eval.eval(board);
        // white has two rooks on open files and black one, so net 1 for white
        Assert.assertEquals(Eval.ROOK_VAL + Eval.ROOK_ON_7TH*2
                + Eval.CONNECTED_MAJORS_ON_7TH
                + Eval.ROOK_OPEN_FILE, eval);

        FenParser.setPos(board, "6k1/3RRr2/8/8/8/8/r7/3K4 w - - 0 1");
        eval = Eval.eval(board);
        Assert.assertEquals(Eval.ROOK_ON_7TH + Eval.CONNECTED_MAJORS_ON_7TH, eval);

        FenParser.setPos(board, "6k1/3RRr2/8/8/8/r7/8/3K4 w - - 0 1");
        eval = Eval.eval(board);
        // deduct for black for rook on A file
        Assert.assertEquals(Eval.ROOK_ON_7TH*2 + Eval.CONNECTED_MAJORS_ON_7TH
                - Eval.ROOK_PST[Square.valueOf(File.FILE_A, Rank.RANK_3).flipVertical().value()]
                , eval);

        FenParser.setPos(board, "6k1/8/8/8/8/8/qr6/7K b - - 0 1");
        Assert.assertEquals(Eval.QUEEN_VAL + Eval.ROOK_VAL
                + Eval.ROOK_ON_7TH * 2
                + Eval.CONNECTED_MAJORS_ON_7TH
                + Eval.ROOK_OPEN_FILE
                + Eval.QUEEN_PST[Square.valueOf(File.FILE_A, Rank.RANK_2).value()]
                - Eval.scale(Eval.KING_SAFETY_PAWN_FAR_AWAY
                + Eval.KING_SAFETY_PAWN_FAR_AWAY
                + Eval.KING_SAFETY_PAWN_FAR_AWAY/2,Eval.QUEEN_VAL+Eval.ROOK_VAL),
                Eval.eval(board));
    }

    @Test
    public void testRookOn7th() throws Exception {
        FenParser.setPos(board, "7k/8/8/8/8/8/r7/7K w - - 0 1");
        Assert.assertEquals(-Eval.ROOK_VAL, Eval.evalMaterial(board));

        Assert.assertEquals(-Eval.ROOK_VAL-Eval.ROOK_ON_7TH-Eval.ROOK_OPEN_FILE, Eval.eval(board));

        // move king out
        FenParser.setPos(board, "7k/8/8/8/8/7K/r7/8 w - - 0 1");
        Assert.assertEquals(-Eval.ROOK_VAL-Eval.ROOK_OPEN_FILE, Eval.eval(board));

        // flipped
        FenParser.setPos(board, "7k/8/8/8/8/7K/r7/8 b - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL+Eval.ROOK_OPEN_FILE, Eval.eval(board));
    }

    @Test
    public void testConnectedMajorsOn7th() throws Exception {
        FenParser.setPos(board, "7k/R2R4/8/8/8/8/8/7K w - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL*2 + Eval.ROOK_ON_7TH*2
                + Eval.CONNECTED_MAJORS_ON_7TH
                + Eval.ROOK_OPEN_FILE * 2,
                Eval.eval(board));

        FenParser.setPos(board, "7k/R2QR3/8/8/8/8/8/7K w - - 0 1");
        int expected = Eval.ROOK_VAL*2 + Eval.QUEEN_VAL
                + Eval.ROOK_ON_7TH*3
                + Eval.CONNECTED_MAJORS_ON_7TH*2
                + Eval.ROOK_OPEN_FILE*2
                - Eval.scale(Eval.KING_SAFETY_PAWN_FAR_AWAY    // F File
                + Eval.KING_SAFETY_PAWN_FAR_AWAY    // G File
                + (Eval.KING_SAFETY_PAWN_FAR_AWAY /2),Eval.ROOK_VAL*2+Eval.QUEEN_VAL); // H File
        Assert.assertEquals(expected, Eval.eval(board));

        // flip it
        FenParser.setPos(board, "7k/R2QR3/8/8/8/8/8/7K b - - 0 1");
        Assert.assertEquals(-expected, Eval.eval(board));
    }

    @Test
    public void testRookOnOpenFile() throws Exception {
        FenParser.setPos(board, "3r3k/8/8/8/8/8/8/7K b - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL + Eval.ROOK_OPEN_FILE, Eval.eval(board));

        FenParser.setPos(board, "3r3k/3r4/8/8/8/8/8/7K b - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL*2 + Eval.ROOK_OPEN_FILE*2, Eval.eval(board));

        FenParser.setPos(board, "3r3k/8/3p4/8/8/8/8/7K b - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL+Eval.PAWN_VAL
                +Eval.PASSED_PAWN
                +Eval.ISOLATED_PAWN
                +Eval.PAWN_PST[Square.valueOf(File.FILE_D, Rank.RANK_6).flipVertical().value()],
                Eval.eval(board));
    }

    @Test
    public void testRookHalfOpenFile() throws Exception {
        // friendly pawn, no bonus
        FenParser.setPos(board, "8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL+Eval.PAWN_VAL
                +Eval.PASSED_PAWN
                +Eval.ISOLATED_PAWN
                +Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_7).value()],
                Eval.eval(board));

        FenParser.setPos(board, "8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");
        Assert.assertEquals(Eval.ROOK_VAL-Eval.PAWN_VAL-Eval.PASSED_PAWN
                -Eval.ISOLATED_PAWN
                +Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_7).flipVertical().value()]
                +Eval.ROOK_HALF_OPEN_FILE,
                Eval.eval(board));
    }

    @Test
    public void testQueenPST() throws Exception {
        FenParser.setPos(board,"3kq3/8/8/8/8/8/8/3K4 b - - 0 1");
        Assert.assertEquals(Eval.QUEEN_VAL, Eval.evalMaterial(board));
        Assert.assertEquals(Eval.QUEEN_VAL
                + Eval.QUEEN_PST[Square.valueOf(File.FILE_E, Rank.RANK_8).flipVertical().value()],
                Eval.eval(board));
    }

    @Test
    public void testBishopPST() throws Exception {
        FenParser.setPos(board, "6k1/3B4/8/8/8/8/8/K7 w - - 0 1");
        int eval = Eval.eval(board);
        Assert.assertEquals(Eval.getPieceValue(Bishop.WHITE_BISHOP)
                + Eval.BISHOP_PST[Square.valueOf(File.FILE_D, Rank.RANK_7).value()], eval);

        FenParser.setPos(board, "6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        eval = Eval.eval(board);
        Assert.assertEquals(Eval.getPieceValue(Bishop.WHITE_BISHOP)
                + Eval.BISHOP_PST[Square.valueOf(File.FILE_D, Rank.RANK_5).value()], eval);
    }

    @Test
    public void testKingPST() throws Exception {
        FenParser.setPos(board, "rnbqkbnr/pppppppp/8/8/3P4/3BPN2/PPP2PPP/RNBQR1K1 w - - 0 1");

        Assert.assertEquals(0,Eval.evalMaterial(board));
        int score = Eval.eval(board);

        int tropismDelta =
                Eval.KNIGHT_TROPISM * Square.valueOf(File.FILE_F,Rank.RANK_3).distance(board.getKingSquare(Color.BLACK))
                - Eval.KNIGHT_TROPISM * Square.valueOf(File.FILE_G,Rank.RANK_1).distance(board.getKingSquare(Color.BLACK));

        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_D, Rank.RANK_4).value()]
                - Eval.PAWN_PST[Square.valueOf(File.FILE_D, Rank.RANK_2).value()]
                + Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_3).value()]
                - Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_2).value()]
                + Eval.BISHOP_PST[Square.valueOf(File.FILE_D, Rank.RANK_3).value()]
                - Eval.BISHOP_PST[Square.valueOf(File.FILE_F, Rank.RANK_1).value()]
                + Eval.KNIGHT_PST[Square.valueOf(File.FILE_F, Rank.RANK_3).value()]
                - Eval.KNIGHT_PST[Square.valueOf(File.FILE_G, Rank.RANK_1).value()]
                + Eval.ROOK_PST[Square.valueOf(File.FILE_E, Rank.RANK_1).value()]
                - Eval.ROOK_PST[Square.valueOf(File.FILE_H, Rank.RANK_1).value()]
                + Eval.KING_PST[Square.valueOf(File.FILE_G, Rank.RANK_1).value()]
                - Eval.KING_PST[Square.valueOf(File.FILE_E, Rank.RANK_1).value()]
                + tropismDelta
                ,score);
    }

    @Test
    public void testKingEndGamePST() throws Exception {
        FenParser.setPos(board, "8/8/8/4k3/8/8/8/RNB2K2 w - - 0 1");

        int expectedMaterial = Eval.ROOK_VAL + Eval.KNIGHT_VAL + Eval.BISHOP_VAL;
        Assert.assertEquals(expectedMaterial,Eval.evalMaterial(board));

        int expected = expectedMaterial
                + Eval.ROOK_OPEN_FILE
                + Eval.KNIGHT_PST[Square.valueOf(File.FILE_B, Rank.RANK_1).value()]
                + Eval.KNIGHT_TROPISM * Square.valueOf(File.FILE_B,Rank.RANK_1).distance(board.getKingSquare(Color.BLACK))
                + Eval.KING_ENDGAME_PST[Square.valueOf(File.FILE_F,Rank.RANK_1).value()]
                - Eval.KING_PST[Square.valueOf(File.FILE_E, Rank.RANK_5).value()]
                - Eval.scale(Eval.KING_SAFETY_MIDDLE_OPEN_FILE,Eval.ROOK_VAL+Eval.KNIGHT_VAL+Eval.BISHOP_VAL);
        Assert.assertEquals(expected, Eval.eval(board));

        // by removing a bishop both sides should be evaluated as in the endgame
        FenParser.setPos(board, "8/8/8/4k3/8/8/8/RN3K2 b - - 0 1");
        expected = -Eval.ROOK_VAL - Eval.KNIGHT_VAL
                - Eval.ROOK_OPEN_FILE
                - Eval.KNIGHT_PST[Square.valueOf(File.FILE_B, Rank.RANK_1).value()]
                - Eval.KNIGHT_TROPISM * Square.valueOf(File.FILE_B,Rank.RANK_1).distance(board.getKingSquare(Color.BLACK))
                - Eval.KING_ENDGAME_PST[Square.valueOf(File.FILE_F,Rank.RANK_1).value()]
                + Eval.KING_ENDGAME_PST[Square.valueOf(File.FILE_E, Rank.RANK_5).value()];
        Assert.assertEquals(expected, Eval.eval(board));
    }

    @Test
    public void testEvalMaterial() throws Exception {
        board.resetBoard();
        Assert.assertEquals(0, Eval.evalMaterial(board));

        FenParser.setPos(board, "6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        Assert.assertEquals(Eval.BISHOP_VAL, Eval.evalMaterial(board));

        FenParser.setPos(board, "6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");
        Assert.assertEquals(Eval.BISHOP_VAL-Eval.ROOK_VAL, Eval.evalMaterial(board));
    }

    @Test
    public void testMaterialScores() throws Exception {
        FenParser.setPos(board, "8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");
        OrderedPair<Integer,Integer> npScores = Eval.getNonPawnMaterialScore(board);
        Assert.assertTrue(Eval.QUEEN_VAL+Eval.KNIGHT_VAL == npScores.getE1());
        Assert.assertTrue(Eval.ROOK_VAL+Eval.BISHOP_VAL == npScores.getE2());

        OrderedPair<Integer,Integer> pScores = Eval.getPawnMaterialScore(board);
        Assert.assertTrue(0 == pScores.getE1());
        Assert.assertTrue(Eval.PAWN_VAL == pScores.getE2());
    }

    @Test
    public void testDoubledAndIsolatedPawns() throws Exception {
        FenParser.setPos(board, "k7/p1p3p1/3p3p/1P5P/1PP1P3/8/8/K7 b - - 0 1");

        Assert.assertEquals(0, Eval.evalMaterial(board));

        int score = Eval.eval(board);

        int expected =
                Eval.KING_ENDGAME_PST[Square.valueOf(File.FILE_A, Rank.RANK_8).flipVertical().value()]
                    + Eval.PAWN_PST[Square.valueOf(File.FILE_A, Rank.RANK_7).flipVertical().value()]
                    + Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_7).flipVertical().value()]
                    + Eval.PAWN_PST[Square.valueOf(File.FILE_D, Rank.RANK_6).flipVertical().value()]
                    + Eval.PAWN_PST[Square.valueOf(File.FILE_G, Rank.RANK_7).flipVertical().value()]
                    + Eval.PAWN_PST[Square.valueOf(File.FILE_H, Rank.RANK_6).flipVertical().value()]
                    + Eval.ISOLATED_PAWN // black pawn on A7
                    - Eval.KING_ENDGAME_PST[Square.valueOf(File.FILE_A, Rank.RANK_1).value()]
                    - Eval.PAWN_PST[Square.valueOf(File.FILE_B, Rank.RANK_5).value()]
                    - Eval.PAWN_PST[Square.valueOf(File.FILE_B, Rank.RANK_4).value()]
                    - Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_4).value()]
                    - Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_4).value()]
                    - Eval.PAWN_PST[Square.valueOf(File.FILE_H, Rank.RANK_5).value()]
                    - Eval.ISOLATED_PAWN * 2  // white pawns on E4 and H1
                    - Eval.DOUBLED_PAWN * 2 // white pawns on b4 and b5
                    ;

        Assert.assertEquals(expected, score);
    }

    @Test
    public void evalKingSafetyMiddleFiles() throws Exception {
        Board b = Board.INSTANCE;

        // initial position then e3 .. no penalty
        FenParser.setPos(b, "rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        int score1 = Eval.eval(b);
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_3).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_E, Rank.RANK_2).value()]
                , score1);

        //  open file for both, so still 0
        FenParser.setPos(b, "rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        Assert.assertEquals(0, Eval.eval(b));

        // remove both queens.  open e file.  put black king on d8
        FenParser.setPos(b, "rnbk1bnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNB1KBNR b KQ - 0 1");
        Assert.assertEquals(-Eval.scale(Eval.KING_SAFETY_MIDDLE_OPEN_FILE,Eval.ROOK_VAL*2+Eval.KNIGHT_VAL*2+Eval.BISHOP_VAL*2)
                +Eval.KING_PST[Square.valueOf(File.FILE_D, Rank.RANK_8).flipVertical().value()], Eval.eval(b));
    }

    @Test
    public void evalKingSafetyKingSide() throws Exception {
        Board b = Board.INSTANCE;

        // no pawns advanced
        FenParser.setPos(b, "rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w kq - 0 1");
        Assert.assertEquals(0, Eval.eval(b));

        // white pawn on f3
        FenParser.setPos(b, "rnbq1rk1/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQ1RK1 w kq - 0 1");
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_F, Rank.RANK_3).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_F, Rank.RANK_2).value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_ONE_AWAY,Eval.ROOK_VAL*2+Eval.KNIGHT_VAL+Eval.BISHOP_VAL+Eval.QUEEN_VAL)
                , Eval.eval(b));

        // white pawn on g4
        FenParser.setPos(b, "rnbq1rk1/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQ1RK1 w kq - 0 1");
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_G, Rank.RANK_4).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_G, Rank.RANK_2).value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_TWO_AWAY,Eval.ROOK_VAL*2+Eval.KNIGHT_VAL+Eval.BISHOP_VAL+Eval.QUEEN_VAL)
                , Eval.eval(b));

        // black pawn on h4
        FenParser.setPos(b, "rnbq1rk1/ppppppp1/8/8/7p/8/PPPPPPPP/RNBQ1RK1 b KQkq - 0 1");
        Assert.assertEquals(
                Eval.PAWN_PST[Square.valueOf(File.FILE_H, Rank.RANK_4).flipVertical().value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_H, Rank.RANK_7).flipVertical().value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_FAR_AWAY/2,Eval.ROOK_VAL*2+Eval.KNIGHT_VAL+Eval.BISHOP_VAL+Eval.QUEEN_VAL)
                , Eval.eval(b));

    }

    @Test
    public void evalKingSafeQueenSide() throws Exception {
        Board b = Board.INSTANCE;

        // white pawn on c3
        FenParser.setPos(b, "1krq1bnr/pppppppp/8/8/8/2P5/PP1PPPPP/1KRQ1BNR w kq - 0 1");
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_3).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_C, Rank.RANK_2).value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_ONE_AWAY,Eval.ROOK_VAL*2+Eval.QUEEN_VAL+Eval.BISHOP_VAL+Eval.KNIGHT_VAL)
                , Eval.eval(b));

        // white pawn on b4
        FenParser.setPos(b, "1krq1bnr/pppppppp/8/8/1P6/8/P1PPPPPP/1KRQ1BNR w kq - 0 1");
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_B, Rank.RANK_4).value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_B, Rank.RANK_2).value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_TWO_AWAY,Eval.ROOK_VAL*2+Eval.QUEEN_VAL+Eval.BISHOP_VAL+Eval.KNIGHT_VAL)
                , Eval.eval(b));

        // black pawn on a4
        FenParser.setPos(b, "1krq1bnr/1ppppppp/8/8/p7/8/PPPPPPPP/1KRQ1BNR b kq - 0 1");
        Assert.assertEquals(Eval.PAWN_PST[Square.valueOf(File.FILE_A, Rank.RANK_4).flipVertical().value()]
                -Eval.PAWN_PST[Square.valueOf(File.FILE_A, Rank.RANK_7).flipVertical().value()]
                +Eval.scale(Eval.KING_SAFETY_PAWN_FAR_AWAY/2,Eval.ROOK_VAL*2+Eval.QUEEN_VAL+Eval.BISHOP_VAL+Eval.KNIGHT_VAL)
                , Eval.eval(b));

    }

    @Test
    public void testScale() {
        int material = Eval.ROOK_VAL * 2 + Eval.KNIGHT_VAL * 2 + Eval.BISHOP_VAL * 2 + Eval.QUEEN_VAL;
        Assert.assertEquals(100, Eval.scale(100, material));

        Assert.assertEquals(15, Eval.scale(100, Eval.ROOK_VAL));
    }
}
