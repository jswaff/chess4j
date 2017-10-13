package com.jamesswafford.chess4j.eval;


import java.util.HashMap;
import java.util.Map;

import com.jamesswafford.chess4j.Color;
import com.jamesswafford.chess4j.board.Bitboard;
import com.jamesswafford.chess4j.board.Board;
import com.jamesswafford.chess4j.board.Magic;
import com.jamesswafford.chess4j.board.squares.East;
import com.jamesswafford.chess4j.board.squares.File;
import com.jamesswafford.chess4j.board.squares.Rank;
import com.jamesswafford.chess4j.board.squares.Square;
import com.jamesswafford.chess4j.hash.PawnTranspositionTableEntry;
import com.jamesswafford.chess4j.hash.TTHolder;
import com.jamesswafford.chess4j.pieces.Bishop;
import com.jamesswafford.chess4j.pieces.King;
import com.jamesswafford.chess4j.pieces.Knight;
import com.jamesswafford.chess4j.pieces.Pawn;
import com.jamesswafford.chess4j.pieces.Piece;
import com.jamesswafford.chess4j.pieces.Queen;
import com.jamesswafford.chess4j.pieces.Rook;
import com.jamesswafford.chess4j.utils.OrderedPair;
import com.jamesswafford.chess4j.utils.PawnUtils;

public final class Eval {

	public static final int QUEEN_VAL  = 900;
	public static final int ROOK_VAL   = 500;
	public static final int KNIGHT_VAL = 300;
	public static final int BISHOP_VAL = 320;
	public static final int PAWN_VAL   = 100;
	public static final int ALL_NONPAWN_PIECES_VAL = QUEEN_VAL + ROOK_VAL*2 + KNIGHT_VAL*2 + BISHOP_VAL*2;
	
	// having majors on the 7th is huge advantage.  This might actually be too small.
	public static final int ROOK_ON_7TH = 50;
	public static final int CONNECTED_MAJORS_ON_7TH = 80;
	
	// an open file is one with no pawns of either color on it
	public static final int ROOK_OPEN_FILE = 25;
	
	// a half-open file is one with enemy pawns but not our own
	public static final int ROOK_HALF_OPEN_FILE = 15;
	
	// a passed pawn is a pawn with no enemy pawn in front of it or on an adjacent file
	public static final int PASSED_PAWN = 20;
	
	// an isolated pawn is one with no friendly pawn on an adjacent file
	public static final int ISOLATED_PAWN = -20;
	
	// a doubled pawn is a pawn that resides on the same file as a friendly pawn
	// note this would get "awarded" to both pawns
	public static final int DOUBLED_PAWN = -10;
	
	public static final int KING_SAFETY_PAWN_ONE_AWAY = -10;
	public static final int KING_SAFETY_PAWN_TWO_AWAY = -20;
	public static final int KING_SAFETY_PAWN_FAR_AWAY = -30;
	public static final int KING_SAFETY_MIDDLE_OPEN_FILE = -50;

	public static final int KNIGHT_TROPISM = 2;

	private static Map<Class<?>,Integer> pieceValMap;
	
	static {
		pieceValMap = new HashMap<Class<?>,Integer>();
		pieceValMap.put(King.class, Integer.MAX_VALUE);
		pieceValMap.put(Queen.class, QUEEN_VAL);
		pieceValMap.put(Rook.class, ROOK_VAL);
		pieceValMap.put(Bishop.class, BISHOP_VAL);
		pieceValMap.put(Knight.class, KNIGHT_VAL);
		pieceValMap.put(Pawn.class, PAWN_VAL);
	}

	// A8 ... H8
	//    ...
	// A1 ... H1

	public static final int[] BISHOP_PST = {
		 0, 0,  0,  0,  0,  0, 0, 0,
		 0, 7,  7,  7,  7,  7, 7, 0,
		 0, 7, 15, 15, 15, 15, 7, 0,
		 0, 7, 15, 20, 20, 15, 7, 0,
		 0, 7, 15, 20, 20, 15, 7, 0,
		 0, 7, 15, 15, 15, 15, 7, 0,
		 0, 7,  7,  7,  7,  7, 7, 0,
		 0, 0,  0,  0,  0,  0, 0, 0 };

	public static final int[] KNIGHT_PST = {
		-5, -5, -5, -5, -5, -5, -5, -5,
		-5,  0, 10, 10, 10, 10,  0, -5,
		-5,  0, 15, 20, 20, 15,  0, -5,
		-5,  5, 10, 15, 15, 10,  5, -5,
		-5,  5, 10, 15, 15, 10,  5, -5,
		-5,  0,  8,  0,  0,  8,  0, -5,
		-5,  0,  0,  5,  5,  0,  0, -5,
		-10,-10, -5, -5, -5, -5,-10,-10 };
	
	public static final int[] PAWN_PST = {
		 0,  0,  0,  0,  0,  0,  0,  0,
		30, 30, 30, 30, 30, 30, 30, 30,
		14, 14, 14, 18, 18, 14, 14, 14,
		 7,  7,  7, 10, 10,  7,  7,  7,
		 5,  5,  5,  7,  7,  5,  5,  5,
		 3,  3,  3,  5,  5,  3,  3,  3,
		 0,  0,  0, -3, -3,  0,  0,  0,
		 0,  0,  0,  0,  0,  0,  0,  0 }; 

	public static final int[] ROOK_PST = {
		 0,  0,  0,  0,  0,  0,  0,  0,
		 0,  0,  0,  0,  0,  0,  0,  0,
		-5,  0,  0,  0,  0,  0,  0, -5,
		-5,  0,  0,  0,  0,  0,  0, -5,
		-5,  0,  0,  0,  0,  0,  0, -5,
		-5,  0,  0,  0,  0,  0,  0, -5,
		-5,  0,  0,  0,  0,  0,  0, -5,
		 0,  0,  0,  0,  0,  0,  0,  0 }; 
	
	public static final int[] KING_PST = {
	   -30,-30,-30,-30,-30,-30,-30,-30,
	   -30,-30,-30,-30,-30,-30,-30,-30,
	   -30,-30,-30,-30,-30,-30,-30,-30,
	   -30,-30,-30,-30,-30,-30,-30,-30,
	   -30,-30,-30,-30,-30,-30,-30,-30,
	   -20,-20,-20,-20,-20,-20,-20,-20,
	   -10,-10,-10,-10,-10,-10,-10,-10,
	     0, 10, 20,-25,  0,-25, 20,  0		
	};
	
	public static final int[] KING_ENDGAME_PST = {
		 0,  0,  0,  0,  0,  0,  0,  0,
		 0, 10, 10, 10, 10, 10, 10,  0,
		 0, 10, 20, 20, 20, 20, 10,  0,
		 0, 10, 20, 25, 25, 20, 10,  0,
		 0, 10, 20, 25, 25, 20, 10,  0,
		 0, 10, 20, 20, 20, 20, 10,  0,
		 0, 10, 10, 10, 10, 10, 10,  0,
		 0,  0,  0,  0,  0,  0,  0,  0 };
	
	public static final int[] QUEEN_PST = {
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1,  0,  0,  0,  0,  0,  0, -1,
		-1,  0,  1,  1,  1,  1,  0, -1,
		-1,  0,  1,  2,  2,  1,  0, -1,
		-1,  0,  1,  2,  2,  1,  0, -1,
		-1,  0,  1,  1,  1,  1,  0, -1,
		-1,  0,  0,  0,  0,  0,  0, -1,
		-1, -1, -1, -1, -1, -1, -1, -1 };

	private Eval() { }

	public static int eval(Board board) {
		return eval(board,false);
	}
	
	public static int eval(Board board,boolean materialOnly) {
	
		OrderedPair<Integer,Integer> matNPScore = getNonPawnMaterialScore(board);
		OrderedPair<Integer,Integer> matPScore = getPawnMaterialScore(board);
		
		int score = matNPScore.getE1() - matNPScore.getE2() + matPScore.getE1() - matPScore.getE2();
		assert((board.getPlayerToMove()==Color.WHITE ? score : -score) == evalMaterial(board));
		
		if (!materialOnly) {
			score += evalPawns(board);
			score += evalKnights(board);
			score += evalBishops(board);
			score += evalRooks(board);
			score += evalQueens(board);
			score += evalKings(board,matNPScore);
		}
		
		return board.getPlayerToMove()==Color.WHITE?score:-score;
	}

