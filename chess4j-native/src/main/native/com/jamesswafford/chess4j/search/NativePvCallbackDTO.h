#ifndef NATIVE_PV_CALLBACK_DTO_HEADER_GUARD
#define NATIVE_PV_CALLBACK_DTO_HEADER_GUARD


#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int NativePvCallbackDTO_register(JNIEnv* env);

extern jclass NativePvCallbackDTO;
extern jmethodID NativePvCallbackDTO_with;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif