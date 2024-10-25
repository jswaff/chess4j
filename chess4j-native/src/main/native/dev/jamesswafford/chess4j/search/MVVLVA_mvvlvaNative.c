#include "dev_jamesswafford_chess4j_search_MVVLVA.h"

#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/search.h>
#include <prophet/parameters.h>

/*
 * Class:     dev_jamesswafford_chess4j_search_MVVLVA
 * Method:    mvvlvaNative
 * Signature: (J)I
 */
JNIEXPORT jint 
JNICALL Java_dev_jamesswafford_chess4j_search_MVVLVA_mvvlvaNative
  (JNIEnv *env, jclass UNUSED(clazz), jlong mv)
{
    jint retval = 0;

    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }

    int32_t native_score = mvvlva((move_t)mv);
    retval = (jint) native_score;

    return retval;
}
