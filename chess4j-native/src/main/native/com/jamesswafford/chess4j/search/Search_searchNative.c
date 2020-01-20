#include <prophet/search.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>
#include "../init/p4_init.h"

#include <stdlib.h>
#include <string.h>

/*
 * Class:     com_jamesswafford_chess4j_search_v2_Search
 * Method:    searchNative
 * Signature: (Ljava/lang/String;IIILcom/jamesswafford/chess4j/search/v2/SearchStats;)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_search_v2_Search_searchNative
  (JNIEnv *env, jobject UNUSED(search_obj), jstring board_fen, jint depth, 
    jint alpha, jint beta, jobject search_stats)

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
    stats_t native_stats;
    int32_t native_score = search(&pos, depth, alpha, beta, &native_stats);
    retval = (jint) native_score;

    /* set the search stats */
    jclass class_SearchStats = (*env)->GetObjectClass(env, search_stats);
    jfieldID fidNodes = (*env)->GetFieldID(env, class_SearchStats, "nodes", "J");
    (*env)->SetLongField(env, search_stats, fidNodes, native_stats.nodes);

    jfieldID fidFailHighs = (*env)->GetFieldID(env, class_SearchStats, "failHighs", "J");
    (*env)->SetLongField(env, search_stats, fidFailHighs, native_stats.fail_highs);

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);


    return retval;
}
