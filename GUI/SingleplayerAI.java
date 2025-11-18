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
 * Single Player Chess Game GUI (with AI hooks)
 *
 * This class preserves the original Singleplayer UI/behavior and adds an AI opponent
 * interface so you can plug in an AI without changing this file again.
 */
public class SingleplayerAI extends JFrame {

    // ============= AI Opponent Interface & Hooks =============
    /**
     * A minimal AI interface: return a move as {startRow,startCol,endRow,endCol}.
     * Return null when no legal move is available.
     */
    public interface AIOpponent {
        int[] chooseMove(Board board, PieceColor aiColor);
    }

    private AIOpponent aiOpponent = null;
    private PieceColor aiColor = null; // The side controlled by AI (WHITE/BLACK) or null when disabled

    /** Configure AI opponent (pass null to disable). */
    public void setAIOpponent(AIOpponent opponent, PieceColor color) {
        this.aiOpponent = opponent;
        this.aiColor = opponent == null ? null : color;
        maybeMakeAIMove();
    }

    // Core Game Components
    private ChessBoard chessBoard;
    private GameSession gameSession;
    private JLabel statusLabel;

    public SingleplayerAI() {
        super("Chess - Single Player (AI Ready)");
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        Board board = new Board();
        gameSession = new GameSession(board);
        gameSession.start();
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);

