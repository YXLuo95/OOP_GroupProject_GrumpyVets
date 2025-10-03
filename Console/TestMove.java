//to test if movePiece method works
public class TestMove {
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println("Initial Board:");
        board.printBoard();

        // Create a move from (1, 0) to (3, 0) - moving a white pawn forward two squares
        Move move = new Move(1, 0, 3, 0);
        board.movePiece(move);

        System.out.println("\nBoard after moving pawn from (1,0) to (3,0):");
        board.printBoard();
    }
}