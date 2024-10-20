#include <prophet/parameters.h>

#include <dev_jamesswafford_chess4j_search_AlphaBetaSearch.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"


jobject g_pv_callback = NULL;

/*
 * Class:     dev_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    setPvCallBackNative
 * Signature: (Ljava/util/function/Consumer;)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_search_AlphaBetaSearch_setPvCallBackNative
  (JNIEnv *env, jobject UNUSED(search_obj), jobject pv_callback)
{

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet4 not initialized!");
        return;
    }

    g_pv_callback = pv_callback;
}
