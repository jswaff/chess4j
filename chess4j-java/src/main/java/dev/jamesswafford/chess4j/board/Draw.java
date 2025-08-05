package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.io.DrawBoard;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.utils.MoveUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static dev.jamesswafford.chess4j.pieces.Bishop.BLACK_BISHOP;
import static dev.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static dev.jamesswafford.chess4j.pieces.Knight.BLACK_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static dev.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static dev.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static dev.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;

public class Draw {

    private static final Logger LOGGER = LogManager.getLogger(Draw.class);

    static {
        Initializer.init();
    }

    public static boolean isDraw(Board board, List<Undo> undos) {
        return isDrawBy50MoveRule(board) || isDrawLackOfMaterial(board) ||
                isDrawByRep(board, undos, 1);
    }

    public static boolean isDrawBy50MoveRule(Board board) {
        return board.getFiftyCounter() >= 100;
    }

    /**
     * Determine if a position is drawn by lack of mating material.
     *
     * From the xboard documentation:
     * Note that (in accordance with FIDE rules) only KK, KNK, KBK and KBKB with
     * all bishops on the same color can be claimed as draws on the basis of
     * insufficient mating material. The end-games KNNK, KBKN, KNKN and KBKB with
     * unlike bishops do have mate positions, and cannot be claimed. Complex draws
     * based on locked Pawn chains will not be recognized as draws by most
     * interfaces, so do not claim in such positions, but just offer a draw or play
     * on.
     *
     * @param board - the board
     * @return - if the position is drawn by lack of mating material
     */
    public static boolean isDrawLackOfMaterial(Board board) {

        if (board.getNumPieces(BLACK_PAWN) > 0 || board.getNumPieces(WHITE_PAWN) > 0 ||
            board.getNumPieces(BLACK_ROOK) > 0 || board.getNumPieces(WHITE_ROOK) > 0 ||
            board.getNumPieces(BLACK_QUEEN) > 0 || board.getNumPieces(WHITE_QUEEN) > 0)
        {
            return false;
        }

        int numBlackKnights = board.getNumPieces(BLACK_KNIGHT);
        int numWhiteKnights = board.getNumPieces(WHITE_KNIGHT);
        int numKnights = numBlackKnights + numWhiteKnights;

        int numBlackBishops = board.getNumPieces(BLACK_BISHOP);
        int numWhiteBishops = board.getNumPieces(WHITE_BISHOP);
        int numBishops = numBlackBishops + numWhiteBishops;

        // if there are any knights at all, this must be a KNK ending to be a draw.
        if (numKnights > 1) {
            return false;
        }
        if (numKnights == 1 && numBishops > 0) {
            return false;
        }

        // if there is more than one bishop on either side, it isn't a draw.
        if (numBlackBishops > 1 || numWhiteBishops > 1) {
            return false;
        }

        // are there opposing bishops on different color squares? - not a draw
        if (numWhiteBishops == 1 && numBlackBishops == 1) {

            Square wSq = Square.valueOf(Bitboard.lsb(board.getWhiteBishops()));
            Square bSq = Square.valueOf(Bitboard.lsb(board.getBlackBishops()));

            if (wSq.isLight() != bSq.isLight()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDrawByRep(Board board, List<Undo> undos, int numPrev) {
        long currentZobristKey = board.getZobristKey();

        // only consider the positions since the last reversible move
        int toIndex = undos.size();
        int fromIndex = toIndex - board.getFiftyCounter();
        if (fromIndex < 0) fromIndex = 0;

        int numPrevVisits = 0;
        for (int i=fromIndex;i<toIndex;i++) {
            Undo u = undos.get(i);
            if (u.getZobristKey()==currentZobristKey) numPrevVisits++;
        }

        boolean rep = numPrevVisits >= numPrev;

        assert(verifyDrawByRepIsEqual(rep, board, undos, numPrev));

        return rep;
    }

    private static boolean verifyDrawByRepIsEqual(boolean javaRep, Board board, List<Undo> undos, int numPrev) {
        if (Initializer.nativeCodeInitialized()) {
            String fen = FENBuilder.createFen(board, true);
            Board copyBoard = board.deepCopy();
            assert(undos.size() >= board.getFiftyCounter());
            for (int i=0;i<board.getFiftyCounter();i++) {
                int ind = undos.size() - 1 - i;
                Undo u = undos.get(ind);
                copyBoard.undoMove(u);
            }
            String nonReversibleFen = FENBuilder.createFen(copyBoard, true);
            List<Long> movePath = undos.stream()
                    .map(u -> MoveUtils.toNativeMove(u.getMove()))
                    .collect(Collectors.toList());

            try {
                boolean nativeRep = isDrawByRepNative(fen, nonReversibleFen, movePath, numPrev);
                if (javaRep != nativeRep) {
                    LOGGER.error("Draw by rep not equal!  javaRep: {}, nativeRep: {}", javaRep, nativeRep);
                    LOGGER.error("fen: {}", fen);
                    DrawBoard.drawBoard(board);
                    undos.forEach(u -> LOGGER.error(u.toString()));
                    LOGGER.error("originalFen: {}", nonReversibleFen);
                    DrawBoard.drawBoard(copyBoard);
                    return false;
                }
                return true;
            } catch (IllegalStateException e) {
                LOGGER.error(e);
                throw e;
            }
        } else {
            return true;
        }
    }

    /**
     *
     * @param fen - current position
     * @param nonReversibleFen - position after last non-reversible move
     * @param movePath - move history since the start of the game
     * @param numPrev - threshold of previous repetitions to consider this a draw
     * @return whether the position should be evaluated as a draw by repetition
     */
    private static native boolean isDrawByRepNative(String fen, String nonReversibleFen, List<Long> movePath,
                                                    int numPrev);
}
