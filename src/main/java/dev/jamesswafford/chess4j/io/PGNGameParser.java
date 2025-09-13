package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PGNGameParser {

    private static final String tagPattern = "\\[([A-Za-z0-9_]+)\\s+\"(.*?)\"]";

    public PGNGame parseGame(String pgn) throws ParseException, IllegalMoveException {
        List<PGNTag> tags = getPGNTags(pgn);
        List<MoveWithNAG> moves = getMoves(pgn);
        PGNResult result = getResult(pgn);

        return new PGNGame(tags,moves,result);
    }

    private List<MoveWithNAG> getMoves(String pgn) throws ParseException, IllegalMoveException {
        List<MoveWithNAG> moves = new ArrayList<>();

        String moveText = getMoveText(pgn);

        List<PGNMoveTextToken> tokens = PGNMoveTextTokenizer.tokenize(moveText);

        MoveParser mp = new MoveParser();

        Board board = new Board();

        for (int i=0;i<tokens.size();i++) {
            PGNMoveTextToken token = tokens.get(i);
            if (PGNMoveTextTokenType.MOVE.equals(token.getTokenType())) {
                Move m = mp.parseMove(token.getValue(), board);
                // if the next token is a NAG (annotation), include it
                String nag = null;
                if (i < tokens.size()-1 && PGNMoveTextTokenType.NAG.equals(tokens.get(i+1).getTokenType())) {
                    nag = tokens.get(i+1).getValue();
                }
                moves.add(new MoveWithNAG(m, nag));
                board.applyMove(m);
            }
        }

        return moves;
    }

    private String getMoveText(String pgn) {

        Pattern p = Pattern.compile("\\[(.*)?]");
        Matcher m = p.matcher(pgn);

        // TODO: this breaks "end of line comments"
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
            throw new ParseException("Could not determine game result");
        }

        return result;
    }

    /**
     * Parse text to get a PGNTag
     *
     * @param tagTxt - text , possibly with PGN tag
     * @return - the PGN tag, or null if the text doesn't represent a PGN tag
     */
    private PGNTag parseTag(String tagTxt) {
        Pattern r = Pattern.compile(tagPattern);
        Matcher m = r.matcher(tagTxt);

        if (!m.find()) return null;
        return new PGNTag(m.group(1),m.group(2));
    }
}
