#include "dev_jamesswafford_chess4j_search_SEE.h"

#include "../prophet-jni.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/search.h>

/*
 * Class:     dev_jamesswafford_chess4j_search_SEE
 * Method:    seeNative
 * Signature: (Ljava/lang/String;J)I
 */
JNIEXPORT jint JNICALL Java_dev_jamesswafford_chess4j_search_SEE_seeNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jlong mv)
{
    jint retval = 0;

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

    int32_t native_score = see(&pos, (move_t)mv);
    retval = (jint) native_score;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
