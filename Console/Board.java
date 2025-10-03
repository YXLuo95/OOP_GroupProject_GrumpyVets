
public class Board {
    
    // define an 8x8 array to represent the chess board
    // using 2D array of Piece objects
    private Piece[][] squares = new Piece[8][8];    


    // constructor to initialize the board with pieces in starting positions
    // given pieces on the board a unique ID
    public Board() {
        // Initialize white pieces
        squares[0][0] = new Piece(PieceType.ROOK, PieceColor.WHITE, 1);
        squares[0][1] = new Piece(PieceType.KNIGHT, PieceColor.WHITE, 2);
        squares[0][2] = new Piece(PieceType.BISHOP, PieceColor.WHITE, 3);
        squares[0][3] = new Piece(PieceType.QUEEN, PieceColor.WHITE, 4);
        squares[0][4] = new Piece(PieceType.KING, PieceColor.WHITE, 5);
        squares[0][5] = new Piece(PieceType.BISHOP, PieceColor.WHITE, 6);
        squares[0][6] = new Piece(PieceType.KNIGHT, PieceColor.WHITE, 7);
        squares[0][7] = new Piece(PieceType.ROOK, PieceColor.WHITE, 8);
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new Piece(PieceType.PAWN, PieceColor.WHITE, 9 + i);
        }

        // Initialize black pieces
        squares[7][0] = new Piece(PieceType.ROOK, PieceColor.BLACK, 17);
        squares[7][1] = new Piece(PieceType.KNIGHT, PieceColor.BLACK, 18);
        squares[7][2] = new Piece(PieceType.BISHOP, PieceColor.BLACK, 19);
        squares[7][3] = new Piece(PieceType.QUEEN, PieceColor.BLACK, 20);
        squares[7][4] = new Piece(PieceType.KING, PieceColor.BLACK, 21);
        squares[7][5] = new Piece(PieceType.BISHOP, PieceColor.BLACK, 22);
        squares[7][6] = new Piece(PieceType.KNIGHT, PieceColor.BLACK, 23);
        squares[7][7] = new Piece(PieceType.ROOK, PieceColor.BLACK, 24);
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new Piece(PieceType.PAWN, PieceColor.BLACK, 25 + i);
        }

        // Initialize empty squares
        for (int row = 2; row <= 5;
                row++) {
                for (int col = 0; col < 8; col++) {
                    squares[row][col] = null; // Empty square
                }
            }
    }

    // method to get the piece at a specific position
    public Piece getPieceAt(int row, int col) {
        return squares[row][col];
    }

    // method to move a piece from one position to another  
    // move is a Move object that contains start and end positions
    public void movePiece(Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        Piece pieceToMove = squares[startRow][startCol];
        squares[endRow][endCol] = pieceToMove;
        squares[startRow][startCol] = null; // Empty the starting square
    }
    

    // method to print the board to the console
    // using exsiting PieceType and PieceColor enums
    public void printBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    char pieceChar;
                    switch (piece.getType()) {
                        case KING:
                            pieceChar = 'K';
                            break;
                        case QUEEN:
                            pieceChar = 'Q';
                            break;
                        case ROOK:
                            pieceChar = 'R';
                            break;
                        case BISHOP:
                            pieceChar = 'B';
                            break;
                        case KNIGHT:
                            pieceChar = 'N';
                            break;
                        case PAWN:
                            pieceChar = 'P';
                            break;
                        default:
                            pieceChar = '?';
                    }
                    if (piece.getColor() == PieceColor.WHITE) {
                        System.out.print(Character.toUpperCase(pieceChar) + " ");
                    } else {
                        System.out.print(Character.toLowerCase(pieceChar) + " ");
                    }
                }
            }
            System.out.println();
        }
    }
}
