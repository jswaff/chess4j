package com.jamesswafford.chess4j.movegen;

import java.util.ArrayList;
import java.util.List;

import com.jamesswafford.chess4j.board.Color;
import com.jamesswafford.chess4j.board.*;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.init.Initializer;
import com.jamesswafford.chess4j.io.FenBuilder;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.utils.BoardUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static java.util.stream.Collectors.toList;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;
import static com.jamesswafford.chess4j.utils.MoveUtils.convertNativeMove;

public final class MoveGen implements MoveGenerator {

    private static final Log LOGGER = LogFactory.getLog(MoveGen.class);

    static {
        Initializer.init();
    }

    public static void genBishopMoves(Board board,List<Move> moves,boolean caps,boolean noncaps) {
        Piece piece;
        long pieceMap;

        if (board.getPlayerToMove()==Color.WHITE) {
            piece = WHITE_BISHOP;
            pieceMap = board.getWhiteBishops();
        } else {
            piece = BLACK_BISHOP;
            pieceMap = board.getBlackBishops();
        }

        while (pieceMap != 0) {
            int sqVal = Bitboard.msb(pieceMap);
            long moveMap = Magic.getBishopMoves(board,sqVal,getTargetSquares(board,caps,noncaps));
            addMoves(board,piece,Square.valueOf(sqVal),moveMap,moves);
            pieceMap ^= Bitboard.squares[sqVal];
        }
    }

    public static void genKingMoves(Board board,List<Move> moves,boolean caps,boolean noncaps) {
        Piece piece;

        if (board.getPlayerToMove()==Color.WHITE) {
            piece = WHITE_KING;
        } else {
            piece = BLACK_KING;
        }

        Square fromSq = board.getKingSquare(board.getPlayerToMove());

        long moveMap = Bitboard.kingMoves[fromSq.value()] & getTargetSquares(board,caps,noncaps);
        addMoves(board,piece,fromSq,moveMap,moves);

        if (noncaps) {
            genCastlingMoves(board,moves);
        }
    }

    public static void genKnightMoves(Board board,List<Move> moves,boolean caps,boolean noncaps) {
        Piece piece;
        long pieceMap;

        if (board.getPlayerToMove()==Color.WHITE) {
            piece = WHITE_KNIGHT;
            pieceMap = board.getWhiteKnights();
        } else {
            piece = BLACK_KNIGHT;
            pieceMap = board.getBlackKnights();
        }

        while (pieceMap != 0) {
            int sqVal = Bitboard.msb(pieceMap);
            long moveMap = Bitboard.knightMoves[sqVal] & getTargetSquares(board,caps,noncaps);
            addMoves(board,piece,Square.valueOf(sqVal),moveMap,moves);
            pieceMap ^= Bitboard.squares[sqVal];
        }
    }

    public static List<Move> genLegalMoves(Board board) {
        return genPseudoLegalMoves(board)
                .stream()
                .filter(m -> isMoveLegal(board,m))
                .collect(toList());
    }

