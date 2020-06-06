#ifndef QUINTET_HEADER_GUARD
#define QUINTET_HEADER_GUARD


#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int Quintet_register(JNIEnv* env);

extern jclass Quintet;
extern jmethodID Quintet_with;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif