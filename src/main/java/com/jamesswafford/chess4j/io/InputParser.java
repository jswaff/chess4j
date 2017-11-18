package com.jamesswafford.chess4j.io;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.App;
import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.BookMove;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.search.Search;
import com.jamesswafford.chess4j.search.SearchIterator;
import com.jamesswafford.chess4j.utils.GameResult;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;
import com.jamesswafford.chess4j.utils.Perft;

public class InputParser {

    private static final Log logger = LogFactory.getLog(InputParser.class);
    private static final InputParser INSTANCE = new InputParser();
    private boolean forceMode;
    private Thread searchThread;
    private Color engineColor;

    private InputParser() { }

    public static InputParser getInstance() {
        return INSTANCE;
    }

    public void parseCommand(String command) throws IllegalMoveException,ParseException {
        logger.info("# parsing: " + command);

        String[] input = command.split("\\s+");
        String cmd = input[0];
        if ("accepted".equals(cmd)) {
        } else if ("analyze".equals(cmd)) {
        } else if ("black".equals(cmd)) {
        } else if ("bk".equals(cmd)) {
            bk();
        } else if ("bkmoves".equals(cmd)) {
            bkmoves();
        } else if ("computer".equals(cmd)) {
        } else if ("db".equals(cmd)) {
            db();
        } else if ("draw".equals(cmd)) {
        } else if ("easy".equals(cmd)) {
            SearchIterator.ponderEnabled = false;
        } else if ("eval".equals(cmd)) {
            eval();
        } else if ("force".equals(cmd)) {
            force();
        } else if ("go".equals(cmd)) {
            go();
        } else if ("hard".equals(cmd)) {
            SearchIterator.ponderEnabled = true;
        } else if ("hint".equals(cmd)) {
        } else if ("ics".equals(cmd)) {
        } else if ("level".equals(cmd)) {
            level(input);
        } else if ("name".equals(cmd)) {
            logger.info("# opponent is: " + input[1]);
        } else if ("new".equals(cmd)) {
            newGame();
        } else if ("nopost".equals(cmd)) {
            SearchIterator.showThinking = false;
        } else if ("otim".equals(cmd)) {
        } else if ("perft".equals(cmd)) {
            perft(input);
        } else if ("pgn2book".equals(cmd)) {
            pgn2book(input);
        } else if ("playother".equals(cmd)) {
            playother();
        } else if ("ping".equals(cmd)) {
            ping(input);
        } else if ("post".equals(cmd)) {
            SearchIterator.showThinking = true;
        } else if ("protover".equals(cmd)) {
            protover(input);
        } else if ("quit".equals(cmd)) {
            quit();
        } else if ("random".equals(cmd)) {
        } else if ("rating".equals(cmd)) {
        } else if ("rejected".equals(cmd)) {
        } else if ("remove".equals(cmd)) {
            remove();
        } else if ("result".equals(cmd)) {
            result(input);
        } else if ("sd".equals(cmd)) {
            sd(input);
        } else if ("setboard".equals(cmd)) {
            setboard(input);
        } else if ("st".equals(cmd)) {
        } else if ("time".equals(cmd)) {
            time(input);
        } else if ("undo".equals(cmd)) {
            undo();
        } else if ("usermove".equals(cmd)) {
            usermove(input);
        } else if ("variant".equals(cmd)) {
            variant(input);
        } else if ("white".equals(cmd)) {
        } else if ("xboard".equals(cmd)) {
        } else if ("?".equals(cmd)) {
            moveNow();
        } else {
            throw new ParseException("Invalid command: " + cmd);
        }
    }

    private void bk() {
        SearchIterator.useOpeningBook = !SearchIterator.useOpeningBook;
        if (SearchIterator.useOpeningBook) {
            logger.info("\topening book on.\n\n");
        } else {
            logger.info("\topening book off.\n\n");
        }
    }

    private void bkmoves() {
        List<BookMove> bookMoves = App.getOpeningBook().getMoves(Board.INSTANCE);
        logger.info("book moves:");
        for (BookMove bookMove : bookMoves) {
            logger.info("\t" + bookMove);
        }
    }

    private void db() {
        DrawBoard.drawBoard(Board.INSTANCE);
    }

    private void eval() {
        int eval = Eval.eval(Board.INSTANCE);
        logger.info("eval=" + eval);
    }

    private void force() {
        stopSearchThread();
        forceMode = true;
    }

    /**
     * Leave force mode and set the engine to play the color that is on move. Associate the engine's
     * clock with the color that is on move, the opponent's clock with the color that is not on move.
     * Start the engine's clock. Start thinking and eventually make a move.
     *
     */
    private void go() {
        stopSearchThread();
        forceMode = false;
        thinkAndMakeMove();
    }

