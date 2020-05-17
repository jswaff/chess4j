#include <stdbool.h>
#include "ArrayList.h"

jclass ArrayList = NULL;
jmethodID ArrayList_init = NULL;
jmethodID ArrayList_add = NULL;


static volatile bool ArrayList_registered = false;

int ArrayList_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register java.util.ArrayList once. */
    if (ArrayList_registered)
    {
        return 0;
    }

    /* register ArrayList class */
    tempClassID = (*env)->FindClass(env, "java/util/ArrayList");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference to this class */
    ArrayList = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == ArrayList)
        return 1;

    /* we don't need this local reference anymore */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register init() method */
    ArrayList_init =
        (*env)->GetMethodID(
            env, ArrayList, "<init>", "()V");
    if (NULL == ArrayList_init)
        return 1;

    /* register add method */
    ArrayList_add =
        (*env)->GetMethodID(
            env, ArrayList, "add", "(Ljava/lang/Object;)Z");
    if (NULL == ArrayList_add)
        return 1;

    /* success */
    ArrayList_registered = true;

    return 0;
}