    public static void genPawnMoves(Board board,List<Move> moves,boolean caps,boolean noncaps) {

        long allPieces = board.getWhitePieces() | board.getBlackPieces();
        long pmap;

        if (board.getPlayerToMove()==Color.WHITE) {
            if (caps) {
                long targets = board.getBlackPieces();
                if (board.getEPSquare() != null) targets |= Bitboard.squares[board.getEPSquare().value()];

                // attacks west
                pmap = ((board.getWhitePawns() & ~Bitboard.files[FILE_A.getValue()]) >> 9) & targets;
                while (pmap != 0) {
                    int toSqVal = Bitboard.msb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    Piece captured = toSq==board.getEPSquare() ? BLACK_PAWN : board.getPiece(toSq);
                    addPawnMove(moves,WHITE_PAWN,toSq.southEast().get(),toSq,captured,
                            toSq==board.getEPSquare());
                    pmap ^= Bitboard.squares[toSqVal];
                }

                // attacks east
                pmap = ((board.getWhitePawns() & ~Bitboard.files[File.FILE_H.getValue()]) >> 7) & targets;
                while (pmap != 0) {
                    int toSqVal = Bitboard.msb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    Piece captured = toSq==board.getEPSquare() ? BLACK_PAWN : board.getPiece(toSq);
                    addPawnMove(moves,WHITE_PAWN,toSq.southWest().get(),toSq,captured,toSq==board.getEPSquare());
                    pmap ^= Bitboard.squares[toSqVal];
                }

                // push promotions
                pmap = ((board.getWhitePawns() & Bitboard.ranks[RANK_7.getValue()]) >> 8) & ~allPieces;
                while (pmap != 0) {
                    int toSqVal = Bitboard.msb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    addPawnMove(moves,WHITE_PAWN,toSq.south().get(),toSq,null,false);
                    pmap ^= Bitboard.squares[toSqVal];
                }
            }

            // pawn pushes less promotions
            if (noncaps) {
                pmap = ((board.getWhitePawns() & ~Bitboard.ranks[RANK_7.getValue()]) >> 8) & ~allPieces;
                while (pmap != 0) {
                    int toSqVal = Bitboard.msb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    moves.add(new Move(WHITE_PAWN,toSq.south().get(),toSq,null));
                    if (toSq.rank()==RANK_3 && board.getPiece(toSq.north().get())==null) {
                        moves.add(new Move(WHITE_PAWN,toSq.south().get(),toSq.north().get(),null));
                    }
                    pmap ^= Bitboard.squares[toSqVal];
                }
            }
        } else {
            if (caps) {
                long targets = board.getWhitePieces();
                if (board.getEPSquare() != null) targets |= Bitboard.squares[board.getEPSquare().value()];

                // attacks west
                pmap = ((board.getBlackPawns() & ~Bitboard.files[FILE_A.getValue()]) << 7) & targets;
                while (pmap != 0) {
                    int toSqVal = Bitboard.lsb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    Piece captured = toSq==board.getEPSquare() ? WHITE_PAWN : board.getPiece(toSq);
                    addPawnMove(moves,BLACK_PAWN,toSq.northEast().get(),toSq,captured,toSq==board.getEPSquare());
                    pmap ^= Bitboard.squares[toSqVal];
                }

                // attacks east
                pmap = ((board.getBlackPawns() & ~Bitboard.files[FILE_H.getValue()]) << 9) & targets;
                while (pmap != 0) {
                    int toSqVal = Bitboard.lsb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    Piece captured = toSq==board.getEPSquare() ? WHITE_PAWN : board.getPiece(toSq);
                    addPawnMove(moves,BLACK_PAWN,toSq.northWest().get(),toSq,captured,toSq==board.getEPSquare());
                    pmap ^= Bitboard.squares[toSqVal];
                }

                // push promotions
                pmap = ((board.getBlackPawns() & Bitboard.ranks[RANK_2.getValue()]) << 8) & ~allPieces;
                while (pmap != 0) {
                    int toSqVal = Bitboard.lsb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    addPawnMove(moves,BLACK_PAWN,toSq.north().get(),toSq,null,false);
                    pmap ^= Bitboard.squares[toSqVal];
                }
            }

            // pawn pushes less promotions
            if (noncaps) {
                pmap = ((board.getBlackPawns() & ~Bitboard.ranks[RANK_2.getValue()]) << 8) & ~allPieces;
                while (pmap != 0) {
                    int toSqVal = Bitboard.lsb(pmap);
                    Square toSq = Square.valueOf(toSqVal);
                    moves.add(new Move(BLACK_PAWN,toSq.north().get(),toSq,null));
                    if (toSq.rank()==RANK_6 && board.getPiece(toSq.south().get())==null) {
                        moves.add(new Move(BLACK_PAWN,toSq.north().get(),toSq.south().get(),null));
                    }
                    pmap ^= Bitboard.squares[toSqVal];
                }
            }
        }
    }

