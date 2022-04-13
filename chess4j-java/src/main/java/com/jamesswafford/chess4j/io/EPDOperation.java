package com.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EPDOperation {

    private final String epdOpcode;
    private final List<String> epdOperands = new ArrayList<>();

    public EPDOperation(String epdOpCode) {
        this.epdOpcode=epdOpCode;
    }

    public EPDOperation(String epdOpCode,String epdOperand) {
        this(epdOpCode);
        this.epdOperands.add(epdOperand);
    }

    public void addOperand(String epdOperand) {
        epdOperands.add(epdOperand);
    }

    public String getEpdOpcode() {
        return epdOpcode;
    }

    public List<String> getEpdOperands() {
        return Collections.unmodifiableList(epdOperands);
    }

}
