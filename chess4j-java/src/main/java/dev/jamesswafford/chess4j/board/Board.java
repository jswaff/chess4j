package dev.jamesswafford.chess4j.board;

import dev.jamesswafford.chess4j.board.squares.Rank;
import dev.jamesswafford.chess4j.board.squares.Square;
import dev.jamesswafford.chess4j.exceptions.ParseException;
import dev.jamesswafford.chess4j.hash.Zobrist;
import dev.jamesswafford.chess4j.nn.NeuralNetwork;
import dev.jamesswafford.chess4j.pieces.King;
import dev.jamesswafford.chess4j.pieces.Piece;
import dev.jamesswafford.chess4j.utils.BlankRemover;
import dev.jamesswafford.chess4j.utils.PieceFactory;
import lombok.Getter;

import java.util.*;

import static dev.jamesswafford.chess4j.board.CastlingRights.*;
import static dev.jamesswafford.chess4j.board.squares.Rank.RANK_1;
import static dev.jamesswafford.chess4j.board.squares.Rank.RANK_8;
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


public final class Board {

    public static final String INITIAL_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final Map<Square,Piece> pieceMap = new HashMap<>();
    private final Map<Piece,Integer> pieceCountsMap = new HashMap<>();
    private final MyCastlingRights castlingRights = new MyCastlingRights();
    @Getter
    private Color playerToMove;
    @Getter
    private Square epSquare;
    @Getter
    private int moveCounter;
    @Getter
    private int fiftyCounter;
    private Square whiteKingSquare, blackKingSquare;
    @Getter
    private long whitePawns, blackPawns;
    @Getter
    private long whiteKnights, blackKnights;
    @Getter
    private long whiteBishops, blackBishops;
    @Getter
    private long whiteRooks, blackRooks;
    @Getter
    private long whiteQueens, blackQueens;
    @Getter
    private long whitePieces, blackPieces;
    private long zobristKey;
    private long pawnKey;
    private final double[][] nn_accumulators = new double[2][NeuralNetwork.NN_SIZE_L1];

    public Board() {
        this(INITIAL_POS);
    }

    public Board(String fen) {

        // initialize the piece counts map
        pieceCountsMap.put(WHITE_KING, 0);
        pieceCountsMap.put(BLACK_KING, 0);
        pieceCountsMap.put(WHITE_QUEEN, 0);
        pieceCountsMap.put(BLACK_QUEEN, 0);
        pieceCountsMap.put(WHITE_ROOK, 0);
        pieceCountsMap.put(BLACK_ROOK, 0);
        pieceCountsMap.put(WHITE_BISHOP, 0);
        pieceCountsMap.put(BLACK_BISHOP, 0);
        pieceCountsMap.put(WHITE_KNIGHT, 0);
        pieceCountsMap.put(BLACK_KNIGHT, 0);
        pieceCountsMap.put(WHITE_PAWN, 0);
        pieceCountsMap.put(BLACK_PAWN, 0);

        setPos(fen);
    }

    public void addToNN_Accumulator(int ind1, int ind2, double val) {
        assert(ind1==0 || ind1==1);
        assert(ind2 >= 0 && ind2 < NeuralNetwork.NN_SIZE_L1);
        nn_accumulators[ind1][ind2] += val;
    }

    public Undo applyMove(Move move) {
        assert(verify());

        Undo undo = new Undo(move, fiftyCounter, castlingRights.getValue(), epSquare, zobristKey);

        swapPlayer();
        moveCounter++;

        if (move.captured()!=null) {
            fiftyCounter = 0;
            removeCapturedPiece(move);
        } else {
            fiftyCounter++;
        }

        clearEPSquare();
        addPieceToDestination(move);
        removeCastlingAvailability(move);
        removePiece(move.from());

        assert(verify());

        return undo;
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
        // TODO: nn_accumulators
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        }
        Board that = (Board)obj;
        if (!this.pieceMap.equals(that.pieceMap)) {
            return false;
        }
        if (!this.pieceCountsMap.equals(that.pieceCountsMap)) {
            return false;
        }
        if (!this.castlingRights.equals(that.castlingRights)) {
            return false;
        }
        if (!this.getPlayerToMove().equals(that.getPlayerToMove())) {
            return false;
        }
        if (this.epSquare==null) {
            if (that.epSquare!=null) {
                return false;
            }
        } else {
            if (!this.epSquare.equals(that.epSquare)) {
                return false;
            }
        }
        if (this.blackKingSquare==null) {
            if (that.blackKingSquare!=null) {
                return false;
            }
        } else {
            if (!this.blackKingSquare.equals(that.blackKingSquare)) {
                return false;
            }
        }
        if (this.whiteKingSquare==null) {
            if (that.whiteKingSquare!=null) {
                return false;
            }
        } else {
            if (!this.whiteKingSquare.equals(that.whiteKingSquare)) {
                return false;
            }
        }
        if (this.whitePawns!=that.whitePawns) {
            return false;
        }
        if (this.blackPawns!=that.blackPawns) {
            return false;
        }
        if (this.whiteKnights!=that.whiteKnights) {
            return false;
        }
        if (this.blackKnights!=that.blackKnights) {
            return false;
        }
        if (this.whiteBishops!=that.whiteBishops) {
            return false;
        }
        if (this.blackBishops!=that.blackBishops) {
            return false;
        }
        if (this.whiteRooks!=that.whiteRooks) {
            return false;
        }
        if (this.blackRooks!=that.blackRooks) {
            return false;
        }
        if (this.whiteQueens!=that.whiteQueens) {
            return false;
        }
        if (this.blackQueens!=that.blackQueens) {
            return false;
        }
        if (this.whitePieces!=that.whitePieces) {
            return false;
        }
        if (this.blackPieces!=that.blackPieces) {
            return false;
        }
        if (this.moveCounter!=that.moveCounter) {
            return false;
        }
        if (this.fiftyCounter!=that.fiftyCounter) {
            return false;
        }
        // TODO: nn_accumulators

