package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www6.chessclub.com/help/PGN-spec
 * @author James
 *
 */
public final class PGNParser {

    private static final String tagPattern = "\\[([A-Za-z0-9_]+)\\s+\\\"(.*?)\\\"\\]";

    private List<Move> getMoves(String pgn) throws ParseException, IllegalMoveException {
        List<Move> moves = new ArrayList<Move>();

        String moveText = getMoveText(pgn);

        // get rid of comments
        moveText = moveText.replaceAll("\\{(.*)?\\}", "");

        // get rid of move indicators
        moveText = moveText.replaceAll("\\d+\\.", "");

        // get rid of game end indicator
        moveText = moveText.replaceAll("1-0", "").replaceAll("0-1", "")
                .replaceAll("1/2-1/2", "").replaceAll("\\*", "");

        // get rid of extra white space
        moveText = moveText.replaceAll("\\s+", " ").trim();

        String[] mvs = moveText.split(" ");
        MoveParser mp = new MoveParser();

        Board board = new Board();

        for (String mv : mvs) {
            Move m = mp.parseMove(mv, board);
            moves.add(m);
            board.applyMove(m);
        }

        return moves;
    }

    private String getMoveText(String pgn) {

        Pattern p = Pattern.compile("\\[(.*)?\\]");
        Matcher m = p.matcher(pgn);

        return m.replaceAll("").replaceAll("\n", " ").replaceAll("\r", " ").trim();
    }

    private List<PGNTag> getPGNTags(String pgn) {
        List<PGNTag> tags = new ArrayList<>();

        Pattern r = Pattern.compile(tagPattern);
        Matcher m = r.matcher(pgn);

        while (m.find()) {
            tags.add(parseTag(m.group()));
        }

        return tags;
    }

    private PGNResult getResult(String pgn) throws ParseException {
        PGNResult result;
        if (pgn.trim().endsWith("1-0")) {
            result = PGNResult.WHITE_WINS;
        } else if (pgn.trim().endsWith("0-1")) {
            result = PGNResult.BLACK_WINS;
        } else if (pgn.trim().endsWith("1/2-1/2")) {
            result = PGNResult.DRAW;
        } else if (pgn.trim().endsWith("*")) {
            result = PGNResult.ADJOURNED;
        } else {
            throw new ParseException("Could not determine game result.");
        }

        return result;
    }

    public synchronized PGNGame parseGame(String pgn) throws ParseException, IllegalMoveException {
        List<PGNTag> tags = getPGNTags(pgn);
        List<Move> moves = getMoves(pgn);
        PGNResult result = getResult(pgn);

        return new PGNGame(tags,moves,result);
    }

    /**
     * Parse the text to get a PGNTag  , or return null if it doesn't represent a PGN tag.
     *
     * @param tagTxt
     * @return
     */
    private PGNTag parseTag(String tagTxt) {
        Pattern r = Pattern.compile(tagPattern);
        Matcher m = r.matcher(tagTxt);

        if (!m.find()) return null;
        return new PGNTag(m.group(1),m.group(2));
    }
}
