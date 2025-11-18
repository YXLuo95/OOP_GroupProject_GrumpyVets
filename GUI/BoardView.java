package GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.swing.*;
import logic.Board;
import objects.Piece;
import objects.PieceColor;

/**
 * Reusable Swing component that renders an 8×8 chess board with
 * both click-to-move and drag-and-drop interactions.
 *
 * Responsibilities:
 * - Visual-only: BoardView does not own game rules or state. It reads from suppliers
 *   and delegates move attempts to a provided MovePerformer.
 * - Input handling: Click selection and drag operations, including hover/selection highlights.
 * - Rendering: Unicode piece symbols, square coloring, and dragged-piece overlay.
 *
 * Coordinates:
 * - 0-based indices with (row=0, col=0) at top-left. Each cell is CELL_SIZE×CELL_SIZE pixels.
 *
 * Threading:
 * - All interactions occur on Swing's EDT. Call repaint/update from EDT.
 */
public class BoardView extends JPanel {
    /**
     * Callback to attempt a move. Should apply game rules and mutate the model if legal.
     * Return true on success, false otherwise.
     */
    public interface MovePerformer {
        boolean perform(int sr, int sc, int er, int ec);
    }

    /**
     * Listener invoked after a move has been successfully performed.
     * Typical uses: update status, repaint, trigger AI/network actions.
     */
    public interface MoveListener {
        void onMoveSuccess(int sr, int sc, int er, int ec);
    }

    private final Supplier<Board> boardSupplier;
    private final Supplier<PieceColor> currentTurnSupplier;
    private final BooleanSupplier isGameOverSupplier;
    private final MovePerformer movePerformer;
    private final MoveListener moveListener; // e.g., update status, trigger AI, send network

    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 75;

    // Flipped orientation: when true board is shown from Black's perspective (rank 8 at bottom).
    private boolean flipped = false;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean isDragging = false;
    private int dragStartRow = -1;
    private int dragStartCol = -1;
    private final Point dragOffset = new Point();
    private final Point currentDragPosition = new Point();

        /**
         * Create a BoardView bound to external game-state suppliers and callbacks.
         * @param boardSupplier        supplies the current Board to render
         * @param currentTurnSupplier  supplies the side to move (for input gating)
         * @param isGameOverSupplier   supplies whether the game is over (disables input)
         * @param movePerformer        validates/applies a move; returns true if executed
         * @param moveListener         notified on successful moves (optional; may be null)
         */
        public BoardView(
            Supplier<Board> boardSupplier,
            Supplier<PieceColor> currentTurnSupplier,
            BooleanSupplier isGameOverSupplier,
            MovePerformer movePerformer,
            MoveListener moveListener
        ) {
        this.boardSupplier = boardSupplier;
        this.currentTurnSupplier = currentTurnSupplier;
        this.isGameOverSupplier = isGameOverSupplier;
        this.movePerformer = movePerformer;
        this.moveListener = moveListener;

        setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int displayCol = e.getX() / CELL_SIZE;
                int displayRow = e.getY() / CELL_SIZE;
                int col = flipped ? BOARD_SIZE - 1 - displayCol : displayCol;
                int row = flipped ? BOARD_SIZE - 1 - displayRow : displayRow;
                if (inBounds(row, col)) handleCellClick(row, col);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOverSupplier.getAsBoolean()) return;
                int displayCol = e.getX() / CELL_SIZE;
                int displayRow = e.getY() / CELL_SIZE;
                int col = flipped ? BOARD_SIZE - 1 - displayCol : displayCol;
                int row = flipped ? BOARD_SIZE - 1 - displayRow : displayRow;
                if (!inBounds(row, col)) return;

