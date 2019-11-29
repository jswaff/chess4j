#include <prophet/eval.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>

/*
 * Class:     com_jamesswafford_chess4j_eval_Eval
 * Method:    evalNative
 * Signature: (Lcom/jamesswafford/chess4j/board/Board;)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_eval_Eval_evalNative
  (JNIEnv* UNUSED(env), jclass UNUSED(clazz), jobject UNUSED(board))
{
    jint retval = 0;


    position_t pos;
    set_pos(&pos, "8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - 12 47");

    // int32_t native_score = eval(&pos);
    // retval = (jint) native_score;

    return retval;
}
