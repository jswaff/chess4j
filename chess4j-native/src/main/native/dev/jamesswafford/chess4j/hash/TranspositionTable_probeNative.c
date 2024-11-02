#include "dev_jamesswafford_chess4j_hash_TranspositionTable.h"

#include "../prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/hash.h>
#include <prophet/position.h>

extern hash_table_t htbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_TranspositionTable
 * Method:    probeNative
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_dev_jamesswafford_chess4j_hash_TranspositionTable_probeNative
  (JNIEnv *env, jobject UNUSED(htable), jstring board_fen)
{
    jlong retval = 0;

    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
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

    /* probe the table */
    uint64_t val = probe_hash(&htbl, pos.hash_key);
    retval = (jlong) val;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
