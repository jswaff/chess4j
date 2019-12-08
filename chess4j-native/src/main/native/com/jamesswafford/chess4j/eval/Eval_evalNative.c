#include <prophet/eval.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>
#include "../init/p4_init.h"

#include <stdlib.h>
#include <string.h>


/*
 * Class:     com_jamesswafford_chess4j_eval_Eval
 * Method:    evalNative
 * Signature: (Ljava/lang/String;Z)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_eval_Eval_evalNative
  (JNIEnv* env, jclass UNUSED(clazz), jstring board_fen, jboolean material_only)
{
    jint retval = 0;

    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }

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


    int32_t native_score = eval(&pos, (bool)material_only);
    retval = (jint) native_score;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
