import java.util.Scanner;
import logic.Board;
import logic.BoardPrinter;
import logic.GameSession;
import logic.Notation;
import objects.PromotionChoice;

public class MainConsole {

    public static void main(String[] args) {
        // try-with-resources ensures the scanner is closed automatically
        try (Scanner scanner = new Scanner(System.in)) {

            Board board = new Board();
            GameSession session = new GameSession(board);
            session.start();

            // Console promotion selector for real moves (trial boards auto-queen)
            board.setPromotionSelector((color, r, c) -> {
                System.out.printf("Promote %s pawn at %s. Choose [q,r,b,n] (default q): ",
                        color, Notation.toAlg(r, c));
                String line = scanner.nextLine().trim().toLowerCase();
                if (line.isEmpty()) return PromotionChoice.QUEEN;
                return switch (line.charAt(0)) {
                    case 'r' -> PromotionChoice.ROOK;
                    case 'b' -> PromotionChoice.BISHOP;
                    case 'n' -> PromotionChoice.KNIGHT;
                    default  -> PromotionChoice.QUEEN;
                };
            });

            System.out.println("=== Java Chess (Console) ===");
            printHelp();
            BoardPrinter.print(board);

            // Main REPL loop
            while (true) {
                if (session.isGameOver()) {
                    System.out.println("Game over. Type 'u' to undo or 'q' to quit.");
                }

                System.out.printf("[%s] > ", session.getCurrentTurn());
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String cmd = parts[0].toLowerCase();

                boolean handledCommand = true;
                switch (cmd) {
                    case "q", "quit", "exit" -> {
                        System.out.println("Goodbye!");
                        return; // exit main
                    }
                    case "h", "help" -> {
                        printHelp();
                    }
                    case "p", "print" -> {
                        BoardPrinter.print(board);
                    }
                    case "u", "undo" -> {
                        if (!session.undo()) System.out.println("⚠️  No move to undo.");
                        BoardPrinter.print(board);
                    }
                    case "r", "redo" -> {
                        if (!session.redo()) System.out.println("⚠️  No move to redo.");
                        BoardPrinter.print(board);
                    }
                    default -> handledCommand = false; // not a command; try to parse as a move
                }
                if (handledCommand) continue;

                // ----- Moves -----
                boolean success = false;

                // Case A: algebraic squares, e.g., "e2 e4"
                if (parts.length == 2) {
                    int[] from = Notation.fromAlg(parts[0]);
                    int[] to   = Notation.fromAlg(parts[1]);
                    if (from == null || to == null) {
                        System.out.println("Invalid coordinates. Example: e2 e4");
                    } else {
                        success = session.playMove(from[0], from[1], to[0], to[1]);
                    }
                }
                // Case B: numeric coords, e.g., "6 4 4 4"
                else if (parts.length == 4) {
                    try {
                        int sr = Integer.parseInt(parts[0]);
                        int scCol = Integer.parseInt(parts[1]);
                        int er = Integer.parseInt(parts[2]);
                        int ec = Integer.parseInt(parts[3]);
                        success = session.playMove(sr, scCol, er, ec);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid numbers. Example: 6 4 4 4");
                    }
                } else {
                    System.out.println("Unknown command. Type 'h' for help.");
                    continue;
                }

                // After move feedback
                if (!success) {
                    System.out.println("Move failed (illegal move or wrong turn).");
                } else {
                    BoardPrinter.print(board);
                    System.out.println("Next to move: " + session.getCurrentTurn());
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
            Commands:
              p / print     - show the current board
              u / undo      - undo last move
              r / redo      - redo last undone move
              h / help      - show this help message
              q / quit      - exit the game

            To make a move:
              1) Algebraic squares:  e2 e4
              2) Numeric indices:    6 4 4 4
                 (rows/cols are 0..7; white at bottom)
            """);
    }
}
