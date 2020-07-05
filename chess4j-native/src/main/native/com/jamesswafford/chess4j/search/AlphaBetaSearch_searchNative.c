#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>
#include <prophet/util/p4time.h>

#include <com_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../io/PrintLine.h"
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

extern volatile bool stop_search;


static void pv_callback(move_line_t*, int32_t, int32_t, uint64_t, uint64_t);

/*
 * Class:     com_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    searchNative
 * Signature: (Ljava/lang/String;Ljava/util/List;Ljava/util/List;IIILcom/jamesswafford/chess4j/search/SearchStats;JJ)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_search_AlphaBetaSearch_searchNative
  (JNIEnv *env, jobject search_obj, jstring board_fen, jobject prev_moves,
    jobject parent_pv, jint depth, jint alpha, jint beta, jobject search_stats, jlong start_time,
    jlong stop_time)
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


    /* set the position according to the FEN.  We use the FEN instead of the
     * prev_moves list for test suites, which don't have a move history.
     */
    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    position_t pos;
    if (!set_pos(&pos, fen))
    {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set position: %s\n", fen);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            error_buffer);
        goto cleanup;
    }

    /* replay the previous moves for draw checks */
    position_t replay_pos;
    reset_pos(&replay_pos);    
    jint size = (*env)->CallIntMethod(env, prev_moves, ArrayList_size);
    jvalue arg;
    for (int i=0; i<size; i++)
    {
        arg.i = i;
        jobject element = (*env)->CallObjectMethodA(env, prev_moves, ArrayList_get, 
            &arg);
        jlong prev_mv = (*env)->CallLongMethod(env, element, Long_longValue);

        /* apply this move */
        apply_move(&replay_pos, (move_t) prev_mv, undos + replay_pos.move_counter);

        (*env)->DeleteLocalRef(env, element);
    }
    g_ptm = replay_pos.player;


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
    int32_t native_score = search(&pos, &pv, depth, alpha, beta, moves, undos,
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


    /* free resources */
cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

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
