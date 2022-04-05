package com.jamesswafford.chess4j.tuner;

import com.jamesswafford.chess4j.utils.GameResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRecord {

    private String fen;
    private GameResult gameResult;

}
