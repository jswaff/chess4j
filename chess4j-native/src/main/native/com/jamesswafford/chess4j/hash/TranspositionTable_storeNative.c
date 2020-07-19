#include <prophet/hash.h>
#include <prophet/parameters.h>
#include <prophet/position/position.h>

#include <com_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../init/p4_init.h"

extern hash_table_t htbl;

/*
 * Class:     com_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    storeNative
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_com_jamesswafford_chess4j_hash_TranspositionTable_storeNative
  (JNIEnv *env, jobject UNUSED(htable), jstring board_fen, jlong val)
{
    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return;
    }
    
    /* set the position according to the FEN, so we can get the native hash key */
    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    position_t pos;
    if (!set_pos(&pos, fen))
    {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set position: %s\n", fen);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            error_buffer);
        goto cleanup;
    }

    /* store the value in the hash table */
    store_hash_entry(&htbl, pos.hash_key, (uint64_t)val);

    /* free resources */
cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

}
