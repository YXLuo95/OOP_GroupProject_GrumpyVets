package GUI;

import java.awt.*;
import javax.swing.*;
import logic.Board;
import logic.GameSession;
import logic.Rules;
import network.GameMove;
import network.MultiplayerSession;
import objects.*;

/**
 * Multiplayer Chess Game GUI Window
 * 
 * Purpose: Provides a complete multiplayer chess interface with network connectivity options.
 * Features: 
 * - Chess board rendering with drag-and-drop and click-to-move interaction
 * - Network connection controls (host/join/disconnect)
 * - Real-time chat functionality
 * - Back to main menu navigation
 * - Ensures proper chess board rendering even without network connection
 */
public class MultiplayerFrame extends JFrame {
    
    // Game Logic Components
    // Purpose: Core chess game management and board rendering
    private GameSession gameSession;  // Handles chess rules, moves, and game state
    private BoardView chessBoard;    // Reusable board view
    
    // User Interface Components
    // Purpose: Network connectivity and user interaction controls
    private JLabel statusLabel;       // Displays current game status and move information
    private JButton hostButton;       // Button to start hosting a game
    private JButton joinButton;       // Button to join an existing game
    private JButton disconnectButton; // Button to disconnect from network game
    private JTextField hostIpField;   // Input field for target IP address
    private JTextField portField;     // Input field for network port number
    private JTextArea chatArea;       // Display area for chat messages
    private JTextField chatInput;     // Input field for typing chat messages
    private JButton sendChatButton;   // Button to send chat messages
    private JLabel connectionLabel;   // Shows current connection status
    
    // Network Components
    private MultiplayerSession multiplayerSession;
    
    // Selection is handled internally by BoardView
    
    /**
     * Constructor - Initializes the multiplayer chess window
     * Purpose: Sets up the main window properties and initializes all components
     */
    public MultiplayerFrame() {
        super("Chess - Multiplayer");
        initializeComponents();  // Create and configure all UI components
        setupLayout();          // Arrange components in the window layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);     // Set window dimensions (width x height)
        setLocationRelativeTo(null);  // Center window on screen
        setResizable(false);    // Prevent window resizing for consistent layout
    }
    
