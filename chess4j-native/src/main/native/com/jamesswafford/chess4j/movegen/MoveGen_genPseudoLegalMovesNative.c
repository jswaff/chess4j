#include <prophet/const.h>
#include <prophet/movegen.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_movegen_MoveGen.h>
#include "../init/p4_init.h"
#include "../../../../java/lang/Long.h"

#include <stdlib.h>
#include <string.h>

/*
 * Class:     com_jamesswafford_chess4j_movegen_MoveGen
 * Method:    genPseudoLegalMovesNative
 * Signature: (Ljava/lang/String;Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_jamesswafford_chess4j_movegen_MoveGen_genPseudoLegalMovesNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jobject UNUSED(jmoves))
{
    jint retval = 0;

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return 0;
    }

    /* set the position according to the FEN */
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

    /* generate moves */
    move_t moves[MAX_MOVES_PER_PLY];
    move_t* endp = gen_pseudo_legal_moves(moves, &pos, true, true);


    /* add the moves to the java list */
    int num_moves = 0;
    for (const move_t* mp=moves; mp<endp; mp++) 
    {
        /* create Long value representing this move */
        jobject lval = (*env)->CallStaticObjectMethod(
            env, Long, Long_valueOf, (jlong)*mp);

        /* TODO - add to java list */

        ++num_moves;
    }

    retval = num_moves;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);


    return retval;
}
