package com.jamesswafford.chess4j.eval;

import com.jamesswafford.chess4j.Color;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.utils.OrderedPair;

import static org.junit.Assert.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

import static com.jamesswafford.chess4j.eval.Eval.*;

public class EvalTest {

    Board board = Board.INSTANCE;

    @Test
    public void testStartPosIs0() {
        board.resetBoard();
        int eval = eval(board);
        assertEquals(0, eval);
    }

    @Test
    public void testScore1() {
        board.setPos("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        int eval = eval(board);
        assertEquals(-(PAWN_PST[E4.value()] - PAWN_PST[E2.value()]), eval);
    }

    @Test
    public void testScore2() {
        board.setPos("rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        int eval = eval(board);
        assertEquals(PAWN_PST[Square.valueOf(FILE_E, RANK_4).value()]
                -PAWN_PST[Square.valueOf(FILE_E, RANK_2).value()]
                -PAWN_PST[Square.valueOf(FILE_C, RANK_6).flipVertical().value()]
                +PAWN_PST[Square.valueOf(FILE_C, RANK_7).flipVertical().value()]
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

    private void testCaseSymmetry(String fen) {
        board.setPos(fen);
        int eval = eval(board);
        board.flipVertical();
        int eval2 = eval(board);
        assertEquals(eval, eval2);
    }

    @Test
    public void testPieceVals() {
        assertEquals(900, Eval.getPieceValue(WHITE_QUEEN));
        assertEquals(900, Eval.getPieceValue(BLACK_QUEEN));

        assertEquals(500, Eval.getPieceValue(WHITE_ROOK));
        assertEquals(500, Eval.getPieceValue(BLACK_ROOK));

        assertEquals(320, Eval.getPieceValue(WHITE_BISHOP));
        assertEquals(320, Eval.getPieceValue(BLACK_BISHOP));

        assertEquals(300, Eval.getPieceValue(WHITE_KNIGHT));
        assertEquals(300, Eval.getPieceValue(BLACK_KNIGHT));

        assertEquals(100, Eval.getPieceValue(WHITE_PAWN));
        assertEquals(100, Eval.getPieceValue(BLACK_PAWN));
    }

    @Test
    public void testRookPST() {
        board.setPos("6k1/3R4/8/8/8/8/8/3K4 w - - 0 1");
        int eval = eval(board);
        assertEquals(ROOK_VAL + ROOK_ON_7TH + ROOK_OPEN_FILE, eval);

        board.setPos("6k1/3RR3/8/8/8/8/8/3K4 w - - 0 1");
        eval = eval(board);
        assertEquals(ROOK_VAL*2 + ROOK_ON_7TH*2
                + CONNECTED_MAJORS_ON_7TH
                + ROOK_OPEN_FILE * 2, eval);

        board.setPos("6k1/3RRr2/8/8/8/8/8/3K4 w - - 0 1");
        eval = eval(board);
        // white has two rooks on open files and black one, so net 1 for white
        assertEquals(ROOK_VAL + ROOK_ON_7TH*2
                + CONNECTED_MAJORS_ON_7TH
                + ROOK_OPEN_FILE, eval);

        board.setPos("6k1/3RRr2/8/8/8/8/r7/3K4 w - - 0 1");
        eval = eval(board);
        assertEquals(ROOK_ON_7TH + CONNECTED_MAJORS_ON_7TH, eval);

        board.setPos("6k1/3RRr2/8/8/8/r7/8/3K4 w - - 0 1");
        eval = eval(board);
        // deduct for black for rook on A file
        assertEquals(ROOK_ON_7TH*2 + CONNECTED_MAJORS_ON_7TH
                - ROOK_PST[Square.valueOf(FILE_A, RANK_3).flipVertical().value()]
                , eval);

        board.setPos("6k1/8/8/8/8/8/qr6/7K b - - 0 1");
        assertEquals(QUEEN_VAL + ROOK_VAL
                + ROOK_ON_7TH * 2
                + CONNECTED_MAJORS_ON_7TH
                + ROOK_OPEN_FILE
                + QUEEN_PST[Square.valueOf(FILE_A, RANK_2).value()]
                - scale(KING_SAFETY_PAWN_FAR_AWAY
                + KING_SAFETY_PAWN_FAR_AWAY
                + KING_SAFETY_PAWN_FAR_AWAY/2,QUEEN_VAL + ROOK_VAL),
                eval(board));
    }

    @Test
    public void testRookOn7th() {
        board.setPos("7k/8/8/8/8/8/r7/7K w - - 0 1");
        assertEquals(-ROOK_VAL, evalMaterial(board));

        assertEquals(-ROOK_VAL-ROOK_ON_7TH-ROOK_OPEN_FILE, eval(board));

        // move king out
        board.setPos("7k/8/8/8/8/7K/r7/8 w - - 0 1");
        assertEquals(-ROOK_VAL-ROOK_OPEN_FILE, eval(board));

        // flipped
        board.setPos("7k/8/8/8/8/7K/r7/8 b - - 0 1");
        assertEquals(ROOK_VAL+ROOK_OPEN_FILE, eval(board));
    }

    @Test
    public void testConnectedMajorsOn7th() {
        board.setPos("7k/R2R4/8/8/8/8/8/7K w - - 0 1");
        assertEquals(ROOK_VAL*2 + ROOK_ON_7TH*2
                + CONNECTED_MAJORS_ON_7TH
                + ROOK_OPEN_FILE * 2,
                eval(board));

        board.setPos("7k/R2QR3/8/8/8/8/8/7K w - - 0 1");
        int expected = ROOK_VAL*2 + QUEEN_VAL
                + ROOK_ON_7TH*3
                + CONNECTED_MAJORS_ON_7TH*2
                + ROOK_OPEN_FILE*2
                - scale(Eval.KING_SAFETY_PAWN_FAR_AWAY    // F File
                + KING_SAFETY_PAWN_FAR_AWAY    // G File
                + (KING_SAFETY_PAWN_FAR_AWAY /2),ROOK_VAL*2+QUEEN_VAL); // H File
        assertEquals(expected, eval(board));

        // flip it
        board.setPos("7k/R2QR3/8/8/8/8/8/7K b - - 0 1");
        assertEquals(-expected, eval(board));
    }

    @Test
    public void testRookOnOpenFile() {
        board.setPos("3r3k/8/8/8/8/8/8/7K b - - 0 1");
        assertEquals(ROOK_VAL + ROOK_OPEN_FILE, eval(board));

        board.setPos("3r3k/3r4/8/8/8/8/8/7K b - - 0 1");
        assertEquals(ROOK_VAL*2 + ROOK_OPEN_FILE*2, eval(board));

        board.setPos("3r3k/8/3p4/8/8/8/8/7K b - - 0 1");
        assertEquals(ROOK_VAL+PAWN_VAL
                +PASSED_PAWN
                +ISOLATED_PAWN
                +PAWN_PST[Square.valueOf(FILE_D, RANK_6).flipVertical().value()],
                eval(board));
    }

    @Test
    public void testRookHalfOpenFile() {
        // friendly pawn, no bonus
        board.setPos("8/2P5/8/2R5/K7/8/7k/8 w - - 0 1");
        assertEquals(ROOK_VAL+PAWN_VAL
                +PASSED_PAWN
                +ISOLATED_PAWN
                +PAWN_PST[Square.valueOf(FILE_C, RANK_7).value()],
                eval(board));

        board.setPos("8/2p5/8/2R5/K7/8/7k/8 w - - 0 1");
        assertEquals(ROOK_VAL-PAWN_VAL-PASSED_PAWN
                -ISOLATED_PAWN
                +PAWN_PST[Square.valueOf(FILE_C, RANK_7).flipVertical().value()]
                +ROOK_HALF_OPEN_FILE,
                eval(board));
    }

    @Test
    public void testQueenPST() {
        board.setPos("3kq3/8/8/8/8/8/8/3K4 b - - 0 1");
        assertEquals(QUEEN_VAL, evalMaterial(board));
        assertEquals(QUEEN_VAL
                + QUEEN_PST[Square.valueOf(FILE_E, RANK_8).flipVertical().value()],
                eval(board));
    }

    @Test
    public void testBishopPST() {
        board.setPos("6k1/3B4/8/8/8/8/8/K7 w - - 0 1");
        int eval = eval(board);
        assertEquals(getPieceValue(Bishop.WHITE_BISHOP)
                + BISHOP_PST[Square.valueOf(FILE_D, RANK_7).value()], eval);

        board.setPos("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        eval = eval(board);
        assertEquals(Eval.getPieceValue(Bishop.WHITE_BISHOP)
                + BISHOP_PST[Square.valueOf(FILE_D, RANK_5).value()], eval);
    }

    @Test
    public void testKingPST() {
        board.setPos("rnbqkbnr/pppppppp/8/8/3P4/3BPN2/PPP2PPP/RNBQR1K1 w - - 0 1");

        assertEquals(0,evalMaterial(board));
        int score = eval(board);

        int tropismDelta =
                KNIGHT_TROPISM * Square.valueOf(FILE_F,RANK_3).distance(board.getKingSquare(Color.BLACK))
                - KNIGHT_TROPISM * Square.valueOf(FILE_G,RANK_1).distance(board.getKingSquare(Color.BLACK));

        assertEquals(PAWN_PST[Square.valueOf(FILE_D, RANK_4).value()]
                - PAWN_PST[Square.valueOf(FILE_D, RANK_2).value()]
                + PAWN_PST[Square.valueOf(FILE_E, RANK_3).value()]
                - PAWN_PST[Square.valueOf(FILE_E, RANK_2).value()]
                + BISHOP_PST[Square.valueOf(FILE_D, RANK_3).value()]
                - BISHOP_PST[Square.valueOf(FILE_F, RANK_1).value()]
                + KNIGHT_PST[Square.valueOf(FILE_F, RANK_3).value()]
                - KNIGHT_PST[Square.valueOf(FILE_G, RANK_1).value()]
                + ROOK_PST[Square.valueOf(FILE_E, RANK_1).value()]
                - ROOK_PST[Square.valueOf(FILE_H, RANK_1).value()]
                + KING_PST[Square.valueOf(FILE_G, RANK_1).value()]
                - KING_PST[Square.valueOf(FILE_E, RANK_1).value()]
                + tropismDelta
                ,score);
    }

    @Test
    public void testKingEndGamePST() {
        board.setPos("8/8/8/4k3/8/8/8/RNB2K2 w - - 0 1");

        int expectedMaterial = ROOK_VAL + KNIGHT_VAL + BISHOP_VAL;
        assertEquals(expectedMaterial,evalMaterial(board));

        int expected = expectedMaterial
                + ROOK_OPEN_FILE
                + KNIGHT_PST[Square.valueOf(FILE_B, RANK_1).value()]
                + KNIGHT_TROPISM * Square.valueOf(FILE_B,RANK_1).distance(board.getKingSquare(Color.BLACK))
                + KING_ENDGAME_PST[Square.valueOf(FILE_F,RANK_1).value()]
                - KING_PST[Square.valueOf(FILE_E, RANK_5).value()]
                - scale(KING_SAFETY_MIDDLE_OPEN_FILE,ROOK_VAL+KNIGHT_VAL+BISHOP_VAL);
        assertEquals(expected, eval(board));

        // by removing a bishop both sides should be evaluated as in the endgame
        board.setPos("8/8/8/4k3/8/8/8/RN3K2 b - - 0 1");
        expected = -ROOK_VAL - KNIGHT_VAL
                - ROOK_OPEN_FILE
                - KNIGHT_PST[Square.valueOf(FILE_B, RANK_1).value()]
                - KNIGHT_TROPISM * Square.valueOf(FILE_B,RANK_1).distance(board.getKingSquare(Color.BLACK))
                - KING_ENDGAME_PST[Square.valueOf(FILE_F,RANK_1).value()]
                + KING_ENDGAME_PST[Square.valueOf(FILE_E, RANK_5).value()];
        assertEquals(expected, eval(board));
    }

    @Test
    public void testEvalMaterial() {
        board.resetBoard();
        assertEquals(0, evalMaterial(board));

        board.setPos("6k1/8/8/3B4/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL, evalMaterial(board));

        board.setPos("6k1/8/8/3Br3/8/8/8/K7 w - - 0 1");
        assertEquals(BISHOP_VAL-ROOK_VAL, evalMaterial(board));
    }

    @Test
    public void testMaterialScores() {
        board.setPos("8/k7/prb5/K7/QN6/8/8/8 b - - 0 1");
        OrderedPair<Integer,Integer> npScores = Eval.getNonPawnMaterialScore(board);
        assertEquals(QUEEN_VAL + KNIGHT_VAL, (int) npScores.getE1());
        assertEquals(ROOK_VAL + BISHOP_VAL, (int) npScores.getE2());

        OrderedPair<Integer,Integer> pScores = getPawnMaterialScore(board);
        assertEquals(0, (int) pScores.getE1());
        assertEquals(PAWN_VAL, (int) pScores.getE2());
    }

    @Test
    public void testDoubledAndIsolatedPawns() {
        board.setPos("k7/p1p3p1/3p3p/1P5P/1PP1P3/8/8/K7 b - - 0 1");

        assertEquals(0, evalMaterial(board));

        int score = eval(board);

        int expected =
                KING_ENDGAME_PST[Square.valueOf(FILE_A, RANK_8).flipVertical().value()]
                    + PAWN_PST[Square.valueOf(FILE_A, RANK_7).flipVertical().value()]
                    + PAWN_PST[Square.valueOf(FILE_C, RANK_7).flipVertical().value()]
                    + PAWN_PST[Square.valueOf(FILE_D, RANK_6).flipVertical().value()]
                    + PAWN_PST[Square.valueOf(FILE_G, RANK_7).flipVertical().value()]
                    + PAWN_PST[Square.valueOf(FILE_H, RANK_6).flipVertical().value()]
                    + ISOLATED_PAWN // black pawn on A7
                    - KING_ENDGAME_PST[Square.valueOf(FILE_A, RANK_1).value()]
                    - PAWN_PST[Square.valueOf(FILE_B, RANK_5).value()]
                    - PAWN_PST[Square.valueOf(FILE_B, RANK_4).value()]
                    - PAWN_PST[Square.valueOf(FILE_C, RANK_4).value()]
                    - PAWN_PST[Square.valueOf(FILE_E, RANK_4).value()]
                    - PAWN_PST[Square.valueOf(FILE_H, RANK_5).value()]
                    - ISOLATED_PAWN * 2  // white pawns on E4 and H1
                    - DOUBLED_PAWN * 2 // white pawns on b4 and b5
                    ;

        assertEquals(expected, score);
    }

    @Test
    public void evalKingSafetyMiddleFiles() {
        Board b = Board.INSTANCE;

        // initial position then e3 .. no penalty
        b.setPos("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        int score1 = eval(b);
        assertEquals(PAWN_PST[Square.valueOf(FILE_E, RANK_3).value()]
                -PAWN_PST[Square.valueOf(FILE_E, RANK_2).value()]
                , score1);

        //  open file for both, so still 0
        b.setPos("rnbqkbnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(0, eval(b));

        // remove both queens.  open e   put black king on d8
        b.setPos("rnbk1bnr/pppp1ppp/8/8/8/8/PPPP1PPP/RNB1KBNR b KQ - 0 1");
        assertEquals(-scale(KING_SAFETY_MIDDLE_OPEN_FILE,ROOK_VAL*2+KNIGHT_VAL*2+BISHOP_VAL*2)
                +KING_PST[Square.valueOf(FILE_D, RANK_8).flipVertical().value()], Eval.eval(b));
    }

    @Test
    public void evalKingSafetyKingSide() {
        Board b = Board.INSTANCE;

        // no pawns advanced
        b.setPos("rnbq1rk1/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1RK1 w - - 0 1");
        assertEquals(0, eval(b));

        // white pawn on f3
        b.setPos("rnbq1rk1/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQ1RK1 w - - 0 1");
        assertEquals(PAWN_PST[Square.valueOf(FILE_F, RANK_3).value()]
                -PAWN_PST[Square.valueOf(FILE_F, RANK_2).value()]
                +scale(KING_SAFETY_PAWN_ONE_AWAY,ROOK_VAL*2+KNIGHT_VAL+BISHOP_VAL+QUEEN_VAL)
                , eval(b));

        // white pawn on g4
        b.setPos("rnbq1rk1/pppppppp/8/8/6P1/8/PPPPPP1P/RNBQ1RK1 w - - 0 1");
        assertEquals(PAWN_PST[Square.valueOf(FILE_G, RANK_4).value()]
                -PAWN_PST[Square.valueOf(FILE_G, RANK_2).value()]
                +scale(KING_SAFETY_PAWN_TWO_AWAY,ROOK_VAL*2+KNIGHT_VAL+BISHOP_VAL+QUEEN_VAL)
                , eval(b));

        // black pawn on h4
        b.setPos("rnbq1rk1/ppppppp1/8/8/7p/8/PPPPPPPP/RNBQ1RK1 b - - 0 1");
        assertEquals(
                PAWN_PST[Square.valueOf(FILE_H, RANK_4).flipVertical().value()]
                -PAWN_PST[Square.valueOf(FILE_H, RANK_7).flipVertical().value()]
                +scale(Eval.KING_SAFETY_PAWN_FAR_AWAY/2,ROOK_VAL*2+KNIGHT_VAL+BISHOP_VAL+QUEEN_VAL)
                , eval(b));

    }

    @Test
    public void evalKingSafeQueenSide() {
        Board b = Board.INSTANCE;

        // white pawn on c3
        b.setPos("1krq1bnr/pppppppp/8/8/8/2P5/PP1PPPPP/1KRQ1BNR w - - 0 1");
        assertEquals(PAWN_PST[Square.valueOf(FILE_C, RANK_3).value()]
                -PAWN_PST[Square.valueOf(FILE_C, RANK_2).value()]
                +scale(KING_SAFETY_PAWN_ONE_AWAY,ROOK_VAL*2+QUEEN_VAL+BISHOP_VAL+KNIGHT_VAL)
                , eval(b));

        // white pawn on b4
        b.setPos("1krq1bnr/pppppppp/8/8/1P6/8/P1PPPPPP/1KRQ1BNR w - - 0 1");
        assertEquals(PAWN_PST[Square.valueOf(FILE_B, RANK_4).value()]
                -PAWN_PST[Square.valueOf(FILE_B, RANK_2).value()]
                +scale(KING_SAFETY_PAWN_TWO_AWAY,ROOK_VAL*2+QUEEN_VAL+BISHOP_VAL+KNIGHT_VAL)
                , eval(b));

        // black pawn on a4
        b.setPos("1krq1bnr/1ppppppp/8/8/p7/8/PPPPPPPP/1KRQ1BNR b - - 0 1");
        assertEquals(PAWN_PST[Square.valueOf(FILE_A, RANK_4).flipVertical().value()]
                -PAWN_PST[Square.valueOf(FILE_A, RANK_7).flipVertical().value()]
                +scale(KING_SAFETY_PAWN_FAR_AWAY/2,ROOK_VAL*2+QUEEN_VAL+BISHOP_VAL+KNIGHT_VAL)
                , eval(b));

    }

    @Test
    public void testScale() {
        int material = ROOK_VAL * 2 + KNIGHT_VAL * 2 + BISHOP_VAL * 2 + QUEEN_VAL;
        assertEquals(100, scale(100, material));

        assertEquals(15, scale(100, ROOK_VAL));
    }
}