    public static List<Move> genPseudoLegalMoves(Board board) {
        List<Move> moves = genPseudoLegalMoves(board,true,true);
        assert (moveGensAreEqual(moves, board));
        return moves;
    }

    private static boolean moveGensAreEqual(List<Move> javaMoves, Board board) {
        if (Initializer.nativeCodeInitialized()) {
            String fen = FenBuilder.createFen(board, false);
            List<Long> nativeMoves = new ArrayList<>();
            try {
                int nMoves = genPseudoLegalMovesNative(fen, nativeMoves);
                assert (nMoves == nativeMoves.size());
                if (nMoves != javaMoves.size()) {
                    LOGGER.error("Move lists not equal! # java moves: " + javaMoves.size()
                        + ", # native moves: " + nMoves);
                    return false;
                }
                // for every java move, ensure there is exactly one corresponding native move
                for (Move javaMove : javaMoves) {
                    if (nativeMoves.stream()
                            .filter(nativeMove -> javaMove.equals(convertNativeMove(nativeMove, board.getPlayerToMove())))
                            .count() != 1L)
                    {
                        LOGGER.error("No native move found for java move: " + javaMove
                            + ", fen: " + fen);
                        return false;
                    }
                }

                // sort java moves to match order of native moves
                List<Move> sortedJavaMoves = new ArrayList<>();
                for (Long nativeMove : nativeMoves) {
                    Move matchingMove = javaMoves.stream()
                            .filter(javaMove -> javaMove.equals(convertNativeMove(nativeMove, board.getPlayerToMove())))
                            .findFirst().get();
                    sortedJavaMoves.add(matchingMove);
                }
                assert (sortedJavaMoves.size() == javaMoves.size());
                javaMoves.clear();
                javaMoves.addAll(sortedJavaMoves);

                return true;
            } catch (IllegalStateException e) {
                LOGGER.error(e);
                throw e;
            }
        } else {
            return true;
        }
    }

    private static native int genPseudoLegalMovesNative(String fen, List<Long> moves);

    public static List<Move> genPseudoLegalMoves(Board board, boolean caps, boolean noncaps) {
        List<Move> moves = new ArrayList<>(100);

        genPawnMoves(board,moves,caps,noncaps);
        genKnightMoves(board,moves,caps,noncaps);
        genBishopMoves(board,moves,caps,noncaps);
        genRookMoves(board,moves,caps,noncaps);
        genQueenMoves(board,moves,caps,noncaps);
        genKingMoves(board,moves,caps,noncaps);

        return moves;
    }

    public static void genQueenMoves(Board board,List<Move> moves,boolean caps,boolean noncaps) {
        Piece piece;
        long pieceMap;

        if (board.getPlayerToMove()==Color.WHITE) {
            piece = WHITE_QUEEN;
            pieceMap = board.getWhiteQueens();
        } else {
            piece = BLACK_QUEEN;
            pieceMap = board.getBlackQueens();
        }

        while (pieceMap != 0) {
            int sqVal = Bitboard.msb(pieceMap);
            long moveMap = Magic.getQueenMoves(board,sqVal,getTargetSquares(board,caps,noncaps));
            addMoves(board,piece,Square.valueOf(sqVal),moveMap,moves);
            pieceMap ^= Bitboard.squares[sqVal];
        }
    }

    public static void genRookMoves(Board board, List<Move> moves, boolean caps, boolean noncaps) {
        Piece piece;
        long pieceMap;

        if (board.getPlayerToMove()==Color.WHITE) {
            piece = WHITE_ROOK;
            pieceMap = board.getWhiteRooks();
        } else {
            piece = BLACK_ROOK;
            pieceMap = board.getBlackRooks();
        }

        while (pieceMap != 0) {
            int sqVal = Bitboard.msb(pieceMap);
            long moveMap = Magic.getRookMoves(board,sqVal,getTargetSquares(board,caps,noncaps));
            addMoves(board,piece,Square.valueOf(sqVal),moveMap,moves);
            pieceMap ^= Bitboard.squares[sqVal];
        }
    }