        chessBoard = new ChessBoard();
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(chessBoard);
        add(centerPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("White to move", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);

        setSize(700, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        updateStatus();
        // If AI plays white, let it start immediately
        maybeMakeAIMove();
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> dispose());
        toolbar.add(backButton);

        toolbar.addSeparator();

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameSession.start();
            chessBoard.resetSelection();
            updateStatus();
            chessBoard.repaint();
            // If AI is configured and should move first, trigger it
            maybeMakeAIMove();
        });
        toolbar.add(newGameButton);

        toolbar.addSeparator();

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (gameSession.undo()) {
                chessBoard.resetSelection();
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(undoButton);

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> {
            if (gameSession.redo()) {
                chessBoard.resetSelection();
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(redoButton);

        toolbar.addSeparator();

        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> saveGame());
        toolbar.add(saveButton);

        return toolbar;
    }

    private void updateStatus() {
        PieceColor currentTurn = gameSession.getCurrentTurn();
        String turnText = (currentTurn == PieceColor.WHITE) ? "White" : "Black";

        if (gameSession.isGameOver()) {
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
                case 0:
                    startNewGame();
                    break;
                case 1:
                    backToMainMenu();
                    break;
                case 2:
                case JOptionPane.CLOSED_OPTION:
                    System.exit(0);
                    break;
            }
        });
    }

    private void startNewGame() {
        gameSession.start();
        chessBoard.resetSelection();
        updateStatus();
        chessBoard.repaint();
        maybeMakeAIMove();
    }

    private void backToMainMenu() {
        dispose();
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
     * If an AI is configured and it's AI's turn, query AI and make a move.
     * This runs on the EDT synchronously; for heavy AIs, move to a worker thread.
     */
    private void maybeMakeAIMove() {
        if (aiOpponent == null || aiColor == null) return;
        if (gameSession.isGameOver()) return;
        if (gameSession.getCurrentTurn() != aiColor) return;

        int[] mv = aiOpponent.chooseMove(gameSession.getBoard(), aiColor);
        if (mv != null && mv.length == 4) {
            boolean moved = gameSession.playMove(mv[0], mv[1], mv[2], mv[3]);
            if (moved) {
                updateStatus();
                chessBoard.resetSelection();
                chessBoard.repaint();
            }
        }
    }

    // ===================== Inner Board Component =====================
    private class ChessBoard extends JPanel {
        private final int BOARD_SIZE = 8;
        private final int CELL_SIZE = 75;

        private int selectedRow = -1;
        private int selectedCol = -1;

        private boolean isDragging = false;
        private int dragStartRow = -1;
        private int dragStartCol = -1;
        private Point dragOffset = new Point();
        private Point currentDragPosition = new Point();

        public ChessBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
            setBackground(Color.WHITE);

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
                    if (gameSession.isGameOver()) return;

                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;

                    if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                        Piece piece = gameSession.getBoard().getPieceAt(row, col);
                        if (piece != null && piece.getColor() == gameSession.getCurrentTurn()) {
                            isDragging = true;
                            dragStartRow = row;
                            dragStartCol = col;

                            int pieceX = col * CELL_SIZE + CELL_SIZE / 2;
                            int pieceY = row * CELL_SIZE + CELL_SIZE / 2;
                            dragOffset.x = e.getX() - pieceX;
                            dragOffset.y = e.getY() - pieceY;

                            currentDragPosition.x = e.getX();
                            currentDragPosition.y = e.getY();

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

                        if (!gameSession.isGameOver() &&
                            row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                            if (row != dragStartRow || col != dragStartCol) {
                                boolean moved = gameSession.playMove(dragStartRow, dragStartCol, row, col);
                                if (moved) {
                                    updateStatus();
                                    // If AI is configured and it's AI's turn now, make AI move
                                    maybeMakeAIMove();
                                }
                            }
                        }

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

        private void handleCellClick(int row, int col) {
            if (gameSession.isGameOver()) return;

            if (selectedRow == -1) {
                Piece piece = gameSession.getBoard().getPieceAt(row, col);
                if (piece != null && piece.getColor() == gameSession.getCurrentTurn()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            } else {
                if (selectedRow == row && selectedCol == col) {
                    selectedRow = -1;
                    selectedCol = -1;
                } else {
                    boolean moved = gameSession.playMove(selectedRow, selectedCol, row, col);
                    if (moved) {
                        updateStatus();
                        // Trigger AI if needed
                        maybeMakeAIMove();
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

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Color cellColor;
                    if ((row + col) % 2 == 0) cellColor = new Color(240, 217, 181);
                    else cellColor = new Color(181, 136, 99);

                    if (row == selectedRow && col == selectedCol && !isDragging) {
                        cellColor = new Color(255, 255, 0, 128);
                    }

                    g2d.setColor(cellColor);
                    g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    if (isDragging) {
                        Point mousePos = getMousePosition();
                        if (mousePos != null) {
                            int hoverCol = mousePos.x / CELL_SIZE;
                            int hoverRow = mousePos.y / CELL_SIZE;
                            if (hoverRow == row && hoverCol == col &&
                                hoverRow >= 0 && hoverRow < BOARD_SIZE &&
                                hoverCol >= 0 && hoverCol < BOARD_SIZE) {
                                g2d.setColor(new Color(0, 255, 0, 100));
                                g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            }
                        }
                    }

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }

            drawPieces(g2d);
        }

        private void drawPieces(Graphics2D g2d) {
            g2d.setFont(new Font("Serif", Font.BOLD, 48));

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Piece piece = gameSession.getBoard().getPieceAt(row, col);
                    if (piece != null) {
                        if (isDragging && row == dragStartRow && col == dragStartCol) continue;
                        drawPiece(g2d, piece, row, col);
                    }
                }
            }

            if (isDragging && dragStartRow >= 0 && dragStartCol >= 0) {
                Piece draggedPiece = gameSession.getBoard().getPieceAt(dragStartRow, dragStartCol);
                if (draggedPiece != null) {
                    drawDraggedPiece(g2d, draggedPiece, currentDragPosition);
                }
            }
        }

        private void drawPiece(Graphics2D g2d, Piece piece, int row, int col) {
            String symbol = getPieceSymbol(piece);

            Color pieceColor = (piece.getColor() == PieceColor.WHITE) ? Color.WHITE : Color.BLACK;
            Color outlineColor = (piece.getColor() == PieceColor.WHITE) ? Color.BLACK : Color.WHITE;

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int textHeight = fm.getAscent();

            int x = col * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
            int y = row * CELL_SIZE + (CELL_SIZE + textHeight) / 2;

            g2d.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) g2d.drawString(symbol, x + dx, y + dy);
                }
            }

            g2d.setColor(pieceColor);
            g2d.drawString(symbol, x, y);
        }

        private void drawDraggedPiece(Graphics2D g2d, Piece piece, Point position) {
            String symbol = getPieceSymbol(piece);

            Color pieceColor = (piece.getColor() == PieceColor.WHITE) ?
                new Color(255, 255, 255, 200) : new Color(0, 0, 0, 200);
            Color outlineColor = (piece.getColor() == PieceColor.WHITE) ?
                new Color(0, 0, 0, 200) : new Color(255, 255, 255, 200);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(symbol);
            int textHeight = fm.getAscent();

            int x = position.x - dragOffset.x - textWidth / 2;
            int y = position.y - dragOffset.y + textHeight / 2;

            g2d.setColor(outlineColor);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(symbol, x + dx, y + dy);
                    }
                }
            }

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
