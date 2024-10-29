#pragma once

#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int PrintLine_register(JNIEnv* env);

extern jclass PrintLine;
extern jmethodID PrintLine_printNativeLine;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/
