package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Globals;
import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.utils.GameStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PrintGameResult {

    private static final  Logger LOGGER = LogManager.getLogger(PrintGameResult.class);

    private PrintGameResult() { }

    public static void printResult(GameStatus gameStatus) {
        if (GameStatus.CHECKMATED.equals(gameStatus)) {
            if (Globals.getBoard().getPlayerToMove().equals(Color.WHITE)) {
                LOGGER.info("RESULT 0-1 {Black mates}\n");
            } else {
                LOGGER.info("RESULT 1-0 {White mates}\n");
            }
        } else if (GameStatus.STALEMATED.equals(gameStatus)) {
            LOGGER.info("RESULT 1/2-1/2 {Stalemate}\n");
        } else if (GameStatus.DRAW_MATERIAL.equals(gameStatus)) {
            LOGGER.info("RESULT 1/2-1/2 {Draw by lack of mating material}\n");
        } else if (GameStatus.DRAW_BY_50.equals(gameStatus)) {
            LOGGER.info("RESULT 1/2-1/2 {Draw by 50 move rule}\n");
        } else if (GameStatus.DRAW_REP.equals(gameStatus)) {
            LOGGER.info("RESULT 1/2-1/2 {Draw by repetition}\n");
        }
    }
}
