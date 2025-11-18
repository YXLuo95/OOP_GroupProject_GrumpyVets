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
 * Reusable chess board Swing component with click and drag support.
 * It is UI-only and delegates game-state and move execution via suppliers/callbacks.
 */
public class BoardView extends JPanel {
    public interface MovePerformer {
        boolean perform(int sr, int sc, int er, int ec);
    }

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

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean isDragging = false;
    private int dragStartRow = -1;
    private int dragStartCol = -1;
    private final Point dragOffset = new Point();
    private final Point currentDragPosition = new Point();

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
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;
                if (inBounds(row, col)) handleCellClick(row, col);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isGameOverSupplier.getAsBoolean()) return;
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;
                if (!inBounds(row, col)) return;

                Board board = boardSupplier.get();
                if (board == null) return;
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == currentTurnSupplier.get()) {
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

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isDragging) return;
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;

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
                Color cellColor = ((row + col) % 2 == 0)
                        ? new Color(240, 217, 181)
                        : new Color(181, 136, 99);
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
                        if (hoverRow == row && hoverCol == col && inBounds(hoverRow, hoverCol)) {
                            g2d.setColor(new Color(0, 255, 0, 100));
                            g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                    }
                }

                g2d.setColor(Color.BLACK);
                g2d.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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
