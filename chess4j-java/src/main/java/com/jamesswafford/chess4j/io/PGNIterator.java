package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PGNIterator {

    private BufferedReader br;

    public PGNIterator(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    public PGNGame next() throws IOException, ParseException, IllegalMoveException {

        PGNParser parser = new PGNParser();
        String nextGame = getNextPGN();

        if (nextGame != null) {
            return parser.parseGame(nextGame);
        }

        return null;
    }

    /**
     * The first line should be a PGN tag.
     *
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private String getNextPGN() throws IOException, ParseException {

        StringBuilder sb = new StringBuilder();

        boolean foundTags = false;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("[")) {
                foundTags = true;
                sb.append(line).append("\n");
            } else if (foundTags && "".equals(line.trim())) {
                // first line after tags, break
                sb.append("\n");
                break;
            }
        }

        if (!foundTags) { return null; }

        boolean foundMoveText = false;
        while ((line = br.readLine()) != null) {
            if (!"".equals(line.trim())) {
                foundMoveText = true;
                sb.append(line).append("\n");
            } else if (foundMoveText) {
                break;
            }
        }

        if (!foundMoveText) {
            throw new ParseException("found tags but no move text");
        }

        return sb.toString();
    }
}
