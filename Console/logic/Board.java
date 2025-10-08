package logic;

import objects.Piece;
import objects.PieceColor;
import objects.PieceType;

public class Board {
    private Piece[][] squares = new Piece[8][8];

    // Initialize the board with standard chess setup
    public Board() {
        // Clear board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        // ---- WHITE (top, rows 0–1) ----
        // Major pieces
        squares[0][0] = new Piece(PieceType.ROOK,   PieceColor.WHITE);
        squares[0][1] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        squares[0][2] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        squares[0][3] = new Piece(PieceType.QUEEN,  PieceColor.WHITE);
        squares[0][4] = new Piece(PieceType.KING,   PieceColor.WHITE);
        squares[0][5] = new Piece(PieceType.BISHOP, PieceColor.WHITE);
        squares[0][6] = new Piece(PieceType.KNIGHT, PieceColor.WHITE);
        squares[0][7] = new Piece(PieceType.ROOK,   PieceColor.WHITE);

        // Pawns
        for (int c = 0; c < 8; c++) {
            squares[1][c] = new Piece(PieceType.PAWN, PieceColor.WHITE);
        }

        // ---- BLACK (bottom, rows 6–7) ----
        // Pawns
        for (int c = 0; c < 8; c++) {
            squares[6][c] = new Piece(PieceType.PAWN, PieceColor.BLACK);
        }

        // Major pieces
        squares[7][0] = new Piece(PieceType.ROOK,   PieceColor.BLACK);
        squares[7][1] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        squares[7][2] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        squares[7][3] = new Piece(PieceType.QUEEN,  PieceColor.BLACK);
        squares[7][4] = new Piece(PieceType.KING,   PieceColor.BLACK);
        squares[7][5] = new Piece(PieceType.BISHOP, PieceColor.BLACK);
        squares[7][6] = new Piece(PieceType.KNIGHT, PieceColor.BLACK);
        squares[7][7] = new Piece(PieceType.ROOK,   PieceColor.BLACK);
    }

    // --- Utility methods ---

    private boolean inBounds(int r, int c) {
        return 0 <= r && r < 8 && 0 <= c && c < 8;
    }

    public Piece getPieceAt(int row, int col) {
        if (!inBounds(row, col)) return null;
        return squares[row][col];
    }

    public void setPieceAt(int row, int col, Piece piece) {
        if (inBounds(row, col)) {
            squares[row][col] = piece;
        }
    }

    public void printBoard() {
        System.out.println("   a b c d e f g h");
        for (int r = 0; r < 8; r++) {
            System.out.print((r + 1) + "  ");
            for (int c = 0; c < 8; c++) {
                Piece p = squares[r][c];
                if (p == null) {
                    System.out.print(". ");
                } else {
                    char symbol = p.getType().toString().charAt(0);
                    if (p.getColor() == PieceColor.WHITE) {
                        System.out.print(Character.toUpperCase(symbol) + " ");
                    } else {
                        System.out.print(Character.toLowerCase(symbol) + " ");
                    }
                }
            }
            System.out.println(" " + (r + 1));
        }
        System.out.println("   a b c d e f g h");
    }
}
