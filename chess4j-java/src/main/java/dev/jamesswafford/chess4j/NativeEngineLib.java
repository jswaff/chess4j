package dev.jamesswafford.chess4j;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.Undo;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import dev.jamesswafford.chess4j.hash.TranspositionTableEntry;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.io.PrintLine;
import dev.jamesswafford.chess4j.pieces.*;
import dev.jamesswafford.chess4j.search.SearchStats;
import dev.jamesswafford.chess4j.utils.MoveUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.io.File;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.jamesswafford.chess4j.pieces.Bishop.BLACK_BISHOP;
import static dev.jamesswafford.chess4j.pieces.Bishop.WHITE_BISHOP;
import static dev.jamesswafford.chess4j.pieces.King.BLACK_KING;
import static dev.jamesswafford.chess4j.pieces.King.WHITE_KING;
import static dev.jamesswafford.chess4j.pieces.Knight.BLACK_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Knight.WHITE_KNIGHT;
import static dev.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static dev.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;
import static dev.jamesswafford.chess4j.pieces.Queen.BLACK_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Queen.WHITE_QUEEN;
import static dev.jamesswafford.chess4j.pieces.Rook.BLACK_ROOK;
import static dev.jamesswafford.chess4j.pieces.Rook.WHITE_ROOK;

import static java.lang.foreign.ValueLayout.*;

public class NativeEngineLib {

    private NativeEngineLib() {}

    private static MethodHandle mh_init;
    private static MethodHandle mh_eval;
    private static MethodHandle mh_movegen;
    private static MethodHandle mh_nnEval;
    private static MethodHandle mh_nnLoadNetwork;

    // hash related methods
    private static MethodHandle mh_clearMainHashTable;
    private static MethodHandle mh_resizeMainHashTable;
    private static MethodHandle mh_probeMainHashTable;
    private static MethodHandle mh_storeMainHashTable;
    private static MethodHandle mh_getMainHashCollisions;
    private static MethodHandle mh_getMainHashHits;
    private static MethodHandle mh_getMainHashProbes;

    private static MethodHandle mh_clearPawnHashTable;
    private static MethodHandle mh_resizePawnHashTable;
    private static MethodHandle mh_probePawnHashTable;
    private static MethodHandle mh_storePawnHashTable;
    private static MethodHandle mh_getPawnHashCollisions;
    private static MethodHandle mh_getPawnHashHits;
    private static MethodHandle mh_getPawnHashProbes;

    // search related methods
    private static MethodHandle mh_iterateFromFen;
    private static MethodHandle mh_iterateFromMoveHistory;
    private static Board searchBoard;
    private static MemorySegment pvCallbackFunc;
    private static MethodHandle mh_see;
    private static MethodHandle mh_skipTimeChecks;
    private static MethodHandle mh_stopSearch;

    public static void initializeFFM() {
        System.load("/home/james/chess4j/lib/prophet/install/lib/libprophetlib.so"); // FIXME
        SymbolLookup lookup = SymbolLookup.loaderLookup();
        initializeFFM(lookup);
    }

    public static void initializeFFM(File libFile) {
        Arena arena = Arena.global();
        SymbolLookup lookup = SymbolLookup.libraryLookup(libFile.getPath(), arena);
        initializeFFM(lookup);
    }

    private static void initializeFFM(SymbolLookup lookup) {
        Linker linker = Linker.nativeLinker();

        mh_init = linker.downcallHandle(lookup.findOrThrow("init"),
                FunctionDescriptor.of(JAVA_INT));

        mh_eval = linker.downcallHandle(lookup.findOrThrow("eval_from_fen"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_BOOLEAN));

        mh_nnEval = linker.downcallHandle(lookup.findOrThrow("nn_eval_from_fen"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS));
        mh_nnLoadNetwork = linker.downcallHandle(lookup.findOrThrow("load_neural_network"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS));

        mh_see = linker.downcallHandle(lookup.findOrThrow("see_from_fen"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_LONG));

        mh_movegen = linker.downcallHandle(lookup.findOrThrow("generate_moves_from_fen"),
                FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_BOOLEAN, JAVA_BOOLEAN));

