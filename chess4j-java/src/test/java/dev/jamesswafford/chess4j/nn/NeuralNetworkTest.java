package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import dev.jamesswafford.chess4j.pieces.Knight;
import dev.jamesswafford.chess4j.pieces.Pawn;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class NeuralNetworkTest {

    @Test
    public void test1() throws IOException {
        String f = "/home/james/data/chess/models/nn-003-d2.txt";
        NeuralNetwork nn = new NeuralNetwork();
        nn.load(f);

        Board b = new Board();
        System.out.println("initial pos: " + nn.eval(b));
        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E2, Square.E4));
        b.applyMove(new Move(Pawn.BLACK_PAWN, Square.D7, Square.D5));
        b.applyMove(new Move(Pawn.WHITE_PAWN, Square.E4, Square.D5, Pawn.BLACK_PAWN));
        b.applyMove(new Move(Knight.BLACK_KNIGHT, Square.G8, Square.F6));

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(b);
        for (Move mv : moves) {
            Undo u = b.applyMove(mv);
            System.out.println(mv + " NN: " + nn.eval(b) +
                    " HCE: " + Eval.eval(Globals.getEvalWeights(), b));
            b.undoMove(u);
        }
    }
}
