package dev.jamesswafford.chess4j.io;

import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.File;
import dev.jamesswafford.chess4j.board.squares.Rank;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.exceptions.IllegalMoveException;
import dev.jamesswafford.chess4j.exceptions.ParseException;
import dev.jamesswafford.chess4j.movegen.MagicBitboardMoveGenerator;
import dev.jamesswafford.chess4j.pieces.Piece;
import dev.jamesswafford.chess4j.utils.PieceFactory;

import java.util.List;
import java.util.Optional;

import static dev.jamesswafford.chess4j.board.squares.Rank.RANK_1;
import static dev.jamesswafford.chess4j.board.squares.Rank.RANK_8;
import static dev.jamesswafford.chess4j.board.squares.Square.*;
import static dev.jamesswafford.chess4j.pieces.King.BLACK_KING;
import static dev.jamesswafford.chess4j.pieces.King.WHITE_KING;
import static dev.jamesswafford.chess4j.pieces.Pawn.BLACK_PAWN;
import static dev.jamesswafford.chess4j.pieces.Pawn.WHITE_PAWN;

public final class MoveParser {

    private String strMove;
    private Board board;

    public synchronized Move parseMove(String strMove, Board board) throws ParseException, IllegalMoveException {
        this.board = board;
        this.strMove = strMove;
        Move move = parsePseudoMove();

        if (!isLegalMove(move)) {
            throw new IllegalMoveException(move.toString());
        }
        return move;
    }

    private Move getCastlingMove() {
        if (strMove.equalsIgnoreCase("O-O") || strMove.equals("0-0")) {
            if (board.getPlayerToMove().equals(Color.WHITE)) {
                return new Move(WHITE_KING, E1, G1, true);
            } else {
                return new Move(BLACK_KING, E8, G8, true);
            }
        } else if (strMove.equalsIgnoreCase("O-O-O") || strMove.equals("0-0-0")) {
            if (board.getPlayerToMove().equals(Color.WHITE)) {
                return new Move(WHITE_KING, E1, C1, true);
            } else {
                return new Move(BLACK_KING, E8, C8, true);
            }
        }
        return null;
    }

    private Square getDestinationSquare() throws ParseException {
        Square dst;

        if (strMove.length() < 2) {
            throw new ParseException("couldn't translate destination (too short).");
        }

        File dstFile = File.file(strMove.substring(strMove.length() - 2, strMove.length() - 1));
        Rank dstRank = Rank.rank(strMove.substring(strMove.length() - 1));
        dst = Square.valueOf(dstFile, dstRank);
        strMove = strMove.substring(0, strMove.length() - 2);

        return dst;
    }

    private Move getMatchingMove(File srcFile, Rank srcRank, Square dstSquare, Piece piece, Piece promo)
            throws IllegalMoveException {

        Move move = null;
        int nMatches = 0;

        List<Move> legalMoves = MagicBitboardMoveGenerator.genLegalMoves(board);
        for (Move legalMove : legalMoves) {
            if (isMatchToMove(srcFile, srcRank, dstSquare, piece, promo, legalMove)) {
                nMatches++;
                move = legalMove;
            }
        }
        if (nMatches != 1) {
            throw new IllegalMoveException("matches: " + nMatches);
        }
        return move;
    }

    private Piece getMovingPiece() {
        Piece piece = null;
        char p = strMove.charAt(0);
        if (isPieceChar(p) && String.valueOf(p).equals(String.valueOf(p).toUpperCase())) {
            boolean wtm = board.getPlayerToMove().equals(Color.WHITE);
            piece = PieceFactory.getPiece(p, wtm);
            strMove = strMove.substring(1);
        }
        return piece;
    }

