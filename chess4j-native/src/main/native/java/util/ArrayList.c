#include "ArrayList.h"

#include <stdbool.h>

jclass ArrayList = NULL;
jmethodID ArrayList_init = NULL;
jmethodID ArrayList_add = NULL;
jmethodID ArrayList_clear = NULL;
jmethodID ArrayList_get = NULL;
jmethodID ArrayList_size = NULL;


static volatile bool ArrayList_registered = false;

int ArrayList_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register java.util.ArrayList once. */
    if (ArrayList_registered) return 0;

    /* register ArrayList class */
    tempClassID = (*env)->FindClass(env, "java/util/ArrayList");
    if (NULL == tempClassID) return 1;

    /* create a global reference to this class */
    ArrayList = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == ArrayList) return 1;

    /* we don't need this local reference anymore */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register init() method */
    ArrayList_init = (*env)->GetMethodID(env, ArrayList, "<init>", "()V");
    if (NULL == ArrayList_init) return 1;

    /* register add method */
    ArrayList_add = (*env)->GetMethodID(env, ArrayList, "add", "(Ljava/lang/Object;)Z");
    if (NULL == ArrayList_add) return 1;

    /* register clear method */
    ArrayList_clear = (*env)->GetMethodID(env, ArrayList, "clear", "()V");
    if (NULL == ArrayList_clear) return 1;


    /* register get method */
    ArrayList_get = (*env)->GetMethodID(env, ArrayList, "get", "(I)Ljava/lang/Object;");
    if (NULL == ArrayList_get) return 1;

    /* register size method */
    ArrayList_size = (*env)->GetMethodID(env, ArrayList, "size", "()I");
    if (NULL == ArrayList_size) return 1;


    /* success */
    ArrayList_registered = true;

    return 0;
}