        return true;
    }

    public void flipVertical() {
        List<Square> squares = Square.allSquares();
        Map<Square,Piece> myPieceMap = new HashMap<>();

        Square myWhiteKingSq = whiteKingSquare;
        Square myBlackKingSq = blackKingSquare;

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
                addPiece(p.getOppositeColorPiece(), sq.flipVertical());
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

        // set the kings
        whiteKingSquare = myBlackKingSq.flipVertical();
        blackKingSquare = myWhiteKingSq.flipVertical();

        // rebuild the hash keys
        zobristKey = Zobrist.calculateBoardKey(this);
        pawnKey = Zobrist.calculatePawnKey(this);

        // TODO: rebuild accumulators (or just swap?)

        assert(verify());
    }

    public double getNN_Accumulator(int ind1, int ind2) {
        assert(ind1==0 || ind1==1);
        assert(ind2 >= 0 && ind2 < NeuralNetwork.NN_SIZE_L1);
        return nn_accumulators[ind1][ind2];
    }

    public Square getKingSquare(Color player) {
        return player.isWhite() ? getWhiteKingSquare() : getBlackKingSquare();
    }

    public int getNumPieces(Piece p) {
        return pieceCountsMap.get(p);
    }

    public long getPawnKey() {
        assert(pawnKey == Zobrist.calculatePawnKey(this));
        return pawnKey;
    }

    public Piece getPiece(Square square) {
        return pieceMap.get(square);
    }

    public Piece getPiece(int sqVal) {
        return getPiece(Square.valueOf(sqVal));
    }

    public long getZobristKey() {
        assert(zobristKey == Zobrist.calculateBoardKey(this));
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

        hash = hash * 17 + moveCounter;
        hash = hash * 13 + fiftyCounter;
        // TODO: factor in nn_accumulators?

        return hash;
    }

    public boolean isEmpty(Square square) {
        return pieceMap.get(square)==null;
    }

    public void resetBoard() {
        setPos(INITIAL_POS);
    }

    public void setEP(Square ep) {
        assert(ep != null);
        epSquare = ep;
        zobristKey ^= Zobrist.getEnPassantKey(ep);
    }

    public void setNN_Accumulator(int ind1, int ind2, double val) {
        assert(ind1==0 || ind1==1);
        assert(ind2 >= 0 && ind2 < NeuralNetwork.NN_SIZE_L1);
        nn_accumulators[ind1][ind2] = val;
    }

    // the FEN grammar can be found here:
    // http://chessprogramming.wikispaces.com/Forsyth-Edwards+Notation
    // Note the grammar calls for six fields, but in practice the last two
    // are considered optional.
    public void setPos(String fen) {
        String myFen = fen.trim();
        clearBoard();

        myFen = BlankRemover.trim(myFen);
        // split on spaces
        String[] fenPieces = myFen.split(" ");
        if (fenPieces.length < 4) {
            throw new ParseException("not enough parts to FEN.");
        }

        setPieces(fenPieces[0]);
        setPlayer(fenPieces[1]);
        setCastlingRights(fenPieces[2]);
        setEP(fenPieces[3]);

        // Parts 5 and 6 are the half move clock and the full move counter, respectively.
        setHalfMoveClock(fenPieces.length > 4 ? fenPieces[4] : null);
        setFullMoveCounter(fenPieces.length > 5 ? fenPieces[5] : null);

        zobristKey = Zobrist.calculateBoardKey(this);
        pawnKey = Zobrist.calculatePawnKey(this);
        // TODO: build NN accumulators

        if (!verify()) {
            throw new ParseException("Invalid position: " + fen);
        }
    }

    public void swapPlayer() {
        zobristKey ^= Zobrist.getPlayerKey(playerToMove);
        playerToMove = Color.swap(playerToMove);
        zobristKey ^= Zobrist.getPlayerKey(playerToMove);
    }

    public void undoMove(Undo undo) {
        assert(verify());

        swapPlayer();
        epSquare = undo.getEpSquare();
        moveCounter--;
        fiftyCounter = undo.getFiftyCounter();
        castlingRights.setValue(undo.getCastlingRights());

        // remove the moving piece from toSq
        if (getPiece(undo.getMove().to()) != null) {
            removePiece(undo.getMove().to());
        }

        // place the moving piece on fromSq
        addPiece(undo.getMove().piece(),undo.getMove().from());
        if (undo.getMove().piece()==WHITE_KING) {
            whiteKingSquare = undo.getMove().from();
        } else if (undo.getMove().piece()==BLACK_KING) {
            blackKingSquare = undo.getMove().from();
        }

        // restore the captured piece
        if (undo.getMove().captured()!=null) {
            if (undo.getMove().isEpCapture()) {
                if (getPlayerToMove()==Color.BLACK) {
                    addPiece(WHITE_PAWN,epSquare.north().get());
                } else {
                    addPiece(BLACK_PAWN,epSquare.south().get());
                }
            } else {
                addPiece(undo.getMove().captured(),undo.getMove().to());
            }
        } else if (undo.getMove().isCastle()) {
            if (undo.getMove().from() == E1) {
                if (undo.getMove().to() == G1) {
                    removePiece(F1);
                    addPiece(WHITE_ROOK, H1);
                } else {
                    removePiece(D1);
                    addPiece(WHITE_ROOK, A1);
                }
            } else {
                if (undo.getMove().to() == G8) {
                    removePiece(F8);
                    addPiece(BLACK_ROOK, H8);
                } else {
                    removePiece(D8);
                    addPiece(BLACK_ROOK, A8);
                }
            }
        }

        zobristKey = undo.getZobristKey();
        assert(verify());
    }

    private void addCastlingRight(CastlingRights castlingRight) {
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

    private void addPiece(Piece p, Square s) {
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

    private void clearBoard() {
        List<Square> squares = Square.allSquares();
        for (Square sq : squares) {
            if (getPiece(sq) != null) {
                removePiece(sq);
            }
        }

        clearEPSquare();
        Set<CastlingRights> crs = EnumSet.allOf(CastlingRights.class);
        for (CastlingRights cr : crs) {
            clearCastlingRight(cr);
        }
        fiftyCounter = 0;

        assert(pieceCountsMap.get(WHITE_KING)==0);
        assert(pieceCountsMap.get(BLACK_KING)==0);
        assert(pieceCountsMap.get(WHITE_QUEEN)==0);
        assert(pieceCountsMap.get(BLACK_QUEEN)==0);
        assert(pieceCountsMap.get(WHITE_ROOK)==0);
        assert(pieceCountsMap.get(BLACK_ROOK)==0);
        assert(pieceCountsMap.get(WHITE_BISHOP)==0);
        assert(pieceCountsMap.get(BLACK_BISHOP)==0);
        assert(pieceCountsMap.get(WHITE_KNIGHT)==0);
        assert(pieceCountsMap.get(BLACK_KNIGHT)==0);
        assert(pieceCountsMap.get(WHITE_PAWN)==0);
        assert(pieceCountsMap.get(BLACK_PAWN)==0);
    }

    private void clearCastlingRight(CastlingRights castlingRight) {
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

    private Square getBlackKingSquare() {
        assert(blackKingSquare != null);
        assert(getPiece(blackKingSquare) == BLACK_KING);
        return blackKingSquare;
    }

    private Square getWhiteKingSquare() {
        assert(whiteKingSquare != null);
        assert(getPiece(whiteKingSquare) == WHITE_KING);
        return whiteKingSquare;
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

    private void setCastlingRights(String fenPart) throws ParseException {
        if (fenPart.equals("-")) {
            return;
        }

        char[] arr = fenPart.toCharArray();
        for (char c : arr) {
            switch (c) {
                case 'K':
                    addCastlingRight(CastlingRights.WHITE_KINGSIDE);
                    break;
                case 'k':
                    addCastlingRight(CastlingRights.BLACK_KINGSIDE);
                    break;
                case 'Q':
                    addCastlingRight(CastlingRights.WHITE_QUEENSIDE);
                    break;
                case 'q':
                    addCastlingRight(CastlingRights.BLACK_QUEENSIDE);
                    break;
                default:
                    throw new ParseException("invalid character in setCastlingRights: " + fenPart);
            }
        }
    }

    private void setEP(String fenPart) throws ParseException {
        if (fenPart.equals("-")) {
            return;
        }

        char[] arr = fenPart.toCharArray();
        if (arr.length != 2) {
            throw new ParseException("invalid string in setEP: " + fenPart);
        }

        int epsq=0;
        if (arr[0]>='a' && arr[0]<='h') {
            epsq=arr[0]-'a';
        } else {
            throw new ParseException("invalid string in setEP: " + fenPart);
        }

        if (arr[1]>='1' && arr[1]<='8') {
            epsq+= 8 * (8-(arr[1]-'0'));
        } else {
            throw new ParseException("invalid string in setEP: " + fenPart);
        }

        setEP(Square.valueOf(epsq));
    }

    private void setFullMoveCounter(String fenPart) throws ParseException {
        try {
            int fullMoveCounter = fenPart==null? 1 : Integer.parseInt(fenPart);
            moveCounter = (fullMoveCounter-1)*2;
            if (playerToMove == Color.BLACK) {
                moveCounter++;
            }
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private void setHalfMoveClock(String fenPart) throws ParseException {
        try {
            fiftyCounter = fenPart==null ? 0 : Integer.parseInt(fenPart);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private void setPieces(String fenPart) {
        char[] arr = fenPart.toCharArray();
        int sqVal = 0;
        for (char c : arr) {
            Piece piece = PieceFactory.getPiece(String.valueOf(c));
            if (piece != null) {
                Square sq = Square.valueOf(sqVal);
                if (piece == King.BLACK_KING) {
                    blackKingSquare = sq;
                } else if (piece == King.WHITE_KING) {
                    whiteKingSquare = sq;
                }
                addPiece(piece, sq);
                sqVal++;
            } else if (c >= '1' && c <= '8') {
                sqVal += Integer.parseInt(String.valueOf(c));
            }
        }
        if (sqVal != 64) {
            throw new ParseException("Did not set 64 squares: " + fenPart);
        }
    }

    private void setPlayer(String fenPart) throws ParseException {
        if (fenPart.equalsIgnoreCase("b")) {
            playerToMove = Color.BLACK;
        } else if (fenPart.equalsIgnoreCase("w")) {
            playerToMove = Color.WHITE;
        } else {
            throw new ParseException("could not parse player: " + fenPart);
        }
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

        assert(zobristKey==Zobrist.calculateBoardKey(this));
        assert(pawnKey==Zobrist.calculatePawnKey(this));
        // TODO: verify accumulators

        return true;
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

    public void addAll() {
        castlingRights = WHITE_KINGSIDE | WHITE_QUEENSIDE | BLACK_KINGSIDE | BLACK_QUEENSIDE;
    }

    public void clear() {
        castlingRights = 0;
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

    public int getValue() {
        return castlingRights;
    }

    public void setValue(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    @Override
    public int hashCode() {
        return this.castlingRights;
    }

    public boolean isBlackKingside() {
        return (castlingRights & BLACK_KINGSIDE) > 0;
    }

    public boolean isBlackQueenside() {
        return (castlingRights & BLACK_QUEENSIDE) > 0;
    }

    public boolean isWhiteKingside() {
        return (castlingRights & WHITE_KINGSIDE) > 0;
    }

    public boolean isWhiteQueenside() {
        return (castlingRights & WHITE_QUEENSIDE) > 0;
    }

    public void removeBlackKingside() {
        castlingRights &= ~BLACK_KINGSIDE;
    }

    public void removeBlackQueenside() {
        castlingRights &= ~BLACK_QUEENSIDE;
    }

    public void removeWhiteKingside() {
        castlingRights &= ~WHITE_KINGSIDE;
    }

    public void removeWhiteQueenside() {
        castlingRights &= ~WHITE_QUEENSIDE;
    }

    public void setBlackKingside() {
        castlingRights |= BLACK_KINGSIDE;
    }

    public void setBlackQueenside() {
        castlingRights |= BLACK_QUEENSIDE;
    }

    public void setWhiteKingside() {
        castlingRights |= WHITE_KINGSIDE;
    }

    public void setWhiteQueenside() {
        castlingRights |= WHITE_QUEENSIDE;
    }

}
