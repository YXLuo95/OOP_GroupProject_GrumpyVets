package logic;

import objects.Piece;
import objects.PieceColor;

public class Move {
    

    //methods to validate if the piece exists at the source position
    public static boolean isValidSource(int srcRow, int srcCol, Board board) {
        // Check if the source coordinates are within bounds
        if (srcRow < 0 || srcRow >= 8 || srcCol < 0 || srcCol >= 8) {
            return false;
        }
        // Check if there is a piece at the source position
        return board.getPieceAt(srcRow, srcCol) != null;
    }
    //method to validate if it is the correct player's turn
    //using isvalidSource method
    public static boolean isValidTurn(int srcRow, int srcCol, Board board, PieceColor currentTurn) {
        if (!isValidSource(srcRow, srcCol, board)) {
            return false;
        }
        Piece piece = board.getPieceAt(srcRow, srcCol);
        return piece.getColor() == currentTurn;
    }

   //method to validate if the destination is within bounds
    public static boolean isValidDestination(int destRow, int destCol) {
        return destRow >= 0 && destRow < 8 && destCol >= 0 && destCol < 8;
    }

    //method to validate if the destination is not occupied by a piece of the same color
    public static boolean isDestinationOccupiedBySameColor(int destRow, int destCol, Board  board, PieceColor currentTurn) {
        Piece destPiece = board.getPieceAt(destRow, destCol);
        return destPiece != null && destPiece.getColor() == currentTurn;
    }

    //method to validate different piece movement rules
    public static boolean isValidPieceMovement(int srcRow, int srcCol, int destRow, int destCol, Board board) {
        Piece piece = board.getPieceAt(srcRow, srcCol);
        if (piece == null) {
            return false; // No piece at source
        }
        int rowDiff = Math.abs(destRow - srcRow);
        int colDiff = Math.abs(destCol - srcCol);

        switch (piece.getType()) {
            case PAWN:
                // Pawns move forward one square, or two squares from their starting position
                if (piece.getColor() == PieceColor.WHITE) {
                    if (srcRow == 6) { // Starting position
                        return (destRow == srcRow - 1 || destRow == srcRow - 2) && colDiff == 0;
                    } else {
                        return destRow == srcRow - 1 && colDiff == 0;
                    }
                } else { // BLACK
                    if (srcRow == 1) { // Starting position
                        return (destRow == srcRow + 1 || destRow == srcRow + 2) && colDiff == 0;
                    } else {
                        return destRow == srcRow + 1 && colDiff == 0;
                    }
                }
            case ROOK:
                // Rooks move any number of squares along a row or column
                return (rowDiff == 0 || colDiff == 0);
            case KNIGHT:
                // Knights move in an "L" shape: two squares in one direction and then one square perpendicular
                return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
            case BISHOP:
                // Bishops move any number of squares diagonally
                return rowDiff == colDiff;
            case QUEEN:
                // Queens move any number of squares along a row, column, or diagonal
                return (rowDiff == colDiff || rowDiff == 0 || colDiff == 0);
            case KING:
                // Kings move one square in any direction
                return rowDiff <= 1 && colDiff <= 1;
            default:
                return false;
        }
    }

    //method to validate path is clear for pieces that cannot jump
    public static boolean isPathClear(int srcRow, int srcCol, int destRow, int destCol, Board board) {
        Piece piece = board.getPieceAt(srcRow, srcCol);
        if (piece == null) {
            return false; // No piece at source
        }
        int rowStep = Integer.compare(destRow, srcRow);
        int colStep = Integer.compare(destCol, srcCol);

        // Only check path for Rooks, Bishops, and Queens
        if (piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.PAWN || piece.getType() == PieceType.KING) {
            return true; // These pieces can jump or move only one square
        }

        int currentRow = srcRow + rowStep;
        int currentCol = srcCol + colStep;

        while (currentRow != destRow || currentCol != destCol) {
            if (board.getPieceAt(currentRow, currentCol) != null) {
                return false; // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true; // Path is clear
    }

    
}
