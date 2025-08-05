#include "dev_jamesswafford_chess4j_nn_NeuralNetwork.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/nn.h>

#include <stdbool.h>
#include <stdint.h>

extern neural_network_t neural_network;
extern bool use_neural_network;


/*
 * Class:     dev_jamesswafford_chess4j_nn_NeuralNetwork
 * Method:    loadNeuralNetworkNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_nn_NeuralNetwork_loadNeuralNetworkNative
  (JNIEnv *env, jobject nnObj)
{
    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }

    jclass nn_objClass = (*env)->GetObjectClass(env, nnObj);

    /* load W0 */
    jfieldID W0_fid = (*env)->GetFieldID(env, nn_objClass, "W0", "[I");
    jintArray W0_ArrayObj = (jintArray)(*env)->GetObjectField(env, nnObj, W0_fid);
    jsize W0_len = (*env)->GetArrayLength(env, W0_ArrayObj);
    jint* W0 = (*env)->GetIntArrayElements(env, W0_ArrayObj, 0);
    if (!W0) {
        (*env)->ThrowNew(env, IllegalStateException, "Could not reference W0 in NeuralNetwork");
        return;
    }
    for (int i=0;i<W0_len;i++) {
        neural_network.W0[i] = (int8_t)W0[i];
    }

    /* load B0 */
    jfieldID B0_fid = (*env)->GetFieldID(env, nn_objClass, "B0", "[I");
    jintArray B0_ArrayObj = (jintArray)(*env)->GetObjectField(env, nnObj, B0_fid);
    jsize B0_len = (*env)->GetArrayLength(env, B0_ArrayObj);
    jint* B0 = (*env)->GetIntArrayElements(env, B0_ArrayObj, 0);
    if (!B0) {
        (*env)->ThrowNew(env, IllegalStateException, "Could not reference B0 in NeuralNetwork");
        return;
    }
    for (int i=0;i<B0_len;i++) {
        neural_network.B0[i] = (int8_t)B0[i];
    }

    /* load W1 */
    jfieldID W1_fid = (*env)->GetFieldID(env, nn_objClass, "W1", "[I");
    jintArray W1_ArrayObj = (jintArray)(*env)->GetObjectField(env, nnObj, W1_fid);
    jsize W1_len = (*env)->GetArrayLength(env, W1_ArrayObj);
    jint* W1 = (*env)->GetIntArrayElements(env, W1_ArrayObj, 0);
    if (!W1) {
        (*env)->ThrowNew(env, IllegalStateException, "Could not reference W1 in NeuralNetwork");
        return;
    }
    for (int i=0;i<W1_len;i++) {
        neural_network.W1[i] = (int8_t)W1[i];
    }

    /* load B1 */
    jfieldID B1_fid = (*env)->GetFieldID(env, nn_objClass, "B1", "[I");
    jintArray B1_ArrayObj = (jintArray)(*env)->GetObjectField(env, nnObj, B1_fid);
    jsize B1_len = (*env)->GetArrayLength(env, B1_ArrayObj);
    jint* B1 = (*env)->GetIntArrayElements(env, B1_ArrayObj, 0);
    if (!B1) {
        (*env)->ThrowNew(env, IllegalStateException, "Could not reference B1 in NeuralNetwork");
        return;
    }
    for (int i=0;i<B1_len;i++) {
        neural_network.B1[i] = (int8_t)B1[i];
    }

    /* instruct the native engine to use the neural network */
    use_neural_network = true;

    /* cleanup */
    (*env)->ReleaseIntArrayElements(env, W0_ArrayObj, W0, 0);
    (*env)->ReleaseIntArrayElements(env, B0_ArrayObj, B0, 0);
    (*env)->ReleaseIntArrayElements(env, W1_ArrayObj, W1, 0);
    (*env)->ReleaseIntArrayElements(env, B1_ArrayObj, B1, 0);
}
