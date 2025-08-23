package dev.jamesswafford.chess4j;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.io.FENBuilder;
import dev.jamesswafford.chess4j.pieces.*;

import java.io.File;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
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

public class NativeEngineLib {

    private NativeEngineLib() {}

    private static Arena arena;

    private static MethodHandle mh_eval;
    private static MethodHandle mh_mvvlva;

    // hash related methods
    private static MethodHandle mh_getMainHashCollisions;
    private static MethodHandle mh_getMainHashHits;
    private static MethodHandle mh_getMainHashProbes;
    private static MethodHandle mh_getPawnHashCollisions;
    private static MethodHandle mh_getPawnHashHits;
    private static MethodHandle mh_getPawnHashProbes;

    public static void initializeFFM(File libFile) {
        Linker linker = Linker.nativeLinker();
        arena = Arena.global();
        SymbolLookup lookup = SymbolLookup.libraryLookup(libFile.getPath(), arena);

        mh_eval = linker.downcallHandle(lookup.findOrThrow("eval_ffm"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_BOOLEAN));

        mh_mvvlva = linker.downcallHandle(lookup.findOrThrow("mvvlva"),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG));

        mh_getMainHashCollisions = linker.downcallHandle(lookup.findOrThrow("get_main_hash_collisions"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
        mh_getMainHashProbes = linker.downcallHandle(lookup.findOrThrow("get_main_hash_probes"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
        mh_getMainHashHits = linker.downcallHandle(lookup.findOrThrow("get_main_hash_hits"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
        mh_getPawnHashCollisions = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_collisions"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
        mh_getPawnHashProbes = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_probes"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
        mh_getPawnHashHits = linker.downcallHandle(lookup.findOrThrow("get_pawn_hash_hits"),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG));
    }

    public static int eval(Board board, boolean materialOnly) {
        Objects.requireNonNull(mh_eval, "mh_eval must not be null");
        String fen = FENBuilder.createFen(board, false);
        MemorySegment cFen = arena.allocateFrom(fen); // TODO: this may be leaky
        try {
            return (int) mh_eval.invoke(cFen, materialOnly);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke eval; msg: " + e.getMessage());
        }
    }

    public static int mvvlva(Move move) {
        Objects.requireNonNull(mh_mvvlva, "mh_mvvlva must not be null");
        long nativeMove = NativeEngineLib.toNativeMove(move);
        try {
            return (int) mh_mvvlva.invoke(nativeMove);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to invoke mvvlva");
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

    // TODO: make private
    public static Move fromNativeMove(Long nativeMove, Color ptm) {
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

    // TODO: make private
    public static List<Move> fromNativeLine(List<Long> nativeMoves, Color ptm) {
        List<Move> moves = new ArrayList<>();

        for (int i=0; i<nativeMoves.size(); i++) {
            Long nativeMv = nativeMoves.get(i);
            Color color = (i % 2) == 0 ? ptm : Color.swap(ptm);
            moves.add(fromNativeMove(nativeMv, color));
        }

        return moves;
    }

    // TODO: make private
    public static Long toNativeMove(Move mv) {
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

}
