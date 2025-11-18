package GUI;

import logic.Board;
import objects.PieceColor;

/**
 * Minimal AI strategy interface used by GUI single-player modes.
 *
 * Contract:
 * - Implementations must be pure in terms of game state: do NOT mutate the provided {@link Board}.
 * - Coordinate system uses 0-based rows/cols with row 0 at the top and col 0 at the left.
 * - Return value is an int array of length 4: {startRow, startCol, endRow, endCol}.
 * - Return {@code null} if no legal move is available (e.g., checkmate or stalemate).
 * - Threading: current usage calls this on the EDT; heavy AIs should be run off-EDT.
 */
public interface AIOpponent {
    /**
     * Choose a move for the given side without mutating the board.
     * @param board   snapshot-like reference of the current position (do not modify)
     * @param aiColor which color the AI controls
     * @return {sr, sc, er, ec} or {@code null} when no move is available
     */
    int[] chooseMove(Board board, PieceColor aiColor);
}
