#include <stdbool.h>

#include "IllegalStateException.h"

jclass IllegalStateException;


static volatile bool IllegalStateException_registered = false;

int IllegalStateException_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register java.lang.IllegalStateException once. */
    if (IllegalStateException_registered)
    {
        return 0;
    }

    /* register Long class */
    tempClassID = (*env)->FindClass(env, "java/lang/IllegalStateException");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference for this class */
    IllegalStateException = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == IllegalStateException)
        return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);


    /* success */
    IllegalStateException_registered = true;
    return 0;
}
