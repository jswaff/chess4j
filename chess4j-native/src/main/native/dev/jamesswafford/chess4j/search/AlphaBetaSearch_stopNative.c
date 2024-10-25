#include <prophet/parameters.h>

#include <dev_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

extern bool volatile stop_search;

/*
 * Class:     dev_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    stopNative
 * Signature: (Z)V
 */
JNIEXPORT void 
JNICALL Java_dev_jamesswafford_chess4j_search_AlphaBetaSearch_stopNative
  (JNIEnv *env, jobject UNUSED(iterator_obj), jboolean stop)
{
    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }

    stop_search = stop;
}
