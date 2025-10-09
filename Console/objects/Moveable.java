package objects;

import logic.Board;

/** Interface for moveable chess pieces. */
public interface Moveable {
    /**
     * Determines if a piece can move from a starting position to an ending position on the board.
     *
     * @param board the current game board
     * @param startRow starting row
     * @param startCol starting column
     * @param endRow ending row
     * @param endCol ending column
     * @return true if the piece can legally move to the target square
     */
    boolean canMove(Board board, int startRow, int startCol, int endRow, int endCol);
}