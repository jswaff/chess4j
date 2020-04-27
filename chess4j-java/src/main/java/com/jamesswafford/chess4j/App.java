package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.book.OpeningBookSQLiteImpl;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.hash.TranspositionTableEntry;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.InputParser;
import com.jamesswafford.chess4j.utils.TestSuiteProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class App {
    private static final  Logger LOGGER = LogManager.getLogger(App.class);

    private static String bookPath = null;
    private static String testSuiteFile = null;
    private static int testSuiteTime = 10; // default to ten seconds
    private static int maxDepth = 6;

    private App() { }

    private static void processArgument(String arg) {
        if (arg.startsWith("-native")) {
            Initializer.attemptToUseNative = true;
        } else if (arg.startsWith("-suite=")) {
            testSuiteFile = arg.substring(7);
        } else if (arg.startsWith("-depth=")) {
            maxDepth = Integer.parseInt(arg.substring(7));
        } else if (arg.startsWith("-time=")) {
            testSuiteTime = Integer.parseInt(arg.substring(6));
        } else if (arg.startsWith("-book=")) {
            bookPath = arg.substring(6);
        } else if (arg.startsWith("-hash=")) {
            int maxMemBytes = Integer.parseInt(arg.substring(6)) * 1024 * 1024;
            TTHolder.maxEntries = maxMemBytes / TranspositionTableEntry.sizeOf();
        } else if (arg.startsWith("-phash=")) {
            int maxMemBytes = Integer.parseInt(arg.substring(7)) * 1024 * 1024;
            TTHolder.maxPawnEntries = maxMemBytes / PawnTranspositionTableEntry.sizeOf();
        }
    }

    /**
     * Read-Expression-Print-Loop
     */
    private static void repl() {
        BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (true) {
            try {
                input = bin.readLine();
            } catch (IOException e1) {
                LOGGER.error("Caught nonrecoverable I/O exception: " + e1.getMessage());
                System.exit(1);
            }

            try {
                InputParser.getInstance().parseCommand(input);
            } catch (Exception e) {
                LOGGER.warn("Caught (hopefully recoverable) exception: " + e.getMessage());
            }
        }
    }

    private static boolean showDebugMode() {
        LOGGER.info("**** DEBUG MODE ENABLED ****");
        return true;
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("Welcome to chess4j!\n\n");

        assert(showDebugMode());

        for (String arg : args) {
            processArgument(arg);
        }
        TTHolder.initTables();

        if (testSuiteFile != null) {
            TestSuiteProcessor tp = new TestSuiteProcessor();
            tp.processTestSuite(testSuiteFile, maxDepth, testSuiteTime);
            System.exit(0);
        }

        if (bookPath != null) {
            Globals.setOpeningBook(OpeningBookSQLiteImpl.openOrInitialize(bookPath));
        }

        repl();
    }

}
