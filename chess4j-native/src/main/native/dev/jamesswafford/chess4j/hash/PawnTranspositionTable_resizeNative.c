#include "dev_jamesswafford_chess4j_hash_PawnTranspositionTable.h"

#include "../prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/hash.h>

extern hash_table_t phtbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_PawnTranspositionTable
 * Method:    resizeNative
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_hash_PawnTranspositionTable_resizeNative
  (JNIEnv *env, jobject UNUSED(phtable), jlong size_bytes)
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }
    

    int retval = resize_hash_table(&phtbl, (uint64_t) size_bytes);
    if (0 != retval) {
        (*env)->ThrowNew(env, IllegalStateException, "Failed to (re)allocate hash table");
    }
}
