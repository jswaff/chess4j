#pragma once

#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int IllegalStateException_register(JNIEnv* env);

extern jclass IllegalStateException;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/
