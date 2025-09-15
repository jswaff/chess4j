package dev.jamesswafford.chess4j.board.squares;

import java.util.Optional;

public enum File {
    FILE_A(0,"a"),
    FILE_B(1,"b"),
    FILE_C(2,"c"),
    FILE_D(3,"d"),
    FILE_E(4,"e"),
    FILE_F(5,"f"),
    FILE_G(6,"g"),
    FILE_H(7,"h");

    private int value;
    private String label;

    private static File[] files_arr = new File[8];

    static {
        for (int i=0;i<8;i++) {
            for (File f : File.values()) {
                if (f.getValue()==i) {
                    files_arr[i] = f;
                }
            }
        }
    }

    private File(int value,String label) {
        this.value=value;
        this.label=label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public int distance(File f) {
        return Math.abs(f.getValue()-getValue());
    }

    public File flip() {
        return File.file(7-value);
    }

    public boolean eastOf(File f) {
        return this.getValue() > f.getValue();
    }

    public boolean westOf(File f) {
        return this.getValue() < f.getValue();
    }

    public Optional<File> east() {
        if (value==7) {
            return Optional.empty();
        }
        return Optional.of(file(value+1));
    }

    public Optional<File> west() {
        if (value==0) {
            return Optional.empty();
        }
        return Optional.of(file(value-1));
    }

    public static File file(int value) {
        assert(value >= 0 && value <= 7);

        return files_arr[value];
    }

    public static File file(String value) {
        for (File f : File.values()) {
            if (f.getLabel().equalsIgnoreCase(value)) {
                return f;
            }
        }
        throw new IllegalArgumentException("File value not found: " + value);
    }

}
