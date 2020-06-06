#ifndef CONSUMER_HEADER_GUARD
#define CONSUMER_HEADER_GUARD


#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int Consumer_register(JNIEnv* env);

extern jclass Consumer;
extern jmethodID Consumer_accept;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif