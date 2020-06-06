#include <prophet/parameters.h>

#include <stdbool.h>

#include <com_jamesswafford_chess4j_init_Initializer.h>

#include "../../../../java/lang/Long.h"
#include "../../../../java/util/ArrayList.h"
#include "../../../../java/util/function/Consumer.h"
#include "../../../../org/javatuples/Quintet.h"


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

    if (0 != Consumer_register(env))
    {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            "Consumer not initialized!");
        return false;
    }

    int retval = Quintet_register(env);
    if (0 != retval)
    {
        char error_buffer[255];
        sprintf(error_buffer, "Quintet not initialized! - retval: %d\n", retval);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalStateException"), 
            error_buffer);
        return false;
    }

    p4_initialized = true;

    return true;
}
