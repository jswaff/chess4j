#include <stdbool.h>
#include "Quintet.h"

jclass Quintet = NULL;
jmethodID Quintet_with = NULL;


static volatile bool Quintet_registered = false;

int Quintet_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register org.javatuples.Quintet once. */
    if (Quintet_registered)
    {
        return 0;
    }

    /* register Quintet class */
    tempClassID = (*env)->FindClass(env, "org/javatuples/Quintet");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference to this class */
    Quintet = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Quintet)
        return 1;

    /* we don't need this local reference anymore */
    (*env)->DeleteLocalRef(env, tempClassID);


    /* register with method */
    Quintet_with = (*env)->GetStaticMethodID(
        env, Quintet, "with", "Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/javatuples/Quintet;");
    if (NULL == Quintet_with)
        return 1;

    /* success */
    Quintet_registered = true;

    return 0;
}

