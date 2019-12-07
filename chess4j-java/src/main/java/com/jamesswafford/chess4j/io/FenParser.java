package com.jamesswafford.chess4j.io;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.CastlingRights;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.exceptions.ParseException;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.utils.BlankRemover;
import com.jamesswafford.chess4j.utils.PieceFactory;

/**
 * FenParser
 * 
 * @author James
 *
 */
public final class FenParser {

    private static final int MINIMUM_FEN_PARTS = 4;
    private static final int PIECE_PART = 0;
    private static final int PLAYER_PART = 1;
    private static final int CASTLING_PART = 2;
    private static final int EP_PART = 3;
    private static final int HALF_MOVE_CLOCK_PART = 4;
    private static final int FULL_MOVE_COUNTER_PART = 5;

    private FenParser() { }

    public static String getFen(Board b, boolean includeMoveFields) {

        StringBuilder sb = new StringBuilder();

        // get a string of characters for each rank
        for (int ri=0; ri<8; ri++) { // ri(0) = Rank8
            Rank r = Rank.rank(ri);
            int emptyCnt = 0;
            for (int fi=0;fi<8;fi++) {
                File f = File.file(fi);
                Square sq = Square.valueOf(f, r);
                Piece p = b.getPiece(sq);
                if (p != null) {
                    if (emptyCnt != 0) {
                        sb.append(emptyCnt);
                        emptyCnt = 0;
                    }
                    sb.append(p.toString());
                } else {
                    emptyCnt++;
                }
            }
            if (emptyCnt > 0) {
                sb.append(emptyCnt);
            }
            if (ri < 7) {
                sb.append("/");
            }
        }

        // player
        sb.append(" " + (b.getPlayerToMove()==Color.WHITE ? "w":"b"));

        // castling rights
        sb.append(" ");
        if (b.hasCastlingRight(CastlingRights.WHITE_KINGSIDE)) {
            sb.append("K");
        }
        if (b.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE)) {
            sb.append("Q");
        }
        if (b.hasCastlingRight(CastlingRights.BLACK_KINGSIDE)) {
            sb.append("k");
        }
        if (b.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE)) {
            sb.append("q");
        }
        if (!b.hasCastlingRight(CastlingRights.WHITE_KINGSIDE) &&
                !b.hasCastlingRight(CastlingRights.WHITE_QUEENSIDE) &&
                !b.hasCastlingRight(CastlingRights.BLACK_KINGSIDE) &&
                !b.hasCastlingRight(CastlingRights.BLACK_QUEENSIDE))
        {
            sb.append("-");
        }

        // ep square
        sb.append(" ");
        if (b.getEPSquare() != null) {
            sb.append(b.getEPSquare().toString());
        } else {
            sb.append("-");
        }

        if (includeMoveFields) {
            // half move clock
            sb.append(" " + b.getFiftyCounter());

            // full move counter
            sb.append(" ");
            int fenMoves = b.getMoveCounter();
            if (b.getPlayerToMove() == Color.BLACK) {
                fenMoves--;
            }
            fenMoves /= 2;
            fenMoves++;
            sb.append(fenMoves);
        }

        return sb.toString();
    }

    // the FEN grammar can be found here:
    // http://chessprogramming.wikispaces.com/Forsyth-Edwards+Notation
    // Note the grammar calls for six fields, but in practice the last two
    // are considered optional.
    public static void setPos(Board b,String fen) throws ParseException {
        String myFen = fen.trim();
        b.clearBoard();

        myFen = BlankRemover.trim(myFen);
        // split on spaces
        String[] fenPieces = myFen.split(" ");
        if (fenPieces.length < MINIMUM_FEN_PARTS) {
            throw new ParseException("not enough parts to FEN.");
        }

        setPieces(b,fenPieces[PIECE_PART]);
        setPlayer(b,fenPieces[PLAYER_PART]);
        setCastlingRights(b,fenPieces[CASTLING_PART]);
        setEP(b,fenPieces[EP_PART]);

        // Parts 5 and 6 are the half move clock and the full move counter, respectively.
        String halfMoveClock = fenPieces.length > HALF_MOVE_CLOCK_PART ? fenPieces[HALF_MOVE_CLOCK_PART] : null;
        setHalfMoveClock(b,halfMoveClock);

        String fullMoveCounter = fenPieces.length > FULL_MOVE_COUNTER_PART ? fenPieces[FULL_MOVE_COUNTER_PART] : null;
        setFullMoveCounter(b,fullMoveCounter);
    }

    private static void setHalfMoveClock(Board b,String s) throws ParseException {
        try {
            Integer halfMoves = s==null ? 0 : Integer.valueOf(s);
            b.setFiftyCounter(halfMoves);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private static void setFullMoveCounter(Board b,String s) throws ParseException {
        try {
            Integer moveCounter = s==null? 1 : Integer.valueOf(s);
            b.setMoveCounter((moveCounter-1)*2);
            if (Color.BLACK.equals(b.getPlayerToMove())) {
                b.setMoveCounter(b.getMoveCounter()+1);
            }
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private static void setCastlingRights(Board b,String s) throws ParseException {
        if (s.equals("-")) {
            return;
        }

        char[] arr = s.toCharArray();
        for (int i=0;i<arr.length;i++) {
            switch (arr[i]) {
            case 'K':
                b.addCastlingRight(CastlingRights.WHITE_KINGSIDE);
                break;
            case 'k':
                b.addCastlingRight(CastlingRights.BLACK_KINGSIDE);
                break;
            case 'Q':
                b.addCastlingRight(CastlingRights.WHITE_QUEENSIDE);
                break;
            case 'q':
                b.addCastlingRight(CastlingRights.BLACK_QUEENSIDE);
                break;
            default:
                throw new ParseException("invalid character in setCastlingRights: " + s);
            }
        }
    }

    private static void setEP(Board b,String s) throws ParseException {
        if (s.equals("-")) {
            return;
        }

        char[] arr = s.toCharArray();
        if (arr.length != 2) {
            throw new ParseException("invalid string in setEP: " + s);
        }

        int epsq=0;
        if (arr[0]>='a' && arr[0]<='h') {
            epsq=arr[0]-'a';
        } else {
            throw new ParseException("invalid string in setEP: " + s);
        }

        if (arr[1]>='1' && arr[1]<='8') {
            epsq+= 8 * (8-(arr[1]-'0'));
        } else {
            throw new ParseException("invalid string in setEP: " + s);
        }

        b.setEP(Square.valueOf(epsq));
    }

    private static void setPieces(Board b,String s) {
        char[] arr = s.toCharArray();
        int sq=0;
        for (int i=0;i<arr.length;i++) {
            char c = arr[i];
            Piece piece = PieceFactory.getPiece(String.valueOf(c));
            if (piece != null) {
                b.addPiece(piece, Square.valueOf(sq));
                sq++;
            } else if (c >= '1' && c <= '8') {
                sq += Integer.valueOf(String.valueOf(c));
            }
        }
        assert(sq==Square.NUM_SQUARES);
        b.resetKingSquares();
    }

    private static void setPlayer(Board b,String s) throws ParseException {
        Color ptm;
        if (s.equalsIgnoreCase("b")) {
            ptm = Color.BLACK;
        } else if (s.equalsIgnoreCase("w")) {
            ptm = Color.WHITE;
        } else {
            throw new ParseException("could not parse player: " + s);
        }

        if (!ptm.equals(b.getPlayerToMove())) {
            b.swapPlayer();
        }
    }

}
