package com.jamesswafford.chess4j.board.squares;

import java.util.*;

import static com.jamesswafford.chess4j.board.squares.File.*;
import static com.jamesswafford.chess4j.board.squares.Rank.*;

public final class Square {

    public static final int NUM_SQUARES = 64;

    private static final List<Square> SQUARES = new ArrayList<>();
    private static final Map<File,List<Square>> FILE_SQUARES = new HashMap<>();
    private static final Map<Rank,List<Square>> RANK_SQUARES = new HashMap<>();

    private static Square[][] squares_arr = new Square[8][8];

    public static final Square
            A8, B8, C8, D8, E8, F8, G8, H8,
            A7, B7, C7, D7, E7, F7, G7, H7,
            A6, B6, C6, D6, E6, F6, G6, H6,
            A5, B5, C5, D5, E5, F5, G5, H5,
            A4, B4, C4, D4, E4, F4, G4, H4,
            A3, B3, C3, D3, E3, F3, G3, H3,
            A2, B2, C2, D2, E2, F2, G2, H2,
            A1, B1, C1, D1, E1, F1, G1, H1;

    private final Rank rank;
    private final File file;

    private Square(File file, Rank rank) {
        this.file = file;
        this.rank = rank;
    }

    static {
        for (File file : File.values()) {
            List<Square> mySquares = new ArrayList<>();
            for (Rank rank : Rank.values()) {
                mySquares.add(new Square(file, rank));
            }
            FILE_SQUARES.put(file, mySquares);
        }

        for (Rank rank : Rank.values()) {
            List<Square> mySquares = new ArrayList<>();
            for (File file : File.values()) {
                mySquares.add(new Square(file, rank));
            }
            RANK_SQUARES.put(rank, mySquares);
        }
    }

    static {
        for (Rank rank : Rank.values()) {
            for (File file : File.values()) {
                Set<Square> intersection = new HashSet<>(FILE_SQUARES.get(file));
                intersection.retainAll(RANK_SQUARES.get(rank));
                assert(intersection.size() == 1);
                Square sq = intersection.iterator().next();

                SQUARES.add(sq);
                squares_arr[file.getValue()][rank.getValue()] = sq;
            }
        }
    }

    static {
        A1 = Square.valueOf(FILE_A, RANK_1);
        A2 = Square.valueOf(FILE_A, RANK_2);
        A3 = Square.valueOf(FILE_A, RANK_3);
        A4 = Square.valueOf(FILE_A, RANK_4);
        A5 = Square.valueOf(FILE_A, RANK_5);
        A6 = Square.valueOf(FILE_A, RANK_6);
        A7 = Square.valueOf(FILE_A, RANK_7);
        A8 = Square.valueOf(FILE_A, RANK_8);

        B1 = Square.valueOf(FILE_B, RANK_1);
        B2 = Square.valueOf(FILE_B, RANK_2);
        B3 = Square.valueOf(FILE_B, RANK_3);
        B4 = Square.valueOf(FILE_B, RANK_4);
        B5 = Square.valueOf(FILE_B, RANK_5);
        B6 = Square.valueOf(FILE_B, RANK_6);
        B7 = Square.valueOf(FILE_B, RANK_7);
        B8 = Square.valueOf(FILE_B, RANK_8);

        C1 = Square.valueOf(FILE_C, RANK_1);
        C2 = Square.valueOf(FILE_C, RANK_2);
        C3 = Square.valueOf(FILE_C, RANK_3);
        C4 = Square.valueOf(FILE_C, RANK_4);
        C5 = Square.valueOf(FILE_C, RANK_5);
        C6 = Square.valueOf(FILE_C, RANK_6);
        C7 = Square.valueOf(FILE_C, RANK_7);
        C8 = Square.valueOf(FILE_C, RANK_8);

        D1 = Square.valueOf(FILE_D, RANK_1);
        D2 = Square.valueOf(FILE_D, RANK_2);
        D3 = Square.valueOf(FILE_D, RANK_3);
        D4 = Square.valueOf(FILE_D, RANK_4);
        D5 = Square.valueOf(FILE_D, RANK_5);
        D6 = Square.valueOf(FILE_D, RANK_6);
        D7 = Square.valueOf(FILE_D, RANK_7);
        D8 = Square.valueOf(FILE_D, RANK_8);

        E1 = Square.valueOf(FILE_E, RANK_1);
        E2 = Square.valueOf(FILE_E, RANK_2);
        E3 = Square.valueOf(FILE_E, RANK_3);
        E4 = Square.valueOf(FILE_E, RANK_4);
        E5 = Square.valueOf(FILE_E, RANK_5);
        E6 = Square.valueOf(FILE_E, RANK_6);
        E7 = Square.valueOf(FILE_E, RANK_7);
        E8 = Square.valueOf(FILE_E, RANK_8);

        F1 = Square.valueOf(FILE_F, RANK_1);
        F2 = Square.valueOf(FILE_F, RANK_2);
        F3 = Square.valueOf(FILE_F, RANK_3);
        F4 = Square.valueOf(FILE_F, RANK_4);
        F5 = Square.valueOf(FILE_F, RANK_5);
        F6 = Square.valueOf(FILE_F, RANK_6);
        F7 = Square.valueOf(FILE_F, RANK_7);
        F8 = Square.valueOf(FILE_F, RANK_8);

        G1 = Square.valueOf(FILE_G, RANK_1);
        G2 = Square.valueOf(FILE_G, RANK_2);
        G3 = Square.valueOf(FILE_G, RANK_3);
        G4 = Square.valueOf(FILE_G, RANK_4);
        G5 = Square.valueOf(FILE_G, RANK_5);
        G6 = Square.valueOf(FILE_G, RANK_6);
        G7 = Square.valueOf(FILE_G, RANK_7);
        G8 = Square.valueOf(FILE_G, RANK_8);

        H1 = Square.valueOf(FILE_H, RANK_1);
        H2 = Square.valueOf(FILE_H, RANK_2);
        H3 = Square.valueOf(FILE_H, RANK_3);
        H4 = Square.valueOf(FILE_H, RANK_4);
        H5 = Square.valueOf(FILE_H, RANK_5);
        H6 = Square.valueOf(FILE_H, RANK_6);
        H7 = Square.valueOf(FILE_H, RANK_7);
        H8 = Square.valueOf(FILE_H, RANK_8);
    }


