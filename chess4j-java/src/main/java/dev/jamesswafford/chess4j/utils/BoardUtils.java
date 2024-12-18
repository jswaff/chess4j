package dev.jamesswafford.chess4j.utils;

import dev.jamesswafford.chess4j.board.Bitboard;
import dev.jamesswafford.chess4j.board.Board;
import dev.jamesswafford.chess4j.board.Color;
import dev.jamesswafford.chess4j.board.Move;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.movegen.AttackDetector;
import dev.jamesswafford.chess4j.movegen.Magic;
import dev.jamesswafford.chess4j.pieces.*;

import java.util.Optional;

import static dev.jamesswafford.chess4j.board.CastlingRights.*;
import static dev.jamesswafford.chess4j.board.squares.Square.*;
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

public class BoardUtils {

    public static int countPawns(Board board, Color sideToMove) {
        return (int) Square.allSquares().stream()
                .filter(sq -> {
                    Piece p = board.getPiece(sq);
                    return p instanceof Pawn && p.getColor()==sideToMove;
                })
                .count();
    }

    public static int countNonPawns(Board board, Color sideToMove) {
        return (int) Square.allSquares().stream()
                .filter(sq -> {
                    Piece p = board.getPiece(sq);
                    return (p instanceof Queen || p instanceof Rook || p instanceof Bishop || p instanceof Knight)
                            && p.getColor() == sideToMove;
                })
                .count();
    }

    public static boolean isDiagonal(Square sq1,Square sq2) {
        return sq1.rank().distance(sq2.rank()) == sq1.file().distance(sq2.file());
    }

    public static boolean isPseudoLegalMove(Board board, Move m) {

        Piece mover = board.getPiece(m.from());
        if (mover == null) {
            return false;
        }

        // is the piece the correct color for the player on move?
        if (mover.getColor() != board.getPlayerToMove()) {
            return false;
        }

        // is the piece on the from square correct?
        if (m.piece() != mover) {
            return false;
        }

        if (mover==WHITE_PAWN || mover==BLACK_PAWN) {
            return isPseudoLegalPawnMove(board,m);
        }

        // validate capture flag
        if (m.captured() != null) {
            if (board.getPlayerToMove()==Color.WHITE) {
                if (!m.captured().isBlack()) return false;
            } else {
                if (!m.captured().isWhite()) return false;
            }
        } else {
            // not a capture, so destination square should be empty
            if (board.getPiece(m.to()) != null) {
                return false;
            }
        }

        if (mover==WHITE_KNIGHT || mover==BLACK_KNIGHT) {
            return (Bitboard.squares[m.to().value()] & Bitboard.knightMoves[m.from().value()]) != 0;
        } else if (mover==WHITE_BISHOP || mover==BLACK_BISHOP) {
            return (Bitboard.squares[m.to().value()] & Magic.getBishopMoves(board, m.from().value(), ~0L)) != 0;
        } else if (mover==WHITE_ROOK || mover==BLACK_ROOK) {
            return (Bitboard.squares[m.to().value()] & Magic.getRookMoves(board, m.from().value(), ~0L)) != 0;
        } else if (mover==WHITE_QUEEN || mover==BLACK_QUEEN) {
            return (Bitboard.squares[m.to().value()] & Magic.getQueenMoves(board, m.from().value(), ~0L)) != 0;
        } else if (mover==WHITE_KING || mover==BLACK_KING) {
            if ((Bitboard.squares[m.to().value()] & Bitboard.kingMoves[m.from().value()]) != 0)
                return true;

            if (m.isCastle()) {
                if (m.to() == G1 && whiteCanCastleKingSide(board)) {
                    return true;
                } else if (m.to() == C1 && whiteCanCastleQueenSide(board)) {
                    return true;
                } else if (m.to() == G8 && blackCanCastleKingSide(board)) {
                    return true;
                } else return m.to() == C8 && blackCanCastleQueenSide(board);
            }
        }

        return false;
    }

    public static boolean isOpponentInCheck(Board board) {
        Color ptm = board.getPlayerToMove();
        return AttackDetector.attacked(board, board.getKingSquare(Color.swap(ptm)), ptm);
    }

