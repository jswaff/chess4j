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

/* public boolean add(E);
 * descriptor: (Ljava/lang/Object;)Z
 */
extern jmethodID ArrayList_add;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/


#endif