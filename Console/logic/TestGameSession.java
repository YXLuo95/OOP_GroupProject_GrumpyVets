package logic;

/**
 * Minimal smoke test for GameSession + Board:
 * - Starts a new game
 * - Plays: e2->e4 (white), e7->e5 (black)
 * - Undoes twice (back to initial position)
 * - Redoes once
 * Prints the board after each step.
 */
public class TestGameSession {
    public static void main(String[] args) {
        Board board = new Board();
        GameSession session = new GameSession(board);
        session.start();

        System.out.println("=== TestGameSession ===");
        System.out.println("Game start (standard position):");
        BoardPrinter.print(board);

        // --- Play two opening moves: e2e4, e7e5 ---
        boolean ok;

        // White: e2 -> e4  (6,4) -> (4,4)
        ok = session.playMove(6, 4, 4, 4);
        System.out.println("\nWhite plays e2 -> e4 : " + (ok ? "OK" : "FAILED"));
        BoardPrinter.print(board);

        // Black: e7 -> e5  (1,4) -> (3,4)
        ok = session.playMove(1, 4, 3, 4);
        System.out.println("\nBlack plays e7 -> e5 : " + (ok ? "OK" : "FAILED"));
        System.out.println("Moves so far: e2e4 e7e5");
        BoardPrinter.print(board);

        // --- Undo last move (should undo black's e7e5) ---
        ok = session.undo();
        System.out.println("\nUndo one step (revert black's e7e5): " + (ok ? "OK" : "FAILED"));
        BoardPrinter.print(board);

        // --- Undo again (should undo white's e2e4) ---
        ok = session.undo();
        System.out.println("\nUndo one step (revert white's e2e4): " + (ok ? "OK" : "FAILED"));
        BoardPrinter.print(board);

        // --- Redo once (should redo white's e2e4) ---
        ok = session.redo();
        System.out.println("\nRedo one step (re-apply white's e2e4): " + (ok ? "OK" : "FAILED"));
        BoardPrinter.print(board);

        System.out.println("\nCurrent side to move: " + session.getCurrentTurn());
        System.out.println("Game over? " + session.isGameOver());
    }
}
