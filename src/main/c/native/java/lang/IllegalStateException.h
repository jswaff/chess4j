#ifndef ILLEGAL_STATE_EXCEPTION_HEADER_GUARD
#define ILLEGAL_STATE_EXCEPTION_HEADER_GUARD

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


#endif
