#include "dev_jamesswafford_chess4j_hash_TranspositionTable.h"

#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/hash.h>
#include <prophet/parameters.h>

extern hash_table_t htbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    clearNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_hash_TranspositionTable_clearNative
  (JNIEnv *env, jobject UNUSED(htable))
{

    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }
    
    /* clear the table */
    clear_hash_table(&htbl);
}
