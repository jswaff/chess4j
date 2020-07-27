#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>
#include <prophet/util/p4time.h>

#include <com_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../io/PrintLine.h"
#include "../pieces/Piece.h"
#include "../../../../java/lang/Long.h"
#include "../../../../java/util/ArrayList.h"

#include <stdlib.h>
#include <string.h>

/* move stack */
move_t moves[MAX_PLY * MAX_MOVES_PER_PLY];

/* undo stack */
undo_t undos[MAX_HALF_MOVES_PER_GAME];

/* search stats */
stats_t native_stats;

/* keep refs to use in the static helper function */
JNIEnv *g_env;
jobject *g_parent_pv;
color_t g_ptm;

/* flag to stop the search, or in our case as notification the search was stopped */
extern volatile bool stop_search;

/* TODO: these methods are marked "internal" */
void add_piece(position_t* p, int32_t piece, square_t sq);
bool verify_pos(const position_t* pos);
uint64_t build_hash_key(const position_t* pos);
uint64_t build_pawn_key(const position_t* pos);

static void pv_callback(move_line_t*, int32_t, int32_t, uint64_t, uint64_t);


/*
 * Class:     com_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    searchNative
 * Signature: (Lcom/jamesswafford/chess4j/board/Board;Ljava/util/List;IIILcom/jamesswafford/chess4j/search/SearchStats;JJ)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_search_AlphaBetaSearch_searchNative
  (JNIEnv *env, jobject search_obj, jobject board_obj, jobject parent_pv, jint depth, 
    jint alpha, jint beta, jobject search_stats, jlong start_time, jlong stop_time)
{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }

    g_env = env;
    g_parent_pv = &parent_pv;

    /* set the position */
    jclass class_Board = (*env)->GetObjectClass(env, board_obj);
    position_t c4j_pos;
    memset(&c4j_pos, 0, sizeof(position_t));


    /* set the player */
    jmethodID Board_getPlayerToMove = (*env)->GetMethodID(
        env, class_Board, "getPlayerToMove", "()Lcom/jamesswafford/chess4j/board/Color;");
    jobject player_obj = (*env)->CallObjectMethod(env, board_obj, Board_getPlayerToMove);
    jclass class_Color = (*env)->GetObjectClass(env, player_obj);
    jmethodID Color_isWhite = (*env)->GetMethodID(env, class_Color, "isWhite", "()Z");
    bool is_white = (*env)->CallBooleanMethod(env, player_obj, Color_isWhite);
    c4j_pos.player = is_white ? WHITE : BLACK;
    g_ptm = c4j_pos.player;


    /* set the EP square */
    jmethodID Board_getEPSquare = (*env)->GetMethodID(
        env, class_Board, "getEPSquare", "()Lcom/jamesswafford/chess4j/board/squares/Square;");
    jobject ep_sq_obj = (*env)->CallObjectMethod(
        env, board_obj, Board_getEPSquare);
    if (ep_sq_obj == NULL)
    {
        c4j_pos.ep_sq = NO_SQUARE;
    }
    else
    {
        jclass class_Square = (*env)->GetObjectClass(env, ep_sq_obj);
        jmethodID Square_value = (*env)->GetMethodID(env, class_Square, "value", "()I");
        c4j_pos.ep_sq = (*env)->CallIntMethod(env, ep_sq_obj, Square_value);
    }

    /* set the castling rights */
    jmethodID Board_hasWKCastlingRight = (*env)->GetMethodID(
        env, class_Board, "hasWKCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasWKCastlingRight))
    {
        c4j_pos.castling_rights |= CASTLE_WK;
    }
    jmethodID Board_hasWQCastlingRight = (*env)->GetMethodID(
        env, class_Board, "hasWQCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasWQCastlingRight))
    {
        c4j_pos.castling_rights |= CASTLE_WQ;
    }
    jmethodID Board_hasBKCastlingRight = (*env)->GetMethodID(
        env, class_Board, "hasBKCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasBKCastlingRight))
    {
        c4j_pos.castling_rights |= CASTLE_BK;
    }
    jmethodID Board_hasBQCastlingRight = (*env)->GetMethodID(
        env, class_Board, "hasBQCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasBQCastlingRight))
    {
        c4j_pos.castling_rights |= CASTLE_BQ;
    }

    /* set the 50 move counter */
    jmethodID Board_getFiftyCounter = (*env)->GetMethodID(
        env, class_Board, "getFiftyCounter", "()I");
    c4j_pos.fifty_counter = (*env)->CallIntMethod(
        env, board_obj, Board_getFiftyCounter);

    /* set the full move counter */
    jmethodID Board_getMoveCounter = (*env)->GetMethodID(
        env, class_Board, "getMoveCounter", "()I");
    c4j_pos.move_counter = (*env)->CallIntMethod(env, board_obj, Board_getMoveCounter);


    /* add the pieces */
    jmethodID Board_getPiece = (*env)->GetMethodID(
        env, class_Board, "getPiece", "(I)Lcom/jamesswafford/chess4j/pieces/Piece;");
    for (int i=0;i<64;i++)
    {
        jobject piece_obj = (*env)->CallObjectMethod(env, board_obj, Board_getPiece, i);
        if (piece_obj != NULL)
        {
            bool is_white = (*env)->CallBooleanMethod(env, piece_obj, Piece_isWhite);
            if ((*env)->IsInstanceOf(env, piece_obj, Bishop))
            {
                add_piece(&c4j_pos, is_white ? BISHOP : -BISHOP, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, King))
            {
                add_piece(&c4j_pos, is_white ? KING : -KING, i);
                if (is_white)
                {
                    c4j_pos.white_king = i;
                }
                else
                {
                    c4j_pos.black_king = i;
                }
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Knight))
            {
                add_piece(&c4j_pos, is_white ? KNIGHT : -KNIGHT, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Pawn))
            {
                add_piece(&c4j_pos, is_white ? PAWN : -PAWN, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Queen))
            {
                add_piece(&c4j_pos, is_white ? QUEEN : -QUEEN, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Rook))
            {
                add_piece(&c4j_pos, is_white ? ROOK : -ROOK, i);
            }
        }
    }

    /* set the hash keys */
    c4j_pos.hash_key = build_hash_key(&c4j_pos);
    c4j_pos.pawn_key = build_pawn_key(&c4j_pos);

    if (!verify_pos(&c4j_pos))
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Position is not consistent");
        return 0;
    }


    /* set up the search options */
    search_options_t search_opts;
    memset(&search_opts, 0, sizeof(search_options_t));
    search_opts.pv_callback = pv_callback;
    search_opts.start_time = start_time;
    search_opts.stop_time = stop_time;
    search_opts.nodes_between_time_checks = 100000UL;
    if (stop_time > 0 && stop_time - start_time < 10000)
    {
        search_opts.nodes_between_time_checks /= 10;
    }
    if (stop_time > 0 && stop_time - start_time < 1000)
    {
        search_opts.nodes_between_time_checks /= 10;   
    }

    /* perform the search */
    move_line_t pv;
    int32_t native_score = search(&c4j_pos, &pv, depth, alpha, beta, moves, undos,
        &native_stats, &search_opts);
    retval = (jint) native_score;


    /* set the stop flag in the Java code to match the native code's.  This will 
     * prompt the iterative deepening driver to stop. */
    jclass class_AlphaBetaSearch = (*env)->GetObjectClass(env, search_obj);
    if (stop_search)
    {
        jmethodID AlphaBetaSearch_stop = (*env)->GetMethodID(
            env, class_AlphaBetaSearch, "stop", "()V");
        (*env)->CallVoidMethod(env, search_obj, AlphaBetaSearch_stop);
    }
    else
    {
        jmethodID AlphaBetaSearch_unstop = (*env)->GetMethodID(
            env, class_AlphaBetaSearch, "unstop", "()V");
        (*env)->CallVoidMethod(env, search_obj, AlphaBetaSearch_unstop);
    }


    /* copy the search stats to the Java structure */
    jclass class_SearchStats = (*env)->GetObjectClass(env, search_stats);
    jfieldID fid_nodes = (*env)->GetFieldID(env, class_SearchStats, "nodes", "J");
    (*env)->SetLongField(env, search_stats, fid_nodes, native_stats.nodes);

    jfieldID fid_qnodes = (*env)->GetFieldID(env, class_SearchStats, "qnodes", "J");
    (*env)->SetLongField(env, search_stats, fid_qnodes, native_stats.qnodes);

    jfieldID fid_failHighs = (*env)->GetFieldID(env, class_SearchStats, "failHighs", "J");
    (*env)->SetLongField(env, search_stats, fid_failHighs, native_stats.fail_highs);

    jfieldID fid_failLows = (*env)->GetFieldID(env, class_SearchStats, "failLows", "J");
    (*env)->SetLongField(env, search_stats, fid_failLows, native_stats.fail_lows);

    jfieldID fid_draws = (*env)->GetFieldID(env, class_SearchStats, "draws", "J");
    (*env)->SetLongField(env, search_stats, fid_draws, native_stats.draws);

    jfieldID fid_hashFailHighs = (*env)->GetFieldID(env, class_SearchStats, "hashFailHighs", "J");
    (*env)->SetLongField(env, search_stats, fid_hashFailHighs, native_stats.hash_fail_highs);

    jfieldID fid_hashFailLows = (*env)->GetFieldID(env, class_SearchStats, "hashFailLows", "J");
    (*env)->SetLongField(env, search_stats, fid_hashFailLows, native_stats.hash_fail_lows);

    jfieldID fid_hashExactScores = (*env)->GetFieldID(env, class_SearchStats, "hashExactScores", "J");
    (*env)->SetLongField(env, search_stats, fid_hashExactScores, native_stats.hash_exact_scores);


    /* cleanup and get out */
    g_parent_pv = 0;
    g_env = 0;

    return retval;
}


static void pv_callback(move_line_t* pv, int32_t depth, int32_t score, 
    uint64_t elapsed, uint64_t num_nodes)
{
    /* update the parent pv */
    (*g_env)->CallBooleanMethod(g_env, *g_parent_pv, ArrayList_clear);
    for (int i=0; i < pv->n; i++)
    {
        /* create Long value representing this move */
        jobject lval = (*g_env)->CallStaticObjectMethod(
            g_env, Long, Long_valueOf, (jlong)(pv->mv[i]));

        /* add to java list */
        (*g_env)->CallBooleanMethod(g_env, *g_parent_pv, ArrayList_add, lval);
        (*g_env)->DeleteLocalRef(g_env, lval);
    }

    (*g_env)->CallStaticVoidMethod(g_env, PrintLine, PrintLine_printNativeLine, 
        depth, *g_parent_pv, g_ptm==WHITE, score, elapsed, num_nodes);
}
