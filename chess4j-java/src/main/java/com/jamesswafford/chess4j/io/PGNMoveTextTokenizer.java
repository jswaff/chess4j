package com.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNMoveTextTokenizer {

    private static final Pattern numberPattern = Pattern.compile("^\\d+\\.{1,3}");
    private static final Pattern gameResultPattern = Pattern.compile("^(0-1|1-0|1/2-1/2|\\*)");

    public static List<PGNMoveTextToken> tokenize(String moveText) {
        List<PGNMoveTextToken> tokens = new ArrayList<>();

        int currentIndex = 0;
        while (currentIndex < moveText.length()) {
            String subStr = moveText.substring(currentIndex);
            Matcher moveNumberMatcher = numberPattern.matcher(subStr);
            Matcher gameResultMatcher = gameResultPattern.matcher(subStr);
            if (moveNumberMatcher.lookingAt()) {
                String val = moveNumberMatcher.group();
                tokens.add(new PGNMoveTextToken(PGNMoveTextTokenType.MOVE_NUMBER, val));
                currentIndex += val.length();
            } else if (subStr.startsWith("{")) {
                int numLeftBraces = 1;
                int numRightBraces = 0;
                int nagLength = 1;
                while (numRightBraces < numLeftBraces) {
                    char currentChar = subStr.charAt(nagLength);
                    if (currentChar == '{') numLeftBraces++;
                    else if (currentChar == '}') numRightBraces++;
                    nagLength++;
                }
                String nagVal = subStr.substring(0, nagLength);
                tokens.add(new PGNMoveTextToken(PGNMoveTextTokenType.NAG, nagVal));
                currentIndex += nagLength;
            } else if (gameResultMatcher.lookingAt()) {
                String val = gameResultMatcher.group();
                tokens.add(new PGNMoveTextToken(PGNMoveTextTokenType.GAME_RESULT, val));
                currentIndex += val.length();
            } else if (!subStr.startsWith(" ")) { // assume this is a move
                int moveEndInd = subStr.indexOf(" ");
                if (moveEndInd == -1) moveEndInd = subStr.length();
                String moveVal = subStr.substring(0, moveEndInd);
                tokens.add(new PGNMoveTextToken(PGNMoveTextTokenType.MOVE, moveVal));
                currentIndex += moveVal.length();
            } else {
                currentIndex++;
            }

        }

        return tokens;
    }

}
