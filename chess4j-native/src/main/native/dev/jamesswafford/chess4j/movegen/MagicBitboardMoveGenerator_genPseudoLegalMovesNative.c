#include "dev_jamesswafford_chess4j_movegen_MagicBitboardMoveGenerator.h"

#include "../prophet-jni.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/IllegalStateException.h"
#include "../../../../java/lang/Long.h"

#include <prophet/const.h>
#include <prophet/movegen.h>
#include <prophet/position.h>

#include <stdio.h>

/*
 * Class:     dev_jamesswafford_chess4j_movegen_MagicBitboardMoveGenerator
 * Method:    genPseudoLegalMovesNative
 * Signature: (Ljava/lang/String;Ljava/util/List;ZZ)I
 */
JNIEXPORT jint 
JNICALL Java_dev_jamesswafford_chess4j_movegen_MagicBitboardMoveGenerator_genPseudoLegalMovesNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jobject jmoves, jboolean caps, jboolean noncaps)
{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!prophet_initialized)  {
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

    /* generate moves */
    move_t moves[MAX_MOVES_PER_PLY];
    move_t* endp = gen_pseudo_legal_moves(moves, &pos, caps, noncaps);

    /* add the moves to the java list */
    int num_moves = 0;
    for (const move_t* mp=moves; mp<endp; mp++)  {
        /* create Long value representing this move */
        jobject lval = (*env)->CallStaticObjectMethod(env, Long, Long_valueOf, (jlong)*mp);

        /* add to java list */
        (*env)->CallBooleanMethod(env, jmoves, ArrayList_add, lval);
        (*env)->DeleteLocalRef(env, lval);

        ++num_moves;
    }

    retval = num_moves;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

    return retval;
}
