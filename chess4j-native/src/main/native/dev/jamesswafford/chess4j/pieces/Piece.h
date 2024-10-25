#pragma once

#include <jni.h>

/* make this header C++ friendly */
#ifdef __cplusplus
extern "C" {
#endif /*__cplusplus*/

int Piece_register(JNIEnv* env);

extern jclass Piece;
extern jmethodID Piece_isWhite;

extern jclass Bishop;
extern jclass King;
extern jclass Knight;
extern jclass Pawn;
extern jclass Queen;
extern jclass Rook;

/* make this header C++ friendly */
#ifdef __cplusplus
}
#endif /*__cplusplus*/
