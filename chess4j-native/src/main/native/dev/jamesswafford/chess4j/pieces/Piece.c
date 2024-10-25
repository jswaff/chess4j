#include "Piece.h"

#include <stdbool.h>

jclass Piece = NULL;
jclass Bishop = NULL;
jclass King = NULL;
jclass Knight = NULL;
jclass Pawn = NULL;
jclass Queen = NULL;
jclass Rook = NULL;
jmethodID Piece_isWhite = NULL;

static volatile bool Piece_registered = false;

int Piece_register(JNIEnv* env)
{
    jclass tempClassID;

    if (Piece_registered) return 0;

    /* register Piece class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Piece");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Piece = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Piece) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register the isWhite method */
    Piece_isWhite = (*env)->GetMethodID(env, Piece, "isWhite", "()Z");
    if (NULL == Piece_isWhite) return 1;

    /* register Bishop class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Bishop");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Bishop = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Bishop) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register King class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/King");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    King = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == King) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register Knight class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Knight");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Knight = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Knight) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register Pawn class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Pawn");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Pawn = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Pawn) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register Queen class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Queen");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Queen = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Queen) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* register Rook class */
    tempClassID = (*env)->FindClass(env, 
        "dev/jamesswafford/chess4j/pieces/Rook");
    if (NULL == tempClassID) return 1;

    /* create a global reference for this class */
    Rook = (jclass)(*env)->NewGlobalRef(env, tempClassID);
    if (NULL == Rook) return 1;

    /* we don't need this local reference anymore. */
    (*env)->DeleteLocalRef(env, tempClassID);

    /* success */
    Piece_registered = true;
    return 0;
}

