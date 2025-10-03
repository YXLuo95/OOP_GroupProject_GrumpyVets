// This class will validate moves made by players
// It will check if the move is valid according to the rules of chess
public class MoveValidator {
    private Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Move move, PieceColor playerColor) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        // Check if the start and end positions are within the board boundaries
        if (!isWithinBounds(startRow, startCol) || !isWithinBounds(endRow, endCol)) {
            return false;
        }

        Piece piece = board.getPieceAt(startRow, startCol);
        if (piece == null || piece.getColor() != playerColor) {
            return false; // No piece at start position or not player's piece
        }

        Piece targetPiece = board.getPieceAt(endRow, endCol);
        if (targetPiece != null && targetPiece.getColor() == playerColor) {
            return false; // Cannot capture own piece
        }

        // Validate move based on piece type
        switch (piece.getType()) {
            case PAWN:
                return validatePawnMove(move, piece);
            case ROOK:
                return validateRookMove(move);
            case KNIGHT:
                return validateKnightMove(move);
            case BISHOP:
                return validateBishopMove(move);
            case QUEEN:
                return validateQueenMove(move);
            case KING:
                return validateKingMove(move);
            default:
                return false;
        }
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean validatePawnMove(Move move, Piece pawn) {
        int direction = (pawn.getColor() == PieceColor.WHITE) ? -1 : 1;
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        // Standard move
        if (startCol == endCol) {
            if (endRow == startRow + direction && board.getPieceAt(endRow, endCol) == null) {
                return true;
            }
            // Initial double move
            if ((pawn.getColor() == PieceColor.WHITE && startRow == 6 || pawn.getColor() == PieceColor.BLACK && startRow == 1) &&
                endRow == startRow + 2 * direction &&
                board.getPieceAt(startRow + direction, startCol) == null &&
                board.getPieceAt(endRow, endCol) == null) {
                return true;
            }
        }
        // Capture move
        else if (Math.abs(startCol - endCol) == 1 && endRow == startRow + direction) {
            Piece targetPiece = board.getPieceAt(endRow, endCol);
            if (targetPiece != null && targetPiece.getColor() != pawn.getColor()) {
                return true;
            }
        }
        return false;
    }
    private boolean validateRookMove(Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        if (startRow != endRow && startCol != endCol) {
            return false; // Rook moves only in straight lines
        }

        // Check if path is clear
        if (startRow == endRow) {
            int step = (endCol > startCol) ? 1 : -1;
            for (int col = startCol + step; col != endCol; col += step) {
                if (board.getPieceAt(startRow, col) != null) {
                    return false; // Path blocked
                }
            }
        } else {
            int step = (endRow > startRow) ? 1 : -1;
            for (int row = startRow + step; row != endRow; row += step) {
                if (board.getPieceAt(row, startCol) != null) {
                    return false; // Path blocked
                }
            }
        }
        return true;
    }
    private boolean validateKnightMove(Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    private boolean validateBishopMove(Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        if (Math.abs(startRow - endRow) != Math.abs(startCol - endCol)) {
            return false; // Bishop moves only diagonally
        }

        int rowStep = (endRow > startRow) ? 1 : -1;
        int colStep = (endCol > startCol) ? 1 : -1;
        int row = startRow + rowStep;
        int col = startCol + colStep;

        while (row != endRow && col != endCol) {
            if (board.getPieceAt(row, col) != null) {
                return false; // Path blocked
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }
    private boolean validateQueenMove(Move move) {
        // Queen's move is a combination of Rook and Bishop
        return validateRookMove(move) || validateBishopMove(move);
    }
    private boolean validateKingMove(Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);

        return (rowDiff <= 1 && colDiff <= 1);
    }
}