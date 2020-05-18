package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.book.SQLiteBook;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.XBoardHandler;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.SearchParameters;
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
    private static int maxDepth = 0;

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
            // this command line arg is meant to set the size of the memory per table, but the
            // TTHolder will split it between both main tables.
            TTHolder.getInstance().resizeMainTables(2 * maxMemBytes);
        } else if (arg.startsWith("-phash=")) {
            int maxMemBytes = Integer.parseInt(arg.substring(7)) * 1024 * 1024;
            TTHolder.getInstance().resizePawnTable(maxMemBytes);
        }
    }

    /**
     * Read-Expression-Print-Loop
     */
    private static void repl() {
        BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        XBoardHandler XBoardHandler = new XBoardHandler();

        while (true) {
            try {
                input = bin.readLine();
            } catch (IOException e) {
                LOGGER.error("Caught nonrecoverable I/O exception", e);
                System.exit(1);
            }

            try {
                XBoardHandler.parseAndDispatch(input);
            } catch (Exception e) {
                LOGGER.warn("# Caught (hopefully recoverable) exception", e);
            }
        }
    }

    private static boolean showDebugMode() {
        LOGGER.info("# **** DEBUG MODE ENABLED ****");
        return true;
    }

    private static void warmUp() {
        TTHolder.getInstance().clearTables();
        new AlphaBetaSearch().search(new Board(),
                new SearchParameters(3, -Constants.INFINITY, Constants.INFINITY));
    }

    public static void main(String[] args) throws Exception {

        // send "done=0" to prevent XBoard timing out during the initialization sequence.
        LOGGER.info("done=0");

        LOGGER.info("# Welcome to chess4j!\n\n");

        assert(showDebugMode());
        warmUp();

        for (String arg : args) {
            processArgument(arg);
        }

        if (testSuiteFile != null) {
            TestSuiteProcessor tp = new TestSuiteProcessor();
            tp.processTestSuite(testSuiteFile, maxDepth, testSuiteTime);
            System.exit(0);
        }

        if (bookPath != null) {
            Globals.setOpeningBook(SQLiteBook.openOrInitialize(bookPath));
        }

        repl();
    }

}
