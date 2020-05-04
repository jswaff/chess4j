package com.jamesswafford.chess4j.utils;

import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.io.DrawBoard;
import com.jamesswafford.chess4j.io.EPDOperation;
import com.jamesswafford.chess4j.io.EPDParser;
import com.jamesswafford.chess4j.io.MoveParser;
import com.jamesswafford.chess4j.search.SearchIteratorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteProcessor {

    private static final  Logger LOGGER = LogManager.getLogger(TestSuiteProcessor.class);

    private List<Move> getBestMoves(Board b,List<EPDOperation> ops) throws ParseException, IllegalMoveException {
        List<Move> bms = new ArrayList<>();

        MoveParser parser = new MoveParser();
        for (EPDOperation op : ops) {
            if ("bm".equalsIgnoreCase(op.getEpdOpcode())) {
                List<String> operands = op.getEpdOperands();
                for (String operand : operands) {
                    Move bm = parser.parseMove(operand, b);
                    bms.add(bm);
                }
            }
        }

        return bms;
    }

    private void printSummary(int numProblems,List<String> wrongProblems) {
        LOGGER.info("\n\ntest suite complete!");
        LOGGER.info("# problems: " + numProblems);
        DecimalFormat df = new DecimalFormat("0.0");
        int numCorrect = numProblems - wrongProblems.size();
        double pctCorrect = (double) numCorrect / (double) numProblems * 100;
        LOGGER.info("# correct: " + numCorrect + " (" + df.format(pctCorrect) + "%)");
        if (wrongProblems.size()>0) {
            LOGGER.info("incorrect problems:");
            for (String prob : wrongProblems) {
                LOGGER.info(prob);
            }
        }
    }

    private boolean processProblem(String epd, int maxDepth, int secondsPerProblem) throws Exception {
        LOGGER.info("\n\nprocessing epd: " + epd);
        Board board = new Board();
        List<EPDOperation> ops = EPDParser.setPos(board, epd);
        DrawBoard.drawBoard(board);
        List<Move> bms = getBestMoves(board, ops);
        LOGGER.info("best moves: ");
        bms.forEach(bm -> LOGGER.info("\t" + bm));
        TTHolder.getInstance().clearTables();

        SearchIteratorImpl searchIterator = new SearchIteratorImpl();
        searchIterator.setEarlyExitOk(false);
        searchIterator.setMaxDepth(maxDepth);

        List<Move> pv = searchIterator.findPvFuture(board, new ArrayList<>()).get();

        return bms.contains(pv.get(0));
    }

    public void processTestSuite(String testSuite, int maxDepth, int secondsPerProblem) throws Exception {
        LOGGER.info("processing. test suite: " + testSuite);
        LOGGER.info("max depth: " + maxDepth);
        LOGGER.info("seconds per problem: " + secondsPerProblem);

        List<String> wrongProblems = new ArrayList<>();
        int numProblems = 0;

        Path path = FileSystems.getDefault().getPath(testSuite);
        List<String> lines = Files.readAllLines(path, Charset.defaultCharset() );
        for (String line : lines) {
            numProblems++;
            boolean correct = true;
            if (!processProblem(line, maxDepth, secondsPerProblem)) {
                correct = false;
                wrongProblems.add(line);
            }
            LOGGER.info("\n" + (correct?"correct":"incorrect") + " - current score: "
                    + (numProblems-wrongProblems.size() + " / " + numProblems));
        }

        printSummary(numProblems, wrongProblems);
    }

}
