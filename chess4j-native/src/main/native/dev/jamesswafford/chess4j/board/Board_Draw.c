#include "dev_jamesswafford_chess4j_board_Draw.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"
#include "java/lang/Long.h"
#include "java/util/ArrayList.h"

#include <prophet/const.h>
#include <prophet/position.h>

#include <stdbool.h>
#include <stdio.h>
#include <stdint.h>

// TODO: internal method!
bool is_draw_rep(const position_t* pos, const undo_t* u, int prev_reps);

/*
 * Class:     dev_jamesswafford_chess4j_board_Draw
 * Method:    isDrawByRepNative
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/util/List;I)Z
 */
JNIEXPORT jboolean JNICALL Java_dev_jamesswafford_chess4j_board_Draw_isDrawByRepNative
  (JNIEnv *env, jclass UNUSED(clazz), jstring board_fen, jstring orig_board_fen, jobject jmoves, jint num_prev)
{
    jboolean retval = false;

    /* ensure the static library is initialized */
    if (!prophet_initialized)  {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return false;
    }

    const char* fen = (*env)->GetStringUTFChars(env, board_fen, 0);
    const char* orig_fen = (*env)->GetStringUTFChars(env, orig_board_fen, 0);

    /* set the current position according to the FEN */
    position_t pos;
    if (!set_pos(&pos, fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set current position: %s\n", fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* set the original position according to the FEN */
    position_t orig_pos;
    if (!set_pos(&orig_pos, orig_fen)) {
        char error_buffer[255];
        sprintf(error_buffer, "Could not set original position: %s\n", orig_fen);
        (*env)->ThrowNew(env, IllegalStateException, error_buffer);
        goto cleanup;
    }

    /* undo stack */
    undo_t undos[MAX_HALF_MOVES_PER_GAME];
    jint n_undos = (*env)->CallIntMethod(env, jmoves, ArrayList_size);
    for (int i=0;i<n_undos;i++) {
        jobject jmove_obj = (*env)->CallObjectMethod(env, jmoves, ArrayList_get, i);
        jlong jmove = (*env)->CallLongMethod(env, jmove_obj, Long_longValue);
        apply_move(&orig_pos, (move_t)jmove, &undos[i]);
    }

    bool rep = is_draw_rep(&pos, undos, (int)num_prev);
    retval = (jboolean)rep;

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);
    (*env)->ReleaseStringUTFChars(env, orig_board_fen, orig_fen);

    return retval;
}
