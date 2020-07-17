#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_search_SearchIteratorImpl.h>
#include "../init/p4_init.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/Long.h"

#include <stdlib.h>
#include <string.h>


/* move stack */
move_t moves[MAX_PLY * MAX_MOVES_PER_PLY];

/* undo stack */
undo_t undos[MAX_PLY];

/*
 * Class:     com_jamesswafford_chess4j_search_SearchIteratorImpl
 * Method:    iterateNative
 * Signature: (Ljava/lang/String;Ljava/util/List;ILjava/util/List;)V
 */
JNIEXPORT void 
JNICALL Java_com_jamesswafford_chess4j_search_SearchIteratorImpl_iterateNative
  (JNIEnv *env, jobject UNUSED(iterator_obj), jstring board_fen, 
    jobject prev_moves, jint max_depth, jobject pv_moves)
{

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Prophet4 not initialized!");
        return;
    }


    /* set the position according to the FEN.  We use the FEN instead of the
     * prev_moves list for test suites, which don't have a move history.
     */
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

    /* replay the previous moves for draw checks */
    position_t replay_pos;
    reset_pos(&replay_pos);    
    jint size = (*env)->CallIntMethod(env, prev_moves, ArrayList_size);
    jvalue arg;
    for (int i=0; i<size; i++)
    {
        arg.i = i;
        jobject element = (*env)->CallObjectMethodA(env, prev_moves, 
            ArrayList_get, &arg);
        jlong prev_mv = (*env)->CallLongMethod(env, element, Long_longValue);

        /* apply this move */
        apply_move(&replay_pos, (move_t) prev_mv, undos + replay_pos.move_counter);

        (*env)->DeleteLocalRef(env, element);
    }

    /* call the search iterator */
    iterator_options_t opts;
    opts.early_exit_ok = true;
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
    for (int i=0; i < pv.n; i++)
    {
        /* create Long value representing this move */
        jobject lval = (*env)->CallStaticObjectMethod(
            env, Long, Long_valueOf, (jlong)(pv.mv[i]));

        /* add to java list */
        (*env)->CallBooleanMethod(env, pv_moves, ArrayList_add, lval);
        (*env)->DeleteLocalRef(env, lval);
    }

    /* free resources */
cleanup:
    (*env)->ReleaseStringUTFChars(env, board_fen, fen);

}
