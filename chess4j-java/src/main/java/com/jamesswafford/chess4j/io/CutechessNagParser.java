package com.jamesswafford.chess4j.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CutechessNagParser {

    private static final Pattern pattern = Pattern.compile("\\{([+|-]?\\d+.\\d+)/(\\d+) \\d+.\\d+s(.*)}");

    private final String nag;

    public CutechessNagParser(String nag) {
        this.nag = nag;
    }

    public boolean isValid() {
        return pattern.matcher(nag).matches();
    }

    public float score() {
        Matcher m = pattern.matcher(nag);
        if (m.find()) {
            String scoreTxt = m.group(1);
            return Float.parseFloat(scoreTxt);
        } else {
            return 0;
        }
    }

    public int depth() {
        Matcher m = pattern.matcher(nag);
        if (m.find()) {
            String depthTxt = m.group(2);
            return Integer.parseInt(depthTxt);
        } else {
            return 0;
        }
    }

}
