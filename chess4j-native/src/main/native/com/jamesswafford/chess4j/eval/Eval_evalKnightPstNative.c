#include <prophet/eval.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_eval_Eval.h>

/*
 * Class:     com_jamesswafford_chess4j_eval_Eval
 * Method:    evalKnightPstNative
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_eval_Eval_evalKnightPstNative
  (JNIEnv* UNUSED(env), jclass UNUSED(clazz), jint sq)
{
    return eval_knight_pst(sq);
}
