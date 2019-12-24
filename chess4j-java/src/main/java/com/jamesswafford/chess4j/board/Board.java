package com.jamesswafford.chess4j.board;

import java.util.*;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.Zobrist;
import com.jamesswafford.chess4j.movegen.AttackDetector;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Piece;

import static com.jamesswafford.chess4j.pieces.Pawn.*;
import static com.jamesswafford.chess4j.pieces.Knight.*;
import static com.jamesswafford.chess4j.pieces.Bishop.*;
import static com.jamesswafford.chess4j.pieces.Rook.*;
import static com.jamesswafford.chess4j.pieces.Queen.*;
import static com.jamesswafford.chess4j.pieces.King.*;
import static com.jamesswafford.chess4j.board.CastlingRights.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;
import static com.jamesswafford.chess4j.board.squares.Square.*;


public final class Board {

    public static final Board INSTANCE = new Board();

    private List<Undo> undoStack = new ArrayList<>();
    private Map<Square,Piece> pieceMap = new HashMap<>();
    private Map<Piece,Integer> pieceCountsMap = new HashMap<>();
    private MyCastlingRights castlingRights = new MyCastlingRights();
    private Color playerToMove;
    private Square epSquare;
    private int moveCounter;
    private int fiftyCounter;
    private Square whiteKingSquare, blackKingSquare;
    private long whitePawns, blackPawns;
    private long whiteKnights, blackKnights;
    private long whiteBishops, blackBishops;
    private long whiteRooks, blackRooks;
    private long whiteQueens, blackQueens;
    private long whitePieces, blackPieces;
    private long zobristKey;
    private long pawnKey;

    private Board() {
        resetBoard();
    }

