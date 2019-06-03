package com.jamesswafford.chess4j.io;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.jamesswafford.chess4j.board.Move;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.pieces.Pawn;

public class PGNParserTest {

    @Test
    public void testSimplePGN() throws Exception {
        String pgn = "[Event \"F/S Return Match\"]\n"
            + "[Site \"Belgrade, Serbia Yugoslavia|JUG\"]\n"
            + "[Date \"1992.11.04\"]\n"
            + "[Round \"29\"]\n"
            + "[White \"Fischer, Robert J.\"]\n"
            + "[Black \"Spassky, Boris V.\"]\n"
            + "[Result \"1/2-1/2\"]\n"
            + "\n"
            + "1. e4 e5 1/2-1/2\n";

        List<PGNTag> tags = new ArrayList<PGNTag>();
        tags.add(new PGNTag("Event","F/S Return Match"));
        tags.add(new PGNTag("Site","Belgrade, Serbia Yugoslavia|JUG"));
        tags.add(new PGNTag("Date","1992.11.04"));
        tags.add(new PGNTag("Round","29"));
        tags.add(new PGNTag("White","Fischer, Robert J."));
        tags.add(new PGNTag("Black","Spassky, Boris V."));
        tags.add(new PGNTag("Result","1/2-1/2"));

        List<Move> moves = new ArrayList<Move>();
        moves.add(new Move(Pawn.WHITE_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_2),Square.valueOf(File.FILE_E, Rank.RANK_4)));
        moves.add(new Move(Pawn.BLACK_PAWN,Square.valueOf(File.FILE_E, Rank.RANK_7),Square.valueOf(File.FILE_E, Rank.RANK_5)));

        PGNGame pgnGame = new PGNGame(tags,moves,PGNResult.DRAW);

        Assert.assertEquals(pgnGame, new PGNParser().parseGame(pgn));
    }

    @Test
    public void testPGNWithComments() throws Exception {
        String pgn = "[Event \"F/S Return Match\"]\n"
            + "[Site \"Belgrade, Serbia Yugoslavia|JUG\"]\n"
            + "[Date \"1992.11.04\"]\n"
            + "[Round \"29\"]\n"
            + "[White \"Fischer, Robert J.\"]\n"
            + "[Black \"Spassky, Boris V.\"]\n"
            + "[Result \"1/2-1/2\"]\n"
            + "\n"
            + "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 {This opening is called the Ruy Lopez.} \n"
            + "4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8  10. d4 Nbd7 \n"
            + "11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15. Nb1 h6 16. Bh4 c5 17. dxe5 \n"
            + "Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21. Nc4 Nxc4 22. Bxc4 Nb6 \n"
            + "23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7 27. Qe3 Qg5 28. Qxg5 \n"
            + "hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33. f3 Bc8 34. Kf2 Bf5 \n"
            + "35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5 40. Rd6 Kc5 41. Ra6 \n"
            + "Nf2 42. g4 Bd3 43. Re6 1/2-1/2\n";

        PGNGame pgnGame = new PGNParser().parseGame(pgn);
        Assert.assertEquals(PGNResult.DRAW, pgnGame.getResult());
        Assert.assertEquals(85, pgnGame.getMoves().size());
        Assert.assertEquals(7, pgnGame.getTags().size());
        Assert.assertEquals(new PGNTag("White","Fischer, Robert J."), pgnGame.getTags().get(4));
    }

    @Test
    public void testICCGame() throws Exception {
        String pgn = "[Event \"ICC 5 3\"]\n"
            + "[Site \"Internet Chess Club\"]\n"
            + "[Date \"2014.09.02\"]\n"
            + "[Round \"-\"]\n"
            + "[White \"LesserProphet\"]\n"
            + "[Black \"lostit\"]\n"
            + "[Result \"1-0\"]\n"
            + "[ICCResult \"Black resigns\"]\n"
            + "[WhiteElo \"1932\"]\n"
            + "[BlackElo \"1462\"]\n"
            + "[Opening \"Pirc: Austrian attack, dragon formation\"]\n"
            + "[ECO \"B09\"]\n"
            + "[NIC \"PU.05\"]\n"
            + "[Time \"09:37:31\"]\n"
            + "[TimeControl \"300+3\"]\n"
            + "\n"
            + "1. Nc3 d6 2. d4 Nf6 3. f4 g6 4. e4 Bg7 5. Nf3 c5 6. dxc5 Qa5 7. Bb5+ Bd7 8.\n"
            + "Bd3 Qxc5 9. e5 dxe5 10. Nxe5 Nc6 11. Nxd7 Nxd7 12. Qd2 O-O 13. a4 Rfe8 14.\n"
            + "Ne4 Qb6 15. a5 Qc7 16. g4 Rad8 17. h4 e5 18. f5 Nf6 19. Nxf6+ Bxf6 20. Qf2\n"
            + "	e4 21. Bb5 a6 22. Bxc6 Qxc6 23. fxg6 hxg6 24. Rh3 Bg7 25. Qe2 Bd4 26. h5 e3\n"
            + "27. hxg6 Qxg6 28. Bxe3 Bxe3 29. Rxe3 Rxe3 30. Qxe3 Qxc2 31. Qg5+ {Black\n"
            + "	resigns} 1-0\n";

        PGNGame pgnGame = new PGNParser().parseGame(pgn);
        Assert.assertEquals(PGNResult.WHITE_WINS, pgnGame.getResult());
        Assert.assertEquals(61, pgnGame.getMoves().size());
        Assert.assertEquals(15, pgnGame.getTags().size());
        Assert.assertEquals(new PGNTag("TimeControl","300+3"), pgnGame.getTags().get(14));

    }
}
