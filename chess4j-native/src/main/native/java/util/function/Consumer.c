#include <stdbool.h>
#include "Consumer.h"

jclass Consumer = NULL;
jmethodID Consumer_accept = NULL;

static volatile bool Consumer_registered = false;

int Consumer_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register java.util.function.Consumer once. */
    if (Consumer_registered)
    {
        return 0;
    }

    /* register Long class */
    tempClassID = (*env)->FindClass(env, "java/util/function/Consumer");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference for this class */
    Consumer = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Consumer)
        return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register accept method */
    Consumer_accept = (*env)->GetMethodID(env, Consumer, "accept", "(Ljava/lang/Object;)V");
    if (NULL == Consumer_accept)
        return 1;

    /* success */
    Consumer_registered = true;
    return 0;
}
