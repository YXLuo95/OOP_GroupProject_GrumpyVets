package logic;

import java.util.ArrayDeque;
import java.util.Deque;
import objects.Piece;
import objects.PieceColor;

/** Manages a game session: board state, turn order, move history, and post-move evaluation. */
public class GameSession {
    private final Board board;
    private PieceColor currentTurn = PieceColor.WHITE;
    private boolean gameOver = false;

    // State-based undo/redo using deep copies
    private final Deque<Board> undoStack = new ArrayDeque<>();
    private final Deque<Board> redoStack = new ArrayDeque<>();

    public GameSession(Board board) {
        this.board = board;
    }

    /** Starts a fresh game session. */
    public void start() {
        board.resetToStandard();
        currentTurn = PieceColor.WHITE;
        gameOver = false;
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Executes a move if legal:
     * 1) checks piece rule via Piece.canMove,
     * 2) prevents self-check via trial board,
     * 3) performs the real move via Board.movePiece (handles promotion),
     * 4) evaluates check / checkmate / stalemate for the opponent,
     * 5) pushes state for undo/redo and flips the turn.
     */
    public boolean playMove(int sr, int sc, int er, int ec) {
        if (gameOver) {
            System.out.println("Game is over.");
            return false;
        }

        Piece moving = board.getPieceAt(sr, sc);
        if (moving == null) {
            System.out.println("No piece at starting square!");
            return false;
        }

        // Turn check
        if (moving.getColor() != currentTurn) {
            System.out.println("It's " + currentTurn + "'s turn!");
            return false;
        }

        // Basic rule check (path/occupancy/capture rules encapsulated in the piece)
        if (!moving.canMove(board, sr, sc, er, ec)) {
            System.out.println("Illegal move by piece rule!");
            return false;
        }

        // Self-check prevention: simulate the move on a trial board
        Board trial = board.deepCopy();
        trial.movePiece(sr, sc, er, ec);
        if (Rules.isInCheck(trial, currentTurn)) {
            System.out.println("Illegal move: your king would be in check.");
            return false;
        }

        // Passed all checks -> commit the move
        undoStack.push(board.deepCopy()); // save pre-move state
        redoStack.clear();

        Piece captured = board.movePiece(sr, sc, er, ec); // handles promotion internally
        if (captured != null) {
            System.out.println(moving.getType() + " captured " + captured.getType());
        }

        // Evaluate opponent's status
        PieceColor opp = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        if (Rules.isCheckmate(board, opp)) {
            PieceColor winner = currentTurn;
            System.out.println("Checkmate! " + winner + " wins!");
            gameOver = true;
        } else if (Rules.isStalemate(board, opp)) {
            System.out.println("Stalemate!");
            gameOver = true;
        } else if (Rules.isInCheck(board, opp)) {
            System.out.println(opp + " is in check.");
        }

        // Flip the turn only if game is not over
        if (!gameOver) {
            currentTurn = opp;
        }
        
        return true;
    }

    /** Undo last move (if any). */
    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        redoStack.push(board.deepCopy());
        Board prev = undoStack.pop();
        board.copyFrom(prev);
        gameOver = false; // conservative: recompute externally if you track gameOver strictly
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return true;
    }

    /** Redo last undone move (if any). */
    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        undoStack.push(board.deepCopy());
        Board next = redoStack.pop();
        board.copyFrom(next);
        gameOver = false;
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return true;
    }

    // Getters
    public PieceColor getCurrentTurn() { return currentTurn; }
    public Board getBoard() { return board; }
    public boolean isGameOver() { return gameOver; }
    
    // Setters for save/load functionality
    public void setCurrentTurn(PieceColor turn) { this.currentTurn = turn; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void clearHistory() { 
        undoStack.clear(); 
        redoStack.clear(); 
    }
}
