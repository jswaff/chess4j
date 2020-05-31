#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"


extern bool volatile stop_search;



/*
 * Class:     com_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    stopNative
 * Signature: (Z)V
 */
JNIEXPORT void 
JNICALL Java_com_jamesswafford_chess4j_search_AlphaBetaSearch_stopNative
  (JNIEnv *env, jobject UNUSED(iterator_obj), jboolean stop)
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return;
    }

    stop_search = stop;
}
