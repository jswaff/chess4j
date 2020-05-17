#ifndef LONG_HEADER_GUARD
#define LONG_HEADER_GUARD

#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int Long_register(JNIEnv* env);

extern jclass Long;

extern jmethodID Long_valueOf;

extern jmethodID Long_longValue;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif
