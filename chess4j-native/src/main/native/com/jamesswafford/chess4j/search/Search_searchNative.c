#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>
#include "../init/p4_init.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/Long.h"

#include <stdlib.h>
#include <string.h>

/* move stack */
move_t moves[MAX_PLY * MAX_MOVES_PER_PLY];


/*
 * Class:     com_jamesswafford_chess4j_search_v2_Search
 * Method:    searchNative
 * Signature: (Ljava/lang/String;Ljava/util/List;IIILcom/jamesswafford/chess4j/search/v2/SearchStats;)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_search_v2_Search_searchNative
  (JNIEnv *env, jobject UNUSED(search_obj), jstring board_fen, jobject parent_pv, 
    jint depth, jint alpha, jint beta, jobject search_stats)

{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }

    /* set the position according to the FEN */
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

    /* perform the search */
    move_line_t pv;
    stats_t native_stats;
    int32_t native_score = search(&pos, &pv, depth, alpha, beta, moves, 
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

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);


    return retval;
}
