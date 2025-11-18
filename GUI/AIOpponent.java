package GUI;

import logic.Board;
import objects.PieceColor;

/**
 * Minimal AI interface for choosing a move.
 * Return an array {startRow, startCol, endRow, endCol}, or null if no move.
 */
public interface AIOpponent {
    int[] chooseMove(Board board, PieceColor aiColor);
}
