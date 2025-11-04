import logic.GameSession;
import logic.Board;
import objects.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Singleplayer extends JFrame {
    private ChessBoard chessBoard;
    private GameSession gameSession;
    private JLabel statusLabel;
    
    public Singleplayer() {
        super("Chess - Single Player");
        initializeGame();
        setupUI();
    }
    
    private void initializeGame() {
        // Create chess board and game session
        Board board = new Board();
        gameSession = new GameSession(board);
        gameSession.start(); // Start new game
    }
    
    private void setupUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Top toolbar
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Center chess board
        chessBoard = new ChessBoard();
        add(chessBoard, BorderLayout.CENTER);
        
        // 底部状态栏
        statusLabel = new JLabel("White to move", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Set window properties
        setSize(800, 850);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Initial status update
        updateStatus();
    }
    
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> dispose());
        toolbar.add(backButton);
        
        toolbar.addSeparator();
        
        // New game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameSession.start();
            chessBoard.resetSelection();
            updateStatus();
            chessBoard.repaint();
        });
        toolbar.add(newGameButton);
        
        toolbar.addSeparator();
        
        // Undo button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (gameSession.undo()) {
                chessBoard.resetSelection();
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(undoButton);
        
        // Redo button
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (gameSession.redo()) {
                chessBoard.resetSelection();
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(redoButton);
        
        return toolbar;
    }
    
    private void updateStatus() {
        PieceColor currentTurn = gameSession.getCurrentTurn();
        String turnText = (currentTurn == PieceColor.WHITE) ? "White" : "Black";
        
        if (gameSession.isGameOver()) {
            statusLabel.setText("Game Over");
        } else {
            statusLabel.setText(turnText + " to move");
        }
    }
    
    // Inner chess board class
    private class ChessBoard extends JPanel {
        private final int BOARD_SIZE = 8;
        private final int CELL_SIZE = 80;
        private int selectedRow = -1;
        private int selectedCol = -1;
        
        public ChessBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
            setBackground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        handleCellClick(row, col);
                    }
                }
            });
        }
        
        private void handleCellClick(int row, int col) {
            if (gameSession.isGameOver()) {
                return; // Game over, no moves allowed
            }
            
            if (selectedRow == -1) {
                // First click - select piece
                Piece piece = gameSession.getBoard().getPieceAt(row, col);
                if (piece != null && piece.getColor() == gameSession.getCurrentTurn()) {
                    selectedRow = row;
                    selectedCol = col;
                    System.out.println("Selected " + piece.getType() + " at (" + row + "," + col + ")");
                    repaint();
                }
            } else {
                // Second click - attempt move
                if (selectedRow == row && selectedCol == col) {
                    // Click same cell, deselect
                    selectedRow = -1;
                    selectedCol = -1;
                    System.out.println("Deselected piece");
                } else {
                    // Try to move piece using Console game logic
                    System.out.println("Attempting move from (" + selectedRow + "," + selectedCol + ") to (" + row + "," + col + ")");
                    boolean moved = gameSession.playMove(selectedRow, selectedCol, row, col);
                    if (moved) {
                        System.out.println("Move successful!");
                        updateStatus();
                    } else {
                        System.out.println("Move failed!");
                    }
                    selectedRow = -1;
                    selectedCol = -1;
                }
                repaint();
            }
        }
        
        public void resetSelection() {
            selectedRow = -1;
            selectedCol = -1;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw chess board cells
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Color cellColor;
                    if ((row + col) % 2 == 0) {
                        cellColor = new Color(240, 217, 181); // Light cells
                    } else {
                        cellColor = new Color(181, 136, 99);  // Dark cells
                    }
                    
                    // Highlight selected cell
                    if (row == selectedRow && col == selectedCol) {
                        cellColor = new Color(255, 255, 0, 128); // Yellow highlight
                    }
                    
                    g2d.setColor(cellColor);
                    g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // Draw cell borders
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // Draw pieces (from Console Board)
            drawPieces(g2d);
        }
        
        private void drawPieces(Graphics2D g2d) {
            g2d.setFont(new Font("Serif", Font.BOLD, 48));
            
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Piece piece = gameSession.getBoard().getPieceAt(row, col);
                    if (piece != null) {
                        drawPiece(g2d, piece, row, col);
                    }
                }
            }
        }
        
        private void drawPiece(Graphics2D g2d, Piece piece, int row, int col) {
            String symbol = getPieceSymbol(piece);
            
            // Set color
            Color pieceColor = (piece.getColor() == PieceColor.WHITE) ? Color.WHITE : Color.BLACK;
            Color outlineColor = (piece.getColor() == PieceColor.WHITE) ? Color.BLACK : Color.WHITE;
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int textHeight = fm.getAscent();
            
            int x = col * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
            int y = row * CELL_SIZE + (CELL_SIZE + textHeight) / 2;
            
            // Draw outline
            g2d.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(symbol, x + dx, y + dy);
                    }
                }
            }
            
            // Draw piece
            g2d.setColor(pieceColor);
            g2d.drawString(symbol, x, y);
        }
        
        private String getPieceSymbol(Piece piece) {
            boolean isWhite = piece.getColor() == PieceColor.WHITE;
            
            switch (piece.getType()) {
                case KING:   return isWhite ? "♔" : "♚";
                case QUEEN:  return isWhite ? "♕" : "♛";
                case ROOK:   return isWhite ? "♖" : "♜";
                case BISHOP: return isWhite ? "♗" : "♝";
                case KNIGHT: return isWhite ? "♘" : "♞";
                case PAWN:   return isWhite ? "♙" : "♟";
                default:     return "?";
            }
        }
    }
}