    /**
     * Component Initialization Method
     * Purpose: Creates and configures all game components and UI elements
     */
    private void initializeComponents() {
        // Game Session Setup - ensures chess board renders properly even without network connection
        Board board = new Board();           // Create new chess board with standard setup
        gameSession = new GameSession(board); // Initialize game logic controller
        gameSession.start();                 // Start game to properly initialize piece positions
        
        // Network Connection Components
        // Purpose: Allow users to host or join multiplayer games
        hostIpField = new JTextField("localhost", 12);    // Input for target IP address
        portField = new JTextField("8888", 6);            // Input for network port
        hostButton = new JButton("Host Game");            // Button to start hosting
        joinButton = new JButton("Join Game");            // Button to join existing game
        disconnectButton = new JButton("Disconnect");     // Button to disconnect from network
        disconnectButton.setEnabled(false);              // Initially disabled (not connected)
        connectionLabel = new JLabel("Not connected", SwingConstants.CENTER); // Shows connection status
        
        // Status Display Component
        // Purpose: Provides real-time feedback about game state and moves
        statusLabel = new JLabel("Ready to start multiplayer game", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Chess Board Component (reusable)
        chessBoard = new BoardView(
            () -> gameSession.getBoard(),
            () -> gameSession.getCurrentTurn(),
            () -> gameSession.isGameOver(),
            (sr, sc, er, ec) -> gameSession.playMove(sr, sc, er, ec),
            (sr, sc, er, ec) -> {
                updateGameStatus();
                chessBoard.repaint();
                // Send move to opponent if connected
                if (multiplayerSession != null && multiplayerSession.isConnected()) {
                    try {
                        multiplayerSession.makeMove(sr, sc, er, ec);
                        addChatMessage("System", "Move sent to opponent");
                    } catch (Exception ex) {
                        addChatMessage("System", "Failed to send move: " + ex.getMessage());
                    }
                }
            }
        );
        
        // Chat System Components  
        // Purpose: Enable real-time communication between players
        chatArea = new JTextArea(15, 25);                 // Display area for chat messages
        chatArea.setEditable(false);                      // Read-only message display
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatArea.setBackground(new Color(248, 248, 248)); // Light gray background
        
        chatInput = new JTextField(20);                   // Input field for typing messages
        chatInput.addActionListener(e -> sendChatMessage()); // Send message on Enter key
        
        sendChatButton = new JButton("Send");              // Button to send chat messages
        sendChatButton.addActionListener(e -> sendChatMessage());
        
        setupButtonActions(); // Configure button event handlers
    }
    
    /**
     * Button Action Setup Method
     * Purpose: Configures event handlers for all interactive buttons
     */
    private void setupButtonActions() {
        hostButton.addActionListener(e -> hostGame());       // Host a new multiplayer game
        joinButton.addActionListener(e -> joinGame());       // Join an existing game
        disconnectButton.addActionListener(e -> disconnect()); // Disconnect from current game
    }
    
    /**
     * Layout Configuration Method  
     * Purpose: Arranges all UI components in organized panels and sections
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top Section - Connection controls and menu bar
        JPanel topContainer = new JPanel(new BorderLayout());
        
        // Menu Bar Setup
        // Purpose: Provides navigation options and game controls
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem backToMenuItem = new JMenuItem("Back to Main Menu");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        // Menu Action Handlers
        backToMenuItem.addActionListener(e -> backToMainMenu()); // Return to main menu
        exitItem.addActionListener(e -> System.exit(0));        // Close application
        
        gameMenu.add(backToMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        
        // Network Connection Panel
        // Purpose: Contains controls for hosting and joining multiplayer games
        JPanel connectionPanel = new JPanel(new FlowLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Network Connection"));
        
        connectionPanel.add(new JLabel("Host IP:"));  // Label for IP input
        connectionPanel.add(hostIpField);             // IP address input field
        connectionPanel.add(new JLabel("Port:"));     // Label for port input
        connectionPanel.add(portField);               // Port number input field
        
        connectionPanel.add(hostButton);
        connectionPanel.add(joinButton);
        connectionPanel.add(disconnectButton);
        
        // Add back to main menu button
        JButton backToMenuButton = new JButton("Back to Main Menu");
        backToMenuButton.setBackground(new Color(220, 53, 69)); // Red background
        backToMenuButton.setForeground(Color.BLACK);
        backToMenuButton.setFocusPainted(false);
        backToMenuButton.addActionListener(e -> backToMainMenu());
        connectionPanel.add(backToMenuButton);
        
        topContainer.add(connectionPanel, BorderLayout.CENTER);
        topContainer.add(connectionLabel, BorderLayout.SOUTH);
        
        add(topContainer, BorderLayout.NORTH);
        
        // Center Section - Chess Board
        // Purpose: Main game area with interactive chess board
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Chess Board"));
        centerPanel.add(chessBoard); // Add the custom chess board component
        add(centerPanel, BorderLayout.CENTER);
        
        // Right Section - Chat Panel
        // Purpose: Real-time communication interface between players
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        rightPanel.setPreferredSize(new Dimension(300, 0)); // Fixed width for consistent layout
        
        JScrollPane chatScroll = new JScrollPane(chatArea); // Scrollable chat display
        rightPanel.add(chatScroll, BorderLayout.CENTER);
        
        // Chat Input Area
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);      // Message input field
        chatInputPanel.add(sendChatButton, BorderLayout.EAST);   // Send button
        rightPanel.add(chatInputPanel, BorderLayout.SOUTH);
        
        add(rightPanel, BorderLayout.EAST);
        
        // Bottom Section - Status Bar
        // Purpose: Displays current game status and move information
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Host Game Method
     * Purpose: Starts hosting a multiplayer game on the specified port
     * Note: Currently implements UI updates, actual network hosting logic to be implemented
     */
    private void hostGame() {
        try {
            int port = Integer.parseInt(portField.getText()); // Parse port number from input
            statusLabel.setText("Hosting game on port " + port + "...");
            
            // Update UI state for hosting mode
            hostButton.setEnabled(false);      // Disable host button during hosting
            joinButton.setEnabled(false);     // Disable join button during hosting
            disconnectButton.setEnabled(true); // Enable disconnect option
            connectionLabel.setText("Waiting for players...");
            addChatMessage("System", "Hosting game on port " + port);
            
            // Initialize multiplayer session and start hosting
            try {
                multiplayerSession = new MultiplayerSession(gameSession.getBoard(), "Host_" + System.currentTimeMillis());
                multiplayerSession.setGameStateCallback(createGameStateCallback());
                boolean success = multiplayerSession.hostGame(port);
                if (!success) {
                    throw new RuntimeException("Failed to start hosting");
                }
                addChatMessage("System", "Successfully hosting on port " + port);
            } catch (Exception ex) {
                addChatMessage("System", "Hosting failed: " + ex.getMessage());
                // Reset UI on failure
                hostButton.setEnabled(true);
                joinButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                connectionLabel.setText("Not connected");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Join Game Method
     * Purpose: Attempts to connect to an existing multiplayer game
     * Note: Currently implements UI updates, actual network connection logic to be implemented
     */
    private void joinGame() {
        try {
            String hostIp = hostIpField.getText(); // Get target IP address
            int port = Integer.parseInt(portField.getText()); // Parse port number
            statusLabel.setText("Connecting to " + hostIp + ":" + port + "...");
            
            // Update UI state for connecting mode
            hostButton.setEnabled(false);      // Disable host button during connection
            joinButton.setEnabled(false);     // Disable join button during connection
            disconnectButton.setEnabled(true); // Enable disconnect option
            connectionLabel.setText("Connecting...");
            addChatMessage("System", "Attempting to connect to " + hostIp + ":" + port);
            
            // Initialize multiplayer session and connect to host
            try {
                multiplayerSession = new MultiplayerSession(gameSession.getBoard(), "Client_" + System.currentTimeMillis());
                multiplayerSession.setGameStateCallback(createGameStateCallback());
                boolean success = multiplayerSession.joinGame(hostIp, port);
                if (!success) {
                    throw new RuntimeException("Failed to connect to host");
                }
                addChatMessage("System", "Successfully connected to " + hostIp + ":" + port);
            } catch (Exception ex) {
                addChatMessage("System", "Connection failed: " + ex.getMessage());
                // Reset UI on failure
                hostButton.setEnabled(true);
                joinButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                connectionLabel.setText("Not connected");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnect() {
        statusLabel.setText("Disconnected");
        hostButton.setEnabled(true);
        joinButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        connectionLabel.setText("Not connected");
        addChatMessage("System", "Disconnected from game");
        
        // Close network connection if exists
        if (multiplayerSession != null) {
            multiplayerSession.disconnect();
            multiplayerSession = null;
            addChatMessage("System", "Network connection closed");
        }
    }
    
    // Square click is handled inside BoardView
    
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            addChatMessage("You", message);
            chatInput.setText("");
            // Send message to opponent if connected
            if (multiplayerSession != null && multiplayerSession.isConnected()) {
                try {
                    multiplayerSession.sendChat(message);
                } catch (Exception ex) {
                    addChatMessage("System", "Failed to send message: " + ex.getMessage());
                }
            }
        }
    }
    
    private void addChatMessage(String sender, String message) {
        chatArea.append("[" + sender + "]: " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void backToMainMenu() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to return to the main menu?\nAny ongoing game will be lost.",
            "Back to Main Menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // cleanup network connection
            disconnect();
            
            // Close current window
            dispose();
            
            // Try to open main menu
            SwingUtilities.invokeLater(() -> {
                try {
                    // Directly create main menu window instance
                    new MainMenuFrame().setVisible(true);
                } catch (Exception ex) {
                    System.err.println("Could not open main menu: " + ex.getMessage());
                    // return something to prevent hang
                }
            });
        }
    }
    
    /**
     * Update game status and check for game over conditions
     */
    private void updateGameStatus() {
        if (gameSession == null) return;
        statusLabel.setText(StatusText.forSession(gameSession));
        if (gameSession.isGameOver()) {
            PieceColor currentPlayer = gameSession.getCurrentTurn();
            PieceColor opponent = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            if (Rules.isCheckmate(gameSession.getBoard(), currentPlayer)) {
                String winner = (opponent == PieceColor.WHITE) ? "White" : "Black";
                showGameOverDialog("Checkmate!", winner + " wins!");
            } else if (Rules.isStalemate(gameSession.getBoard(), currentPlayer)) {
                showGameOverDialog("Stalemate!", "It's a draw!");
            } else {
                showGameOverDialog("Game Over", "Game has ended.");
            }
        }
    }
    
    /**
     * Shows a popup dialog to declare the winner or game result (Multiplayer version)
     */
    private void showGameOverDialog(String title, String message) {
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
     * Start a new multiplayer game
     */
    private void startNewGame() {
        if (gameSession != null) {
            gameSession.start();           // Reset game to initial state
            updateGameStatus();            // Update status display
            chessBoard.repaint();          // Redraw the board
        }
    }
    
    // BoardView handles painting and interaction; no inner class needed
    
    /**
     * Create callback for handling network events
     */
    private MultiplayerSession.GameStateCallback createGameStateCallback() {
        return new MultiplayerSession.GameStateCallback() {
            @Override
            public void onGameStateChanged(String state) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(state);
                    addChatMessage("System", "Game state: " + state);
                });
            }
            
            @Override
            public void onChatReceived(String message) {
                SwingUtilities.invokeLater(() -> {
                    addChatMessage("Opponent", message);
                });
            }
            
            @Override
            public void onOpponentMove(GameMove move) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        boolean success = gameSession.playMove(move.getStartRow(), move.getStartCol(), 
                                                             move.getEndRow(), move.getEndCol());
                        if (success) {
                            updateGameStatus();
                            chessBoard.repaint();
                            addChatMessage("System", "Opponent moved");
                        }
                    } catch (Exception e) {
                        addChatMessage("System", "Error processing opponent move: " + e.getMessage());
                    }
                });
            }
            
            @Override
            public void onConnectionChanged(boolean connected) {
                SwingUtilities.invokeLater(() -> {
                    if (connected) {
                        connectionLabel.setText("Connected");
                        addChatMessage("System", "Connected to opponent");
                    } else {
                        connectionLabel.setText("Disconnected");
                        addChatMessage("System", "Lost connection");
                    }
                });
            }
            
            @Override
            public void onTurnChanged(boolean myTurn) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(myTurn ? "Your turn" : "Opponent's turn");
                });
            }
        };
    }
    
    // for testing purposes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiplayerFrame().setVisible(true);
        });
    }
}