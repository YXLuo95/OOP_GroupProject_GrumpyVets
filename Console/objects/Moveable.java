package objects;

import logic.Board;


public interface Moveable {
    boolean canMove(Board board, int startRow, int startCol, int endRow, int endCol);
}