package logic;

public class TestGameSession {
    public static void main(String[] args) {
        Board board = new Board();
        GameSession session = new GameSession(board);
        session.start();

        System.out.println("game start");
        board.printBoard();

        // white e2->e4
        session.playMove(6, 4, 4, 4);
        // black e7->e5
        session.playMove(1, 4, 3, 4);
        System.out.println("\nmoves: e2e4 e7e5");
        board.printBoard();

        session.undo();
        System.out.println("\nredo one step");
        board.printBoard();

        session.undo();
        System.out.println("\nredo one step");
        board.printBoard();

        session.redo();
        System.out.println("\nredo one step");
        board.printBoard();
    }
}