    public Rank rank() { return rank; }
    public File file() { return file; }

    @Override
    public String toString() {
        return file.getLabel() + rank.getLabel();
    }

    public boolean isLight() {
        return (rank.getValue() % 2) == (file.getValue() % 2);
    }

    public int value() {
        return rank.getValue()*8 + file.getValue();
    }

    public static Optional<Square> valueOf(Optional<File> file,Optional<Rank> rank) {
        return file.flatMap(f -> rank.map(r -> valueOf(f,r)));
    }

    public static Optional<Square> valueOf(Optional<File> file,Rank rank) {
        return file.map(f -> valueOf(f,rank));
    }

    public static Optional<Square> valueOf(File file, Optional<Rank> rank) {
        return rank.map(r -> valueOf(file,r));
    }

    public static Square valueOf(File file,Rank rank) {
        return squares_arr[file.getValue()][rank.getValue()];
    }

    public static Square valueOf(int sq) {
        return squares_arr[sq&7][sq/8];
    }

    public int rankDistance(Square sq) {
        return Math.abs(this.rank.getValue()-sq.rank.getValue());
    }

    public int fileDistance(Square sq) {
        return Math.abs(this.file.getValue()-sq.file.getValue());
    }

    public int distance(Square sq) {
        return Math.max(rankDistance(sq),fileDistance(sq));
    }

    public Square flipVertical() {
        return Square.valueOf(file, rank.flip());
    }

    public Square flipHorizontal() {
        return Square.valueOf(file.flip(), rank);
    }

    public static List<Square> allSquares() {
        return Collections.unmodifiableList(SQUARES);
    }

    public static List<Square> fileSquares(File file) {
        return Collections.unmodifiableList(FILE_SQUARES.get(file));
    }

    public static List<Square> rankSquares(Rank rank) {
        return Collections.unmodifiableList(RANK_SQUARES.get(rank));
    }

    public Optional<Square> north() {
        return Square.valueOf(file,rank().north());
    }

    public Optional<Square> northEast() {
        return Square.valueOf(file.east(), rank.north());
    }

    public Optional<Square> east() {
        return Square.valueOf(file.east(), rank);
    }

    public Optional<Square> southEast() {
        return Square.valueOf(file.east(), rank.south());
    }

    public Optional<Square> south() {
        return Square.valueOf(file, rank.south());
    }

    public Optional<Square> southWest() {
        return Square.valueOf(file.west(), rank.south());
    }

    public Optional<Square> west() {
        return Square.valueOf(file.west(), rank);
    }

    public Optional<Square> northWest() {
        return Square.valueOf(file.west(), rank().north());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Square)) {
            return false;
        }
        Square sq = (Square)o;
        return sq.file().equals(file) && sq.rank().equals(rank);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 13 + rank.hashCode();
        hash = hash * 17 + file.hashCode();
        return hash;
    }
}