        mh_clearMainHashTable = linker.downcallHandle(lookup.findOrThrow("clear_main_hash_table"),
                FunctionDescriptor.ofVoid());
        mh_resizeMainHashTable = linker.downcallHandle(lookup.findOrThrow("resize_main_hash_table"),
                FunctionDescriptor.ofVoid(JAVA_LONG));
        mh_probeMainHashTable = linker.downcallHandle(lookup.findOrThrow("probe_main_hash_table"),
                FunctionDescriptor.of(JAVA_LONG, ADDRESS));
        mh_storeMainHashTable = linker.downcallHandle(lookup.findOrThrow("store_main_hash_table"),
                FunctionDescriptor.ofVoid(ADDRESS, JAVA_LONG));
        mh_getMainHashCollisions = linker.downcallHandle(lookup.findOrThrow("get_main_hash_collisions"),
                FunctionDescriptor.of(JAVA_LONG));
        mh_getMainHashProbes = linker.downcallHandle(lookup.findOrThrow("get_main_hash_probes"),
                FunctionDescriptor.of(JAVA_LONG));
        mh_getMainHashHits = linker.downcallHandle(lookup.findOrThrow("get_main_hash_hits"),
                FunctionDescriptor.of(JAVA_LONG));

        mh_clearPawnHashTable = linker.downcallHandle(lookup.findOrThrow("clear_pawn_hash_table"),
                FunctionDescriptor.ofVoid());
        mh_resizePawnHashTable = linker.downcallHandle(lookup.findOrThrow("resize_pawn_hash_table"),
                FunctionDescriptor.ofVoid(JAVA_LONG));
        mh_probePawnHashTable = linker.downcallHandle(lookup.findOrThrow("probe_pawn_hash_table"),
                FunctionDescriptor.of(JAVA_LONG, ADDRESS));
        mh_storePawnHashTable = linker.downcallHandle(lookup.findOrThrow("store_pawn_hash_table"),
                FunctionDescriptor.ofVoid(ADDRESS, JAVA_LONG));
        mh_getPawnHashCollisions = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_collisions"),
                FunctionDescriptor.of(JAVA_LONG));
        mh_getPawnHashProbes = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_probes"),
                FunctionDescriptor.of(JAVA_LONG));
        mh_getPawnHashHits = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_hits"),
                FunctionDescriptor.of(JAVA_LONG));


        // set up iterator with callback function for printing the PV
        mh_iterateFromFen = linker.downcallHandle(lookup.findOrThrow("iterate_from_fen"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_BOOLEAN,
                        JAVA_INT, JAVA_INT, ADDRESS));
        mh_iterateFromMoveHistory = linker.downcallHandle(lookup.findOrThrow("iterate_from_move_history"),
                FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_INT,
                        JAVA_BOOLEAN, JAVA_INT, JAVA_INT, ADDRESS));
        try {
            // create a method handle for the Java callback function
            MethodHandle pvCallbackHandle = MethodHandles.lookup().findStatic(
                    NativeEngineLib.class, "pvCallback",
                    MethodType.methodType(void.class, MemorySegment.class, int.class, int.class, int.class,
                            long.class, long.class));

            // create a Java description of the native function
            // typedef void (*pv_func_t)(move_t*, int, int32_t, int32_t, uint64_t, uint64_t);
            FunctionDescriptor pvCallbackDesc = FunctionDescriptor.ofVoid(
                    ADDRESS.withTargetLayout(JAVA_LONG), JAVA_INT, JAVA_INT, JAVA_INT, JAVA_LONG, JAVA_LONG);

            // create a function pointer for pvCallback
            pvCallbackFunc = linker.upcallStub(pvCallbackHandle, pvCallbackDesc, Arena.global());

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mh_skipTimeChecks = linker.downcallHandle(lookup.findOrThrow("set_skip_time_checks_flag"),
                FunctionDescriptor.ofVoid(JAVA_BOOLEAN));
        mh_stopSearch = linker.downcallHandle(lookup.findOrThrow("set_search_stop_flag"),
                FunctionDescriptor.ofVoid(JAVA_BOOLEAN));

        initializeLibrary();
    }

    public static void initializeLibrary() {
        Objects.requireNonNull(mh_init, "mh_init must not be null");
        try {
            int retval = (int) mh_init.invoke();
            if (retval != 0) {
                throw new RuntimeException("native code initialization failed! retval=" + retval);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke init; msg: " + e.getMessage());
        }
    }

    public static int eval(Board board, boolean materialOnly) {
        Objects.requireNonNull(mh_eval, "mh_eval must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            return (int) mh_eval.invoke(cFen, materialOnly);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke eval; msg: " + e.getMessage());
        }
    }

    public static int evalNN(Board board) {
        Objects.requireNonNull(mh_nnEval, "mh_nnEval must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            return (int) mh_nnEval.invoke(cFen);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke evalNN; msg: " + e.getMessage());
        }
    }

    public static void loadNeuralNetwork(File networkFile) {
        Objects.requireNonNull(mh_nnLoadNetwork, "mh_nnLoadNetwork must not be null");

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cNetworkFile = arena.allocateFrom(networkFile.getPath());
            int retval = (int) mh_nnLoadNetwork.invoke(cNetworkFile);
            if (retval != 0) {
                throw new RuntimeException("loading network into native code failed! retval=" + retval);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke loadNeuralNetwork; msg: " + e.getMessage());
        }
    }

    public static int see(Board board, Move move) {
        Objects.requireNonNull(mh_see, "mh_see must not be null");
        String fen = FENBuilder.createFen(board, false);
        long nativeMove = NativeEngineLib.toNativeMove(move);
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            return (int) mh_see.invoke(cFen, nativeMove);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke see; msg: " + e.getMessage());
        }
    }

    public static List<Move> generatePseudoLegalMoves(Board board, boolean caps, boolean noncaps) {
        Objects.requireNonNull(mh_movegen, "mh_movegen must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            List<Move> moves = new ArrayList<>();
            MemorySegment cFen = arena.allocateFrom(fen);
            MemorySegment dataSegment = arena.allocate(JAVA_LONG, 250);
            MemorySegment sizeSegment = arena.allocate(JAVA_INT);

            mh_movegen.invoke(dataSegment, sizeSegment, cFen, caps, noncaps);
            int numMoves = sizeSegment.get(JAVA_INT, 0);

            // read the moves from the data segment
            for (int i=0;i<numMoves;i++) {
                long val = dataSegment.get(JAVA_LONG, i * JAVA_LONG.byteSize());
                if (val != 0) {
                    Move mv = fromNativeMove(val, board.getPlayerToMove());
                    moves.add(mv);
                }
            }

            return moves;
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke movegen; msg: " + e.getMessage());
        }
    }

    public static void clearMainHashTable() {
        Objects.requireNonNull(mh_clearMainHashTable, "mh_clearMainHashTable must not be null");
        try {
            mh_clearMainHashTable.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke clearMainHashTable");
        }
    }

    public static void resizeMainHashTable(long maxBytes) {
        Objects.requireNonNull(mh_resizeMainHashTable, "mh_resizeMainHashTable must not be null");
        try {
            mh_resizeMainHashTable.invoke(maxBytes);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke resizeMainHashTable");
        }
    }

    public static TranspositionTableEntry probeMainHashTable(Board board) {
        Objects.requireNonNull(mh_probeMainHashTable, "mh_probeMainHashTable must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            long val = (long) mh_probeMainHashTable.invoke(cFen);
            return val==0 ? null : new TranspositionTableEntry(board.getZobristKey(), val);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke probeMainHashTable; msg: " + e.getMessage());
        }
    }

    public static void storeMainHashTable(Board board, TranspositionTableEntry hashEntry) {
        Objects.requireNonNull(mh_storeMainHashTable, "mh_storeMainHashTable must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            mh_storeMainHashTable.invoke(cFen, hashEntry.getVal());
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke storeMainHashTable; msg: " + e.getMessage());
        }
    }

    public static long getMainHashCollisions() {
        Objects.requireNonNull(mh_getMainHashCollisions, "mh_getMainHashCollisions must not be null");
        try {
            return (long) mh_getMainHashCollisions.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getMainHashCollisions");
        }
    }

    public static long getMainHashHits() {
        Objects.requireNonNull(mh_getMainHashHits, "mh_getMainHashHits must not be null");
        try {
            return (long) mh_getMainHashHits.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getMainHashHits");
        }
    }

    public static long getMainHashProbes() {
        Objects.requireNonNull(mh_getMainHashProbes, "mh_getMainHashProbes must not be null");
        try {
            return (long) mh_getMainHashProbes.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getMainHashProbes");
        }
    }

    public static void clearPawnHashTable() {
        Objects.requireNonNull(mh_clearPawnHashTable, "mh_clearPawnHashTable must not be null");
        try {
            mh_clearPawnHashTable.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke clearPawnHashTable");
        }
    }

    public static void resizePawnHashTable(long maxBytes) {
        Objects.requireNonNull(mh_resizePawnHashTable, "mh_resizePawnHashTable must not be null");
        try {
            mh_resizePawnHashTable.invoke(maxBytes);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke resizePawnHashTable");
        }
    }

    public static PawnTranspositionTableEntry probePawnHashTable(Board board) {
        Objects.requireNonNull(mh_probePawnHashTable, "mh_probePawnHashTable must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            long val = (long) mh_probePawnHashTable.invoke(cFen);
            return val==0 ? null : new PawnTranspositionTableEntry(board.getPawnKey(), val);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke probePawnHashTable; msg: " + e.getMessage());
        }
    }

    public static void storePawnHashTable(Board board, PawnTranspositionTableEntry hashEntry) {
        Objects.requireNonNull(mh_storePawnHashTable, "mh_storePawnHashTable must not be null");
        String fen = FENBuilder.createFen(board, false);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cFen = arena.allocateFrom(fen);
            mh_storePawnHashTable.invoke(cFen, hashEntry.getVal());
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke storePawnHashTable; msg: " + e.getMessage());
        }
    }

    public static long getPawnHashCollisions() {
        Objects.requireNonNull(mh_getPawnHashCollisions, "mh_getPawnHashCollisions must not be null");
        try {
            return (long) mh_getPawnHashCollisions.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getPawnHashCollisions");
        }
    }

    public static long getPawnHashHits() {
        Objects.requireNonNull(mh_getPawnHashHits, "mh_getPawnHashHits must not be null");
        try {
            return (long) mh_getPawnHashHits.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getPawnHashHits");
        }
    }

    public static long getPawnHashProbes() {
        Objects.requireNonNull(mh_getPawnHashProbes, "mh_getPawnHashProbes must not be null");
        try {
            return (long) mh_getPawnHashProbes.invoke();
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke getPawnHashProbes");
        }
    }

    public static Tuple2<Integer,Integer> iterate(List<Move> pv, SearchStats stats, Board board, final List<Undo> undos,
                                                  boolean earlyExitOK, int maxDepth, int maxTimeMs) {
        Objects.requireNonNull(mh_iterateFromFen, "mh_iterateFromFen must not be null");
        Objects.requireNonNull(mh_iterateFromMoveHistory, "mh_iterateFromMoveHistory must not be null");

        searchBoard = board.deepCopy();

        try (Arena arena = Arena.ofConfined()) {
            MemoryLayout statsLayout = MemoryLayout.structLayout(
                    JAVA_LONG.withName("nodes"),
                    JAVA_LONG.withName("qnodes"),
                    JAVA_LONG.withName("fail_highs"),
                    JAVA_LONG.withName("fail_lows"),
                    JAVA_LONG.withName("hash_fail_highs"),
                    JAVA_LONG.withName("hash_fail_lows"),
                    JAVA_LONG.withName("hash_exact_scores"),
                    JAVA_LONG.withName("draws")
            );
            VarHandle nodesHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("nodes"));
            VarHandle qnodesHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("qnodes"));
            VarHandle failHighsHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("fail_highs"));
            VarHandle failLowsHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("fail_lows"));
            VarHandle hashFailHighsHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("hash_fail_highs"));
            VarHandle hashFailLowsHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("hash_fail_lows"));
            VarHandle hashExactScoresHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("hash_exact_scores"));
            VarHandle drawsHandle = statsLayout.varHandle(MemoryLayout.PathElement.groupElement("draws"));

            MemorySegment statsSegment = arena.allocate(statsLayout);
            MemorySegment pvSegment = arena.allocate(JAVA_LONG, 250);
            MemorySegment pvSizeSegment = arena.allocate(JAVA_INT);
            MemorySegment depthSegment = arena.allocate(JAVA_INT);
            MemorySegment scoreSegment = arena.allocate(JAVA_INT);

            // do we have any move history?
            int retval;
            if (!undos.isEmpty()) {
                Long[] moveHistory = undos.stream().map(u -> toNativeMove(u.getMove())).toArray(Long[]::new);
                MemorySegment moveHistorySegment = arena.allocate(JAVA_LONG, moveHistory.length);
                for (int i=0;i<moveHistory.length;i++) {
                    moveHistorySegment.setAtIndex(JAVA_LONG, i, moveHistory[i]);
                }
                retval = (int) mh_iterateFromMoveHistory.invoke(statsSegment, pvSegment, pvSizeSegment, depthSegment,
                        scoreSegment, moveHistorySegment, moveHistory.length, earlyExitOK, maxDepth, maxTimeMs, pvCallbackFunc);
            } else { // no move history
                String fen = FENBuilder.createFen(board, false);
                MemorySegment cFen = arena.allocateFrom(fen);
                retval = (int) mh_iterateFromFen.invoke(statsSegment, pvSegment, pvSizeSegment, depthSegment,
                        scoreSegment, cFen, earlyExitOK, maxDepth, maxTimeMs, pvCallbackFunc);
            }

            if (retval != 0) {
                throw new RuntimeException("error in iterate! retval=" + retval);
            }

            // copy stats
            stats.nodes = (long) nodesHandle.get(statsSegment, 0L);
            stats.qnodes = (long) qnodesHandle.get(statsSegment, 0L);
            stats.failHighs = (long) failHighsHandle.get(statsSegment, 0L);
            stats.failLows = (long) failLowsHandle.get(statsSegment, 0L);
            stats.hashFailHighs = (long) hashFailHighsHandle.get(statsSegment, 0L);
            stats.hashFailLows = (long) hashFailLowsHandle.get(statsSegment, 0L);
            stats.hashExactScores = (long) hashExactScoresHandle.get(statsSegment, 0L);
            stats.draws = (long) drawsHandle.get(statsSegment, 0L);

            // read the moves from the PV segment
            pv.clear();
            int lengthPv = pvSizeSegment.get(JAVA_INT, 0);
            Color ptm = board.getPlayerToMove();
            for (int i=0;i<lengthPv;i++) {
                long val = pvSegment.get(JAVA_LONG, i * JAVA_LONG.byteSize());
                Move mv = fromNativeMove(val, ptm);
                pv.add(mv);
                ptm = Color.swap(ptm);
            }

            int depth = depthSegment.get(JAVA_INT, 0);
            int score = scoreSegment.get(JAVA_INT, 0);
            //System.out.println("*** depth: " + depth + " score: " + score);

            return Tuple.of(depth, score);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke iterate; msg: " + e.getMessage());
        }
    }

    public static void skipTimeChecks(boolean skip) {
        Objects.requireNonNull(mh_skipTimeChecks, "mh_skipTimeChecks must not be null");
        try {
            mh_skipTimeChecks.invoke(skip);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke skipTimeChecks; msg: " + e.getMessage());
        }
    }

    public static void stopSearch(boolean stop) {
        Objects.requireNonNull(mh_stopSearch, "mh_stopSearch must not be null");
        try {
            mh_stopSearch.invoke(stop);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke stopSearch; msg: " + e.getMessage());
        }
    }

    private static Move fromNativeMove(Long nativeMove, Color ptm) {
        Square fromSq = Square.valueOf((int)(nativeMove & 0x3F));
        Square toSq = Square.valueOf((int)((nativeMove >> 6) & 0x3F));
        Piece piece = fromNativePiece((int)((nativeMove >> 12) & 0x07), ptm);
        Piece promoPiece = fromNativePiece((int)((nativeMove >> 15) & 0x07), ptm);
        Piece capturedPiece = fromNativePiece((int)((nativeMove >> 18) & 0x07), Color.swap(ptm));
        boolean isEpCapture = (int)((nativeMove >> 21) & 0x01) == 1;
        boolean isCastle = (int)((nativeMove >> 22) & 0x01) == 1;

        Move converted = new Move(piece, fromSq, toSq, capturedPiece, promoPiece, isCastle, isEpCapture);

        assert (toNativeMove(converted).equals(nativeMove));

        return converted;
    }

    private static Long toNativeMove(Move mv) {
        if (mv==null) return 0L;

        long nativeMv = (long) mv.from().value() & 0x3F;
        nativeMv |= ((long)mv.to().value() & 0x3F) << 6;
        nativeMv |= (toNativePiece(mv.piece()) & 0x07) << 12;
        if (mv.promotion() != null) {
            nativeMv |= (toNativePiece(mv.promotion()) & 0x07) << 15;
        }
        if (mv.captured() != null) {
            nativeMv |= (toNativePiece(mv.captured()) & 0x07) << 18;
        }
        if (mv.isEpCapture()) {
            nativeMv |= 1L << 21;
        }
        if (mv.isCastle()) {
            nativeMv |= 1L << 22;
        }

        return nativeMv;
    }

    private static Piece fromNativePiece(int pieceType, Color pieceColor) {
        boolean isWhite = pieceColor.isWhite();

        switch (pieceType) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                return isWhite ? WHITE_PAWN : BLACK_PAWN;
            }
            case 2 -> {
                return isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            }
            case 3 -> {
                return isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            }
            case 4 -> {
                return isWhite ? WHITE_ROOK : BLACK_ROOK;
            }
            case 5 -> {
                return isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            }
            case 6 -> {
                return isWhite ? WHITE_KING : BLACK_KING;
            }
            default -> throw new IllegalArgumentException("Don't know how to translate native piece: " + pieceType);
        }
    }

    private static long toNativePiece(Piece piece) {
        if (piece.getClass() == Pawn.class) {
            return 1;
        } else if (piece.getClass() == Knight.class) {
            return 2;
        } else if (piece.getClass() == Bishop.class) {
            return 3;
        } else if (piece.getClass() == Rook.class) {
            return 4;
        } else if (piece.getClass() == Queen.class) {
            return 5;
        } else if (piece.getClass() == King.class) {
            return 6;
        }
        throw new IllegalArgumentException("Invalid piece type in toNativePiece: " + piece);
    }

    // TODO: add a boolean to indicate whether the line is from the iterator
    private static void pvCallback(MemorySegment moves, int numMoves, int depth, int score, long elapsed, long nodes) {
        assert(numMoves > 0);
        MemorySegment cMoves = moves.reinterpret(numMoves * JAVA_LONG.byteSize());
        List<Move> pv = new ArrayList<>();

        // read the moves from the PV segment
        Color ptm = searchBoard.getPlayerToMove();
        for (int i=0;i<numMoves;i++) {
            long val = cMoves.get(JAVA_LONG, i * JAVA_LONG.byteSize());
            Move mv = fromNativeMove(val, ptm);
            pv.add(mv);
            ptm = Color.swap(ptm);
        }

        assert(MoveUtils.isLineValid(pv, searchBoard));
        PrintLine.printLine(false, pv, depth, score, elapsed, nodes);
    }

}
