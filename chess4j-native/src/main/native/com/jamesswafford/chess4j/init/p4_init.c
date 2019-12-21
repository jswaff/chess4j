#include <prophet/parameters.h>

#include <stdbool.h>

#include <com_jamesswafford_chess4j_init_Initializer.h>

extern int init();

volatile bool p4_initialized = false;

/*
 * Class:     com_jamesswafford_chess4j_init_Initializer
 * Method:    p4Init
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_jamesswafford_chess4j_init_Initializer_p4Init
  (JNIEnv* UNUSED(env), jclass UNUSED(clazz))
{
    init();

    p4_initialized = true;

    return true;
}
