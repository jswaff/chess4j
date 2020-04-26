package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.App;
import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.search.SearchIterator;
import com.jamesswafford.chess4j.utils.GameResult;
import com.jamesswafford.chess4j.utils.GameStatus;
import com.jamesswafford.chess4j.utils.GameStatusChecker;
import com.jamesswafford.chess4j.utils.Perft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class InputParser {

    private static final  Logger LOGGER = LogManager.getLogger(InputParser.class);

    private static final InputParser INSTANCE = new InputParser();

    private SearchIterator searchIterator = new SearchIterator(); // TODO: this feels misplaced
    private CompletableFuture<List<Move>> searchFuture;
    private Color engineColor;
    private boolean forceMode = true;
    private int maxMemoryMB = 0;

    private Map<String, Consumer<String[]>> cmdMap = new HashMap<>() {{
        put("accepted", InputParser::noOp);
        put("bk", (String[] cmd) -> PrintBookMoves.printBookMoves(Globals.getBoard()));
        put("computer", InputParser::noOp);
        put("db", (String[] cmd) -> DrawBoard.drawBoard(Globals.getBoard()));
        put("easy", InputParser::noOp);
        put("eval", (String[] cmd) -> LOGGER.info("eval: {}",  Eval.eval(Globals.getBoard())));
        put("force", InputParser.this::force);
        put("go", InputParser.this::go);
        put("hard", InputParser::noOp);
        put("hint", InputParser::noOp);
        put("level", InputParser::level);
        put("memory", InputParser.this::memory);
        put("new", InputParser.this::newGame);
        put("nopost", (String[] cmd) -> searchIterator.setPost(false));
        put("otim", InputParser::noOp);
        put("perft", (String[] cmd) -> Perft.executePerft(Globals.getBoard(), Integer.parseInt(cmd[1])));
        put("pgn2book", InputParser::pgn2book);
        put("ping", InputParser.this::ping);
        put("post", (String[] cmd) -> searchIterator.setPost(true));
        put("protover", InputParser::protover);
        put("quit", InputParser.this::quit);
        put("random", InputParser::noOp);
        put("rating", InputParser::noOp);
        put("rejected", InputParser::noOp);
        put("remove", InputParser.this::remove);
        put("result", InputParser.this::result);
        put("sd", InputParser.this::sd);
        put("st", InputParser.this::st);
        put("setboard", InputParser::setboard);
        put("time", InputParser::time);
        put("undo", InputParser::undo);
        put("usermove", InputParser.this::usermove);
        put("xboard", InputParser::noOp);
        put("?", InputParser.this::moveNow);
    }};

    private InputParser() { }

    public static InputParser getInstance() {
        return INSTANCE;
    }

    public void parseCommand(String command) throws IllegalMoveException, ParseException {
        LOGGER.debug("# parsing: " + command);

        String[] input = command.split("\\s+");
        String cmd = input[0];

        if (cmdMap.containsKey(input[0])) {
            cmdMap.get(input[0]).accept(input);
        } else {
            throw new ParseException("Invalid command: " + cmd);
        }
    }

    private void force(String[] cmd) {
        stopSearchThread();
        forceMode = true;
    }

    /**
     * Leave force mode and set the engine to play the color that is on move. Associate the engine's
     * clock with the color that is on move, the opponent's clock with the color that is not on move.
     * Start the engine's clock. Start thinking and eventually make a move.
     *
     */
    private void go(String[] cmd) {
        stopSearchThread();
        forceMode = false;
        thinkAndMakeMove();
    }

    /**
     * level MPS BASE INC
     *
     * Sets the time controls.
     */
    private static void level(String[] cmd) {
        String mps = cmd[1];
        String base = cmd[2];
        double increment = Double.parseDouble(cmd[3]);
        LOGGER.debug("# level: " + mps + ", " + base + ", " + increment);
        increment *= 1000;
        LOGGER.debug("# increment: " + increment + " ms.");
        // TODO: timer
        //SearchIterator.incrementMS = increment.intValue();
    }

    /**
     * memory N
     *
     * This command informs the engine on how much memory it is allowed to use maximally, in MegaBytes.
     * On receipt of this command, the engine should adapt the size of its hash tables accordingly.
     * This command does only fix the total memory use, the engine has to decide for itself (or be
     * configured by the user by other means) how to divide up the available memory between the various
     * tables it wants to use (e.g. main hash, pawn hash, tablebase cache, bitbases). This command will
     * only be sent to engines that have requested it through the memory feature, and only at the start
     * of a game, as the first of the commands to relay engine option settings just before each "new"
     * command.
     */
    private void memory(String[] cmd) {
        int maxMemoryMB = Integer.parseInt(cmd[1]);

        LOGGER.debug("# received memory command, N=" + maxMemoryMB);

        if (maxMemoryMB != this.maxMemoryMB) {
            int maxMemPerTable = maxMemoryMB * 1024 * 1024 / 3; // DP, AR and pawn
            TTHolder.maxEntries = maxMemPerTable / TTHolder.getAlwaysReplaceTransTable().sizeOfEntry(); // note DP and AR are the same
            TTHolder.maxPawnEntries = maxMemPerTable / TTHolder.getPawnTransTable().sizeOfEntry();
            TTHolder.initTables();
            this.maxMemoryMB = maxMemoryMB;

            // suggest to the JVM that now is good time to garbage collect the previous tables
            System.gc();
        } else {
            LOGGER.debug("# memory usage unchanged, skipping new table instantiation.");
        }
    }

    /**
     * Move now. If your engine is thinking, it should move immediately; otherwise, the command should
     * be ignored (treated as a no-op). It is permissible for your engine to always ignore the ? command.
     * The only bad consequence is that xboard's Move Now menu command will do nothing.
     */
    private void moveNow(String[] cmd) {
//        if (!SearchIterator.isPondering()) {
            stopSearchThread();
//        }
    }

    private void newGame(String[] cmd) {
        stopSearchThread();
        forceMode = false;
        Globals.getBoard().resetBoard();
        Globals.gameUndos.clear();
        engineColor = Color.BLACK;
        searchIterator.setMaxDepth(0);
    }

    private static void noOp(String[] cmd) {
        LOGGER.debug("# no op: " + cmd[0]);
    }

    // TODO: ProcessPgnCmd
    private static void pgn2book(String[] cmd) {
        String fName = cmd[1];
        File pgnFile = new File(fName);
        if (pgnFile.exists()) {
            long startTime = System.currentTimeMillis();
            LOGGER.info("processing pgn: " + fName + " ...");
            LOGGER.info("doing dry run...");
            int n;
            try {
                n = processPGNFile(pgnFile,true);
                LOGGER.info("\nadding " + n + " games to book...");
                processPGNFile(pgnFile,false);
                DecimalFormat df = new DecimalFormat("0.00");
                long elapsed = System.currentTimeMillis() - startTime;
                LOGGER.info("\nfinished in " + df.format((double) elapsed /1000.0) + " seconds.");
            } catch (IOException e) {
                LOGGER.error("There was an I/O error processing the pgn file", e);
            } catch (IllegalMoveException e) {
                LOGGER.error("Illegal move found in PGN file", e);
            }
        } else {
            LOGGER.warn("file " + fName + " not found.");
        }
    }

    private static int processPGNFile(File pgnFile, boolean dryRun) throws IOException, IllegalMoveException {
        int n = 0;

        try {
            if (!dryRun) {
                App.getOpeningBook().dropIndexes();
            }

            FileInputStream fis = new FileInputStream(pgnFile);
            PGNIterator it = new PGNIterator(fis);
            PGNGame pgnGame;
            while ((pgnGame = it.next()) != null) {
                if ((n % 1000)==0) {
                    LOGGER.info(".");
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
     * Because of the way xboard uses the ping command, we should never see a "ping" command
     * when the engine is on move.  However, if we do, we should not respond with the "pong"
     * until after making the move.  The exception is pondering -- if the engine is pondering
     * it should immediately respond with the pong but continue pondering.
     *
     * The documentation specifically mentions a "?" followed by "ping".  In this case
     * the pong should not be sent until after the move is made.
     *
     */
    private void ping(String[] cmd) {
//        if (!SearchIterator.isPondering()) {
            stopSearchThread();
//        }
        // TODO: don't think we should stop the search thread, just wait for it.
        LOGGER.info("pong " + cmd[1]);
    }

    private static void protover(String[] cmd) {
        int version = Integer.parseInt(cmd[1]);
        if (version < 2) {
            LOGGER.error("Error: invalid protocol version.");
            System.exit(1);
        }
        // only sending the features where we want the non-default value
        LOGGER.info("feature analyze=0 colors=0 ping=1 draw=0 debug=1");
        LOGGER.info("feature name=0 nps=0 memory=1");
        LOGGER.info("feature setboard=1 sigint=0 sigterm=0 usermove=1");
        LOGGER.info("feature variants=\"normal\" myname=\"chess4j\"");
        LOGGER.info("feature done=1"); // must be last
    }

    private void quit(String[] cmd) {
        stopSearchThread();
        LOGGER.info("bye...");
        System.exit(0);
    }

    /**
    * Retract a move.  Undoes the last two moves and continues playing the same color.
    * Xboard sends this command only when the user is on move.
    */
    private void remove(String[] cmd) {
        stopSearchThread();
        Globals.getBoard().undoMove(Globals.gameUndos.remove(Globals.gameUndos.size()-1));
        Globals.getBoard().undoMove(Globals.gameUndos.remove(Globals.gameUndos.size()-1));
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
    private void result(String[] cmd) {
        stopSearchThread();

        String result = cmd[1];
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

        LOGGER.info("# result : " + result + " : " + gameResult);

        List<Undo> undos = Globals.gameUndos;
        List<Move> gameMoves = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Undo undo : undos) {
            gameMoves.add(undo.getMove());
            sb.append(undo.getMove().toString()).append(" ");
        }

        LOGGER.info("# game moves: " + sb.toString());

        App.getOpeningBook().learn(gameMoves, engineColor, gameResult);
    }


    /**
     * sd DEPTH
     * The engine will limit its thinking to DEPTH ply.  Note the engine will still respect
     * any time control restrictions in place.
     *
     */
    private void sd(String[] cmd) {
        int depth = Integer.parseInt(cmd[1]);
        LOGGER.debug("# setting depth to : " + depth);
        searchIterator.setMaxDepth(depth);
    }

    private static void setboard(String[] cmd) {
        StringBuilder fen = new StringBuilder();
        for (int i=1;i<cmd.length;i++) {
            if (i>1) {
                fen.append(" ");
            }
            fen.append(cmd[i]);
        }

        Board board = Globals.getBoard().deepCopy();
        try {
            Globals.getBoard().setPos(fen.toString());
            Globals.gameUndos.clear();
        } catch (ParseException e) {
            LOGGER.info("tellusererror illegal position");
            // TODO: revert back to board
            Globals.getBoard().resetBoard();
            throw e;
        }
        DrawBoard.drawBoard(Globals.getBoard());
    }

    /**
     * st TIME
     * Set an exact number of seconds to play per move.
     */
    private void st(String[] cmd) {
        // TODO
    }

    /**
     * Read in the engine's remaining time, in centiseconds.
     */
    private static void time(String[] cmd) {
        int time = Integer.parseInt(cmd[1]);
        time *= 10; // centiseconds to milliseconds
        LOGGER.debug("# MY TIME: " + time);
        // TODO: timer
        //SearchIterator.remainingTimeMS = time;
    }

    /**
    * Back up one move.  Xboard will never send this without putting the engine in
    * "force" mode first.  We don't have to worry about undoing a move the engine made.
    */
    private static void undo(String[] cmd) {
        Globals.getBoard().undoMove(Globals.gameUndos.remove(Globals.gameUndos.size()-1));
    }

    /**
     * Sent when the user makes a move and the engine is already playing the opposite color.
     * The engine may or may not be pondering.
     */
    private void usermove(String[] cmd)  {
        String strMove = cmd[1];
        MoveParser mp = new MoveParser();
        Move mv = mp.parseMove(strMove, Globals.getBoard());
        Globals.gameUndos.add(Globals.getBoard().applyMove(mv));

        if (!forceMode) {
            thinkAndMakeMove();
        }
    }

    /**
     * Stop the search thread, which may or may not be in pondering mode.
     *
     */
    private void stopSearchThread() {
        if (searchFuture==null) {
            return;
        }

        try {
            // TODO: iterator.abort
            searchFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void thinkAndMakeMove() {
        // associate clock with player on move
        engineColor = Globals.getBoard().getPlayerToMove();

        if (!endOfGameCheck()) {
            searchFuture = searchIterator.findPvFuture(Globals.getBoard(), Globals.gameUndos)
                    .thenApply(
                        pv -> {
                            Globals.gameUndos.add(Globals.getBoard().applyMove(pv.get(0)));
                            LOGGER.info("move " + pv.get(0));
                            endOfGameCheck();
                            return pv;
                        }
                    );
        }
    }

    private static boolean endOfGameCheck() {
        GameStatus gameStatus = GameStatusChecker.getGameStatus(Globals.getBoard(), Globals.gameUndos);
        if (gameStatus != GameStatus.INPROGRESS) {
            PrintGameResult.printResult(gameStatus);
            return true;
        }
        return false;
    }
}
