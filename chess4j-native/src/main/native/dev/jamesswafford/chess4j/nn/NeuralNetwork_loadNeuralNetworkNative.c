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
    for (int i=0;i<(768 * NN_SIZE_L1);i++) {
        neural_network.W0[i] = (int8_t)1;
    }

    for (int i=0;i<NN_SIZE_L1;i++) {
        neural_network.B0[i] = (int8_t)1;
    }

    for (int i=0;i<(NN_SIZE_L1 * 2 * NN_SIZE_L2);i++) {
        neural_network.W1[i] = (int8_t)1;
    }

    for (int i=0;i<NN_SIZE_L2;i++) {
        neural_network.B1[i] = (int8_t)1;
    }


    use_neural_network = true;
}