    private void level(String[] input) {
        String mps = input[1];
        String base = input[2];
        Double increment = Double.valueOf(input[3]);
        logger.debug("# level: " + mps + ", " + base + ", " + increment);
        increment *= 1000;
        logger.debug("# increment: " + increment + " ms.");
        SearchIterator.incrementMS = increment.intValue();
    }

    /**
     * Move now. If your engine is thinking, it should move immediately; otherwise, the command should
     * be ignored (treated as a no-op). It is permissible for your engine to always ignore the ? command.
     * The only bad consequence is that xboard's Move Now menu command will do nothing.
     */
    private void moveNow() {
        if (!SearchIterator.isPondering()) {
            stopSearchThread();
        }
    }

    private void newGame() {
        stopSearchThread();
        forceMode = false;
        Board.INSTANCE.resetBoard();
        engineColor = Color.BLACK;
        SearchIterator.maxDepth = 0;
    }

    private void perft(String[] input) {
        int depth = Integer.valueOf(input[1]);
        DrawBoard.drawBoard(Board.INSTANCE);
        long start = System.currentTimeMillis();
        long nodes=Perft.perft(Board.INSTANCE, depth);
        long end = System.currentTimeMillis();
        if (end==start) end=start+1; // HACK to avoid div 0
        DecimalFormat df = new DecimalFormat("0,000");
        logger.info("# nodes: " + df.format(nodes));
        logger.info("# elapsed time: " + (end-start) + " ms");
        logger.info("# rate: " + df.format(nodes*1000/(end-start)) + " n/s\n");
    }

