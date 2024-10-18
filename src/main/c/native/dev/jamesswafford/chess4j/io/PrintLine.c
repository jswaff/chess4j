#include <stdbool.h>
#include "PrintLine.h"

jclass PrintLine = NULL;
jmethodID PrintLine_printNativeLine = NULL;

static volatile bool PrintLine_registered = false;

int PrintLine_register(JNIEnv* env)
{
    jclass tempClassID;

    if (PrintLine_registered)
    {
        return 0;
    }

    /* register PrintLine class */
    tempClassID = (*env)->FindClass(env, 
        "com/jamesswafford/chess4j/io/PrintLine");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference for this class */
    PrintLine = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == PrintLine)
        return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register "printNativeLine" method */
    PrintLine_printNativeLine = (*env)->GetStaticMethodID(env, PrintLine, 
        "printNativeLine", "(ILjava/util/List;ZIJJ)V");
    if (NULL == PrintLine_printNativeLine)
        return 1;

    /* success */
    PrintLine_registered = true;
    return 0;
}
