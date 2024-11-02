#include "dev_jamesswafford_chess4j_hash_PawnTranspositionTable.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/hash.h>

extern hash_table_t phtbl;


/*
 * Class:     dev_jamesswafford_chess4j_hash_PawnTranspositionTable
 * Method:    clearNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_hash_PawnTranspositionTable_clearNative
  (JNIEnv *env, jobject UNUSED(phtable))
{

    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }
    
    /* clear the table */
    clear_hash_table(&phtbl);
}
