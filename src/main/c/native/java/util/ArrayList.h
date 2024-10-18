#ifndef ARRAYLIST_HEADER_GUARD
#define ARRAYLIST_HEADER_GUARD


#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int ArrayList_register(JNIEnv* env);

extern jclass ArrayList;
extern jmethodID ArrayList_init;
extern jmethodID ArrayList_add;
extern jmethodID ArrayList_clear;
extern jmethodID ArrayList_get;
extern jmethodID ArrayList_size;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif
