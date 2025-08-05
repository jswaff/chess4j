#include "dev_jamesswafford_chess4j_board_Draw.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"
#include "java/lang/Long.h"
#include "java/util/ArrayList.h"

#include <prophet/const.h>
#include <prophet/move.h>
#include <prophet/movegen.h>
#include <prophet/position.h>

#include <stdbool.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>

/*
 * Class:     dev_jamesswafford_chess4j_board_Draw
 * Method:    isDrawByRepNative
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/util/List;I)Z
 */
JNIEXPORT jboolean JNICALL Java_dev_jamesswafford_chess4j_board_Draw_isDrawByRepNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jstring non_reversible_board_fen, jobject jmoves, jint num_prev)
{
    jboolean retval = false;

    /* ensure the static library is initialized */
    if (!prophet_initialized)  {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return false;
    }

    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    const char* non_reversible_fen = (*env)->GetStringUTFChars(env, non_reversible_board_fen, 0);

    /* set the current position according to the FEN */
    position_t pos;
    if (!set_pos(&pos, fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set current position: %s\n", fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* set the last non-reversible position according to the FEN */
    position_t non_reversible_pos;
    if (!set_pos(&non_reversible_pos, non_reversible_fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set last non-reversible position: %s\n", non_reversible_fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* set up the undo stack with hash keys of reversible moves */
    undo_t undos[MAX_HALF_MOVES_PER_GAME];
    memset(&undos, 0, sizeof(undo_t));
    jint n_moves = (*env)->CallIntMethod(env, jmoves, ArrayList_size);
    jint offset = n_moves - pos.fifty_counter;
    int num_matches = 0;
    for (uint32_t i=0;i<pos.fifty_counter;i++) {
        jobject jmove_obj = (*env)->CallObjectMethod(env, jmoves, ArrayList_get, offset + i);
        jlong jmove = (*env)->CallLongMethod(env, jmove_obj, Long_longValue);
        move_t mv = (move_t)jmove;
        /* this could be an assert, but this method is only used for debugging anyway */
        if (!is_legal_move(mv, &non_reversible_pos)) {
            char error_buffer[255];
            sprintf(error_buffer, "Illegal move %d: %s\n", i, move_to_str(mv));
            (*env)->ThrowNew(env, IllegalStateException, error_buffer);
            goto cleanup;
        }
        if (non_reversible_pos.hash_key == pos.hash_key) num_matches++;
        apply_move(&non_reversible_pos, mv, &undos[pos.move_counter - pos.fifty_counter + i]);
    }

    /* evaluate for repetition */
    bool rep = is_draw_rep(&pos, undos, (int)num_prev);
    retval = (jboolean)rep;

    /* verify indexing into undos is correct */
    if (rep != (num_matches >= num_prev)) {
        char error_buffer[255];
        sprintf(error_buffer, "Draw by rep error: rep=%d, num_matches=%d, num_prev=%d\n", rep, num_matches, num_prev);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
    }

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);
    (*env)->ReleaseStringUTFChars(env, non_reversible_board_fen, non_reversible_fen);

    return retval;
}
