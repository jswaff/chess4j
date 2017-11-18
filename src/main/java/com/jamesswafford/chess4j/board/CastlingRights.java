package com.jamesswafford.chess4j.board;

public enum CastlingRights {
    WHITE_KINGSIDE("K"),
    WHITE_QUEENSIDE("Q"),
    BLACK_KINGSIDE("k"),
    BLACK_QUEENSIDE("q");

    private String label;

    private CastlingRights(String label) {
        this.label=label;
    }

    public String getLabel() {
        return label;
    }

}
