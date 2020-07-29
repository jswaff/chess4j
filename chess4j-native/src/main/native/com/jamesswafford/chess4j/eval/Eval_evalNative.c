#include <prophet/eval.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>
#include "../board/Board.h"
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"

#include <stdlib.h>
#include <string.h>

/*
 * Class:     com_jamesswafford_chess4j_eval_Eval
 * Method:    evalNative
 * Signature: (Lcom/jamesswafford/chess4j/board/Board;Z)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_eval_Eval_evalNative
  (JNIEnv* env, jclass UNUSED(clazz), jobject board_obj, jboolean material_only)
{
    jint retval = 0;

    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet4 not initialized!");
        return 0;
    }

    /* set the position */
    position_t c4j_pos;
    if (0 != convert(env, board_obj, &c4j_pos))
    {
        (*env)->ThrowNew(env, IllegalStateException, 
            "An error was encountered while converting a position.");
        return 0;
    }

    int32_t native_score = eval(&c4j_pos, (bool)material_only);
    retval = (jint) native_score;

    return retval;
}
