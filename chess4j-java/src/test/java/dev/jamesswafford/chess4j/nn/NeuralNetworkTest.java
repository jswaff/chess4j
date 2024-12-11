package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.pieces.Pawn;
import dev.jamesswafford.chess4j.pieces.Queen;
import org.junit.Test;

public class NeuralNetworkTest {

    @Test
    public void test1() {
        String f = "/home/james/data/chess/network-128.txt";
        NeuralNetwork nn = new NeuralNetwork();
        nn.load(f);

        Board b = new Board();
        System.out.println("score initial pos: " + nn.eval(b));

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        System.out.println("score after e4: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        System.out.println("score after d5: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.C2, Square.C4));
        System.out.println("score after c4: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.C7, Square.C6));
        System.out.println("score after c6: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN));
        System.out.println("score after exd5: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Queen.BLACK_QUEEN, Square.D8, Square.D5, Pawn.WHITE_PAWN));
        System.out.println("score after Qxd5: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.C4, Square.D5, Queen.BLACK_QUEEN));
        System.out.println("score after cxd5: " + nn.eval(b) + " HCE: " + Eval.eval(Globals.getEvalWeights(), b));

        DrawBoard.drawBoard(b);
    }
}