    private Move getPawnPush(Square dst, Piece promotion) throws ParseException {

        boolean wtm = board.getPlayerToMove().equals(Color.WHITE);
        if (wtm) {
            if (dst.rank().equals(RANK_8) && promotion == null) {
                throw new ParseException("white pawn promotion with no promotion piece.");
            }
            Square sq = dst.south().orElseThrow(() -> new ParseException("expected square south of dst"));
            if (WHITE_PAWN.equals(board.getPiece(sq))) {
                return new Move(WHITE_PAWN, sq, dst, null, promotion);
            }
            Optional<Square> optSsq = sq.south();
            if (optSsq.isPresent()) {
                if (WHITE_PAWN.equals(board.getPiece(optSsq.get()))) {
                    return new Move(WHITE_PAWN, optSsq.get(), dst);
                }
            }
        } else {
            if (dst.rank().equals(RANK_1) && promotion == null) {
                throw new ParseException("black pawn promotion with no promotion piece.");
            }
            Square sq = dst.north().orElseThrow(() -> new ParseException("expected square north of dst"));
            if (BLACK_PAWN.equals(board.getPiece(sq))) {
                return new Move(BLACK_PAWN, sq, dst, null, promotion);
            }
            Optional<Square> optNsq = sq.north();
            if (optNsq.isPresent()) {
                if (BLACK_PAWN.equals(board.getPiece(optNsq.get()))) {
                    return new Move(BLACK_PAWN, optNsq.get(), dst);
                }
            }
        }
        throw new ParseException("length==0 and no source is set.");
    }

    private Piece getPromotionPiece() {
        Piece promo = null;

        char p = strMove.charAt(strMove.length() - 1);
        if (isPieceChar(p)) {
            boolean wtm = board.getPlayerToMove().equals(Color.WHITE);
            promo = PieceFactory.getPiece(p, wtm);
            strMove = strMove.substring(0, strMove.length() - 1);
        }

        return promo;
    }

    private boolean isLegalMove(Move move) {
        List<Move> moves = MagicBitboardMoveGenerator.genLegalMoves(board);
        return moves.contains(move);
    }

    private boolean isMatchToMove(File srcFile, Rank srcRank, Square dstSquare, Piece piece, Piece promo,
                                  Move legalMove)
    {
        if (srcFile != null && !srcFile.equals(legalMove.from().file())) {
            return false;
        }
        if (srcRank != null && !srcRank.equals(legalMove.from().rank())) {
            return false;
        }
        if (!dstSquare.equals(legalMove.to())) {
            return false;
        }
        if (!piece.equals(board.getPiece(legalMove.from()))) {
            return false;
        }
        if (promo == null) {
            return legalMove.promotion() == null;
        } else {
            return promo.equals(legalMove.promotion());
        }

    }

    private boolean isPieceChar(char p) {
        Piece piece = PieceFactory.getPiece(p);
        return piece != null;
    }

    private Move parsePseudoMove() throws ParseException, IllegalMoveException {
        strMove = strMove.trim();

        // get rid of any #, +, or = characters.
        strMove = strMove.replace("#", "");
        strMove = strMove.replace("+", "");
        strMove = strMove.replace("=", "");

        // castling move?
        Move castlingMove = getCastlingMove();
        if (castlingMove != null) {
            return castlingMove;
        }

        // remove promotion piece if present
        Piece promotion = getPromotionPiece();

        // translate the destination
        Square dst = getDestinationSquare();

        // try pawn moves like "e4"
        if (strMove.length() == 0) {
            return getPawnPush(dst, promotion);
        }

        // set the piece.
        Piece piece = getMovingPiece();

        // remove the capture designator
        strMove = strMove.replace("x", "");

        // if anything is left, it must be the source
        File srcFile = setSourceFile();
        Rank srcRank = setSourceRank();

        piece = setPieceIfNotSet(piece, srcFile, srcRank);

        // if the piece didn't get set earlier, do it now.
        return getMatchingMove(srcFile, srcRank, dst, piece, promotion);
    }

    private Piece setPieceIfNotSet(Piece piece, File srcFile, Rank srcRank) {
        Piece myPiece = piece;
        if (myPiece == null) {
            if (srcFile != null && srcRank != null) {
                Square srcSq = Square.valueOf(srcFile, srcRank);
                myPiece = board.getPiece(srcSq);
            } else {
                boolean wtm = board.getPlayerToMove().equals(Color.WHITE);
                myPiece = wtm ? WHITE_PAWN : BLACK_PAWN;
            }
        }
        return myPiece;
    }

    private File setSourceFile() {
        File file = null;
        if (strMove.length() > 0) {
            char p = strMove.charAt(0);
            if (p >= 'a' && p <= 'h') {
                file = File.file(String.valueOf(p));
                strMove = strMove.substring(1);
            }
        }

        return file;
    }

    private Rank setSourceRank() {
        Rank rank = null;
        if (strMove.length() > 0) {
            char p = strMove.charAt(0);
            if (p >= '1' && p <= '8') {
                rank = Rank.rank(String.valueOf(p));
                strMove = strMove.substring(0, strMove.length() - 1);
            }
        }
        return rank;
    }

}