	private static int evalBishops(Board board) {
		int score = 0;
		
		long bishopsMap = board.getWhiteBishops();
		while (bishopsMap != 0) {
			int bishopSqVal = Bitboard.msb(bishopsMap);
			Square bishopSq = Square.valueOf(bishopSqVal);
			score += evalBishop(true,bishopSq);
			bishopsMap ^= Bitboard.squares[bishopSqVal];
		}

		bishopsMap = board.getBlackBishops();
		while (bishopsMap != 0) {
			int bishopSqVal = Bitboard.lsb(bishopsMap);
			Square bishopSq = Square.valueOf(bishopSqVal);
			score -= evalBishop(false,bishopSq);
			bishopsMap ^= Bitboard.squares[bishopSqVal];
		}

		return score;
	}

	private static int evalBishop(boolean isWhite,Square sq) {
		return BISHOP_PST[isWhite?sq.value():sq.flipVertical().value()];
	}
	
	private static int evalKnights(Board board) {
		int score = 0;
		
		long knightsMap = board.getWhiteKnights();
		while (knightsMap != 0) {
			int knightSqVal = Bitboard.msb(knightsMap);
			Square knightSq = Square.valueOf(knightSqVal);
			score += evalKnight(board,true,knightSq);
			knightsMap ^= Bitboard.squares[knightSqVal];
		}

		knightsMap = board.getBlackKnights();
		while (knightsMap != 0) {
			int knightSqVal = Bitboard.lsb(knightsMap);
			Square knightSq = Square.valueOf(knightSqVal);
			score -= evalKnight(board,false,knightSq);
			knightsMap ^= Bitboard.squares[knightSqVal];
		}

		return score;
	}
	
	private static int evalKnight(Board board,boolean isWhite,Square sq) {
		int score = 0;
		if (isWhite) {
			score = KNIGHT_PST[sq.value()];
			score += KNIGHT_TROPISM * sq.distance(board.getKingSquare(Color.BLACK));
		} else {
			score = KNIGHT_PST[sq.flipVertical().value()];
			score += KNIGHT_TROPISM * sq.distance(board.getKingSquare(Color.WHITE));
		}
		return score;
	}
	
	private static int evalPawns(Board board) {
		
		// try the pawn hash
		PawnTranspositionTableEntry pte = TTHolder.getPawnTransTable().probe(board.getPawnKey());
		if (pte != null) {
			assert(pte.getScore() == evalPawnsNoHash(board));
			return pte.getScore();
		}
		
		int score = evalPawnsNoHash(board);
		TTHolder.getPawnTransTable().store(board.getPawnKey(), score);
		
		return score;
	}
	
	private static int evalPawnsNoHash(Board board) {
		int score = 0;
		
		long pawnsMap = board.getWhitePawns();
		while (pawnsMap != 0) {
			int pawnSqVal = Bitboard.msb(pawnsMap);
			Square pawnSq = Square.valueOf(pawnSqVal);
			score += evalPawn(board,true,pawnSq);
			pawnsMap ^= Bitboard.squares[pawnSqVal];
		}

		pawnsMap = board.getBlackPawns();
		while (pawnsMap != 0) {
			int pawnSqVal = Bitboard.lsb(pawnsMap);
			Square pawnSq = Square.valueOf(pawnSqVal);
			score -= evalPawn(board,false,pawnSq);
			pawnsMap ^= Bitboard.squares[pawnSqVal];
		}

		return score;
	}
	
	private static int evalPawn(Board board,boolean isWhite,Square sq) {
		int score=0;
		
		score += PAWN_PST[isWhite ? sq.value() : sq.flipVertical().value()];
		if (PawnUtils.isPassedPawn(board,sq,isWhite)) {
			score += PASSED_PAWN;
		}
		if (PawnUtils.isIsolated(board,sq,isWhite)) {
			score += ISOLATED_PAWN;
		}
		if (PawnUtils.isDoubled(board,sq,isWhite)) {
			score += DOUBLED_PAWN;
		}

		return score;
	}
	
	private static int evalConnectedMajorOn7th(Board board,boolean isWhite,Square sq) {
		int score = 0;

		long rookMoves = Magic.getRookMoves(board,sq.value(),
				Bitboard.rays[sq.value()][East.getInstance().value()]);
		
		if (isWhite) {
			if ((rookMoves & (board.getWhiteRooks() | board.getWhiteQueens())) != 0) {
				score += CONNECTED_MAJORS_ON_7TH;
			}
		} else {
			if ((rookMoves & (board.getBlackRooks() | board.getBlackQueens())) != 0) {
				score += CONNECTED_MAJORS_ON_7TH;
			}
		}
		
		return score;
	}
	
