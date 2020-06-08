package com.jamesswafford.chess4j.search;

import java.util.function.Consumer;

public class SearchOptions {

    private Consumer<PvCallbackDTO> pvCallback;
    private long startTime;

    public SearchOptions(Consumer<PvCallbackDTO> pvCallback, long startTime) {
        this.pvCallback = pvCallback;
        this.startTime = startTime;
    }

    public Consumer<PvCallbackDTO> getPvCallback() {
        return pvCallback;
    }

    public void setPvCallback(Consumer<PvCallbackDTO> pvCallback) {
        this.pvCallback = pvCallback;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
