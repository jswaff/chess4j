package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.book.SQLiteBook;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.*;
import com.jamesswafford.chess4j.nn.FENLabeler;
import com.jamesswafford.chess4j.nn.ModelLoader;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.SearchOptions;
import com.jamesswafford.chess4j.search.SearchParameters;
import com.jamesswafford.chess4j.tuner.SQLiteTunerDatasource;
import com.jamesswafford.chess4j.utils.TestSuiteProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;

public final class App {
    private static final  Logger LOGGER = LogManager.getLogger(App.class);

    private static boolean testMode = false;
    private static boolean labelMode = false;
    private static String epdFile = null;
    private static int time = 10;
    private static int depth = 0;
    private static boolean zuriFormat = false;
    private static String outFile = "out.csv";


    private App() { }

    private static void processArgument(String arg) {
        if (arg.startsWith("-native")) {
            Initializer.attemptToUseNative = true;
        } else if (arg.startsWith("-depth=")) {
            depth = Integer.parseInt(arg.substring(7));
        } else if (arg.startsWith("-time=")) {
            time = Integer.parseInt(arg.substring(6));
        } else if (arg.startsWith("-book=")) {
            String path = arg.substring(6);
            LOGGER.info("# loading opening book from {}", path);
            Globals.setOpeningBook(SQLiteBook.openOrInitialize(path));
        } else if (arg.startsWith("-tunerds=")) {
            String path = arg.substring(9);
            LOGGER.info("# loading tuner datasource from {}", path);
            Globals.setTunerDatasource(SQLiteTunerDatasource.openOrInitialize(path));
        } else if (arg.startsWith("-hash=")) {
            int szBytes = Integer.parseInt(arg.substring(6)) * 1024 * 1024;
            TTHolder.getInstance().resizeMainTable(szBytes);
        } else if (arg.startsWith("-phash=")) {
            int szBytes = Integer.parseInt(arg.substring(7)) * 1024 * 1024;
            TTHolder.getInstance().resizePawnTable(szBytes);
        } else if (arg.startsWith("-eval=")) {
            String path = arg.substring(6);
            LOGGER.info("# loading eval properties from {}", path);
            Globals.setEvalWeights(EvalWeightsUtil.load(path));
        } else if (arg.startsWith("-nn=")) {
            String path = arg.substring(4);
            LOGGER.info("# loading model from " + path);
            Globals.setPredictor(ModelLoader.load(path));
        } else if (arg.startsWith("-epd=")) {
            epdFile = arg.substring(5);
            LOGGER.info("# epd {}", epdFile);
        } else if (arg.startsWith("-out=")) {
            outFile = arg.substring(5);
            LOGGER.info("# outFile {}", outFile);
        } else if (arg.startsWith("-test")) {
            testMode = true;
        } else if (arg.startsWith("-label")) {
            labelMode = true;
        } else if (arg.startsWith("-zuri")) {
            zuriFormat = true;
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
        SearchOptions opts = SearchOptions.builder().avoidNative(true).build();
        new AlphaBetaSearch().search(new Board(),
                new SearchParameters(3, -Constants.CHECKMATE, Constants.CHECKMATE), opts);
        TTHolder.getInstance().clearTables();
    }

    public static void main(String[] args) throws Exception {

        // send "done=0" to prevent XBoard timing out during the initialization sequence.
        LOGGER.info("done=0");

        LOGGER.info("# Welcome to chess4j version 6.0!\n\n");

        assert(showDebugMode());

        for (String arg : args) {
            processArgument(arg);
        }

        warmUp();

        if (testMode) {
            TestSuiteProcessor tp = new TestSuiteProcessor();
            tp.processTestSuite(epdFile, depth, time);
        } else if (labelMode) {
            List<FENRecord> fenRecords = EPDParser.load(epdFile, zuriFormat);
            FENLabeler fenLabeler = new FENLabeler();
            fenLabeler.label(fenRecords, 0);
            FENCSVWriter.writeToCSV(fenRecords, outFile);
        } else {
            repl();
        }
    }

}
