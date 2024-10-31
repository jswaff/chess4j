#include "dev_jamesswafford_chess4j_hash_PawnTranspositionTable.h"

#include "../prophet-jni.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/hash.h>
#include <prophet/position.h>

#include <stdint.h>

extern hash_table_t phtbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_PawnTranspositionTable
 * Method:    storeNative
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_hash_PawnTranspositionTable_storeNative
  (JNIEnv *env, jobject UNUSED(phtable), jstring board_fen, jlong val)
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }
    
    /* set the position according to the FEN */
    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    position_t pos;
    if (!set_pos(&pos, fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set position: %s\n", fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* store the value in the hash table */
    store_hash_entry(&phtbl, pos.pawn_key, (uint64_t)val);

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

}
