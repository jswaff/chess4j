#include "dev_jamesswafford_chess4j_search_SearchIteratorImpl.h"

#include "../prophet-jni.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/IllegalStateException.h"
#include "../../../../java/lang/Long.h"

#include <prophet/const.h>
#include <prophet/search.h>

/* move stack */
move_t moves[MAX_PLY * MAX_MOVES_PER_PLY];

/* undo stack */
undo_t undos[MAX_HALF_MOVES_PER_GAME];

/*
 * Class:     dev_jamesswafford_chess4j_search_SearchIteratorImpl
 * Method:    iterateNative
 * Signature: (Ljava/lang/String;ILjava/util/List;)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_search_SearchIteratorImpl_iterateNative
  (JNIEnv *env, jobject UNUSED(clazz), jstring board_fen, jint max_depth, jobject pv_moves)
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
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

    /* call the search iterator */
    iterator_options_t opts;
    opts.early_exit_ok = false;
    opts.max_depth = max_depth;
    opts.max_time_ms = 0; /* we only call the iterator for fixed depth testing */
    opts.post_mode = false;
    opts.clear_hash_each_search = true;

    iterator_context_t ctx;
    ctx.pos = &pos;
    ctx.move_stack = moves;
    ctx.undo_stack = undos;

    move_line_t pv = iterate(&opts, &ctx);

    /* copy the PV to the Java list */
    for (int i=0; i < pv.n; i++) {
        /* create Long value representing this move */
        jobject lval = (*env)->CallStaticObjectMethod(
            env, Long, Long_valueOf, (jlong)(pv.mv[i]));

        /* add to java list */
        (*env)->CallBooleanMethod(env, pv_moves, ArrayList_add, lval);
        (*env)->DeleteLocalRef(env, lval);
    }

cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

}
