package com.jamesswafford.chess4j;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.book.SQLiteBook;
import com.jamesswafford.chess4j.eval.EvalWeights;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.*;
import com.jamesswafford.chess4j.nn.FENLabeler;
import com.jamesswafford.chess4j.nn.ModelLoader;
import com.jamesswafford.chess4j.search.AlphaBetaSearch;
import com.jamesswafford.chess4j.search.SearchOptions;
import com.jamesswafford.chess4j.search.SearchParameters;
import com.jamesswafford.chess4j.tuner.LogisticRegressionTuner;
import com.jamesswafford.chess4j.utils.TestSuiteProcessor;
import io.vavr.Tuple2;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;

public final class App {
    private static final  Logger LOGGER = LogManager.getLogger(App.class);

    private App() { }

    public static void main(String[] args) throws Exception {

        // send "done=0" to prevent XBoard timing out during the initialization sequence.
        LOGGER.info("done=0");

        LOGGER.info("# Welcome to chess4j version 6.0!\n\n");

        assert(showDebugMode());

        Options options = createCommandLineOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        processCommandLineOptions(options, commandLine);
        warmUp();

        if (commandLine.hasOption("test")) {
            runInTestMode(commandLine);
        } else if (commandLine.hasOption("label")) {
            runInLabelMode(commandLine);
        } else if (commandLine.hasOption("tune")) {
            runInTuningMode(commandLine);
        } else {
            repl();
        }
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption(new Option("?", "help", false, "Display help information"));
        options.addOption(new Option("native",  "Run native engine"));
        options.addOption(new Option("zuri",  "EPD parsing should use Zuri format"));

        options.addOption(createOptionWithArg("book", "bookfile", "Enable opening book"));
        options.addOption(createOptionWithArg("depth", "depth", "Maximum search depth"));
        options.addOption(createOptionWithArg("epd", "epdfile", "Specify an EPD file"));
        options.addOption(createOptionWithArg("pgn", "pgnfile", "Specify a PGN file"));
        options.addOption(createOptionWithArg("eval", "propsFile", "Use custom eval weights"));
        options.addOption(createOptionWithArg("hash", "mb", "Specify hash size in mb"));
        options.addOption(createOptionWithArg("nn", "modelfile", "Load neural net"));
        options.addOption(createOptionWithArg("out", "outfile", "Specify an output file"));
        options.addOption(createOptionWithArg("phash", "mb", "Specify pawn hash size in mb"));
        options.addOption(createOptionWithArg("time", "time", "Maximum time per search in seconds"));

        // these options are mutually exclusive
        OptionGroup group = new OptionGroup();
        group.addOption(new Option("label", "Label records in EPD file for training"));
        group.addOption(new Option("test", "Process test suite"));
        group.addOption(Option.builder("tune")
                .numberOfArgs(2)
                //.optionalArg(true)
                .valueSeparator(' ')
                .argName("lr> <iterations")
                .desc("Tune evaluation weights")
                .build());
        options.addOptionGroup(group);

        return options;
    }

    private static Option createOptionWithArg(String name, String argName, String description) {
        return Option.builder(name)
                .hasArg()
                .argName(argName)
                .desc(description)
                .build();
    }

    private static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("chess4j", options);
    }

    private static void processCommandLineOptions(Options options, CommandLine commandLine) {
        if (commandLine.hasOption("?")) printHelp(options);
        if (commandLine.hasOption("native")) Initializer.attemptToUseNative = true;
        if (commandLine.hasOption("book"))
            Globals.setOpeningBook(SQLiteBook.openOrInitialize(commandLine.getOptionValue("book")));
        if (commandLine.hasOption("eval"))
            Globals.setEvalWeights(EvalWeightsUtil.load(commandLine.getOptionValue("eval")));
        if (commandLine.hasOption("hash"))
            TTHolder.getInstance().resizeMainTable(Long.parseLong(commandLine.getOptionValue("hash")) *1024*1024);
        if (commandLine.hasOption("nn"))
            Globals.setPredictor(ModelLoader.load(commandLine.getOptionValue("nn")));
        if (commandLine.hasOption("phash"))
            TTHolder.getInstance().resizePawnTable(Long.parseLong(commandLine.getOptionValue("phash")) *1024*1024);
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

    private static void runInTestMode(CommandLine commandLine) throws Exception {
        if (!commandLine.hasOption("epd")) {
            throw new IllegalArgumentException("label must be used in conjunction with epd");
        }
        String epdFile = commandLine.getOptionValue("epd");

        int depth = 0;
        if (commandLine.hasOption("depth")) depth = Integer.parseInt(commandLine.getOptionValue("depth"));

        int time = 10;
        if (commandLine.hasOption("time")) time = Integer.parseInt(commandLine.getOptionValue("time"));

        LOGGER.info("running in test mode epd {} depth {} time {}", epdFile, depth, time);
        TestSuiteProcessor tp = new TestSuiteProcessor();
        tp.processTestSuite(epdFile, depth, time);
    }

    private static void runInLabelMode(CommandLine commandLine) throws IOException {
        if (!commandLine.hasOption("epd") && !commandLine.hasOption("pgn")) {
            throw new IllegalArgumentException("label must be used in conjunction with epd or pgn");
        }
        if (!commandLine.hasOption("out")) {
            throw new IllegalArgumentException("label must be used in conjunction with out");
        }
        String outFile = commandLine.getOptionValue("out");

        int depth = 0;
        if (commandLine.hasOption("depth")) depth = Integer.parseInt(commandLine.getOptionValue("depth"));

        List<FENRecord> fenRecords;
        if (commandLine.hasOption("epd")) {
            String epdFile = commandLine.getOptionValue("epd");
            boolean zuri = commandLine.hasOption("zuri");
            fenRecords = EPDParser.load(epdFile, zuri);
        } else { // PGN
            String pgnFile = commandLine.getOptionValue("pgn");
            fenRecords = PGNFileParser.load(pgnFile, true);
        }
        FENLabeler fenLabeler = new FENLabeler();
        fenLabeler.label(fenRecords, depth);
        FENCSVWriter.writeToCSV(fenRecords, outFile);
    }

    private static void runInTuningMode(CommandLine commandLine) throws IOException {
        String[] args = commandLine.getOptionValues("tune");
        double learningRate = Double.parseDouble(args[0]);
        int iterations = Integer.parseInt(args[1]);

        if (!commandLine.hasOption("epd")) {
            throw new IllegalArgumentException("tune must be used in conjunction with epd");
        }
        String epdFile = commandLine.getOptionValue("epd");

        if (!commandLine.hasOption("out")) {
            throw new IllegalArgumentException("tune must be used in conjunction with out");
        }
        String outFile = commandLine.getOptionValue("out");

        boolean zuri = commandLine.hasOption("zuri");

        List<FENRecord> fenRecords = EPDParser.load(epdFile, zuri);
        LogisticRegressionTuner tuner = new LogisticRegressionTuner();
        Tuple2<EvalWeights, Double> optimizedWeights =
                tuner.optimize(Globals.getEvalWeights(), fenRecords, learningRate, iterations);
        EvalWeightsUtil.store(optimizedWeights._1, outFile, "Error: " + optimizedWeights._2);
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

}