	private static int evalMajorOn7th(Board board,boolean isWhite,Square sq) {
		int score = 0;

		if (isWhite) {
			if (sq.rank()==Rank.RANK_7 && board.getKingSquare(Color.BLACK).rank()==Rank.RANK_8) {
				score += ROOK_ON_7TH;
				score += evalConnectedMajorOn7th(board,isWhite,sq);
			}			
		} else {
			if (sq.rank()==Rank.RANK_2 && board.getKingSquare(Color.WHITE).rank()==Rank.RANK_1) {
				score += ROOK_ON_7TH;
				score += evalConnectedMajorOn7th(board,isWhite,sq);
			}			
		}
		
		return score;
	}
	
	private static int evalRookOpenFile(Board board,boolean isWhite,Square sq) {
		int score = 0;
		
		long friends,enemies;
		if (isWhite) {
			friends = board.getWhitePawns();
			enemies = board.getBlackPawns();
		} else {
			friends = board.getBlackPawns();
			enemies = board.getWhitePawns();
		}
		
		long fileMask = Bitboard.files[sq.file().getValue()] ^ Bitboard.squares[sq.value()];
		if ((fileMask & friends)==0) {
			if ((fileMask & enemies)!=0) {
				score += ROOK_HALF_OPEN_FILE;
			} else {
				score += ROOK_OPEN_FILE;
			}
		}
		
		return score;
	}
	
	private static int evalRooks(Board board) {
		int score = 0;
		
		long rooksMap = board.getWhiteRooks();
		while (rooksMap != 0) {
			int rookSqVal = Bitboard.msb(rooksMap);
			Square rookSq = Square.valueOf(rookSqVal);
			score += evalRook(board,true,rookSq);
			rooksMap ^= Bitboard.squares[rookSqVal];
		}

		rooksMap = board.getBlackRooks();
		while (rooksMap != 0) {
			int rookSqVal = Bitboard.lsb(rooksMap);
			Square rookSq = Square.valueOf(rookSqVal);
			score -= evalRook(board,false,rookSq);
			rooksMap ^= Bitboard.squares[rookSqVal];
		}

		return score;
	}

	private static int evalRook(Board board,boolean isWhite,Square sq) {
		int score = ROOK_PST[isWhite?sq.value():sq.flipVertical().value()];
		score += evalMajorOn7th(board,isWhite,sq);
		score += evalRookOpenFile(board,isWhite,sq);
		return score;
	}
	
	// returns a score from the perspective of white
	private static int evalKings(Board b,OrderedPair<Integer,Integer> matNPScore) {
		int score = 0;
	
		final int ENDGAME_THRESHOLD = KNIGHT_VAL * 2 + ROOK_VAL;
		
		Square whiteKingSq = b.getKingSquare(Color.WHITE);
		Square blackKingSq = b.getKingSquare(Color.BLACK);
		
		// if black has a lot of material then eval white in middle game
		if (matNPScore.getE2() >= ENDGAME_THRESHOLD) {  
			score += KING_PST[whiteKingSq.value()];
			score += scale(evalKingSafety(true,b),matNPScore.getE2());
		} else {
			score += KING_ENDGAME_PST[whiteKingSq.value()];				
		}
		if (matNPScore.getE1() >= ENDGAME_THRESHOLD) {
			score -= KING_PST[blackKingSq.flipVertical().value()];
			score -= scale(evalKingSafety(false,b),matNPScore.getE1());
		} else {
			score -= KING_ENDGAME_PST[blackKingSq.flipVertical().value()];				
		}
		
		return score;
	}
	
	// this will return a score from the perspective of the player
	private static int evalKingSafety(boolean isWhite,Board board) {
		int score = 0;

		Square kingSq;
		if (isWhite) {
			kingSq = board.getKingSquare(Color.WHITE);
			// which side are we on? 
			if (kingSq.file().eastOf(File.FILE_E)) {
				// check that pawns on f,g,h are not too far away
				if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY /2;
				} else if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY /2;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY /2;
				}
				
			} else if (kingSq.file().westOf(File.FILE_D)) {
				if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}

