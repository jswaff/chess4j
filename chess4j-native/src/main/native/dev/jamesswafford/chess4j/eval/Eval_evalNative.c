#include <dev_jamesswafford_chess4j_eval_Eval.h>

#include "../prophet-jni.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <prophet/eval.h>
#include <prophet/position.h>

#include <stdbool.h>
#include <stdio.h>
#include <stdint.h>

/*
 * Class:     dev_jamesswafford_chess4j_eval_Eval
 * Method:    evalNative
 * Signature: (Ljava/lang/String;Z)I
 */
JNIEXPORT jint JNICALL Java_dev_jamesswafford_chess4j_eval_Eval_evalNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jboolean material_only)
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

    int32_t native_score = eval(&pos, (bool)material_only, false);
    retval = (jint) native_score;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
