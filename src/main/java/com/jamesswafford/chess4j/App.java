package com.jamesswafford.chess4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jamesswafford.chess4j.book.AbstractOpeningBook;
import com.jamesswafford.chess4j.book.OpeningBookSQLiteImpl;
import com.jamesswafford.chess4j.exceptions.IllegalMoveException;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.io.InputParser;
import com.jamesswafford.chess4j.search.SearchIterator;
import com.jamesswafford.chess4j.utils.TestSuiteProcessor;

public final class App {
	private static final Log LOGGER = LogFactory.getLog(App.class);

	private static AbstractOpeningBook openingBook;
	private static String bookPath = null;
	private static String testSuiteFile = null;
	private static int testSuiteTime = 10; // default to five seconds
	
	private App() { }

	private static void processArgument(String arg) {
		if (arg.startsWith("-suite=")) {
			testSuiteFile = arg.substring(7); // "-suite=" is 7 characters
		} else if (arg.startsWith("-time=")) {
			testSuiteTime = Integer.valueOf(arg.substring(6));
		} else if (arg.startsWith("-book=")) {
			bookPath = arg.substring(6);
		} else if (arg.startsWith("-hash=")) {
			TTHolder.maxEntries = Integer.valueOf(arg.substring(6));
		} else if (arg.startsWith("-phash=")) {
			TTHolder.maxPawnEntries = Integer.valueOf(arg.substring(7));
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
				LOGGER.debug("Caught nonrecoverable I/O exception: " + e1.getMessage());
				System.exit(1);
			}
			
			try {
				InputParser.getInstance().parseCommand(input);
			} catch (IllegalMoveException ime) {
				LOGGER.error("Illegal move");
			} catch (ParseException pe) {
				LOGGER.error("Parse error: " + pe.getMessage());
			} catch (Exception e) {
				LOGGER.debug("Caught (hopefully recoverable) exception: " + e.getMessage());
			}
		}
	}
	
	private static boolean showDebugMode() {
		LOGGER.info("**** DEBUG MODE ENABLED ****");
		return true;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LOGGER.info("Welcome to chess4j!\n\n");
		
		assert(showDebugMode());
		
		for (String arg : args) {
			processArgument(arg);
		}
		TTHolder.initTables();
		
		if (testSuiteFile != null) {
			TestSuiteProcessor tp = new TestSuiteProcessor();
			tp.processTestSuite(testSuiteFile,testSuiteTime);
			System.exit(0);
		}
		
		if (bookPath != null) {
			initBook();
			SearchIterator.useOpeningBook = true;
		}
		
		repl();
	}

	public static AbstractOpeningBook getOpeningBook() {
		return openingBook;
	}
	
	private static void initBook() throws Exception {
		LOGGER.info("# initializing book: " + bookPath);
		
		File bookFile = new File(bookPath);
		boolean initBook = !bookFile.exists();
				
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + bookPath);
		OpeningBookSQLiteImpl sqlOpeningBook = new OpeningBookSQLiteImpl(conn);
		
		if (initBook) {
			LOGGER.info("# could not find " + bookPath + ", creating...");
			sqlOpeningBook.initializeBook();
			LOGGER.info("# ... finished.");
		} else {
			sqlOpeningBook.loadZobristKeys();
		}

		openingBook = sqlOpeningBook;
		
		LOGGER.info("# book initialization complete. " + openingBook.getTotalMoveCount() + " moves in book file.");		
	}
}
