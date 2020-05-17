#include <prophet/parameters.h>
#include <prophet/position/move.h>

#include <com_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"

#include <string.h>


extern move_line_t last_pv;


/*
 * Class:     com_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    initializeNativeSearch
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jamesswafford_chess4j_search_AlphaBetaSearch_initializeNativeSearch
  (JNIEnv *env, jobject UNUSED(search_obj))
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return;
    }


    memset(&last_pv, 0, sizeof(move_line_t));
}