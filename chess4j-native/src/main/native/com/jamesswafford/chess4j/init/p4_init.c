#include <prophet/parameters.h>

#include <stdbool.h>

#include <com_jamesswafford_chess4j_init_Initializer.h>

#include "../../../../java/lang/Long.h"
#include "../../../../java/util/ArrayList.h"


extern int init();

volatile bool p4_initialized = false;

/*
 * Class:     com_jamesswafford_chess4j_init_Initializer
 * Method:    p4Init
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_jamesswafford_chess4j_init_Initializer_p4Init
  (JNIEnv* env, jclass UNUSED(clazz))
{
    init();

    if (0 != Long_register(env))
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Long not initialized!");
        return false;
    }

    if (0 != ArrayList_register(env))
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "ArrayList not initialized!");
        return false;
    }

    p4_initialized = true;

    return true;
}
