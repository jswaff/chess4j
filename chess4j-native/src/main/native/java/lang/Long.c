#include <stdbool.h>
#include "Long.h"

jclass Long = NULL;
jmethodID Long_valueOf = NULL;
jmethodID Long_longValue = NULL;

static volatile bool Long_registered = false;

int Long_register(JNIEnv* env)
{
    jclass tempClassID;

    /* only register java.lang.Long once. */
    if (Long_registered) return 0;

    /* register Long class */
    tempClassID = (*env)->FindClass(env, "java/lang/Long");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Long = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Long) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register valueOf method */
    Long_valueOf = (*env)->GetStaticMethodID(
        env, Long, "valueOf", "(J)Ljava/lang/Long;");
    if (NULL == Long_valueOf) return 1;

    /* register longValue method */
    Long_longValue = (*env)->GetMethodID(env, Long, "longValue", "()J");
    if (NULL == Long_longValue) return 1;

    /* success */
    Long_registered = true;
    return 0;
}
