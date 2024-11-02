#include "dev_jamesswafford_chess4j_search_AlphaBetaSearch.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <stdbool.h>

extern bool volatile skip_time_checks;

/*
 * Class:     dev_jamesswafford_chess4j_search_AlphaBetaSearch
 * Method:    skipTimeChecksNative
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_search_AlphaBetaSearch_skipTimeChecksNative
  (JNIEnv *env, jobject UNUSED(iterator_obj), jboolean java_skip_time_checks)
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }

    skip_time_checks = java_skip_time_checks;
}
