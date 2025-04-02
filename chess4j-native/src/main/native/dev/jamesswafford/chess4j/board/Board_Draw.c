#include "dev_jamesswafford_chess4j_board_Draw.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/position.h>

#include <stdbool.h>
#include <stdio.h>
#include <stdint.h>

/*
 * Class:     dev_jamesswafford_chess4j_board_Draw
 * Method:    isDrawByRepNative
 * Signature: (Ljava/lang/String;Ljava/util/List;I)Z
 */
JNIEXPORT jboolean JNICALL Java_dev_jamesswafford_chess4j_board_Draw_isDrawByRepNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jobject UNUSED(jundos), jint UNUSED(num_prev))
{
    jboolean retval = false;

    /* ensure the static library is initialized */
    if (!prophet_initialized)  {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return false;
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

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
