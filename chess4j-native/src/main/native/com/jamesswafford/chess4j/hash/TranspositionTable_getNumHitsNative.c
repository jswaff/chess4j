#include <prophet/hash.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../init/p4_init.h"

extern hash_table_t htbl;


/*
 * Class:     com_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    getNumHitsNative
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_jamesswafford_chess4j_hash_TranspositionTable_getNumHitsNative
  (JNIEnv *env, jobject UNUSED(htable))
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }
    
    return htbl.hits;
}
