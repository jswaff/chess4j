package dev.jamesswafford.chess4j.board;

public enum CastlingRights {
    WHITE_KINGSIDE("K"),
    WHITE_QUEENSIDE("Q"),
    BLACK_KINGSIDE("k"),
    BLACK_QUEENSIDE("q");

    private final String label;

    CastlingRights(String label) {
        this.label=label;
    }

    public String getLabel() {
        return label;
    }

}
