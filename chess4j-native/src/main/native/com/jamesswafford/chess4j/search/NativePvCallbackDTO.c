#include <stdbool.h>
#include "NativePvCallbackDTO.h"

jclass NativePvCallbackDTO = NULL;
jmethodID NativePvCallbackDTO_with = NULL;

static volatile bool NativePvCallbackDTO_registered = false;

int NativePvCallbackDTO_register(JNIEnv* env)
{
    jclass tempClassID;

    if (NativePvCallbackDTO_registered)
    {
        return 0;
    }

    /* register Long class */
    tempClassID = (*env)->FindClass(env, 
        "com/jamesswafford/chess4j/search/NativePvCallbackDTO");
    if (NULL == tempClassID)
        return 1;

    /* create a global reference for this class */
    NativePvCallbackDTO = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == NativePvCallbackDTO)
        return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register "with" method */
    NativePvCallbackDTO_with = (*env)->GetStaticMethodID(env, NativePvCallbackDTO, 
        "with", "(ILjava/util/List;IIJ)Lcom/jamesswafford/chess4j/search/NativePvCallbackDTO;");
    if (NULL == NativePvCallbackDTO_with)
        return 1;

    /* success */
    NativePvCallbackDTO_registered = true;
    return 0;
}
