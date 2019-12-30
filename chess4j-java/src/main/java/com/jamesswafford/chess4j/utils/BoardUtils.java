package com.jamesswafford.chess4j.utils;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.movegen.AttackDetector;
import com.jamesswafford.chess4j.movegen.Magic;
import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;

import java.util.Optional;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;

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
            if ((Bitboard.squares[m.to().value()] & Bitboard.knightMoves[m.from().value()]) != 0)
                return true;
        } else if (mover==WHITE_BISHOP || mover==BLACK_BISHOP) {
            if ((Bitboard.squares[m.to().value()] & Magic.getBishopMoves(board, m.from().value(), ~0L)) != 0)
                return true;
        } else if (mover==WHITE_ROOK || mover==BLACK_ROOK) {
            if ((Bitboard.squares[m.to().value()] & Magic.getRookMoves(board, m.from().value(), ~0L)) != 0)
                return true;
        } else if (mover==WHITE_QUEEN || mover==BLACK_QUEEN) {
            if ((Bitboard.squares[m.to().value()] & Magic.getQueenMoves(board, m.from().value(), ~0L)) != 0)
                return true;
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
                } else if (m.to() == C8 && blackCanCastleQueenSide(board)) {
                    return true;
                }
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
                    if (m.to()==board.getEPSquare() && board.getPiece(m.to().south().get())==BLACK_PAWN)
                        return true;
                } else {
                    if (board.getPiece(m.to())==m.captured()) return true;
                }
            } else {
                if (m.isEpCapture()) {
                    if (m.to()==board.getEPSquare() && board.getPiece(m.to().north().get())==WHITE_PAWN)
                        return true;
                } else {
                    if (board.getPiece(m.to())==m.captured()) return true;
                }
            }
        }

        return false;
    }

}
