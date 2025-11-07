package GUI;

import logic.GameSession;
import logic.Board;
import logic.GameSave;
import logic.Rules;
import objects.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Single Player Chess Game GUI
 * 
 * Purpose: Provides a complete single-player chess experience with enhanced user interaction
 * Features:
 * - Interactive chess board with drag-and-drop and click-to-move functionality
 * - Game control toolbar (New Game, Undo, Redo, Save, Back to Menu)
 * - Real-time status updates and move validation
 * - Integration with console chess logic for rule enforcement
 * - Enhanced visual feedback including piece selection and drag highlighting
 * - Responsive GUI that updates based on game state changes
 */
public class Singleplayer extends JFrame {
    
    // Core Game Components
    // Purpose: Manage chess game logic and visual representation
    private ChessBoard chessBoard;    // Custom interactive chess board component
    private GameSession gameSession;  // Chess game logic controller and state manager
    private JLabel statusLabel;       // Display area for game status and move information
    
    /**
     * Constructor - Initializes Single Player Chess Window
     * Purpose: Sets up the complete single-player chess gaming interface
     */
    public Singleplayer() {
        super("Chess - Single Player");
        initializeGame();  // Set up chess game logic
        setupUI();         // Configure user interface components
    }
    
    /**
     * Game Initialization Method
     * Purpose: Creates and configures the chess game session with a standard board setup
     */
    private void initializeGame() {
        Board board = new Board();         // Create new chess board with standard piece layout
        gameSession = new GameSession(board); // Initialize game logic controller
        gameSession.start();              // Start new game with white to move
    }
    
    /**
     * User Interface Setup Method  
     * Purpose: Configures and arranges all GUI components in the window layout
     */
    private void setupUI() {
        // Window Configuration
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Top Section - Game Control Toolbar
        // Purpose: Provides quick access to game functions and navigation
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Center Section - Interactive Chess Board
        // Purpose: Main game area with enhanced piece interaction capabilities
        chessBoard = new ChessBoard();
        JPanel centerPanel = new JPanel(new GridBagLayout()); // Centers the board
        centerPanel.add(chessBoard);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Section - Game Status Display
        // Purpose: Shows current player turn, game state, and move feedback
        statusLabel = new JLabel("White to move", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Window Properties Configuration
        setSize(700, 750);        // Optimized dimensions for centered chess board
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false);      // Fixed size for consistent layout
        
        // Initialize game status display
        updateStatus();
    }
    
