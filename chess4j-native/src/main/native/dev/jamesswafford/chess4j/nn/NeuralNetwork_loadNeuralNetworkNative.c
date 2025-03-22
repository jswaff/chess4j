#include "dev_jamesswafford_chess4j_nn_NeuralNetwork.h"

#include "dev/jamesswafford/chess4j/prophet-jni.h"
#include "java/lang/IllegalStateException.h"

#include <prophet/position.h>

#include <stdbool.h>

//extern neural_network_t neural_network;
extern bool use_neural_network;



/*
 * Class:     dev_jamesswafford_chess4j_nn_NeuralNetwork
 * Method:    loadNeuralNetworkNative
 * Signature: (Ldev/jamesswafford/chess4j/nn/NeuralNetwork;)V
 */
JNIEXPORT void JNICALL Java_dev_jamesswafford_chess4j_nn_NeuralNetwork_loadNeuralNetworkNative
  (JNIEnv *env, jclass UNUSED(clazz), jobject UNUSED(obj))
{

    /* ensure the static library is initialized */
    if (!prophet_initialized) {
        (*env)->ThrowNew(env, IllegalStateException, "Prophet not initialized!");
        return;
    }

    /* load neural network */

    use_neural_network = true;
}
