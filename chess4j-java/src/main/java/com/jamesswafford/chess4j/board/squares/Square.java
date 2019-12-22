package com.jamesswafford.chess4j.board.squares;

import java.util.*;

public final class Square {

    public static final int NUM_SQUARES = 64;

    private static final List<Square> SQUARES = new ArrayList<>();
    private static final Map<File,List<Square>> FILE_SQUARES = new HashMap<>();
    private static final Map<Rank,List<Square>> RANK_SQUARES = new HashMap<>();

    private final Rank rank;
    private final File file;

    private static Square[][] squares_arr = new Square[8][8];

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