    /**
     * Toolbar Creation Method
     * Purpose: Creates and configures the game control toolbar with all functional buttons
     */
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false); // Prevent toolbar from being moved
        
        // Navigation Button - Return to Main Menu
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> dispose()); // Close single player window
        toolbar.add(backButton);
        
        toolbar.addSeparator();
        
        // Game Control Button - Start New Game
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameSession.start();           // Reset game to initial state
            chessBoard.resetSelection();   // Clear any selected pieces
            updateStatus();               // Refresh status display
            chessBoard.repaint();         // Redraw the board
        });
        toolbar.add(newGameButton);
        
        toolbar.addSeparator();
        
        // Move History Button - Undo Last Move
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (gameSession.undo()) {      // Attempt to undo last move
                chessBoard.resetSelection(); // Clear selection after undo
                updateStatus();             // Update game status
                chessBoard.repaint();       // Refresh board display
            }
        });
        toolbar.add(undoButton);
        
        // Move History Button - Redo Previously Undone Move  
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (gameSession.redo()) {      // Attempt to redo move
                chessBoard.resetSelection(); // Clear selection after redo
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(redoButton);
        
        toolbar.addSeparator();
        
        // Save game button
        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> saveGame());
        toolbar.add(saveButton);

        return toolbar;
    }

    private void updateStatus() {
        // Update status label based on current turn and game state
        PieceColor currentTurn = gameSession.getCurrentTurn();
        String turnText = (currentTurn == PieceColor.WHITE) ? "White" : "Black";
        
        // Check for game over
        if (gameSession.isGameOver()) {
            // Check what type of game ending it is
            PieceColor currentPlayer = gameSession.getCurrentTurn();
            PieceColor opponent = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            
            if (Rules.isCheckmate(gameSession.getBoard(), currentPlayer)) {
                String winner = (opponent == PieceColor.WHITE) ? "White" : "Black";
                statusLabel.setText("Checkmate! " + winner + " wins!");
                showGameOverDialog("Checkmate!", winner + " wins!");
            } else if (Rules.isStalemate(gameSession.getBoard(), currentPlayer)) {
                statusLabel.setText("Stalemate - Draw!");
                showGameOverDialog("Stalemate!", "It's a draw!");
            } else {
                statusLabel.setText("Game Over");
                showGameOverDialog("Game Over", "Game has ended.");
            }
        } else if (Rules.isInCheck(gameSession.getBoard(), gameSession.getCurrentTurn())) {
            statusLabel.setText(turnText + " in check - move to safety!");
        } else {
            statusLabel.setText(turnText + " to move");
        }
    }
    
    /**
     * Shows a popup dialog to declare the winner or game result
     */
    private void showGameOverDialog(String title, String message) {
        // Use SwingUtilities.invokeLater to ensure the dialog shows on the EDT
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"New Game", "Back to Menu", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                this,
                message + "\n\nWhat would you like to do?",
                title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            switch (choice) {
                case 0: // New Game
                    startNewGame();
                    break;
                case 1: // Back to Menu
                    backToMainMenu();
                    break;
                case 2: // Exit
                case JOptionPane.CLOSED_OPTION:
                    System.exit(0);
                    break;
            }
        });
    }
    
    /**
     * Start a new game
     */
    private void startNewGame() {
        gameSession.start();           // Reset game to initial state
        chessBoard.resetSelection();   // Clear any selected pieces
        updateStatus();               // Refresh status display
        chessBoard.repaint();         // Redraw the board
    }
    
    /**
     * Return to main menu
     */
    private void backToMainMenu() {
        dispose(); // Close single player window
    }
    
    private void saveGame() {
        String saveName = JOptionPane.showInputDialog(this, 
            "Enter save name:", 
            "Save Game", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (saveName != null && !saveName.trim().isEmpty()) {
            if (GameSave.saveGame(gameSession, saveName.trim())) {
                JOptionPane.showMessageDialog(this, 
                    "Game saved successfully!", 
                    "Save Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save game!", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Interactive Chess Board Inner Class
     * 
     * Purpose: Provides enhanced chess board interaction with dual input methods
     * Features:
     * - Click-and-move: Traditional chess interface (click piece, click destination)
     * - Drag-and-drop: Modern interface (drag piece to destination)
     * - Real-time visual feedback during piece manipulation
     * - Integration with chess game logic for move validation
     * - Responsive highlighting and smooth user experience
     */
    private class ChessBoard extends JPanel {
        // Board Display Constants
        private final int BOARD_SIZE = 8;     // 8x8 chess board
        private final int CELL_SIZE = 75;     // Optimized square size for centered layout
        
        // Click-and-Move State Variables
        // Purpose: Track piece selection for traditional click interface
        private int selectedRow = -1;         // Currently selected piece row (-1 = none)
        private int selectedCol = -1;         // Currently selected piece column (-1 = none)
        
        // Drag-and-Drop State Variables
        // Purpose: Track piece dragging operations for modern interface
        private boolean isDragging = false;           // Flag indicating active drag operation
        private int dragStartRow = -1;               // Starting row of dragged piece (-1 = none)
        private int dragStartCol = -1;               // Starting column of dragged piece (-1 = none)
        private Point dragOffset = new Point();      // Mouse offset from piece center for smooth dragging
        private Point currentDragPosition = new Point(); // Current mouse position during drag operation
        
        /**
         * ChessBoard Constructor
         * Purpose: Initializes the interactive chess board with enhanced mouse handling
         */
        public ChessBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
            setBackground(Color.WHITE);
            
            // Mouse listener for clicks and drag operations
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        handleCellClick(row, col);
                    }
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    // Don't allow any interaction if game is over
                    if (gameSession.isGameOver()) {
                        return;
                    }
                    
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        Piece piece = gameSession.getBoard().getPieceAt(row, col);
                        if (piece != null && piece.getColor() == gameSession.getCurrentTurn()) {
                            // Start dragging
                            isDragging = true;
                            dragStartRow = row;
                            dragStartCol = col;
                            
                            // Calculate offset from piece center
                            int pieceX = col * CELL_SIZE + CELL_SIZE / 2;
                            int pieceY = row * CELL_SIZE + CELL_SIZE / 2;
                            dragOffset.x = e.getX() - pieceX;
                            dragOffset.y = e.getY() - pieceY;
                            
                            currentDragPosition.x = e.getX();
                            currentDragPosition.y = e.getY();
                            
                            // Also select the piece for visual feedback
                            selectedRow = row;
                            selectedCol = col;
                            repaint();
                        }
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDragging) {
                        int col = e.getX() / CELL_SIZE;
                        int row = e.getY() / CELL_SIZE;
                        
                        // Check if game is over before allowing drag move
                        if (gameSession.isGameOver()) {
                            // Game is over, don't allow move
                        } else if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                            if (row != dragStartRow || col != dragStartCol) {
                                // Try to move the piece
                                boolean moved = gameSession.playMove(dragStartRow, dragStartCol, row, col);
                                if (moved) {
                                    updateStatus();
                                }
                            }
                        }
                        
                        // Reset drag state
                        isDragging = false;
                        dragStartRow = -1;
                        dragStartCol = -1;
                        selectedRow = -1;
                        selectedCol = -1;
                        repaint();
                    }
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDragging) {
                        currentDragPosition.x = e.getX();
                        currentDragPosition.y = e.getY();
                        repaint();
                    }
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        // Handle cell click for selecting/moving pieces
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
        
        // Reset selection
        public void resetSelection() {
            selectedRow = -1;
            selectedCol = -1;
        }
        
        // Paint component
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
                    
                    // Highlight selected cell (for click-and-move)
                    if (row == selectedRow && col == selectedCol && !isDragging) {
                        cellColor = new Color(255, 255, 0, 128); // Yellow highlight
                    }
                    
                    g2d.setColor(cellColor);
                    g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // Show drop target during drag operation
                    if (isDragging) {
                        Point mousePos = getMousePosition();
                        if (mousePos != null) {
                            int hoverCol = mousePos.x / CELL_SIZE;
                            int hoverRow = mousePos.y / CELL_SIZE;
                            if (hoverRow == row && hoverCol == col && 
                                hoverRow >= 0 && hoverRow < BOARD_SIZE && 
                                hoverCol >= 0 && hoverCol < BOARD_SIZE) {
                                // Highlight potential drop target
                                g2d.setColor(new Color(0, 255, 0, 100)); // Green highlight
                                g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            }
                        }
                    }
                    
                    // Draw cell borders
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // Draw pieces (from Console Board)
            drawPieces(g2d);
        }
        
        // Draw pieces on board
        private void drawPieces(Graphics2D g2d) {
            g2d.setFont(new Font("Serif", Font.BOLD, 48));
            
            // Iterate over board squares
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Piece piece = gameSession.getBoard().getPieceAt(row, col);
                    if (piece != null) {
                        // Don't draw the piece at its original position if it's being dragged
                        if (isDragging && row == dragStartRow && col == dragStartCol) {
                            continue; // Skip drawing this piece as it's being dragged
                        }
                        drawPiece(g2d, piece, row, col);
                    }
                }
            }
            
            // Draw the dragged piece at current cursor position
            if (isDragging && dragStartRow >= 0 && dragStartCol >= 0) {
                Piece draggedPiece = gameSession.getBoard().getPieceAt(dragStartRow, dragStartCol);
                if (draggedPiece != null) {
                    drawDraggedPiece(g2d, draggedPiece, currentDragPosition);
                }
            }
        }
        
        // Draw individual piece
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
        
        // Draw a piece being dragged at the cursor position
        private void drawDraggedPiece(Graphics2D g2d, Piece piece, Point position) {
            String symbol = getPieceSymbol(piece);
            
            // Set color with slight transparency to show it's being dragged
            Color pieceColor = (piece.getColor() == PieceColor.WHITE) ? 
                new Color(255, 255, 255, 200) : new Color(0, 0, 0, 200);
            Color outlineColor = (piece.getColor() == PieceColor.WHITE) ? 
                new Color(0, 0, 0, 200) : new Color(255, 255, 255, 200);
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int textHeight = fm.getAscent();
            
            // Center the piece on cursor position, accounting for drag offset
            int x = position.x - dragOffset.x - textWidth / 2;
            int y = position.y - dragOffset.y + textHeight / 2;
            
            // Draw outline for better visibility
            g2d.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(symbol, x + dx, y + dy);
                    }
                }
            }
            
            // Draw the dragged piece
            g2d.setColor(pieceColor);
            g2d.drawString(symbol, x, y);
        }
        
        // Get Unicode symbol for piece
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
