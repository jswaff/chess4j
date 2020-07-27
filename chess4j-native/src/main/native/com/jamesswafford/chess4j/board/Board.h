#ifndef BOARD_HEADER_GUARD
#define BOARD_HEADER_GUARD

#include <jni.h>
#include <prophet/position/position.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int convert(JNIEnv *env, jobject board_obj, position_t* pos);

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif