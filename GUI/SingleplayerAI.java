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
 * This class preserves the original Singleplayer UI/behavior and adds an AI opponent
 * interface so you can plug in an AI without changing this file again.
 */
public class SingleplayerAI extends JFrame {

    // ============= AI Opponent Interface & Hooks =============
    /**
     * A minimal AI interface: return a move as {startRow,startCol,endRow,endCol}.
     * Return null when no legal move is available.
     */
    private AIOpponent aiOpponent = null;
    private PieceColor aiColor = null; // The side controlled by AI (WHITE/BLACK) or null when disabled

    /** Configure AI opponent (pass null to disable). */
    public void setAIOpponent(AIOpponent opponent, PieceColor color) {
        this.aiOpponent = opponent;
        this.aiColor = opponent == null ? null : color;
        maybeMakeAIMove();
    }

    // Core Game Components
    private BoardView chessBoard;
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

        chessBoard = new BoardView(
            () -> gameSession.getBoard(),
            () -> gameSession.getCurrentTurn(),
            () -> gameSession.isGameOver(),
            (sr, sc, er, ec) -> gameSession.playMove(sr, sc, er, ec),
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
}
