package logic;

import objects.Piece;

public class Move {

    /**
     * Try to execute a move for the current side to move in the given session.
     * Returns true if the move was made and the turn was switched.
     * (No special moves or check validation yet.)
     */
    public static boolean makeMove(GameSession session, int sr, int sc, int er, int ec) {
        Board board = session.getBoard();

        // Basic presence & turn check
        Piece from = board.getPieceAt(sr, sc);
        if (from == null) return false;
        if (from.getColor() != session.getCurrentTurn()) return false;

        // Validate the move by piece rules + occupancy/path rules
        if (!MoveLogic.isValidMove(board, sr, sc, er, ec, session.getCurrentTurn())) {
            return false;
        }

        // Execute the move (simple move/capture)
        board.setPieceAt(er, ec, from);
        board.setPieceAt(sr, sc, null);

        // Switch side to move
        session.switchTurn();
        return true;
    }
}
