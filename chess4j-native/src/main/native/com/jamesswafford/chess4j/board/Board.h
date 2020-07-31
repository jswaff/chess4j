#ifndef BOARD_HEADER_GUARD
#define BOARD_HEADER_GUARD

#include <jni.h>
#include <prophet/position/position.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

/**
 * \brief - convert a chess4j (Java) position into a Prophet4 (C) data 
 * structure.
 *
 * \param env           the JNI environment
 * \param board_obj     the Java position
 * \param pos           pointer to a P4 chess position
 *
 * \return - 0 on success, non-zero on failure
 */
int convert(JNIEnv *env, jobject board_obj, position_t* pos);

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif