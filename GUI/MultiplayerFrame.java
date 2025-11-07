package GUI;

import logic.GameSession;
import logic.Board;
import objects.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private ChessBoard chessBoard;    // Custom panel for interactive chess board display
    
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
    
    // Chess Piece Selection State
    // Purpose: Tracks which piece is currently selected for click-and-move interaction
    private int selectedRow = -1;     // Row of currently selected piece (-1 = none)
    private int selectedCol = -1;     // Column of currently selected piece (-1 = none)
    
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
        
        // Chess Board Component
        // Purpose: Interactive game board supporting both click-and-move and drag-and-drop
        chessBoard = new ChessBoard();
        
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
        
        // 添加回到主菜单按钮
        JButton backToMenuButton = new JButton("Back to Main Menu");
        backToMenuButton.setBackground(new Color(220, 53, 69)); // 红色背景
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
            
            // TODO: Implement actual network hosting logic
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
            
            // TODO: Implement actual network connection logic
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
        // TODO: 实际的断开连接逻辑
    }
    
    private void handleSquareClick(int row, int col) {
        if (gameSession == null) return;
        
        if (selectedRow == -1) {
            // 选择棋子
            Piece piece = gameSession.getBoard().getPieceAt(row, col);
            
            if (piece != null && piece.getColor() == gameSession.getCurrentTurn()) {
                selectedRow = row;
                selectedCol = col;
                chessBoard.repaint(); // 重新绘制以显示选择
                statusLabel.setText("Selected piece at " + (char)('a' + col) + (8 - row) + ". Click destination.");
            }
        } else {
            // 尝试移动
            try {
                boolean success = gameSession.playMove(selectedRow, selectedCol, row, col);
                if (success) {
                    statusLabel.setText("Move: " + (char)('a' + selectedCol) + (8 - selectedRow) + 
                                      " to " + (char)('a' + col) + (8 - row));
                    // TODO: 发送移动到对手
                } else {
                    statusLabel.setText("Invalid move. Try again.");
                }
            } catch (Exception e) {
                statusLabel.setText("Move failed: " + e.getMessage());
            }
            
            // 重置选择
            selectedRow = -1;
            selectedCol = -1;
            chessBoard.repaint();
        }
    }
    
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            addChatMessage("You", message);
            chatInput.setText("");
            // TODO: 发送消息到对手
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
            // 清理网络连接
            disconnect();
            
            // 关闭当前窗口
            dispose();
            
            // 尝试打开主菜单
            SwingUtilities.invokeLater(() -> {
                try {
                    Class<?> mainMenuClass = Class.forName("MainMenuApp");
                    Object mainMenu = mainMenuClass.getDeclaredConstructor().newInstance();
                    mainMenuClass.getMethod("setVisible", boolean.class).invoke(mainMenu, true);
                } catch (Exception ex) {
                    System.err.println("Could not open main menu: " + ex.getMessage());
                    // 如果无法打开主菜单，至少不要让程序挂起
                }
            });
        }
    }
    
    /**
     * Chess Board Inner Class
     * 
     * Purpose: Handles chess board rendering and user interaction processing
     * Features:
     * - Supports both click-and-move and drag-and-drop piece movement
     * - Real-time visual feedback during piece manipulation
     * - Integrates with chess game logic for move validation
     * - Provides smooth user experience with highlighted squares and piece tracking
     */
    private class ChessBoard extends JPanel {
        // Board Display Constants
        private static final int BOARD_SIZE = 480;        // Total board size in pixels
        private static final int CELL_SIZE = BOARD_SIZE / 8; // Individual square size (60px)
        
        // Drag and Drop State Variables
        // Purpose: Track piece dragging operations and mouse interactions
        private boolean isDragging = false;           // Flag indicating if a piece is being dragged
        private int dragStartRow = -1;               // Starting row of dragged piece (-1 = none)
        private int dragStartCol = -1;               // Starting column of dragged piece (-1 = none)
        private Point dragOffset = new Point();      // Mouse offset from piece center during drag
        private Point currentDragPosition = new Point(); // Current mouse position during drag
        
        public ChessBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
            setBackground(Color.WHITE);
            
            // Enhanced mouse handler supporting both click-and-move and drag-and-drop
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                        handleSquareClick(row, col);
                    }
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    if (gameSession == null) return;
                    
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    if (row >= 0 && row < 8 && col >= 0 && col < 8) {
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
                    if (isDragging && gameSession != null) {
                        int col = e.getX() / CELL_SIZE;
                        int row = e.getY() / CELL_SIZE;
                        
                        // Attempt to drop the piece
                        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                            if (row != dragStartRow || col != dragStartCol) {
                                // Try to move the piece
                                boolean moved = gameSession.playMove(dragStartRow, dragStartCol, row, col);
                                if (moved) {
                                    statusLabel.setText("Move: " + (char)('a' + dragStartCol) + (8 - dragStartRow) + 
                                                      " to " + (char)('a' + col) + (8 - row));
                                } else {
                                    statusLabel.setText("Invalid move. Try again.");
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
        
        /**
         * Paint Component Override Method
         * Purpose: Custom rendering of the complete chess board interface
         * Called automatically by Swing when the component needs to be redrawn
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw chess board squares and highlights
            drawBoard(g2d);
            
            // Draw chess pieces (works even without network connection)
            if (gameSession != null) {
                drawPieces(g2d);
            }
            
            // Draw selection highlight for click-and-move mode
            if (selectedRow >= 0 && selectedCol >= 0) {
                drawSelection(g2d);
            }
        }
        
        /**
         * Board Drawing Method
         * Purpose: Renders the 8x8 chess board with alternating colors and interactive highlights
         */
        private void drawBoard(Graphics2D g2d) {
            Color lightBrown = new Color(240, 217, 181); // Light square color
            Color darkBrown = new Color(181, 136, 99);   // Dark square color
            
            // Draw each square with appropriate coloring and highlights
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    // Standard alternating square colors
                    Color color = (row + col) % 2 == 0 ? lightBrown : darkBrown;
                    
                    // Highlight selected square for click-and-move interaction
                    if (row == selectedRow && col == selectedCol && !isDragging) {
                        color = new Color(255, 255, 0, 128); // Semi-transparent yellow highlight
                    }
                    
                    g2d.setColor(color);
                    g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // Show potential drop target during drag operation
                    if (isDragging) {
                        Point mousePos = getMousePosition(); // Get current mouse position
                        if (mousePos != null) {
                            int hoverCol = mousePos.x / CELL_SIZE;
                            int hoverRow = mousePos.y / CELL_SIZE;
                            if (hoverRow == row && hoverCol == col && 
                                hoverRow >= 0 && hoverRow < 8 && 
                                hoverCol >= 0 && hoverCol < 8) {
                                // Highlight potential drop target
                                g2d.setColor(new Color(0, 255, 0, 100)); // Green highlight
                                g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            }
                        }
                    }
                }
            }
            
            // 绘制边框
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, BOARD_SIZE, BOARD_SIZE);
        }
        
        private void drawPieces(Graphics2D g2d) {
            g2d.setFont(new Font("Serif", Font.BOLD, 48));
            
            Board board = gameSession.getBoard();
            if (board == null) return;
            
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = board.getPieceAt(row, col);
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
        
        private void drawPiece(Graphics2D g2d, Piece piece, int row, int col) {
            String symbol = getPieceSymbol(piece);
            
            // set colors based on piece color
            Color pieceColor = (piece.getColor() == PieceColor.WHITE) ? Color.WHITE : Color.BLACK;
            Color outlineColor = (piece.getColor() == PieceColor.WHITE) ? Color.BLACK : Color.WHITE;
            
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int textHeight = fm.getAscent();
            
            int x = col * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
            int y = row * CELL_SIZE + (CELL_SIZE + textHeight) / 2;
            
            // draw outline for better visibility
            g2d.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(symbol, x + dx, y + dy);
                    }
                }
            }
            
            // draw the piece
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
        
        private void drawSelection(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 0, 100)); // 半透明黄色
            g2d.fillRect(selectedCol * CELL_SIZE, selectedRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(selectedCol * CELL_SIZE, selectedRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        private String getPieceSymbol(Piece piece) {
            boolean isWhite = piece.getColor() == PieceColor.WHITE;
            
            switch (piece.getType()) {
                case PAWN:
                    return isWhite ? "♙" : "♟";
                case ROOK:
                    return isWhite ? "♖" : "♜";
                case KNIGHT:
                    return isWhite ? "♘" : "♞";
                case BISHOP:
                    return isWhite ? "♗" : "♝";
                case QUEEN:
                    return isWhite ? "♕" : "♛";
                case KING:
                    return isWhite ? "♔" : "♚";
                default:
                    return "?";
            }
        }
    }
    
    // for testing purposes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiplayerFrame().setVisible(true);
        });
    }
}