package dev.jamesswafford.chess4j.nn;

import dev.jamesswafford.chess4j.Globals;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.eval.Eval;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import org.junit.Test;

import java.util.List;

public class NeuralNetworkTest {

    @Test
    public void test1() {
        String f = "/home/james/data/chess/network.txt";
        NeuralNetwork nn = new NeuralNetwork();
        nn.load(f);

        Board b = new Board();
        System.out.println("score initial pos: " + nn.eval(b));

        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(b);
        for (Move mv : moves) {
            Undo u = b.applyMove(mv);
            System.out.println(mv + " NN: " + nn.eval(b) +
                    " HCE: " + Eval.eval(Globals.getEvalWeights(), b));
            b.undoMove(u);
        }
    }
}
