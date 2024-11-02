#include "dev_jamesswafford_chess4j_hash_TranspositionTable.h"

#include "../prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/hash.h>

extern hash_table_t htbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    getNumCollisionsNative
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_dev_jamesswafford_chess4j_hash_TranspositionTable_getNumCollisionsNative
  (JNIEnv *env, jobject UNUSED(htable))
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }

    return htbl.collisions;
}
