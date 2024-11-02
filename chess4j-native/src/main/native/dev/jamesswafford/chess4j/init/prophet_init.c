#include "dev_jamesswafford_chess4j_init_Initializer.h"

#include "../prophet-jni.h"

#include "../io/PrintLine.h"
#include "java/lang/IllegalStateException.h"
#include "java/lang/Long.h"
#include "java/util/ArrayList.h"

#include <stdbool.h>

extern bool logging_enabled;
extern int init();

volatile bool prophet_initialized = false;

/*
 * Class:     dev_jamesswafford_chess4j_init_Initializer
 * Method:    prophetInit
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_dev_jamesswafford_chess4j_init_Initializer_prophetInit
  (JNIEnv *env, jclass UNUSED(clazz))
{
    logging_enabled = false;
    init();

    if (0 != IllegalStateException_register(env)) {
        /* well this is bad */
        return false;
    }

    if (0 != Long_register(env)) {
        (*env)->ThrowNew(env, IllegalStateException, "Long not initialized!");
        return false;
    }

    if (0 != ArrayList_register(env)) {
        (*env)->ThrowNew(env, IllegalStateException, "ArrayList not initialized!");
        return false;
    }

    if (0 != PrintLine_register(env)) {
        (*env)->ThrowNew(env, IllegalStateException, "PrintLine not initialized!");
        return false;
    }

    prophet_initialized = true;

    return true;
}
