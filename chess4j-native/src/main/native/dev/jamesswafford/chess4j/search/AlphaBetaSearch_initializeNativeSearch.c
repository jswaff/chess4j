#include <prophet/parameters.h>
#include <prophet/position/move.h>
#include <prophet/search.h>

#include <dev_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <string.h>

extern move_line_t last_pv;
extern stats_t native_stats;


/*
 * Class:     dev_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    initializeNativeSearch
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_search_AlphaBetaSearch_initializeNativeSearch
  (JNIEnv *env, jobject UNUSED(search_obj))
{
    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }


    memset(&last_pv, 0, sizeof(move_line_t));
    memset(&native_stats, 0, sizeof(stats_t));
}
