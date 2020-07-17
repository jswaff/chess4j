#include <prophet/hash.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../init/p4_init.h"

extern hash_table_t htbl;

/*
 * Class:     com_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    resizeNative
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jamesswafford_chess4j_hash_TranspositionTable_resizeNative
  (JNIEnv *env, jobject UNUSED(htable), jint size_bytes)
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return;
    }
    

    resize_hash_table(&htbl, (uint32_t) size_bytes);
}