    private static void addMoves(Board board, Piece piece, Square fromSq, long moveMap, List<Move> moves) {
        while (moveMap != 0) {
            int toVal = Bitboard.lsb(moveMap);
            Square toSq = Square.valueOf(toVal);
            Piece toPiece = board.getPiece(toSq);
            moves.add(new Move(piece,fromSq,toSq,toPiece));
            moveMap ^= Bitboard.squares[toVal];
        }
    }

    private static void addPawnMove(List<Move> moves, Pawn movingPawn, Square fromSq, Square toSq, Piece captured,
                                    boolean epCapture)
    {
        if (toSq.rank()==RANK_1 || toSq.rank()==RANK_8) {
            boolean isWhite = toSq.rank()==RANK_8;
            assert((isWhite && movingPawn==WHITE_PAWN) || (!isWhite && movingPawn==BLACK_PAWN));
            moves.add(new Move(movingPawn,fromSq,toSq,captured,isWhite?WHITE_QUEEN:BLACK_QUEEN));
            moves.add(new Move(movingPawn,fromSq,toSq,captured,isWhite?WHITE_ROOK:BLACK_ROOK));
            moves.add(new Move(movingPawn,fromSq,toSq,captured,isWhite?WHITE_BISHOP:BLACK_BISHOP));
            moves.add(new Move(movingPawn,fromSq,toSq,captured,isWhite?WHITE_KNIGHT:BLACK_KNIGHT));
        } else {
            moves.add(new Move(movingPawn,fromSq,toSq,captured,epCapture));
        }
    }

    private static void genCastlingMoves(Board board, List<Move> moves) {
        Color player = board.getPlayerToMove();

        if (player.isWhite()) {
            Square fromSq = E1;
            if (BoardUtils.whiteCanCastleKingSide(board)) {
                moves.add(new Move(WHITE_KING,fromSq,G1,true));
            }
            if (BoardUtils.whiteCanCastleQueenSide(board)) {
                moves.add(new Move(WHITE_KING,fromSq,C1,true));
            }
        } else {
            Square fromSq = E8;
            if (BoardUtils.blackCanCastleKingSide(board)) {
                moves.add(new Move(BLACK_KING,fromSq,G8,true));
            }
            if (BoardUtils.blackCanCastleQueenSide(board)) {
                moves.add(new Move(BLACK_KING,fromSq,C8,true));
            }
        }
    }

    private static long getTargetSquares(Board board,boolean caps,boolean noncaps) {
        long targets = 0;

        if (caps) {
            targets = board.getPlayerToMove()==Color.WHITE ? board.getBlackPieces() : board.getWhitePieces();
        }

        if (noncaps) {
            targets |= ~(board.getWhitePieces() | board.getBlackPieces());
        }

        return targets;
    }

    private static boolean isMoveLegal(Board board, Move m) {
        Undo undo = board.applyMove(m);
        boolean legal = !BoardUtils.isOpponentInCheck(board);
        board.undoMove(undo);
        return legal;
    }

    @Override
    public List<Move> generateLegalMoves(Board board) {
        return genLegalMoves(board);
    }

    @Override
    public List<Move> generatePseudoLegalMoves(Board board) {
        return genPseudoLegalMoves(board);
    }

    @Override
    public List<Move> generatePseudoLegalCaptures(Board board) {
        return genPseudoLegalMoves(board, true, false);
    }

    @Override
    public List<Move> generatePseudoLegalNonCaptures(Board board) {
        return genPseudoLegalMoves(board, false, true);
    }
}
