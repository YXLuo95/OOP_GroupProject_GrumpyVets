package GUI;

import logic.GameSession;
import logic.Rules;
import objects.PieceColor;

/**
 * Helper to generate consistent status label text for any game state.
 * Purpose: Centralizes status text logic for reuse across different GUI components
 * Features:
 * - Analyzes game session state to produce appropriate status messages
 * - Handles turn indication, check, checkmate, stalemate, and game over scenarios
 * - Static utility class for easy access without instantiation
 */
public final class StatusText {
    private StatusText() {}

    public static String forSession(GameSession gameSession) {
        if (gameSession == null) return "";
        PieceColor currentTurn = gameSession.getCurrentTurn();
        String turnText = (currentTurn == PieceColor.WHITE) ? "White" : "Black";

        if (gameSession.isGameOver()) {
            PieceColor opponent = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            if (Rules.isCheckmate(gameSession.getBoard(), currentTurn)) {
                String winner = (opponent == PieceColor.WHITE) ? "White" : "Black";
                return "Checkmate! " + winner + " wins!";
            } else if (Rules.isStalemate(gameSession.getBoard(), currentTurn)) {
                return "Stalemate - Draw!";
            } else {
                return "Game Over";
            }
        }

        if (Rules.isInCheck(gameSession.getBoard(), currentTurn)) {
            return turnText + " in check - move to safety!";
        }
        return turnText + " to move";
    }
}
