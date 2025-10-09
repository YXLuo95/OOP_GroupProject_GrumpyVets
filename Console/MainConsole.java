
import java.util.Scanner;
import logic.Board;
import logic.GameSession;
/**
 * Console-based chess game driver.
 * Allows players to make moves, undo/redo, and print the board.
 * Uses algebraic notation (e.g., "e2 e4") or numeric coordinates (e.g., "6 4 4 4").
 */
public class MainConsole {
    public static void main(String[] args) {
        Board board = new Board();
        GameSession session = new GameSession(board);
        session.start();

        System.out.println("=== Java Chess (Console Version) ===");
        printHelp();
        board.printBoard();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.printf("[%s] > ", session.getCurrentTurn());
            if (!sc.hasNext()) break;
            String cmd = sc.next();

            // --- General commands ---
            switch (cmd.toLowerCase()) {
                case "q", "quit", "exit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                case "h", "help" -> {
                    printHelp();
                    continue;
                }
                case "p", "print" -> {
                    board.printBoard();
                    continue;
                }
                case "u", "undo" -> {
                    if (!session.undo()) System.out.println("⚠️  No move to undo.");
                    board.printBoard();
                    continue;
                }
                case "r", "redo" -> {
                    if (!session.redo()) System.out.println("⚠️  No move to redo.");
                    board.printBoard();
                    continue;
                }
            }

            // --- Try to parse as algebraic notation (e.g., e2 e4) ---
            boolean success = false;
            int[] from = Board.fromAlg(cmd);  // check if it's like "e2"
            if (from != null) {
                // Read destination square
                if (!sc.hasNext()) {
                    System.out.println("Usage: e2 e4");
                    continue;
                }
                String toAlg = sc.next();
                int[] to = Board.fromAlg(toAlg);
                if (to == null) {
                    System.out.println("Invalid coordinates. Example: e2 e4");
                    continue;
                }
                success = session.playMove(from[0], from[1], to[0], to[1]);
            } else {
                // --- Try numeric coordinates: 6 4 4 4 ---
                try {
                    int sr = Integer.parseInt(cmd);
                    int scCol = sc.nextInt();
                    int er = sc.nextInt();
                    int ec = sc.nextInt();
                    success = session.playMove(sr, scCol, er, ec);
                } catch (Exception e) {
                    System.out.println("Invalid input. Example: e2 e4  OR  6 4 4 4");
                    sc.nextLine(); // clear bad input
                    continue;
                }
            }

            // --- Show results ---
            if (!success) {
                System.out.println("Move failed (illegal move or wrong turn).");
            } else {
                board.printBoard();
                System.out.println("Next to move: " + session.getCurrentTurn());
            }
        }
    }

    /** Prints a simple help menu */
    private static void printHelp() {
        System.out.println("""
            Commands:
              p / print     - show the current board
              u / undo      - undo last move
              r / redo      - redo last undone move
              h / help      - show this help message
              q / quit      - exit the game

            To make a move:
              1) Use algebraic notation:  e2 e4
              2) Or numeric coordinates:  6 4 4 4
            """);
    }
}
