#include <prophet/const.h>
#include <prophet/search.h>
#include <prophet/parameters.h>

#include <com_jamesswafford_chess4j_search_SearchIteratorImpl.h>
#include "../board/Board.h"
#include "../init/p4_init.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/lang/IllegalStateException.h"
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
 * Signature: (Lcom/jamesswafford/chess4j/board/Board;ILjava/util/List;)V
 */
JNIEXPORT void 
JNICALL Java_com_jamesswafford_chess4j_search_SearchIteratorImpl_iterateNative
  (JNIEnv *env, jobject UNUSED(iterator_obj), jobject board_obj, jint max_depth, jobject pv_moves)
{

    /* ensure the static library is initialized */
    if (!p4_initialized) 
    {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet4 not initialized!");
        return;
    }

    /* set the position */
    position_t c4j_pos;
    if (0 != convert(env, board_obj, &c4j_pos))
    {
        (*env)->ThrowNew(env, IllegalStateException, "An error was encountered while converting a position.");
        return;
    }

    /* call the search iterator */
    iterator_options_t opts;
    opts.early_exit_ok = false;
    opts.max_depth = max_depth;
    opts.max_time_ms = 0; /* we only call the iterator for fixed depth testing */
    opts.post_mode = false;
    opts.clear_hash_each_search = true;

    iterator_context_t ctx;
    ctx.pos = &c4j_pos;
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

}