    private void pgn2book(String[] input) {
        String fName = input[1];
        java.io.File f = new java.io.File(fName);
        if (f.exists()) {
            long startTime = System.currentTimeMillis();
            logger.info("processing pgn: " + fName + " ...");
            logger.info("doing dry run...");
            int n;
            try {
                n = processPGNFile(f,true);
                logger.info("\nadding " + n + " games to book...");
                processPGNFile(f,false);
                DecimalFormat df = new DecimalFormat("0.00");
                long elapsed = System.currentTimeMillis() - startTime;
                logger.info("\nfinished in " + df.format(Double.valueOf(elapsed)/1000.0) + " seconds.");
                Board.INSTANCE.resetBoard();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("file " + fName + " not found.");
        }
    }

    private int processPGNFile(java.io.File f,boolean dryRun) throws Exception {
        int n = 0;

        try {
            if (!dryRun) {
                App.getOpeningBook().dropIndexes();
            }

            FileInputStream fis = new FileInputStream(f);
            PGNIterator it = new PGNIterator(fis);
            PGNGame pgnGame;
            while ((pgnGame = it.next()) != null) {
                if ((n % 1000)==0) {
                    System.out.println(".");
                }
                if (!dryRun) {
                    App.getOpeningBook().addToBook(pgnGame);
                }
                n++;
            }
            fis.close();
        } finally {
            if (!dryRun) {
                App.getOpeningBook().addIndexes();
            }
        }

        return n;
    }

    /**
    * Leave force mode and set the engine to play the color that is not on move.
    * Associate the opponent's clock with the color that is on move, the engine's
    * clock with the color that is not on move.  Start the opponent's clock.  If
    * pondering is enabled, the engine should begin pondering.  If the engine later
    * receives a move, it should start thinking and eventually reply.
    */
    private void playother() {
        // TODO
    }

    /**
     * Because of the way xboard uses the ping command, we should never see a "ping" command
     * when the engine is on move.  However, if we do, we should not respond with the "pong"
     * until after making the move.  The exception is pondering -- if the engine is pondering
     * it should immediately respond with the pong but continue pondering.
     *
     * The documentation specifically mentions a "?" followed by "ping".  In this case
     * the pong should not be sent until after the move is made.
     *
     */
    private void ping(String[] input) {
        if (!SearchIterator.isPondering()) {
            stopSearchThread();
        }
        logger.info("pong " + input[1]);
    }

    private void protover(String[] input) {
        Integer version = Integer.valueOf(input[1]);
        if (version < 2) {
            logger.info("Error: invalid protocol version.");
            System.exit(1);
        }
        logger.info("feature analyze=0 black=0 colors=0 ping=1 draw=0 debug=1 edit=0 ics=1");
        logger.info("feature level=0 name=1 nps=0 memory=0 playother=0 pause=0 resume=0 reuse=1 san=0");
        logger.info("feature setboard=1 sigint=0 sigterm=0 smp=0 st=0 time=1 usermove=1");
        logger.info("feature white=0 variants=\"normal\" myname=\"chess4j\"");
        logger.info("feature done=1"); // must be last
    }

    private void quit() {
        stopSearchThread();
        logger.info("bye...");
        System.exit(0);
    }

    /**
    * Retract a move.  Undoes the last two moves and continues playing the same color.
    * Xboard sends this command only when the user is on move.
    */
    private void remove() {
        stopSearchThread();
        Board.INSTANCE.undoLastMove();
        Board.INSTANCE.undoLastMove();
    }

    /**
     * RESULT is either 1-0, 0-1, 1/2-1/2, or *, indicating whether white won, black won,
     * the game was drawn, or the game unfinished.  The COMMENT string is purely human
     * readable.  It is subject to change.
     *
     * We will get a RESULT command even if we already know the game ended.  If we send
     * the RESULT command (e.g. after mating the opponent), we will still get a RESULT
     * command back.
     *
     * We will NOT get a RESULT command if the user stops playing by selecting Reset,
     * Edit Game, Exit, or the like.
     */
    private void result(String[] input) {
        stopSearchThread();

        String result = input[1];
        GameResult gameResult = GameResult.UNKNOWN;
        if ("1-0".equals(result)) {
            gameResult = Color.WHITE.equals(engineColor) ? GameResult.WIN : GameResult.LOSS;
        } else if ("0-1".equals(result)) {
            gameResult = Color.BLACK.equals(engineColor) ? GameResult.WIN : GameResult.LOSS;
        } else if ("1/2-1/2".equals(result)) {
            gameResult = GameResult.DRAW;
        } else if ("*".equals(result)) {
            gameResult = GameResult.ADJOURNED;
        }

        logger.info("# result : " + result + " : " + gameResult);

        List<Undo> undos = Board.INSTANCE.getUndos();
        List<Move> gameMoves = new ArrayList<Move>();
        StringBuilder sb = new StringBuilder();
        for (Undo undo : undos) {
            gameMoves.add(undo.getMove());
            sb.append(undo.getMove().toString() + " ");
        }

        logger.info("# game moves: " + sb.toString());

        App.getOpeningBook().learn(gameMoves, engineColor, gameResult);
    }


    /**
     * sd DEPTH
     * The engine will limit its thinking to DEPTH ply.  Note the engine will still respect
     * any time control restrictions in place.
     *
     */
    private void sd(String[] input) {
        Integer depth = Integer.valueOf(input[1]);
        logger.info("# setting depth to : " + depth);
        SearchIterator.maxDepth = depth;
    }

    private void setboard(String[] input) {
        StringBuilder fen = new StringBuilder();
        for (int i=1;i<input.length;i++) {
            if (i>1) {
                fen.append(" ");
            }
            fen.append(input[i]);
        }
        try {
            FenParser.setPos(Board.INSTANCE,fen.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DrawBoard.drawBoard(Board.INSTANCE);
    }

    /**
     * Read in the engine's remaining time, in centiseconds.
     */
    private void time(String[] input) {
        Integer time = Integer.valueOf(input[1]);
        time *= 10; // centiseconds to milliseconds
        logger.info("# MY TIME: " + time);
        SearchIterator.remainingTimeMS = time;
    }

    /**
    * Back up one move.  Xboard will never send this without putting the engine in
    * "force" mode first.  We don't have to worry about undoing a move the engine made.
    */
    private void undo() {
        Board.INSTANCE.undoLastMove();
    }

    /**
     * Sent when the user makes a move and the engine is already playing the opposite color.
     * The engine may or may not be pondering.
     */
    private void usermove(String[] input) throws IllegalMoveException, ParseException {
        String strMove = input[1];
        MoveParser mp = new MoveParser();
        Move mv = mp.parseMove(strMove,Board.INSTANCE);
        Board.INSTANCE.applyMove(mv);

        if (!forceMode) {
            boolean predicted = mv.equals(SearchIterator.getPonderMove());
            logger.info("# pondering?: " + SearchIterator.isPondering() + ", predicted?: " + predicted);

            boolean startNewSearch;
            synchronized (SearchIterator.ponderMutex) {
                if (SearchIterator.isPondering() && predicted) {
                    SearchIterator.calculateSearchTimes();
                    SearchIterator.stopPondering();
                    startNewSearch = false;
                } else {
                    startNewSearch = true;
                }
            }

            if (startNewSearch) {
                stopSearchThread();
                thinkAndMakeMove();
            }
        }
    }

    private void variant(String[] input) {
        if (! "normal".equals(input[1])) {
            logger.info("Error: unsupported variant.");
        }
    }

    /**
     * Stop the search thread, which may or may not be in pondering mode.
     *
     */
    private void stopSearchThread() {
        if (searchThread==null) {
            return;
        }
        try {
            SearchIterator.abortIterator = true;
            Search.abortSearch = true;
            searchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void thinkAndMakeMove() {
        // associate clock with player on move
        engineColor = Board.INSTANCE.getPlayerToMove();

        GameStatus gs = GameStatusChecker.getGameStatus();
        if (gs != GameStatus.INPROGRESS) {
            PrintGameResult.printResult(gs);
            return;
        }

        searchThread = SearchIterator.think();
    }

}
