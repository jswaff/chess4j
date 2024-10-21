#include "Board.h"

#include "../pieces/Piece.h"

#include <string.h>

/* TODO: these methods are marked "internal" */
void add_piece(position_t* p, int32_t piece, square_t sq);
bool verify_pos(const position_t* pos);
uint64_t build_hash_key(const position_t* pos);
uint64_t build_pawn_key(const position_t* pos);

/**
 * \brief - convert a chess4j (Java) position into a Prophet4 (C) data 
 * structure.
 *
 * \param env           the JNI environment
 * \param board_obj     the Java position
 * \param pos           pointer to a P4 chess position
 *
 * \return - 0 on success, non-zero on failure
 */
int convert(JNIEnv *env, jobject board_obj, position_t* pos)
{
    memset(pos, 0, sizeof(position_t));

    jclass class_Board = (*env)->GetObjectClass(env, board_obj);

    /* set the player */
    jmethodID Board_getPlayerToMove = (*env)->GetMethodID(
        env, class_Board, "getPlayerToMove", "()Ldev/jamesswafford/chess4j/board/Color;");
    jobject player_obj = (*env)->CallObjectMethod(env, board_obj, Board_getPlayerToMove);
    jclass class_Color = (*env)->GetObjectClass(env, player_obj);
    jmethodID Color_isWhite = (*env)->GetMethodID(env, class_Color, "isWhite", "()Z");
    bool is_white = (*env)->CallBooleanMethod(env, player_obj, Color_isWhite);
    pos->player = is_white ? WHITE : BLACK;

    /* set the EP square */
    jmethodID Board_getEPSquare = (*env)->GetMethodID(
        env, class_Board, "getEPSquare", "()Ldev/jamesswafford/chess4j/board/squares/Square;");
    jobject ep_sq_obj = (*env)->CallObjectMethod(env, board_obj, Board_getEPSquare);
    if (ep_sq_obj == NULL)
    {
        pos->ep_sq = NO_SQUARE;
    }
    else
    {
        jclass class_Square = (*env)->GetObjectClass(env, ep_sq_obj);
        jmethodID Square_value = (*env)->GetMethodID(env, class_Square, "value", "()I");
        pos->ep_sq = (*env)->CallIntMethod(env, ep_sq_obj, Square_value);
    }

    /* set the castling rights */
    jmethodID Board_hasWKCastlingRight = (*env)->GetMethodID(env, class_Board, "hasWKCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasWKCastlingRight))
    {
        pos->castling_rights |= CASTLE_WK;
    }
    jmethodID Board_hasWQCastlingRight = (*env)->GetMethodID(env, class_Board, "hasWQCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasWQCastlingRight))
    {
        pos->castling_rights |= CASTLE_WQ;
    }
    jmethodID Board_hasBKCastlingRight = (*env)->GetMethodID(env, class_Board, "hasBKCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasBKCastlingRight))
    {
        pos->castling_rights |= CASTLE_BK;
    }
    jmethodID Board_hasBQCastlingRight = (*env)->GetMethodID(env, class_Board, "hasBQCastlingRight", "()Z");
    if ((*env)->CallBooleanMethod(env, board_obj, Board_hasBQCastlingRight))
    {
        pos->castling_rights |= CASTLE_BQ;
    }

    /* set the 50 move counter */
    jmethodID Board_getFiftyCounter = (*env)->GetMethodID(env, class_Board, "getFiftyCounter", "()I");
    pos->fifty_counter = (*env)->CallIntMethod(env, board_obj, Board_getFiftyCounter);

    /* set the full move counter */
    jmethodID Board_getMoveCounter = (*env)->GetMethodID(env, class_Board, "getMoveCounter", "()I");
    pos->move_counter = (*env)->CallIntMethod(env, board_obj, Board_getMoveCounter);


    /* add the pieces */
    jmethodID Board_getPiece = (*env)->GetMethodID(
        env, class_Board, "getPiece", "(I)Ldev/jamesswafford/chess4j/pieces/Piece;");
    for (int i=0;i<64;i++)
    {
        jobject piece_obj = (*env)->CallObjectMethod(env, board_obj, Board_getPiece, i);
        if (piece_obj != NULL)
        {
            bool is_white = (*env)->CallBooleanMethod(env, piece_obj, Piece_isWhite);
            if ((*env)->IsInstanceOf(env, piece_obj, Bishop))
            {
                add_piece(pos, is_white ? BISHOP : -BISHOP, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, King))
            {
                add_piece(pos, is_white ? KING : -KING, i);
                if (is_white)
                {
                    pos->white_king = i;
                }
                else
                {
                    pos->black_king = i;
                }
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Knight))
            {
                add_piece(pos, is_white ? KNIGHT : -KNIGHT, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Pawn))
            {
                add_piece(pos, is_white ? PAWN : -PAWN, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Queen))
            {
                add_piece(pos, is_white ? QUEEN : -QUEEN, i);
            }
            else if ((*env)->IsInstanceOf(env, piece_obj, Rook))
            {
                add_piece(pos, is_white ? ROOK : -ROOK, i);
            }
        }
    }

    /* set the hash keys */
    pos->hash_key = build_hash_key(pos);
    pos->pawn_key = build_pawn_key(pos);

    /* verify the position */
    if (!verify_pos(pos))
    {
        return 1;
    }


    /* success */
    return 0;
}