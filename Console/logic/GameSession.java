package logic;

import java.util.ArrayDeque;
import java.util.Deque;
import objects.Piece;
import objects.PieceColor;


/** Manages a game session, including the board state, turn order, and move history. */
public class GameSession {
    // board and current turn for control turns
    private final Board board;
    // white starts first
    private PieceColor currentTurn = PieceColor.WHITE;

    // undo/redo stacks 
    // to implement undo/redo functionality
    private final Deque<Board> undoStack = new ArrayDeque<>();
    private final Deque<Board> redoStack = new ArrayDeque<>();

    // constructor
    public GameSession(Board board) {
        this.board = board;
    }

    // start a new game session
    public void start() {
        board.resetToStandard();
        currentTurn = PieceColor.WHITE;
        undoStack.clear();
        redoStack.clear();
    }

        /**
         * Executes a move (checks legality with Piece.canMove and updates the board with setPieceAt()).
         */
        public boolean playMove(int sr, int sc, int er, int ec) {
            Piece moving = board.getPieceAt(sr, sc);
            if (moving == null) {
                System.out.println("No piece at starting position!");
                return false;
            }

            // check turn flag 
            if (moving.getColor() != currentTurn) {
                System.out.println("It's not " + moving.getColor() + "'s turn!");
                return false;
            }

            // check if the move is legal
            if (!moving.canMove(board, sr, sc, er, ec)) {
                System.out.println("Illegal move!");
                return false;
            }

            // keep history for undo/redo
            // after move is validated
            undoStack.push(board.deepCopy());
            redoStack.clear();

            // get the target piece (if any, it's the captured piece)
            Piece target = board.getPieceAt(er, ec);

            // clear the starting square
            board.setPieceAt(sr, sc, null);

            // place the piece at the destination square (setPieceAt will automatically update moving's coordinates)
            board.setPieceAt(er, ec, moving);

            if (target != null) {
                System.out.println("✅ " + moving.getType() + " captured " + target.getType());
            }

            // switch turn flag
            currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

            return true;
        }

        /* Undo the last move */
        public boolean undo() {
            /* Check if there is a move to undo */
            if (undoStack.isEmpty()) return false;
            redoStack.push(board.deepCopy());
            Board prev = undoStack.pop();
            board.copyFrom(prev);
            currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            return true;
        }
        /* Redo the last undone move */
        public boolean redo() {
            if (redoStack.isEmpty()) return false;
            undoStack.push(board.deepCopy());
            Board next = redoStack.pop();
            board.copyFrom(next);
            currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            return true;
        }

        // Getters
        public PieceColor getCurrentTurn() { return currentTurn; }
        public Board getBoard() { return board; }
}