				if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_2))==Pawn.WHITE_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_3))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY /2;
				} else if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_4))==Pawn.WHITE_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY /2;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY /2;
				}
			} else {
				// check if open file
				if ( ((board.getWhitePawns() | board.getBlackPawns()) 
						& Bitboard.files[kingSq.file().getValue()])==0)
				{
					score += KING_SAFETY_MIDDLE_OPEN_FILE;
				}
			}
			// scale down with material?
		} else {
			kingSq = board.getKingSquare(Color.BLACK);
			if (kingSq.file().eastOf(File.FILE_E)) {
				if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_F, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_G, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY /2;
				} else if (board.getPiece(Square.valueOf(File.FILE_H, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY /2;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY /2;
				}
			} else if (kingSq.file().westOf(File.FILE_D)) {
				if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_C, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}

				if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY;
				} else if (board.getPiece(Square.valueOf(File.FILE_B, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY;
				}
				
				if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_7))==Pawn.BLACK_PAWN);
				else if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_6))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_ONE_AWAY /2;
				} else if (board.getPiece(Square.valueOf(File.FILE_A, Rank.RANK_5))==Pawn.BLACK_PAWN) {
					score += KING_SAFETY_PAWN_TWO_AWAY /2;
				} else {
					score += KING_SAFETY_PAWN_FAR_AWAY /2;
				}
			} else {
				// check if open file
				if ( ((board.getWhitePawns() | board.getBlackPawns()) 
						& Bitboard.files[kingSq.file().getValue()])==0)
				{
					score += KING_SAFETY_MIDDLE_OPEN_FILE;
				}
			}			
		}
		
		return score;
	}
	
	private static int evalQueens(Board board) {
		int score = 0;
		
		long queensMap = board.getWhiteQueens();
		while (queensMap != 0) {
			int queenSqVal = Bitboard.msb(queensMap);
			Square queenSq = Square.valueOf(queenSqVal);
			score += evalQueen(board,true,queenSq);
			queensMap ^= Bitboard.squares[queenSqVal];
		}

		queensMap = board.getBlackQueens();
		while (queensMap != 0) {
			int queenSqVal = Bitboard.lsb(queensMap);
			Square queenSq = Square.valueOf(queenSqVal);
			score -= evalQueen(board,false,queenSq);
			queensMap ^= Bitboard.squares[queenSqVal];
		}

		return score;
	}
	
	private static int evalQueen(Board board,boolean isWhite,Square sq) {
		int score = QUEEN_PST[isWhite?sq.value():sq.flipVertical().value()];
		score += evalMajorOn7th(board,isWhite,sq);
		return score;
	}
	
	public static int getPieceValue(Piece piece) {
		return pieceValMap.get(piece.getClass());
	}
	
	public static OrderedPair<Integer,Integer> getPawnMaterialScore(Board board) {
		return new OrderedPair<Integer,Integer>(board.getNumPieces(Pawn.WHITE_PAWN) * PAWN_VAL,
				board.getNumPieces(Pawn.BLACK_PAWN) * PAWN_VAL);
	}
	
	public static OrderedPair<Integer,Integer> getNonPawnMaterialScore(Board board) {
		int wScore = board.getNumPieces(Queen.WHITE_QUEEN) * QUEEN_VAL
				+ board.getNumPieces(Rook.WHITE_ROOK) * ROOK_VAL
				+ board.getNumPieces(Knight.WHITE_KNIGHT) * KNIGHT_VAL
				+ board.getNumPieces(Bishop.WHITE_BISHOP) * BISHOP_VAL;
				
		int bScore = board.getNumPieces(Queen.BLACK_QUEEN) * QUEEN_VAL
				+ board.getNumPieces(Rook.BLACK_ROOK) * ROOK_VAL
				+ board.getNumPieces(Knight.BLACK_KNIGHT) * KNIGHT_VAL
				+ board.getNumPieces(Bishop.BLACK_BISHOP) * BISHOP_VAL;
		
		return new OrderedPair<Integer,Integer>(wScore,bScore);
	}
	
	public static int evalMaterial(Board board) {
		int score = 0;
		
		for (Square sq : Square.allSquares()) {
			Piece p = board.getPiece(sq);
			if (p != null) { 
				if (p.isWhite()) {
					score += getPieceValue(p);
				} else {
					score -= getPieceValue(p);
				}
			}
		}

		return board.getPlayerToMove().equals(Color.WHITE)?score:-score;
	}
	
	public static int scale(int score,int material) {
		
		return score * material / ALL_NONPAWN_PIECES_VAL;
	}
}
