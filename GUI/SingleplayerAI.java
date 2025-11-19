package GUI;

import java.awt.*;
import javax.swing.*;
import logic.Board;
import logic.GameSave;
import logic.GameSession;
import logic.Rules;
import objects.*;

/**
 * Single Player Chess Game GUI (with AI hooks)
 *
 * This class preserves the original single-player UI/behavior and adds an AI opponent
 * interface so you can plug in an AI without changing this file again.
 *
 *   summary:
 * - Provides hook points for an {@link AIOpponent} to make moves automatically.
 * - Set the AI instance and its color; when it's its turn, it will be asked for a move.
 * - Does not implement any AI logic here—only wiring, triggering, and UI integration.
 */
public class SingleplayerAI extends JFrame {

    // ============= AI Opponent Interface & Hooks =============
    /**
     * A minimal AI interface: return a move as {startRow,startCol,endRow,endCol}.
     * Return null when no legal move is available.
     */
    private AIOpponent aiOpponent = null;
    /**
     * Color controlled by the AI (WHITE/BLACK); null means AI disabled.
     */
    private PieceColor aiColor = null; // The side controlled by AI (WHITE/BLACK) or null when disabled

    /**
     * Configure or disable the AI opponent.
     *
     * @param opponent AI instance; pass null to disable
     * @param color    Color the AI will play (ignored if opponent is null)
     */
    public void setAIOpponent(AIOpponent opponent, PieceColor color) {
        this.aiOpponent = opponent;
        this.aiColor = opponent == null ? null : color;
        maybeMakeAIMove();
    }

    // Core Game Components
    /**
     * Board view component: renders the board/pieces and handles user interaction.
     */
    private BoardView chessBoard;
    /**
     * Game session encapsulating board state, move history, undo/redo logic.
     */
    private GameSession gameSession;
    /**
     * Status label at bottom: current turn, check/checkmate/draw info.
     */
    private JLabel statusLabel;

    /**
     * Constructor: initializes game and builds UI.
     */
    public SingleplayerAI() {
        super("Chess - Single Player (AI Ready)");
        initializeGame();
        setupUI();
    }

    /**
     * Initialize game state: create board and session, then start.
     */
    private void initializeGame() {
        Board board = new Board();
        gameSession = new GameSession(board);
        gameSession.start();
    }

    /**
     * Build and lay out Swing UI: toolbar, board panel, status bar.
     * If AI plays white, attempts AI move immediately after setup.
     */
    private void setupUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);

        chessBoard = new BoardView(
            () -> gameSession.getBoard(),      // Supplier: current board
            () -> gameSession.getCurrentTurn(), // Supplier: whose turn
            () -> gameSession.isGameOver(),     // Supplier: game over state
            (sr, sc, er, ec) -> gameSession.playMove(sr, sc, er, ec), // Executor: perform move
            // Callback after a successful human move: update status, maybe trigger AI, repaint
            (sr, sc, er, ec) -> {
                updateStatus();
                // After human move, if AI should move next, trigger it
                maybeMakeAIMove();
                chessBoard.repaint();
            }
        );
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

    /**
     * Create toolbar: back to menu / new game / undo / redo / save.
     */
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton backButton = new JButton("Back to Menu");
        // Close current window (return to higher-level menu).
        backButton.addActionListener(e -> dispose());
        toolbar.add(backButton);

        toolbar.addSeparator();

        JButton newGameButton = new JButton("New Game");
        // Start a fresh game: reset session, clear selection, refresh, trigger AI if needed.
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
        // Undo last move; if successful refresh UI.
        undoButton.addActionListener(e -> {
            if (gameSession.undo()) {
                chessBoard.resetSelection();
                updateStatus();
                chessBoard.repaint();
            }
        });
        toolbar.add(undoButton);

        JButton redoButton = new JButton("Redo");
        // Redo a move; if successful refresh UI.
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
        // Save current game to file (see saves/ directory).
        saveButton.addActionListener(e -> saveGame());
        toolbar.add(saveButton);

        return toolbar;
    }

    /**
     * Update status label; if game over, show result dialog.
     */
    private void updateStatus() {
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
     * Show dialog with end-of-game result and options: new game / back to menu / exit.
     *
     * @param title   dialog title
     * @param message result message
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
                case 0:
                    // Start new game
                    startNewGame();
                    break;
                case 1:
                    // Back to main menu
                    backToMainMenu();
                    break;
                case 2:
                case JOptionPane.CLOSED_OPTION:
                    // Exit application
                    System.exit(0);
                    break;
            }
        });
    }

    /**
     * Start a new session and trigger AI move if it plays first.
     */
    private void startNewGame() {
        gameSession.start();
        chessBoard.resetSelection();
        updateStatus();
        chessBoard.repaint();
        maybeMakeAIMove();
    }

    /**
     * Dispose this window (return to parent UI).
     */
    private void backToMainMenu() {
        dispose();
    }

    /**
     * Save current game: prompt for name; report success/failure.
     * 
     */
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
     *
     * @param aiOpponent the AI opponent instance
     * @param aiColor    the color the AI plays as
     * 
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
}
