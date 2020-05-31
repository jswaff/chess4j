package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.Undo;
import com.jamesswafford.chess4j.book.OpeningBook;
import com.jamesswafford.chess4j.eval.Eval;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.search.SearchIterator;
import com.jamesswafford.chess4j.search.SearchIteratorImpl;
import com.jamesswafford.chess4j.utils.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class XBoardHandler {

    private static final  Logger LOGGER = LogManager.getLogger(XBoardHandler.class);

    private OpeningBook openingBook;
    private int bookMisses;
    private SearchIterator searchIterator;
    private CompletableFuture<List<Move>> searchFuture;
    private Color engineColor;
    private boolean forceMode = true;
    private boolean fixedTimePerMove;
    private int incrementMs;

    private final Map<String, Consumer<String[]>> cmdMap = new HashMap<>() {{
        put("accepted", XBoardHandler::noOp);
        put("bk", (String[] cmd) -> PrintBookMoves.printBookMoves(Globals.getBoard()));
        put("computer", XBoardHandler::noOp);
        put("db", (String[] cmd) -> DrawBoard.drawBoard(Globals.getBoard()));
        put("easy", XBoardHandler::noOp);
        put("eval", (String[] cmd) -> LOGGER.info("eval: {}",  Eval.eval(Globals.getBoard())));
        put("force", XBoardHandler.this::force);
        put("go", XBoardHandler.this::go);
        put("hard", XBoardHandler::noOp);
        put("hint", XBoardHandler::noOp);
        put("level", XBoardHandler.this::level);
        put("memory", XBoardHandler.this::memory);
        put("new", XBoardHandler.this::newGame);
        put("nopost", (String[] cmd) -> searchIterator.setPost(false));
        put("otim", XBoardHandler::noOp);
        put("perft", (String[] cmd) -> Perft.executePerft(Globals.getBoard(), Integer.parseInt(cmd[1])));
        put("pgn2book", XBoardHandler.this::pgnToBook);
        put("ping", XBoardHandler.this::ping);
        put("post", (String[] cmd) -> searchIterator.setPost(true));
        put("protover", XBoardHandler::protover);
        put("quit", XBoardHandler.this::quit);
        put("random", XBoardHandler::noOp);
        put("rating", XBoardHandler::noOp);
        put("rejected", XBoardHandler::noOp);
        put("remove", XBoardHandler.this::remove);
        put("result", XBoardHandler.this::result);
        put("sd", XBoardHandler.this::sd);
        put("setboard", XBoardHandler::setboard);
        put("st", XBoardHandler.this::st);
        put("time", XBoardHandler.this::time);
        put("undo", XBoardHandler::undo);
        put("usermove", XBoardHandler.this::usermove);
        put("xboard", XBoardHandler::noOp);
        put("?", XBoardHandler.this::moveNow);
    }};

    public XBoardHandler() {
        Globals.getOpeningBook().ifPresent(openingBook1 -> this.openingBook = openingBook1);
        searchIterator = new SearchIteratorImpl();
    }

    public Color getEngineColor() {
        return engineColor;
    }

    public boolean isForceMode() {
        return forceMode;
    }

    public void setOpeningBook(OpeningBook openingBook) {
        this.openingBook = openingBook;
    }

    public void setSearchIterator(SearchIterator searchIterator) {
        this.searchIterator = searchIterator;
    }

    public void parseAndDispatch(String command) throws IllegalMoveException, ParseException {
        LOGGER.debug("# parsing: " + command);

        String[] input = command.split("\\s+");
        String cmd = input[0];

        if (cmdMap.containsKey(input[0])) {
            cmdMap.get(input[0]).accept(input);
        } else {
            LOGGER.info("Error (unknown command): " + cmd);
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
        forceMode = false;
        thinkAndMakeMove();
    }

    /**
     * Set the time control.
     * Syntax: level #moves base increment
     * Examples:
     *    level 40 5 0 - play 40 moves in 5 minutes (after which another 5 minutes, ad infinitum).
     *    level 0 2 12 - play the entire game with a 2 minute base + 12 second increment per move.
     */
    private void level(String[] cmd) {
        incrementMs = Integer.parseInt(cmd[3]) * 1000;
        fixedTimePerMove = false;
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
        TTHolder.getInstance().resizeAllTables(maxMemoryMB * 1024 * 1024);
    }

    /**
     * Move now. If the engine is thinking and it is its turn, it will stop thinking and move immediately.
     * If the engine is not thinking (or pondering), the command is ignored.
     */
    private void moveNow(String[] cmd) {
//        if (!SearchIterator.isPondering()) {  // TODO
            stopSearchThread();
//        }
    }

    private void newGame(String[] cmd) {
        stopSearchThread();
        bookMisses = 0;
        forceMode = false;
        Globals.getBoard().resetBoard();
        Globals.getGameUndos().clear();
        engineColor = Color.BLACK;
        searchIterator.setMaxDepth(0);
    }

    private static void noOp(String[] cmd) {
        LOGGER.debug("# no op: " + cmd[0]);
    }

    private void pgnToBook(String[] cmd) {
        if (openingBook != null) {
            openingBook.addToBook(new File(cmd[1]));
        } else {
            LOGGER.warn("There is no opening book.");
        }
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
//        if (!SearchIterator.isPondering()) { // TODO
//            stopSearchThread();
//        }

        // wait for any active search to finish
        if (searchFuture != null) {
            searchFuture.join();
        }
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
        // TODO: may be pondering
        Globals.getBoard().undoMove(Globals.getGameUndos().remove(Globals.getGameUndos().size()-1));
        Globals.getBoard().undoMove(Globals.getGameUndos().remove(Globals.getGameUndos().size()-1));
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

        LOGGER.info("# result: " + result + " - " + gameResult);

        List<Move> gameMoves = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Undo undo : Globals.getGameUndos()) {
            gameMoves.add(undo.getMove());
            sb.append(undo.getMove().toString()).append(" ");
        }

        LOGGER.info("# game moves: " + sb.toString());

        if (openingBook != null) {
            openingBook.learn(gameMoves, engineColor, gameResult);
        }
    }


    /**
     * sd DEPTH
     * The engine will limit its thinking to DEPTH ply.  Note the engine will still respect
     * any time control restrictions in place.
     *
     */
    private void sd(String[] cmd) {
        int depth = Integer.parseInt(cmd[1]);
        LOGGER.debug("# setting depth to {}", depth);
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

        try {
            // attempt on the copy as a "dry run"
            Globals.getBoard().deepCopy().setPos(fen.toString());
            Globals.getBoard().setPos(fen.toString());
            Globals.getGameUndos().clear();
        } catch (ParseException e) {
            LOGGER.info("tellusererror Illegal position");
        }
    }

    /**
     * st TIME
     * Set an exact number of seconds to play per move.
     */
    private void st(String[] cmd) {
        int seconds = Integer.parseInt(cmd[1]);
        LOGGER.debug("# setting search time to {} seconds per move", seconds);
        fixedTimePerMove = true;
        searchIterator.setMaxTime(seconds * 1000);
    }

    /**
     * Read in the engine's remaining time, in centiseconds.
     */
    private void time(String[] cmd) {
        int centis = Integer.parseInt(cmd[1]);
        if (fixedTimePerMove) {
            int timeMs = centis * 10 - 100; // leave 1/10th of a second for overhead
            if (timeMs < 0) timeMs = 0;
            searchIterator.setMaxTime(timeMs);
        } else {
            searchIterator.setMaxTime(TimeUtils.getSearchTime(centis * 10, incrementMs));
        }
    }

    /**
    * Back up one move.  Xboard will never send this without putting the engine in
    * "force" mode first.  We don't have to worry about undoing a move the engine made.
    */
    private static void undo(String[] cmd) {
        Globals.getBoard().undoMove(Globals.getGameUndos().remove(Globals.getGameUndos().size()-1));
    }

    /**
     * Sent when the user makes a move and the engine is already playing the opposite color.
     * The engine may or may not be pondering.
     */
    private void usermove(String[] cmd)  {
        String strMove = cmd[1];
        MoveParser mp = new MoveParser();
        Move mv = null;
        try {
            mv = mp.parseMove(strMove, Globals.getBoard());
        } catch (Exception e) {
            LOGGER.info("Illegal move: " + strMove);
        }
        if (mv != null) {
            Globals.getGameUndos().add(Globals.getBoard().applyMove(mv));
            if (!endOfGameCheck() && !forceMode) {
                thinkAndMakeMove();
            }
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

        searchIterator.stop();
        searchFuture.join();
    }

    private void thinkAndMakeMove() {
        assert(!endOfGameCheck());

        // associate clock with player on move
        engineColor = Globals.getBoard().getPlayerToMove();

        AtomicBoolean playedBookMove = new AtomicBoolean(false);
        if (openingBook != null && bookMisses < 3) {
            openingBook.getMoveWeightedRandomByFrequency(Globals.getBoard())
                    .ifPresentOrElse(bookMove -> {
                        Globals.getGameUndos().add(Globals.getBoard().applyMove(bookMove.getMove()));
                        LOGGER.debug("# book hit");
                        LOGGER.info("move " + bookMove.getMove());
                        endOfGameCheck();
                        playedBookMove.set(true);
                    }, () -> LOGGER.debug("# book miss {}", ++bookMisses));
        }
        if (!playedBookMove.get()) {
            // These are copied for testing purposes.  The iterator will not modify.
            Board board = Globals.getBoard().deepCopy();
            List<Undo> undos = new ArrayList<>(Globals.getGameUndos());
            searchIterator.unstop();
            searchFuture = searchIterator.findPvFuture(board, undos)
                    .thenApply(
                            pv -> {
                                Globals.getGameUndos().add(Globals.getBoard().applyMove(pv.get(0)));
                                LOGGER.info("move " + pv.get(0));
                                endOfGameCheck();
                                return pv;
                            });
        }
    }

    private static boolean endOfGameCheck() {
        GameStatus gameStatus = GameStatusChecker.getGameStatus(Globals.getBoard(), Globals.getGameUndos());
        if (gameStatus != GameStatus.INPROGRESS) {
            PrintGameResult.printResult(gameStatus);
            return true;
        }
        return false;
    }
}
