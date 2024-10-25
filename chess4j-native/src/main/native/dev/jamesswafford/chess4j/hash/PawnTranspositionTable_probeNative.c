#include <prophet/hash.h>
#include <prophet/parameters.h>
#include <prophet/position/position.h>

#include <dev_jamesswafford_chess4j_hash_TranspositionTable.h>
#include "../board/Board.h"
#include "../init/p4_init.h"
#include "../../../../java/lang/IllegalStateException.h"


extern hash_table_t phtbl;

/*
 * Class:     dev_jamesswafford_chess4j_hash_PawnTranspositionTable
 * Method:    probeNative
 * Signature: (Ldev/jamesswafford/chess4j/board/Board;)J
 */
JNIEXPORT jlong JNICALL Java_dev_jamesswafford_chess4j_hash_PawnTranspositionTable_probeNative
  (JNIEnv *env, jobject UNUSED(phtable), jobject board_obj)
{
    jlong retval = 0;

    /* ensure the static library is initialized */
    if (!p4_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return 0;
    }
    
    /* set the position */
    position_t c4j_pos;
    if (0 != convert(env, board_obj, &c4j_pos)) {
        (*env)->ThrowNew(env, IllegalStateException, 
            "An error was encountered while converting a position.");
        return 0;
    }

    /* probe the table */
    uint64_t val = probe_hash(&phtbl, c4j_pos.pawn_key);
    retval = (jlong) val;

    return retval;
}