    public static boolean isPlayerInCheck(Board board) {
        Color ptm = board.getPlayerToMove();
        return AttackDetector.attacked(board, board.getKingSquare(ptm), Color.swap(ptm));
    }

    public static boolean blackCanCastleQueenSide(Board board) {
        if (!board.hasCastlingRight(BLACK_QUEENSIDE)) {
            return false;
        }

        boolean pathIsClear = board.isEmpty(D8) && board.isEmpty(C8) && board.isEmpty(B8);
        if (!pathIsClear) {
            return false;
        }

        Color opponent = Color.swap(board.getPlayerToMove());
        boolean wouldCrossCheck = AttackDetector.attacked(board, E8, opponent)
                || AttackDetector.attacked(board, D8, opponent);

        return !wouldCrossCheck;
    }

    public static boolean blackCanCastleKingSide(Board board) {
        if (!board.hasCastlingRight(BLACK_KINGSIDE)) {
            return false;
        }
        boolean pathIsClear = board.isEmpty(F8) && board.isEmpty(G8);
        if (!pathIsClear) {
            return false;
        }

        Color opponent = Color.swap(board.getPlayerToMove());
        boolean wouldCrossCheck  = AttackDetector.attacked(board, E8, opponent)
                || AttackDetector.attacked(board, F8, opponent);

        return !wouldCrossCheck;
    }

    public static boolean whiteCanCastleKingSide(Board board) {
        if (!board.hasCastlingRight(WHITE_KINGSIDE)) {
            return false;
        }

        boolean pathIsClear = board.isEmpty(F1) && board.isEmpty(G1);
        if (!pathIsClear) {
            return false;
        }

        Color opponent = Color.swap(board.getPlayerToMove());
        boolean wouldCrossCheck = AttackDetector.attacked(board, E1, opponent)
                || AttackDetector.attacked(board, F1, opponent);

        return !wouldCrossCheck;
    }

    public static boolean whiteCanCastleQueenSide(Board board) {
        if (!board.hasCastlingRight(WHITE_QUEENSIDE)) {
            return false;
        }

        boolean pathIsClear = board.isEmpty(D1) && board.isEmpty(C1) && board.isEmpty(B1);
        if (!pathIsClear) {
            return false;
        }

        Color opponent = Color.swap(board.getPlayerToMove());
        boolean wouldCrossCheck = AttackDetector.attacked(board, E1, opponent)
                || AttackDetector.attacked(board, D1, opponent);

        return !wouldCrossCheck;
    }

    private static boolean isPseudoLegalPawnMove(Board board, Move m) {

        if (m.captured()==null) {
            if (board.getPiece(m.to()) != null) return false;
            if (board.getPlayerToMove()==Color.WHITE) {
                Square nsq = m.from().north().get();
                if (m.to()==nsq) return true;
                Optional<Square> nnsq = nsq.north();
                if (nnsq.isPresent()) {
                    if (m.to()==nnsq.get() && board.getPiece(nsq)==null) return true;
                }
            } else {
                Square ssq = m.from().south().get();
                if (m.to()==ssq) return true;
                Optional<Square> sssq = ssq.south();
                if (sssq.isPresent()) {
                    if (m.to()==sssq.get() && board.getPiece(ssq)==null) return true;
                }
            }
        } else {
            if ((Bitboard.pawnAttacks[m.from().value()][board.getPlayerToMove().getColor()]
                    & Bitboard.squares[m.to().value()]) == 0) return false;

            if (board.getPlayerToMove()==Color.WHITE) {
                if (m.isEpCapture()) {
                    return m.to() == board.getEpSquare() && board.getPiece(m.to().south().get()) == BLACK_PAWN;
                } else {
                    return board.getPiece(m.to()) == m.captured();
                }
            } else {
                if (m.isEpCapture()) {
                    return m.to() == board.getEpSquare() && board.getPiece(m.to().north().get()) == WHITE_PAWN;
                } else {
                    return board.getPiece(m.to()) == m.captured();
                }
            }
        }

        return false;
    }

}
