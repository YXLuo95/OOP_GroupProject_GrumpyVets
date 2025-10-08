
import java.util.Scanner;
import logic.*;

public class Main {

    // Convert "e2" -> int[2]{row, col} for a board with rank 1 at TOP (row 0) and rank 8 at BOTTOM (row 7).
    private static int[] parseSquare(String s) {
        if (s == null || s.length() != 2) return null;
        char file = Character.toLowerCase(s.charAt(0));
        char rank = s.charAt(1);

        if (file < 'a' || file > 'h') return null;
        if (rank < '1' || rank > '8') return null;

        int col = file - 'a';              // a->0 ... h->7
        int row = (rank - '1');            // '1'->0 ... '8'->7  (rank 1 is TOP)
        return new int[]{row, col};
    }

    private static String algebraic(int row, int col) {
        return "" + (char)('a' + col) + (char)('1' + row);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Console Chess!");
            GameSession gameSession = new GameSession();
            Board board = gameSession.getBoard();
            board.printBoard();

            while (true) {
                System.out.println("Current turn: " + gameSession.getCurrentTurn());
                System.out.print("Enter your move (e.g., e2 e4 or e2e4) or 'exit' to quit: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Thanks for playing!");
                    break;
                }

                // allow "e2 e4" or "e2e4"
                String cleaned = input.replaceAll("\\s+", "");
                if (cleaned.length() != 4) {
                    System.out.println("Invalid input. Use 'e2 e4' or 'e2e4'.");
                    continue;
                }

                String fromStr = cleaned.substring(0, 2);
                String toStr   = cleaned.substring(2, 4);

                int[] from = parseSquare(fromStr);
                int[] to   = parseSquare(toStr);

                if (from == null || to == null) {
                    System.out.println("Invalid coordinates. Files must be a–h and ranks 1–8.");
                    continue;
                }

                int sr = from[0], sc = from[1], er = to[0], ec = to[1];

                boolean moveMade = Move.makeMove(gameSession, sr, sc, er, ec);
                if (!moveMade) {
                    System.out.println("Invalid move from " + fromStr + " to " + toStr + ". Try again.");
                    continue;
                }

                System.out.println("Moved " + fromStr + " -> " + toStr);
                board.printBoard();
            }
        }
    }
}
