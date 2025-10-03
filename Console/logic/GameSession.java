package logic;
import objects.PieceColor;


public class GameSession {
    private Board board;
    private PieceColor currentTurn;

    public GameSession() {
        board = new Board();
        currentTurn = PieceColor.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public void switchTurn() {
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }   
}
