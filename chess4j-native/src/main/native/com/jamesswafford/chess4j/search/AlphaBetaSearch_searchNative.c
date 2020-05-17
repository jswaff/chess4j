#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/Long.h"

#include <stdlib.h>
#include <string.h>

/* move stack */
move_t moves[MAX_PLY * MAX_MOVES_PER_PLY];

/* undo stack */
undo_t undos[MAX_PLY];

/*
 * Class:     com_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    searchNative
 * Signature: (Ljava/lang/String;Ljava/util/List;Ljava/util/List;IIILcom/jamesswafford/chess4j/search/SearchStats;)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_search_AlphaBetaSearch_searchNative
  (JNIEnv *env, jobject UNUSED(search_obj), jstring board_fen, jobject prev_moves,
    jobject parent_pv, jint depth, jint alpha, jint beta, jobject search_stats)

{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }


    /* retrieve the java.util.List interface class */
    jclass class_List = (*env)->FindClass(env, "java/util/List");

    /* retrieve the size and get methods */
    jmethodID midSize = (*env)->GetMethodID(env, class_List, "size", "()I");
    jmethodID midGet = (*env)->GetMethodID(env, class_List, "get", "(I)Ljava/lang/Object;");

    /* retrieve the java.lang.Long class */
    jclass class_Long = (*env)->FindClass(env, "java/lang/Long");

    /* retrieve the longValue method */
    jmethodID midLongValue = (*env)->GetMethodID(env, class_Long, "longValue", "()J");


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
    jint size = (*env)->CallIntMethod(env, prev_moves, midSize);
    jvalue arg;
    for (int i=0; i<size; i++)
    {
        arg.i = i;
        jobject element = (*env)->CallObjectMethodA(env, prev_moves, midGet, &arg);
        jlong prev_mv = (*env)->CallLongMethod(env, element, midLongValue);

        /* apply this move */
        apply_move(&replay_pos, (move_t) prev_mv, undos + replay_pos.move_counter);

        (*env)->DeleteLocalRef(env, element);
    }


    /* perform the search */
    move_line_t pv;
    stats_t native_stats;
    int32_t native_score = search(&pos, &pv, depth, alpha, beta, moves, undos,
        &native_stats);
    retval = (jint) native_score;

    /* copy the PV to the Java list */
    for (int i=0; i < pv.n; i++)
    {
        /* create Long value representing this move */
        jobject lval = (*env)->CallStaticObjectMethod(
            env, Long, Long_valueOf, (jlong)(pv.mv[i]));

        /* add to java list */
        (*env)->CallBooleanMethod(env, parent_pv, ArrayList_add, lval);
        (*env)->DeleteLocalRef(env, lval);
    }

    /* copy the search stats to the Java structure */
    jclass class_SearchStats = (*env)->GetObjectClass(env, search_stats);
    jfieldID fidNodes = (*env)->GetFieldID(env, class_SearchStats, "nodes", "J");
    (*env)->SetLongField(env, search_stats, fidNodes, native_stats.nodes);

    jfieldID fidFailHighs = (*env)->GetFieldID(env, class_SearchStats, "failHighs", "J");
    (*env)->SetLongField(env, search_stats, fidFailHighs, native_stats.fail_highs);

    jfieldID fidFailLows = (*env)->GetFieldID(env, class_SearchStats, "failLows", "J");
    (*env)->SetLongField(env, search_stats, fidFailLows, native_stats.fail_lows);

    jfieldID fidDraws = (*env)->GetFieldID(env, class_SearchStats, "draws", "J");
    (*env)->SetLongField(env, search_stats, fidDraws, native_stats.draws);


    /* free resources */
cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
