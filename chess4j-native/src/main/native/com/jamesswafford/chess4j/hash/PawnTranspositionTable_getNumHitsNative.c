#include <prophet/hash.h>
#include <prophet/parameters.h>

#include <dev_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

extern hash_table_t phtbl;


/*
 * Class:     dev_jamesswafford_chess4j_hash_PawnTranspositionTable
 * Method:    getNumHitsNative
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_dev_jamesswafford_chess4j_hash_PawnTranspositionTable_getNumHitsNative
  (JNIEnv *env, jobject UNUSED(phtable))
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }
    
    return phtbl.hits;
}
