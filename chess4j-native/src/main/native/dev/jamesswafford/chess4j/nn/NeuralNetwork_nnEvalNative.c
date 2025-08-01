#include "dev_jamesswafford_chess4j_nn_NeuralNetwork.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/nn.h>
#include <prophet/position.h>

#include <stdio.h>
#include <stdint.h>

extern neural_network_t neural_network;

/*
 * Class:     dev_jamesswafford_chess4j_nn_NeuralNetwork
 * Method:    nnEvalNative
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_dev_jamesswafford_chess4j_nn_NeuralNetwork_nnEvalNative
  (JNIEnv *env, jobject UNUSED(obj), jstring board_fen)
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

    int32_t native_score = nn_eval(&pos, &neural_network);
    retval = (jint) native_score;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