    public void addCastlingRight(CastlingRights castlingRight) {
        if (castlingRight == WHITE_KINGSIDE) {
            if (!castlingRights.isWhiteKingside()) {
                castlingRights.setWhiteKingside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == WHITE_QUEENSIDE) {
            if (!castlingRights.isWhiteQueenside()) {
                castlingRights.setWhiteQueenside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == BLACK_KINGSIDE) {
            if (!castlingRights.isBlackKingside()) {
                castlingRights.setBlackKingside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == BLACK_QUEENSIDE) {
            if (!castlingRights.isBlackQueenside()) {
                castlingRights.setBlackQueenside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else {
            throw new IllegalArgumentException("illegal castling right: " + castlingRight);
        }
    }

    private void addPieceToDestination(Move m) {
        Piece p = getPiece(m.from());
        addPiece(p,m.to());
        if (p==WHITE_PAWN) {
            fiftyCounter = 0;
            Optional<Square> nnSq = m.from().north().flatMap(Square::north);
            if (nnSq.isPresent() && m.to()==nnSq.get()) {
                setEP(m.from().north().get());
            } else if (m.to().rank()==RANK_8) {
                assert(m.promotion()!=null);
                removePiece(m.to());
                addPiece(m.promotion(),m.to());
            }
        } else if (p==BLACK_PAWN) {
            fiftyCounter = 0;
            Optional<Square> ssSq = m.from().south().flatMap(Square::south);
            if (ssSq.isPresent() && m.to()==ssSq.get()) {
                setEP(m.from().south().get());
            } else if (m.to().rank()==RANK_1) {
                assert(m.promotion()!=null);
                removePiece(m.to());
                addPiece(m.promotion(),m.to());
            }
        } else if (p==WHITE_KING) {
            whiteKingSquare = m.to();
            if (m.from() == E1) {
                if (m.to() == G1) {
                    assert(m.isCastle());
                    fiftyCounter = 0;
                    removePiece(H1);
                    addPiece(WHITE_ROOK, F1);
                } else if (m.to() == C1) {
                    assert(m.isCastle());
                    fiftyCounter = 0;
                    removePiece(A1);
                    addPiece(WHITE_ROOK, D1);
                }
            }
        } else if (p==BLACK_KING) {
            blackKingSquare = m.to();
            if (m.from() == E8) {
                if (m.to() == G8) {
                    assert(m.isCastle());
                    fiftyCounter = 0;
                    removePiece(H8);
                    addPiece(BLACK_ROOK, F8);
                } else if (m.to() == C8) {
                    assert(m.isCastle());
                    fiftyCounter = 0;
                    removePiece(A8);
                    addPiece(BLACK_ROOK, D8);
                }
            }
        }
    }

    public void addPiece(Piece p,Square s) {
        assert(p != null);
        assert(getPiece(s)==null);

        pieceMap.put(s, p);
        long bb = Bitboard.squares[s.value()];

        if (p.isWhite()) {
            whitePieces |= bb;
            if (p==WHITE_PAWN) {
                whitePawns |= bb;
                pawnKey ^= Zobrist.getPieceKey(s, p);
            } else if (p==WHITE_KNIGHT) {
                whiteKnights |= bb;
            } else if (p==WHITE_BISHOP) {
                whiteBishops |= bb;
            } else if (p==WHITE_ROOK) {
                whiteRooks |= bb;
            } else if (p==WHITE_QUEEN) {
                whiteQueens |= bb;
            }
        } else {
            blackPieces |= bb;
            if (p==BLACK_PAWN) {
                blackPawns |= bb;
                pawnKey ^= Zobrist.getPieceKey(s, p);
            } else if (p==BLACK_KNIGHT) {
                blackKnights |= bb;
            } else if (p==BLACK_BISHOP) {
                blackBishops |= bb;
            } else if (p==BLACK_ROOK) {
                blackRooks |= bb;
            } else if (p==BLACK_QUEEN) {
                blackQueens |= bb;
            }
        }

        pieceCountsMap.put(p, pieceCountsMap.get(p)+1);
        zobristKey ^= Zobrist.getPieceKey(s, p);
    }

    public void applyMove(Move m) {
        assert(verify());
        undoStack.add(new Undo(m,fiftyCounter,castlingRights.getValue(),epSquare,zobristKey));

        swapPlayer();
        moveCounter++;
        fiftyCounter++; // may get reset

        if (m.captured()!=null) {
            fiftyCounter = 0;
            removeCapturedPiece(m);
        }

        clearEPSquare();
        addPieceToDestination(m);
        removeCastlingAvailability(m);
        removePiece(m.from());

        assert(verify());
    }

    public void clearBoard() {
        List<Square> squares = Square.allSquares();
        for (Square sq : squares) {
            if (getPiece(sq)!=null) {
                removePiece(sq);
            }
        }

        clearEPSquare();
        Set<CastlingRights> crs = EnumSet.allOf(CastlingRights.class);
        for (CastlingRights cr : crs) {
            clearCastlingRight(cr);
        }
        fiftyCounter = 0;
        undoStack.clear();

        assert(pieceCountsMap.get(WHITE_PAWN)==0);
        assert(pieceCountsMap.get(BLACK_PAWN)==0);
        assert(pieceCountsMap.get(WHITE_QUEEN)==0);
        assert(pieceCountsMap.get(BLACK_QUEEN)==0);
        assert(pieceCountsMap.get(WHITE_ROOK)==0);
        assert(pieceCountsMap.get(BLACK_ROOK)==0);
        assert(pieceCountsMap.get(WHITE_KNIGHT)==0);
        assert(pieceCountsMap.get(BLACK_KNIGHT)==0);
        assert(pieceCountsMap.get(WHITE_BISHOP)==0);
        assert(pieceCountsMap.get(BLACK_BISHOP)==0);
    }

    public void clearCastlingRight(CastlingRights castlingRight) {
        if (castlingRight == WHITE_KINGSIDE) {
            if (castlingRights.isWhiteKingside()) {
                castlingRights.removeWhiteKingside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == WHITE_QUEENSIDE) {
            if (castlingRights.isWhiteQueenside()) {
                castlingRights.removeWhiteQueenside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == BLACK_KINGSIDE) {
            if (castlingRights.isBlackKingside()) {
                castlingRights.removeBlackKingside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else if (castlingRight == BLACK_QUEENSIDE) {
            if (castlingRights.isBlackQueenside()) {
                castlingRights.removeBlackQueenside();
                zobristKey ^= Zobrist.getCastlingKey(castlingRight);
            }
        } else {
            throw new IllegalArgumentException("illegal castling right: " + castlingRight);
        }
    }

    public Square clearEPSquare() {
        Square sq = epSquare;
        if (sq != null) {
            zobristKey ^= Zobrist.getEnPassantKey(sq);
            epSquare = null;
        }
        return sq;
    }

    public synchronized Board deepCopy() {
        Board b = new Board();
        b.undoStack.clear();
        b.undoStack.addAll(undoStack);
        b.pieceMap.clear();
        for (Square sq : pieceMap.keySet()) {
            b.pieceMap.put(sq, pieceMap.get(sq));
        }
        b.castlingRights.setValue(castlingRights.getValue());
        b.playerToMove=playerToMove;
        b.epSquare=epSquare;
        b.whiteKingSquare=whiteKingSquare;
        b.blackKingSquare=blackKingSquare;
        b.whitePawns=whitePawns;
        b.blackPawns=blackPawns;
        b.whiteKnights=whiteKnights;
        b.blackKnights=blackKnights;
        b.whiteBishops=whiteBishops;
        b.blackBishops=blackBishops;
        b.whiteRooks=whiteRooks;
        b.blackRooks=blackRooks;
        b.whiteQueens=whiteQueens;
        b.blackQueens=blackQueens;
        b.whitePieces=whitePieces;
        b.blackPieces=blackPieces;
        b.fiftyCounter=fiftyCounter;
        b.moveCounter=moveCounter;
        b.zobristKey=zobristKey;
        b.pawnKey=pawnKey;
        for (Piece p : pieceCountsMap.keySet()) {
            b.pieceCountsMap.put(p, pieceCountsMap.get(p));
        }
        return b;
    }

    public boolean equalExceptMoveHistory(Board otherBoard,boolean strict) {
        if (!this.pieceMap.equals(otherBoard.pieceMap)) {
            return false;
        }
        if (!this.pieceCountsMap.equals(otherBoard.pieceCountsMap)) {
            return false;
        }
        if (!this.castlingRights.equals(otherBoard.castlingRights)) {
            return false;
        }
        if (!this.getPlayerToMove().equals(otherBoard.getPlayerToMove())) {
            return false;
        }
        if (this.epSquare==null) {
            if (otherBoard.epSquare!=null) {
                return false;
            }
        } else {
            if (!this.epSquare.equals(otherBoard.epSquare)) {
                return false;
            }
        }
        if (this.blackKingSquare==null) {
            if (otherBoard.blackKingSquare!=null) {
                return false;
            }
        } else {
            if (!this.blackKingSquare.equals(otherBoard.blackKingSquare)) {
                return false;
            }
        }
        if (this.whiteKingSquare==null) {
            if (otherBoard.whiteKingSquare!=null) {
                return false;
            }
        } else {
            if (!this.whiteKingSquare.equals(otherBoard.whiteKingSquare)) {
                return false;
            }
        }
        if (this.whitePawns!=otherBoard.whitePawns) {
            return false;
        }
        if (this.blackPawns!=otherBoard.blackPawns) {
            return false;
        }
        if (this.whiteKnights!=otherBoard.whiteKnights) {
            return false;
        }
        if (this.blackKnights!=otherBoard.blackKnights) {
            return false;
        }
        if (this.whiteBishops!=otherBoard.whiteBishops) {
            return false;
        }
        if (this.blackBishops!=otherBoard.blackBishops) {
            return false;
        }
        if (this.whiteRooks!=otherBoard.whiteRooks) {
            return false;
        }
        if (this.blackRooks!=otherBoard.blackRooks) {
            return false;
        }
        if (this.whiteQueens!=otherBoard.whiteQueens) {
            return false;
        }
        if (this.blackQueens!=otherBoard.blackQueens) {
            return false;
        }
        if (this.whitePieces!=otherBoard.whitePieces) {
            return false;
        }
        if (this.blackPieces!=otherBoard.blackPieces) {
            return false;
        }
        if (strict) {
            if (this.moveCounter!=otherBoard.moveCounter) {
                return false;
            }
            if (this.fiftyCounter!=otherBoard.fiftyCounter) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        }
        Board that = (Board)obj;
        if (!equalExceptMoveHistory(that,true)) {
            return false;
        }
        if (!this.undoStack.equals(that.undoStack)) {
            return false;
        }
        return true;
    }

    private Square findKingSquare(King kingSquare) {
        return Square.allSquares()
                .stream()
                .filter(sq -> getPiece(sq)==kingSquare)
                .findFirst()
                .get();
    }

    public void flipVertical() {
        List<Square> squares = Square.allSquares();
        Map<Square,Piece> myPieceMap = new HashMap<>();

        // remove pieces, remembering where they were
        for (Square sq : squares) {
            Piece p = getPiece(sq);
            if (p != null) {
                removePiece(sq);
                myPieceMap.put(sq, p);
            }
        }

        // now flip them around
        for (Square sq : squares) {
            Piece p = myPieceMap.get(sq);
            if (p != null) {
                addPiece(p.getOppositeColorPiece(),sq.flipVertical());
            }
        }

        swapPlayer();

        Square myEP = clearEPSquare();
        if (myEP != null) {
            setEP(myEP.flipVertical());
        }

        // flip castling rights
        MyCastlingRights myCastlingRights = new MyCastlingRights(castlingRights.getValue());

        // first remove existing rights
        if (castlingRights.isWhiteKingside()) {
            clearCastlingRight(WHITE_KINGSIDE);
        }
        if (castlingRights.isWhiteQueenside()) {
            clearCastlingRight(WHITE_QUEENSIDE);
        }
        if (castlingRights.isBlackKingside()) {
            clearCastlingRight(BLACK_KINGSIDE);
        }
        if (castlingRights.isBlackQueenside()) {
            clearCastlingRight(BLACK_QUEENSIDE);
        }

        if (myCastlingRights.isWhiteKingside()) {
            addCastlingRight(BLACK_KINGSIDE);
        }
        if (myCastlingRights.isWhiteQueenside()) {
            addCastlingRight(BLACK_QUEENSIDE);
        }
        if (myCastlingRights.isBlackKingside()) {
            addCastlingRight(WHITE_KINGSIDE);
        }
        if (myCastlingRights.isBlackQueenside()) {
            addCastlingRight(WHITE_QUEENSIDE);
        }

        resetKingSquares();
        assert(verify());
    }

    private Square getBlackKingSquare() {
        assert(blackKingSquare!=null);
        assert(blackKingSquare.equals(findKingSquare(King.BLACK_KING)));
        return blackKingSquare;
    }

    public Square getEPSquare() {
        return epSquare;
    }

    public int getFiftyCounter() {
        return fiftyCounter;
    }

    public Square getKingSquare(Color player) {
        return player.isWhite() ? getWhiteKingSquare() : getBlackKingSquare();
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public int getNumPieces(Piece p) {
        return pieceCountsMap.get(p);
    }

    public long getPawnKey() {
        assert(pawnKey == Zobrist.getPawnKey(this));
        return pawnKey;
    }

    public Piece getPiece(Square square) {
        return pieceMap.get(square);
    }

    public Color getPlayerToMove() {
        return playerToMove;
    }

    public List<Undo> getUndos() {
        return Collections.unmodifiableList(undoStack);
    }

    private Square getWhiteKingSquare() {
        assert(whiteKingSquare!=null);
        assert(whiteKingSquare.equals(findKingSquare(King.WHITE_KING)));
        return whiteKingSquare;
    }

    public long getZobristKey() {
        assert(zobristKey == Zobrist.getBoardKey(this));
        return zobristKey;
    }

    public boolean hasCastlingRight(CastlingRights cr) {
        if (WHITE_KINGSIDE==cr) {
            return castlingRights.isWhiteKingside();
        } else if (WHITE_QUEENSIDE==cr) {
            return castlingRights.isWhiteQueenside();
        } else if (BLACK_KINGSIDE==cr) {
            return castlingRights.isBlackKingside();
        } else if (BLACK_QUEENSIDE==cr) {
            return castlingRights.isBlackQueenside();
        } else {
            throw new IllegalArgumentException("unknown castling right: " + cr);
        }
    }

    @Override
    public int hashCode() {
        int hash = hashCodeWithoutMoveHistory(true);
        hash = hash * 31 + undoStack.hashCode();

        return hash;
    }

    public int hashCodeWithoutMoveHistory(boolean strict) {
        int hash = pieceMap.hashCode();
        hash = hash * 17 + castlingRights.hashCode();
        hash = hash * 13 + playerToMove.hashCode();
        hash = hash * 31 + (epSquare == null ? 0 : epSquare.hashCode());
        hash = hash * 17 + (whiteKingSquare == null ? 0 : whiteKingSquare.hashCode());
        hash = hash * 13 + (blackKingSquare == null ? 0 : blackKingSquare.hashCode());
        hash = hash * 23 + Long.valueOf(whitePawns).hashCode();
        hash = hash * 29 + Long.valueOf(blackPawns).hashCode();
        hash = hash * 31 + Long.valueOf(whiteKnights).hashCode();
        hash = hash * 37 + Long.valueOf(blackKnights).hashCode();
        hash = hash * 43 + Long.valueOf(whiteBishops).hashCode();
        hash = hash * 47 + Long.valueOf(blackBishops).hashCode();
        hash = hash * 53 + Long.valueOf(whiteRooks).hashCode();
        hash = hash * 59 + Long.valueOf(blackRooks).hashCode();
        hash = hash * 61 + Long.valueOf(whiteQueens).hashCode();
        hash = hash * 67 + Long.valueOf(blackQueens).hashCode();
        hash = hash * 71 + Long.valueOf(whitePieces).hashCode();
        hash = hash * 73 + Long.valueOf(blackPieces).hashCode();

        if (strict) {
            hash = hash * 17 + moveCounter;
            hash = hash * 13 + fiftyCounter;
        }
        return hash;
    }

    public boolean isEmpty(Square square) {
        return pieceMap.get(square)==null;
    }

    public boolean isOpponentInCheck() {
        return AttackDetector.attacked(this,getKingSquare(Color.swap(playerToMove)),playerToMove);
    }

    public boolean isPlayerInCheck() {
        return AttackDetector.attacked(this,getKingSquare(playerToMove),Color.swap(playerToMove));
    }

    private void removeRookCastlingAvailability(Square sq) {
        if (sq == A1) {
            if (castlingRights.isWhiteQueenside()) {
                zobristKey ^= Zobrist.getCastlingKey(WHITE_QUEENSIDE);
                castlingRights.removeWhiteQueenside();
            }
        } else if (sq == H1) {
            if (castlingRights.isWhiteKingside()) {
                zobristKey ^= Zobrist.getCastlingKey(WHITE_KINGSIDE);
                castlingRights.removeWhiteKingside();
            }
        } else if (sq == A8) {
            if (castlingRights.isBlackQueenside()) {
                zobristKey ^= Zobrist.getCastlingKey(BLACK_QUEENSIDE);
                castlingRights.removeBlackQueenside();
            }
        } else if (sq == H8) {
            if (castlingRights.isBlackKingside()) {
                zobristKey ^= Zobrist.getCastlingKey(BLACK_KINGSIDE);
                castlingRights.removeBlackKingside();
            }
        }
    }

    private void removeCastlingAvailability(Move m) {
        // if capturing a rook remove its castling availability
        if (m.captured() != null) {
            removeRookCastlingAvailability(m.to());
        }

        // if a rook or king is moving, remove their castling availability
        Piece p = getPiece(m.from());
        if (p==WHITE_ROOK || p==BLACK_ROOK) {
            removeRookCastlingAvailability(m.from());
        } else if (p==WHITE_KING) {
            if (castlingRights.isWhiteKingside()) {
                zobristKey ^= Zobrist.getCastlingKey(WHITE_KINGSIDE);
                castlingRights.removeWhiteKingside();
            }
            if (castlingRights.isWhiteQueenside()) {
                zobristKey ^= Zobrist.getCastlingKey(WHITE_QUEENSIDE);
                castlingRights.removeWhiteQueenside();
            }
        } else if (p==BLACK_KING) {
            if (castlingRights.isBlackKingside()) {
                zobristKey ^= Zobrist.getCastlingKey(BLACK_KINGSIDE);
                castlingRights.removeBlackKingside();
            }
            if (castlingRights.isBlackQueenside()) {
                zobristKey ^= Zobrist.getCastlingKey(BLACK_QUEENSIDE);
                castlingRights.removeBlackQueenside();
            }
        }
    }

    private void removeCapturedPiece(Move m) {
        assert(m.captured()!=null);
        Piece captured;
        if (m.isEpCapture()) {
            assert(epSquare != null);
            assert(m.to()==epSquare);
            // remove pawn
            if (getPlayerToMove()==Color.WHITE) { // black WAS on move
                captured = removePiece(epSquare.north().get());
                assert(captured==WHITE_PAWN);
            } else {
                captured = removePiece(epSquare.south().get());
                assert(captured==BLACK_PAWN);
            }
        } else {
            removePiece(m.to());
        }

    }

    private Piece removePiece(Square sq) {
        Piece p = getPiece(sq);
        assert(p != null);

        long bb_sq = Bitboard.squares[sq.value()];
        if (p.isWhite()) {
            whitePieces ^= bb_sq;
            if (p==WHITE_PAWN) {
                whitePawns ^= bb_sq;
                pawnKey ^= Zobrist.getPieceKey(sq, p);
            } else if (p==WHITE_KNIGHT) {
                whiteKnights ^= bb_sq;
            } else if (p==WHITE_BISHOP) {
                whiteBishops ^= bb_sq;
            } else if (p==WHITE_ROOK) {
                whiteRooks ^= bb_sq;
            } else if (p==WHITE_QUEEN) {
                whiteQueens ^= bb_sq;
            }
        } else {
            blackPieces ^= bb_sq;
            if (p==BLACK_PAWN) {
                blackPawns ^= bb_sq;
                pawnKey ^= Zobrist.getPieceKey(sq, p);
            } else if (p==BLACK_KNIGHT) {
                blackKnights ^= bb_sq;
            } else if (p==BLACK_BISHOP) {
                blackBishops ^= bb_sq;
            } else if (p==BLACK_ROOK) {
                blackRooks ^= bb_sq;
            } else if (p==BLACK_QUEEN) {
                blackQueens ^= bb_sq;
            }
        }

        pieceMap.remove(sq);
        pieceCountsMap.put(p, pieceCountsMap.get(p)-1);
        assert(pieceCountsMap.get(p) >= 0);
        zobristKey ^= Zobrist.getPieceKey(sq, p);

        return p;
    }

    public void resetBoard() {
        undoStack.clear();
        pieceMap.clear();
        pieceCountsMap.put(WHITE_QUEEN, 0);
        pieceCountsMap.put(BLACK_QUEEN, 0);
        pieceCountsMap.put(WHITE_ROOK, 0);
        pieceCountsMap.put(BLACK_ROOK, 0);
        pieceCountsMap.put(WHITE_KNIGHT, 0);
        pieceCountsMap.put(BLACK_KNIGHT, 0);
        pieceCountsMap.put(WHITE_BISHOP, 0);
        pieceCountsMap.put(BLACK_BISHOP, 0);
        pieceCountsMap.put(WHITE_KING, 0);
        pieceCountsMap.put(BLACK_KING, 0);
        pieceCountsMap.put(WHITE_PAWN, 0);
        pieceCountsMap.put(BLACK_PAWN, 0);

        whitePawns = blackPawns = 0;
        whiteKnights = blackKnights = 0;
        whiteBishops = blackBishops = 0;
        whiteRooks = blackRooks = 0;
        whiteQueens = blackQueens = 0;
        whitePieces = blackPieces = 0;

        zobristKey = 0;
        pawnKey = 0;

        addPiece(BLACK_ROOK, A8);
        addPiece(BLACK_KNIGHT, B8);
        addPiece(BLACK_BISHOP, C8);
        addPiece(BLACK_QUEEN, D8);
        addPiece(BLACK_KING, E8);
        addPiece(BLACK_BISHOP, F8);
        addPiece(BLACK_KNIGHT, G8);
        addPiece(BLACK_ROOK, H8);

        Square.rankSquares(Rank.RANK_7).stream().forEach(sq -> addPiece(BLACK_PAWN,sq));
        Square.rankSquares(Rank.RANK_2).stream().forEach(sq -> addPiece(WHITE_PAWN,sq));

        addPiece(WHITE_ROOK, A1);
        addPiece(WHITE_KNIGHT, B1);
        addPiece(WHITE_BISHOP, C1);
        addPiece(WHITE_QUEEN, D1);
        addPiece(WHITE_KING, E1);
        addPiece(WHITE_BISHOP, F1);
        addPiece(WHITE_KNIGHT, G1);
        addPiece(WHITE_ROOK, H1);

        castlingRights.clear();
        EnumSet.allOf(CastlingRights.class).stream().forEach(cr -> addCastlingRight(cr));

        playerToMove = Color.WHITE;
        zobristKey ^= Zobrist.getPlayerKey(Color.WHITE);

        epSquare = null;
        whiteKingSquare = E1;
        blackKingSquare = E8;
        moveCounter = 0;
        fiftyCounter = 0;
    }

    public void resetKingSquares() {
        blackKingSquare = findKingSquare(BLACK_KING);
        whiteKingSquare = findKingSquare(WHITE_KING);
    }

    public void setEP(Square ep) {
        assert(ep != null);
        epSquare = ep;
        zobristKey ^= Zobrist.getEnPassantKey(ep);
    }

    public void setFiftyCounter(int fiftyCounter) {
        this.fiftyCounter=fiftyCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter=moveCounter;
    }

    public void swapPlayer() {
        zobristKey ^= Zobrist.getPlayerKey(playerToMove);
        playerToMove = Color.swap(playerToMove);
        zobristKey ^= Zobrist.getPlayerKey(playerToMove);
    }

    public void undoLastMove() {
        assert(verify());
        assert(undoStack.size() > 0);
        int ind = undoStack.size()-1;
        Undo u = undoStack.remove(ind);

        swapPlayer();
        epSquare = u.getEpSquare();
        moveCounter--;
        fiftyCounter = u.getFiftyCounter();
        castlingRights.setValue(u.getCastlingRights());

        // remove the moving piece from toSq
        if (getPiece(u.getMove().to()) != null) {
            removePiece(u.getMove().to());
        }

        // place the moving piece on fromSq
        addPiece(u.getMove().piece(),u.getMove().from());
        if (u.getMove().piece()==WHITE_KING) {
            whiteKingSquare = u.getMove().from();
        } else if (u.getMove().piece()==BLACK_KING) {
            blackKingSquare = u.getMove().from();
        }

        // restore the captured piece
        if (u.getMove().captured()!=null) {
            if (u.getMove().isEpCapture()) {
                if (getPlayerToMove()==Color.BLACK) {
                    addPiece(WHITE_PAWN,epSquare.north().get());
                } else {
                    addPiece(BLACK_PAWN,epSquare.south().get());
                }
            } else {
                addPiece(u.getMove().captured(),u.getMove().to());
            }
        } else if (u.getMove().isCastle()) {
            if (u.getMove().from() == E1) {
                if (u.getMove().to() == G1) {
                    removePiece(F1);
                    addPiece(WHITE_ROOK, H1);
                } else {
                    removePiece(D1);
                    addPiece(WHITE_ROOK, A1);
                }
            } else {
                if (u.getMove().to() == G8) {
                    removePiece(F8);
                    addPiece(BLACK_ROOK, H8);
                } else {
                    removePiece(D8);
                    addPiece(BLACK_ROOK, A8);
                }
            }
        }

        zobristKey = u.getZobristKey();
        assert(verify());
    }

    private boolean verify() {

        assert(moveCounter >= fiftyCounter);

        // test king placement
        Square myWhiteKingSq=null;
        Square myBlackKingSq=null;
        for (Square sq : Square.allSquares()) {
            Piece p = getPiece(sq);
            if (p==WHITE_KING) {
                assert(myWhiteKingSq==null);
                myWhiteKingSq = sq;
            }
            if (p==BLACK_KING) {
                assert(myBlackKingSq==null);
                myBlackKingSq = sq;
            }
        }
        assert(myWhiteKingSq != null && myWhiteKingSq==whiteKingSquare);
        assert(myBlackKingSq != null && myBlackKingSq==blackKingSquare);

        // ep square
        if (epSquare != null) {
            if (playerToMove==Color.BLACK) {
                assert(epSquare.rank()==Rank.RANK_3);
                assert(getPiece(epSquare.north().get())==WHITE_PAWN);
            } else {
                assert(epSquare.rank()==Rank.RANK_6);
                assert(getPiece(epSquare.south().get())==BLACK_PAWN);
            }
        }

        long myWhitePawns=0,myBlackPawns=0;
        long myWhiteKnights=0,myBlackKnights=0;
        long myWhiteBishops=0,myBlackBishops=0;
        long myWhiteRooks=0,myBlackRooks=0;
        long myWhiteQueens=0,myBlackQueens=0;
        long myWhitePieces=0,myBlackPieces=0;

        int numWhitePawns=0,numBlackPawns=0;
        int numWhiteKnights=0,numBlackKnights=0;
        int numWhiteBishops=0,numBlackBishops=0;
        int numWhiteRooks=0,numBlackRooks=0;
        int numWhiteQueens=0,numBlackQueens=0;


        for (Square sq : Square.allSquares()) {
            Piece p = getPiece(sq);
            long bb = Bitboard.squares[sq.value()];
            if (p != null) {
                if (p.isWhite()) {
                    myWhitePieces |= bb;
                } else {
                    myBlackPieces |= bb;
                }
            }

            if (WHITE_PAWN==p) {
                myWhitePawns |= bb;
                numWhitePawns++;
            } else if (BLACK_PAWN==p) {
                myBlackPawns |= bb;
                numBlackPawns++;
            } else if (WHITE_KNIGHT==p) {
                myWhiteKnights |= bb;
                numWhiteKnights++;
            } else if (BLACK_KNIGHT==p) {
                myBlackKnights |= bb;
                numBlackKnights++;
            } else if (WHITE_BISHOP==p) {
                myWhiteBishops |= bb;
                numWhiteBishops++;
            } else if (BLACK_BISHOP==p) {
                myBlackBishops |= bb;
                numBlackBishops++;
            } else if (WHITE_ROOK==p) {
                myWhiteRooks |= bb;
                numWhiteRooks++;
            } else if (BLACK_ROOK==p) {
                myBlackRooks |= bb;
                numBlackRooks++;
            } else if (WHITE_QUEEN==p) {
                myWhiteQueens |= bb;
                numWhiteQueens++;
            } else if (BLACK_QUEEN==p) {
                myBlackQueens |= bb;
                numBlackQueens++;
            }
        }

        assert(whitePawns == myWhitePawns);
        assert(blackPawns == myBlackPawns);
        assert(whiteKnights == myWhiteKnights);
        assert(blackKnights == myBlackKnights);
        assert(whiteBishops == myWhiteBishops);
        assert(blackBishops == myBlackBishops);
        assert(whiteRooks == myWhiteRooks);
        assert(blackRooks == myBlackRooks);
        assert(whiteQueens == myWhiteQueens);
        assert(blackQueens == myBlackQueens);
        assert(whitePieces == myWhitePieces);
        assert(blackPieces == myBlackPieces);

        assert(pieceCountsMap.get(WHITE_PAWN)==numWhitePawns);
        assert(pieceCountsMap.get(BLACK_PAWN)==numBlackPawns);
        assert(pieceCountsMap.get(WHITE_KNIGHT)==numWhiteKnights);
        assert(pieceCountsMap.get(BLACK_KNIGHT)==numBlackKnights);
        assert(pieceCountsMap.get(WHITE_BISHOP)==numWhiteBishops);
        assert(pieceCountsMap.get(BLACK_BISHOP)==numBlackBishops);
        assert(pieceCountsMap.get(WHITE_ROOK)==numWhiteRooks);
        assert(pieceCountsMap.get(BLACK_ROOK)==numBlackRooks);
        assert(pieceCountsMap.get(WHITE_QUEEN)==numWhiteQueens);
        assert(pieceCountsMap.get(BLACK_QUEEN)==numBlackQueens);

        // assert castling rights make sense
        if (hasCastlingRight(BLACK_QUEENSIDE)) {
            assert(getPiece(E8)==BLACK_KING);
            assert(getPiece(A8)==BLACK_ROOK);
        }
        if (hasCastlingRight(BLACK_KINGSIDE)) {
            assert(getPiece(E8)==BLACK_KING);
            assert(getPiece(H8)==BLACK_ROOK);
        }
        if (hasCastlingRight(WHITE_QUEENSIDE)) {
            assert(getPiece(E1)==WHITE_KING);
            assert(getPiece(A1)==WHITE_ROOK);
        }
        if (hasCastlingRight(WHITE_KINGSIDE)) {
            assert(getPiece(E1)==WHITE_KING);
            assert(getPiece(H1)==WHITE_ROOK);
        }

        assert(zobristKey==Zobrist.getBoardKey(this));
        assert(pawnKey==Zobrist.getPawnKey(this));

        return true;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public long getBlackQueens() {
        return blackQueens;
    }

    public long getWhitePieces() {
        return whitePieces;
    }

    public long getBlackPieces() {
        return blackPieces;
    }


}

class MyCastlingRights {

    private static final int WHITE_KINGSIDE = 0x01;
    private static final int WHITE_QUEENSIDE = 0x02;
    private static final int BLACK_KINGSIDE = 0x04;
    private static final int BLACK_QUEENSIDE = 0x08;

    private int castlingRights;

    public MyCastlingRights() {
        clear();
    }

    public MyCastlingRights(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    public void setWhiteKingside() {
        castlingRights |= WHITE_KINGSIDE;
    }

    public void setWhiteQueenside() {
        castlingRights |= WHITE_QUEENSIDE;
    }

    public void setBlackKingside() {
        castlingRights |= BLACK_KINGSIDE;
    }

    public void setBlackQueenside() {
        castlingRights |= BLACK_QUEENSIDE;
    }

    public void removeWhiteKingside() {
        castlingRights &= ~WHITE_KINGSIDE;
    }

    public void removeWhiteQueenside() {
        castlingRights &= ~WHITE_QUEENSIDE;
    }

    public void removeBlackKingside() {
        castlingRights &= ~BLACK_KINGSIDE;
    }

    public void removeBlackQueenside() {
        castlingRights &= ~BLACK_QUEENSIDE;
    }

    public boolean isWhiteKingside() {
        return (castlingRights & WHITE_KINGSIDE) > 0;
    }

    public boolean isWhiteQueenside() {
        return (castlingRights & WHITE_QUEENSIDE) > 0;
    }

    public boolean isBlackKingside() {
        return (castlingRights & BLACK_KINGSIDE) > 0;
    }

    public boolean isBlackQueenside() {
        return (castlingRights & BLACK_QUEENSIDE) > 0;
    }

    public void addAll() {
        castlingRights = WHITE_KINGSIDE | WHITE_QUEENSIDE | BLACK_KINGSIDE | BLACK_QUEENSIDE;
    }

    public void clear() {
        castlingRights = 0;
    }

    public int getValue() {
        return castlingRights;
    }

    public void setValue(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyCastlingRights)) {
            return false;
        }

        MyCastlingRights that = (MyCastlingRights)obj;
        if (this.castlingRights != that.castlingRights) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return this.castlingRights;
    }


}

