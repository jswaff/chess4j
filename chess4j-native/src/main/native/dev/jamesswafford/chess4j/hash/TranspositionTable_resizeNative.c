#include <prophet/hash.h>
#include <prophet/parameters.h>

#include <dev_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

extern hash_table_t htbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    resizeNative
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_hash_TranspositionTable_resizeNative
  (JNIEnv *env, jobject UNUSED(htable), jlong size_bytes)
{
    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }

    int retval = resize_hash_table(&htbl, (uint64_t) size_bytes);
    if (0 != retval) {
        (*env)->ThrowNew(env, IllegalStateException, "Failed to (re)allocate hash table");
    }
}
