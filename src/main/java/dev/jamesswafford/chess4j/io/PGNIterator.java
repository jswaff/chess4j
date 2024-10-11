package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PGNIterator {

    private final Scanner scanner;

    public PGNIterator(File pgnFile) throws FileNotFoundException {
        FileInputStream pgnFileInputStream = new FileInputStream(pgnFile);
        scanner = new Scanner(pgnFileInputStream, StandardCharsets.UTF_8);
    }

    public PGNGame next() throws ParseException, IllegalMoveException {

        PGNGameParser parser = new PGNGameParser();
        String nextGame = getNextPGN();

        if (nextGame != null) {
            return parser.parseGame(nextGame);
        }

        return null;
    }

    private String getNextPGN() throws ParseException {

        StringBuilder sb = new StringBuilder();

        boolean foundTags = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
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
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
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
