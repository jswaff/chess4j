package dev.jamesswafford.chess4j.search;

import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;

/**
 * The history heuristic.  Tracks how often each quiet move, indexed by side to move and from/to squares, has caused a
 * beta cutoff.  Moves that cause a cutoff are rewarded, and quiet moves searched before them that did not are
 * penalized, both in proportion to the square of the remaining depth.
 *
 * Updates use a "gravity" formula that pulls scores toward zero as they grow, which keeps every score within
 * [-MAX_SCORE, MAX_SCORE] and lets stale information decay without a separate aging pass.
 */
public class History implements HistoryStore {

    private static final History INSTANCE = new History();

    static final int MAX_SCORE = 16384;

    private final int[][][] scores;

    private History() {
        scores = new int[2][Square.NUM_SQUARES][Square.NUM_SQUARES];
    }

    public void addCutoff(Color player, Move move, int depth) {
        update(player, move, bonus(depth));
    }

    public void addFailure(Color player, Move move, int depth) {
        update(player, move, -bonus(depth));
    }

    public void clear() {
        for (int c=0;c<2;c++) {
            for (int from=0;from<Square.NUM_SQUARES;from++) {
                for (int to=0;to<Square.NUM_SQUARES;to++) {
                    scores[c][from][to] = 0;
                }
            }
        }
    }

    public int getScore(Color player, Move move) {
        return scores[player.isWhite() ? 1 : 0][move.from().value()][move.to().value()];
    }

    private int bonus(int depth) {
        return Math.min(depth * depth, MAX_SCORE);
    }

    private void update(Color player, Move move, int delta) {
        assert(move.captured()==null);
        assert(move.promotion()==null);
        int c = player.isWhite() ? 1 : 0;
        int from = move.from().value();
        int to = move.to().value();
        scores[c][from][to] += delta - scores[c][from][to] * Math.abs(delta) / MAX_SCORE;
        assert(Math.abs(scores[c][from][to]) <= MAX_SCORE);
    }

    public static History getInstance() {
        return INSTANCE;
    }
}
