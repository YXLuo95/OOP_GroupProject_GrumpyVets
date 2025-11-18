package GUI;

import java.awt.*;
import javax.swing.*;
import logic.Board;
import logic.GameSave;
import logic.GameSession;
import logic.Rules;
import objects.*;

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
    private BoardView chessBoard;    // Reusable interactive chess board component
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
        chessBoard = new BoardView(
            () -> gameSession.getBoard(),
            () -> gameSession.getCurrentTurn(),
            () -> gameSession.isGameOver(),
            (sr, sc, er, ec) -> gameSession.playMove(sr, sc, er, ec),
            (sr, sc, er, ec) -> {
                updateStatus();
                chessBoard.repaint();
            }
        );
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

}
