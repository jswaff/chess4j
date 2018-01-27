package com.jamesswafford.chess4j.board;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.squares.Direction;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Bitboard {

    public static long[] squares = new long[64];
    public static long[] ranks = new long[8];
    public static long[] files = new long[8];
    public static long[][] rays = new long[64][8];
    public static long[] knightMoves = new long[64];
    public static long[] kingMoves = new long[64];
    public static long[][] pawnAttacks = new long[64][2];

    static {
        for (int i=0;i<64;i++) {
            squares[i] = 1L << i;
        }
        for (int i=0;i<8;i++) {
            files[i] = ranks[i] = 0;
            File f = File.file(i);
            Rank r = Rank.rank(i);
            for (int j=0;j<64;j++) {
                Square sq = Square.valueOf(j);
                if (sq.file()==f) {
                    files[i] |= squares[j];
                }
                if (sq.rank()==r) {
                    ranks[i] |= squares[j];
                }
            }
        }
    }

    // initialize rays
    static {
        for (int i=0;i<64;i++) {
            for (int j=0;j<8;j++) {
                rays[i][j] = 0;
            }

            for (int j=0;j<64;j++) {
                if (i != j) {
                    Optional<Direction> dir = Direction.getDirectionTo(i,j);
                    int finalI = i; int finalJ = j;
                    dir.ifPresent(d -> rays[finalI][d.value()] |= Bitboard.squares[finalJ]);
                }
            }
        }
    }

    // initialize knight moves
    static {
        Square.allSquares().forEach(sq -> {
            knightMoves[sq.value()] = 0;

            Consumer<Square> addKnightTarget = targetSq -> knightMoves[sq.value()] |= squares[targetSq.value()];

            sq.north().flatMap(Square::northWest).ifPresent(addKnightTarget);
            sq.north().flatMap(Square::northEast).ifPresent(addKnightTarget);
            sq.east().flatMap(Square::northEast).ifPresent(addKnightTarget);
            sq.east().flatMap(Square::southEast).ifPresent(addKnightTarget);
            sq.south().flatMap(Square::southEast).ifPresent(addKnightTarget);
            sq.south().flatMap(Square::southWest).ifPresent(addKnightTarget);
            sq.west().flatMap(Square::southWest).ifPresent(addKnightTarget);
            sq.west().flatMap(Square::northWest).ifPresent(addKnightTarget);
        });
    }

    // initialize king moves
    static {
        Square.allSquares().forEach(sq -> {
            kingMoves[sq.value()] = 0;

            Consumer<Square> addKingTarget = targetSq -> kingMoves[sq.value()] |= squares[targetSq.value()];

            sq.north().ifPresent(addKingTarget);
            sq.northEast().ifPresent(addKingTarget);
            sq.east().ifPresent(addKingTarget);
            sq.southEast().ifPresent(addKingTarget);
            sq.south().ifPresent(addKingTarget);
            sq.southWest().ifPresent(addKingTarget);
            sq.west().ifPresent(addKingTarget);
            sq.northWest().ifPresent(addKingTarget);
        });
    }

    // initialize pawn attacks
    static {
        Square.allSquares().forEach(sq -> {
            pawnAttacks[sq.value()][Color.BLACK.getColor()] = 0;
            pawnAttacks[sq.value()][Color.WHITE.getColor()] = 0;

            BiConsumer<Square,Color> addPawnTarget = (targetSq,pawnColor) ->
                    pawnAttacks[sq.value()][pawnColor.getColor()] |= squares[targetSq.value()];

            Consumer<Square> addWhitePawnTarget = targetSq -> addPawnTarget.accept(targetSq,Color.WHITE);
            Consumer<Square> addBlackPawnTarget = targetSq -> addPawnTarget.accept(targetSq,Color.BLACK);

            sq.northWest().ifPresent(addWhitePawnTarget);
            sq.northEast().ifPresent(addWhitePawnTarget);
            sq.southWest().ifPresent(addBlackPawnTarget);
            sq.southEast().ifPresent(addBlackPawnTarget);
        });
    }

    private long val;

    public Bitboard(int sq) {
        this.val = squares[sq];
    }

    public Bitboard(long val) {
        this.val = val;
    }

    public static long isolateLSB(long mask,int index) {
        int n=0;

        for (int i=0;i<64;i++) {
            if ((squares[i] & mask) != 0) {
                if (n==index) {
                    return squares[i];
                }
                n++;
            }
        }

        return 0;
    }


    public int lsb() {
        return lsb(val);
    }

    public static int lsb(long val) {
        return Long.numberOfTrailingZeros(val);
    }

    public int msb() {
        return msb(val);
    }

    public static int msb(long val) {
        return 63 - Long.numberOfLeadingZeros(val);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        for (int i=0;i<64;i++) {
            if ((squares[i] & val) == 0) {
                sb.append("0");
            } else {
                sb.append("1");
            }
            if (Square.valueOf(i).file()==File.FILE_H) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

}
