package dev.jamesswafford.chess4j;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.book.SQLiteBook;
import dev.jamesswafford.chess4j.eval.EvalWeights;
import dev.jamesswafford.chess4j.hash.TTHolder;
import dev.jamesswafford.chess4j.init.Initializer;
import dev.jamesswafford.chess4j.io.*;
import dev.jamesswafford.chess4j.nn.NeuralNetwork;
import dev.jamesswafford.chess4j.search.AlphaBetaSearch;
import dev.jamesswafford.chess4j.search.SearchOptions;
import dev.jamesswafford.chess4j.search.SearchParameters;
import dev.jamesswafford.chess4j.tuner.LogisticRegressionTuner;
import dev.jamesswafford.chess4j.utils.TestSuiteProcessor;
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

        LOGGER.info("# Welcome to chess4j version 6.1!\n\n");

        assert(showDebugMode());

        Options options = createCommandLineOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        processCommandLineOptions(options, commandLine);
        warmUp();

        String mode = commandLine.hasOption("mode") ? commandLine.getOptionValue("mode") : "normal";
        if ("bookbuild".equals(mode)) {
            runInBookBuildingMode(commandLine);
        } else if ("test".equals(mode)) {
            runInTestMode(commandLine);
        } else if ("label".equals(mode)) {
            runInLabelMode(commandLine);
        } else if ("tune".equals(mode)) {
            runInTuningMode(commandLine);
        } else if ("normal".equals(mode)) {
            repl();
        } else {
            LOGGER.error("unknown runtime mode {}", mode);
        }
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption(new Option("?", "help", false, "Display help information"));
        options.addOption(new Option("native",  "Run native engine"));

        options.addOption(createOptionWithArg("book", "bookfile", "Specify and enable opening book"));
        options.addOption(createOptionWithArg("depth", "depth", "Maximum search depth"));
        options.addOption(createOptionWithArg("csv", "csvfile", "Specify a CSV file"));
        options.addOption(createOptionWithArg("epd", "epdfile", "Specify an EPD file"));
        options.addOption(createOptionWithArg("pgn", "pgnfile", "Specify a PGN file"));
        options.addOption(createOptionWithArg("eval", "propsFile", "Use custom eval weights"));
        options.addOption(createOptionWithArg("hash", "mb", "Specify hash size in mb"));
        options.addOption(createOptionWithArg("epochs", "epochs", "Number of epochs for tuning"));
        options.addOption(createOptionWithArg("lr", "learningRate", "Learning rate for tuning"));
        options.addOption(createOptionWithArg("mode", "mode", "Runtime mode: normal | bookbuild | label | test | tune"));
        options.addOption(createOptionWithArg("nn", "weightsFile", "Load neural net"));
        options.addOption(createOptionWithArg("out", "outfile", "Specify an output file"));
        options.addOption(createOptionWithArg("phash", "mb", "Specify pawn hash size in mb"));
        options.addOption(createOptionWithArg("time", "time", "Maximum time per search in seconds"));

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
            Globals.setNeuralNetwork(new NeuralNetwork(commandLine.getOptionValue("nn")));
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

    private static void runInBookBuildingMode(CommandLine commandLine) {
        if (!commandLine.hasOption("book")) {
            throw new IllegalArgumentException("bookbuild mode must be used in conjunction with book");
        }
        if (!commandLine.hasOption("pgn")) {
            throw new IllegalArgumentException("bookbuild must be used in conjunction with pgn");
        }
        Globals.getOpeningBook().ifPresent(openingBook -> {
            File pgnFile = new File(commandLine.getOptionValue("pgn"));
            openingBook.addToBook(pgnFile);
        });
    }

    private static void runInTestMode(CommandLine commandLine) throws Exception {
        if (!commandLine.hasOption("epd")) {
            throw new IllegalArgumentException("test mode must be used in conjunction with epd");
        }
        String epdFile = commandLine.getOptionValue("epd");

        int depth = 0;
        if (commandLine.hasOption("depth")) {
            depth = Integer.parseInt(commandLine.getOptionValue("depth"));
        } else {
            LOGGER.warn("optional parameter depth not specified");
        }

        int time = 10;
        if (commandLine.hasOption("time")) {
            time = Integer.parseInt(commandLine.getOptionValue("time"));
        } else {
            LOGGER.warn("optional parameter time not specified");
        }

        LOGGER.info("running in test mode epd {} depth {} time {}", epdFile, depth, time);
        TestSuiteProcessor tp = new TestSuiteProcessor();
        tp.processTestSuite(epdFile, depth, time);
    }

    private static void runInLabelMode(CommandLine commandLine) {
        if (!commandLine.hasOption("csv")) {
            throw new IllegalArgumentException("label mode must be used in conjunction with csv");
        }
        String inFile = commandLine.getOptionValue("csv");

        if (!commandLine.hasOption("out")) {
            throw new IllegalArgumentException("label mode must be used in conjunction with out");
        }
        String outFile = commandLine.getOptionValue("out");

        int depth = -1;
        if (commandLine.hasOption("depth")) {
            depth = Integer.parseInt(commandLine.getOptionValue("depth"));
        } else {
            LOGGER.warn("optional parameter depth not specified.  HCE will be used.");
        }

        FENCSVUtils.relabel(inFile, outFile, depth);
    }

    private static void runInTuningMode(CommandLine commandLine) throws IOException {
        if (!commandLine.hasOption("epd")) {
            throw new IllegalArgumentException("tune mode must be used in conjunction with epd");
        }
        String epdFile = commandLine.getOptionValue("epd");

        if (!commandLine.hasOption("out")) {
            throw new IllegalArgumentException("tune mode must be used in conjunction with out");
        }
        String outFile = commandLine.getOptionValue("out");

        double learningRate = 1.0;
        if (commandLine.hasOption("lr")) {
            learningRate = Double.parseDouble(commandLine.getOptionValue("lr"));
        } else {
            LOGGER.warn("optional parameter lr not specified");
        }

        int epochs = 100;
        if (commandLine.hasOption("epochs")) {
            epochs = Integer.parseInt(commandLine.getOptionValue("epochs"));
        } else {
            LOGGER.warn("optional parameter epochs not specified");
        }

        List<FENRecord> fenRecords = EPDParser.load(epdFile);

        LogisticRegressionTuner tuner = new LogisticRegressionTuner();
        Tuple2<EvalWeights, Double> optimizedWeights =
                tuner.optimize(Globals.getEvalWeights(), fenRecords, learningRate, epochs);
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
