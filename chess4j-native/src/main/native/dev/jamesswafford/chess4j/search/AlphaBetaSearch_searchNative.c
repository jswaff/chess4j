#include "dev_jamesswafford_chess4j_search_AlphaBetaSearch.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "dev/jamesswafford/chess4j/io/PrintLine.h"
#include "java/lang/IllegalStateException.h"
#include "java/lang/Long.h"
#include "java/util/ArrayList.h"

#include <prophet/const.h>
#include <prophet/move.h>
#include <prophet/movegen.h>
#include <prophet/position.h>
#include <prophet/search.h>

#include <assert.h>
#include <stdlib.h>
#include <string.h>

/* move stack */
move_t native_moves[MAX_PLY * MAX_MOVES_PER_PLY];

/* undo stack */
undo_t native_undos[MAX_HALF_MOVES_PER_GAME];

/* flag to stop the search, or in our case as notification the search was stopped */
extern volatile bool stop_search;

/* search stats */
stats_t native_stats;

/* keep refs to use in the static helper function */
JNIEnv *g_env;
jobject *g_parent_pv;
color_t g_ptm;

/* forward decls */
static void pv_callback(move_t*, int, int32_t, int32_t, uint64_t, uint64_t);

/*
 * Class:     dev_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    searchNative
 * Signature: (Ljava/lang/String;Ljava/util/List;IIILdev/jamesswafford/chess4j/search/SearchStats;JJZLjava/lang/String;Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_dev_jamesswafford_chess4j_search_AlphaBetaSearch_searchNative
  (JNIEnv *env, jobject search_obj, jstring board_fen, jobject parent_pv, jint depth,
  jint alpha, jint beta, jobject search_stats, jlong start_time, jlong stop_time, jboolean post,
  jstring UNUSED(non_reversible_board_fen), jobject UNUSED(move_path))
{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }

    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    //const char* non_reversible_fen = (*env)->GetStringUTFChars(env, non_reversible_board_fen, 0);

    /* set the position according to the FEN */
    position_t pos;
    if (!set_pos(&pos, fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set position: %s\n", fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* set the last non-reversible position according to the FEN */
//    position_t non_reversible_pos;
//    if (!set_pos(&non_reversible_pos, non_reversible_fen)) {
//        char error_buffer[255];
//        sprintf(error_buffer, "Could not set last non-reversible position: %s\n", non_reversible_fen);
//        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
//        goto cleanup;
//    }

    /* remember some variables to use in the PV callback */
    g_env = env;
    g_parent_pv = &parent_pv;
    g_ptm = pos.player;

    /* set up the search options */
    search_options_t search_opts;
    memset(&search_opts, 0, sizeof(search_options_t));
    if (post) search_opts.pv_callback = pv_callback;
    search_opts.start_time = start_time;
    search_opts.stop_time = stop_time;
    search_opts.nodes_between_time_checks = 100000UL;
    if (stop_time > 0 && stop_time - start_time < 10000) {
        search_opts.nodes_between_time_checks /= 10;
    }
    if (stop_time > 0 && stop_time - start_time < 1000) {
        search_opts.nodes_between_time_checks /= 10;   
    }

    /* set up the undo stack */
    memset(&native_undos, 0, sizeof(undo_t));
//    jint n_moves = (*env)->CallIntMethod(env, move_path, ArrayList_size);
//    if ((int)n_moves >= (int)pos.fifty_counter) {
//        jint offset = n_moves - pos.fifty_counter;
//        for (uint32_t i=0;i<pos.fifty_counter;i++) {
//            jobject jmove_obj = (*env)->CallObjectMethod(env, move_path, ArrayList_get, offset + i);
//            jlong jmove = (*env)->CallLongMethod(env, jmove_obj, Long_longValue);
//            move_t mv = (move_t)jmove;
//#if 0
//            if (!is_legal_move(mv, &non_reversible_pos)) {
//                char error_buffer[255];
//                sprintf(error_buffer, "Illegal move %d: %s\n", i, move_to_str(mv));
//                (*env)->ThrowNew(env, IllegalStateException, error_buffer);
//                goto cleanup;
//            }
//#endif
//            assert(is_legal_move(mv, &non_reversible_pos));
//            apply_move(&non_reversible_pos, mv, &native_undos[pos.move_counter - pos.fifty_counter + i]);
//        }
//    }

    /* perform the search */
    move_line_t pv;
    int32_t native_score = search(&pos, &pv, depth, alpha, beta, native_moves, native_undos,
        &native_stats, &search_opts);
    retval = (jint) native_score;


    /* set the stop flag in the Java code to match the native code's.  This will 
     * prompt the iterative deepening driver to stop. */
    jclass class_AlphaBetaSearch = (*env)->GetObjectClass(env, search_obj);
    if (stop_search) {
        jmethodID AlphaBetaSearch_stop = (*env)->GetMethodID(
            env, class_AlphaBetaSearch, "stop", "()V");
        (*env)->CallVoidMethod(env, search_obj, AlphaBetaSearch_stop);
    } else {
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


cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);
    //(*env)->ReleaseStringUTFChars(env, non_reversible_board_fen, non_reversible_fen);

    return retval;
}


static void pv_callback(move_t* pv, int num_pv, int32_t depth, int32_t score, uint64_t elapsed, uint64_t num_nodes)
{
    /* update the parent pv */
    (*g_env)->CallBooleanMethod(g_env, *g_parent_pv, ArrayList_clear);
    for (int i=0; i < num_pv; i++) {
        /* create Long value representing this move */
        jobject lval = (*g_env)->CallStaticObjectMethod(g_env, Long, Long_valueOf, (jlong)(pv[i]));

        /* add to java list */
        (*g_env)->CallBooleanMethod(g_env, *g_parent_pv, ArrayList_add, lval);
        (*g_env)->DeleteLocalRef(g_env, lval);
    }

    (*g_env)->CallStaticVoidMethod(g_env, PrintLine, PrintLine_printNativeLine, 
        depth, *g_parent_pv, g_ptm==WHITE, score, elapsed, num_nodes);
}
