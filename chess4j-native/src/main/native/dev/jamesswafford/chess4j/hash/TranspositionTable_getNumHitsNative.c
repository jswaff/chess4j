#include "dev_jamesswafford_chess4j_hash_TranspositionTable.h"

#include "../../../../parameters.h"
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/hash.h>

extern hash_table_t htbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    getNumHitsNative
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_dev_jamesswafford_chess4j_hash_TranspositionTable_getNumHitsNative
  (JNIEnv *env, jobject UNUSED(htable))
{
    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }
    
    return htbl.hits;
}
