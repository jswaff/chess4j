package dev.jamesswafford.chess4j.search;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

@Builder
@Data
public class SearchOptions {

    private Consumer<PvCallbackDTO> pvCallback;
    private long startTime;
    private long stopTime;
    private long nodesBetweenTimeChecks;
    private long nodeCountLastTimeCheck;
    private boolean avoidResearches;

}