                Board board = boardSupplier.get();
                if (board == null) return;
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == currentTurnSupplier.get()) {
                    isDragging = true;
                    dragStartRow = row;
                    dragStartCol = col;

                    int drawCol = flipped ? BOARD_SIZE - 1 - col : col;
                    int drawRow = flipped ? BOARD_SIZE - 1 - row : row;
                    int pieceX = drawCol * CELL_SIZE + CELL_SIZE / 2;
                    int pieceY = drawRow * CELL_SIZE + CELL_SIZE / 2;
                    dragOffset.x = e.getX() - pieceX;
                    dragOffset.y = e.getY() - pieceY;
                    currentDragPosition.x = e.getX();
                    currentDragPosition.y = e.getY();

                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging) return;
                int displayCol = e.getX() / CELL_SIZE;
                int displayRow = e.getY() / CELL_SIZE;
                int col = flipped ? BOARD_SIZE - 1 - displayCol : displayCol;
                int row = flipped ? BOARD_SIZE - 1 - displayRow : displayRow;

                if (!isGameOverSupplier.getAsBoolean() && inBounds(row, col)) {
                    if (row != dragStartRow || col != dragStartCol) {
                        boolean moved = movePerformer.perform(dragStartRow, dragStartCol, row, col);
                        if (moved && moveListener != null) moveListener.onMoveSuccess(dragStartRow, dragStartCol, row, col);
                    }
                }

                isDragging = false;
                dragStartRow = -1;
                dragStartCol = -1;
                selectedRow = -1;
                selectedCol = -1;
                repaint();
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

    /** Set flipped orientation (Black's perspective) and repaint. */
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        repaint();
    }

    /**
     * Clear any selected cell (e.g., after undo/redo/new game) and repaint.
     */
    public void resetSelection() {
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void handleCellClick(int row, int col) {
        if (isGameOverSupplier.getAsBoolean()) return;
        Board board = boardSupplier.get();
        if (board == null) return;

        if (selectedRow == -1) {
            Piece piece = board.getPieceAt(row, col);
            if (piece != null && piece.getColor() == currentTurnSupplier.get()) {
                selectedRow = row;
                selectedCol = col;
                repaint();
            }
        } else {
            if (selectedRow == row && selectedCol == col) {
                selectedRow = -1;
                selectedCol = -1;
            } else {
                boolean moved = movePerformer.perform(selectedRow, selectedCol, row, col);
                if (moved && moveListener != null) moveListener.onMoveSuccess(selectedRow, selectedCol, row, col);
                selectedRow = -1;
                selectedCol = -1;
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw cells
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int drawRow = flipped ? BOARD_SIZE - 1 - row : row;
                int drawCol = flipped ? BOARD_SIZE - 1 - col : col;

                Color cellColor = ((row + col) % 2 == 0)
                        ? new Color(240, 217, 181)
                        : new Color(181, 136, 99);
                if (row == selectedRow && col == selectedCol && !isDragging) {
                    cellColor = new Color(255, 255, 0, 128);
                }
                g2d.setColor(cellColor);
                g2d.fillRect(drawCol * CELL_SIZE, drawRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                if (isDragging) {
                    Point mousePos = getMousePosition();
                    if (mousePos != null) {
                        int hoverDisplayCol = mousePos.x / CELL_SIZE;
                        int hoverDisplayRow = mousePos.y / CELL_SIZE;
                        int hoverCol = flipped ? BOARD_SIZE - 1 - hoverDisplayCol : hoverDisplayCol;
                        int hoverRow = flipped ? BOARD_SIZE - 1 - hoverDisplayRow : hoverDisplayRow;
                        if (hoverRow == row && hoverCol == col && inBounds(hoverRow, hoverCol)) {
                            g2d.setColor(new Color(0, 255, 0, 100));
                            g2d.fillRect(drawCol * CELL_SIZE, drawRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                    }
                }

                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawCol * CELL_SIZE, drawRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw pieces
        Board board = boardSupplier.get();
        if (board != null) {
            drawPieces(g2d, board);
        }
    }

    private void drawPieces(Graphics2D g2d, Board board) {
        g2d.setFont(new Font("Serif", Font.BOLD, 48));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    if (isDragging && row == dragStartRow && col == dragStartCol) continue;
                    drawPiece(g2d, piece, row, col);
                }
            }
        }

        if (isDragging && dragStartRow >= 0 && dragStartCol >= 0) {
            Piece draggedPiece = board.getPieceAt(dragStartRow, dragStartCol);
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

        int drawRow = flipped ? BOARD_SIZE - 1 - row : row;
        int drawCol = flipped ? BOARD_SIZE - 1 - col : col;
        int x = drawCol * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
        int y = drawRow * CELL_SIZE + (CELL_SIZE + textHeight) / 2;

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
        Color pieceColor = (piece.getColor() == PieceColor.WHITE)
                ? new Color(255, 255, 255, 200)
                : new Color(0, 0, 0, 200);
        Color outlineColor = (piece.getColor() == PieceColor.WHITE)
                ? new Color(0, 0, 0, 200)
                : new Color(255, 255, 255, 200);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getAscent();

        int x = position.x - dragOffset.x - textWidth / 2;
        int y = position.y - dragOffset.y + textHeight / 2;

        g2d.setColor(outlineColor);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) g2d.drawString(symbol, x + dx, y + dy);
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
