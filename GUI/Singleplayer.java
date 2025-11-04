import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Singleplayer extends JFrame {
    private ChessBoard chessBoard;
    private JLabel statusLabel;
    
    public Singleplayer() {
        super("Chess - Single Player");
        setupUI();
    }
    
    private void setupUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 顶部工具栏
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // 中心棋盘
        chessBoard = new ChessBoard();
        add(chessBoard, BorderLayout.CENTER);
        
        // 底部状态栏
        statusLabel = new JLabel("White to move", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.SOUTH);
        
        // 设置窗口属性
        setSize(800, 850);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // 返回按钮
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> dispose());
        toolbar.add(backButton);
        
        toolbar.addSeparator();
        
        // 新游戏按钮
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            chessBoard.resetBoard();
            statusLabel.setText("White to move");
        });
        toolbar.add(newGameButton);
        
        return toolbar;
    }
    
    // 内部棋盘类
    private class ChessBoard extends JPanel {
        private final int BOARD_SIZE = 8;
        private final int CELL_SIZE = 80;
        private int selectedRow = -1;
        private int selectedCol = -1;
        private String[][] board; // 简单的字符串表示棋盘
        
        public ChessBoard() {
            setPreferredSize(new Dimension(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE));
            setBackground(Color.WHITE);
            initializeBoard();
            
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
        
        private void initializeBoard() {
            board = new String[8][8];
            
            // 初始化黑方棋子
            board[0][0] = "♜"; board[0][1] = "♞"; board[0][2] = "♝"; board[0][3] = "♛";
            board[0][4] = "♚"; board[0][5] = "♝"; board[0][6] = "♞"; board[0][7] = "♜";
            for (int i = 0; i < 8; i++) {
                board[1][i] = "♟";
            }
            
            // 中间空格
            for (int row = 2; row < 6; row++) {
                for (int col = 0; col < 8; col++) {
                    board[row][col] = "";
                }
            }
            
            // 初始化白方棋子
            for (int i = 0; i < 8; i++) {
                board[6][i] = "♙";
            }
            board[7][0] = "♖"; board[7][1] = "♘"; board[7][2] = "♗"; board[7][3] = "♕";
            board[7][4] = "♔"; board[7][5] = "♗"; board[7][6] = "♘"; board[7][7] = "♖";
        }
        
        private void handleCellClick(int row, int col) {
            if (selectedRow == -1) {
                // 第一次点击 - 选择棋子
                if (!board[row][col].isEmpty()) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
            } else {
                // 第二次点击 - 尝试移动
                if (selectedRow == row && selectedCol == col) {
                    // 点击同一个格子，取消选择
                    selectedRow = -1;
                    selectedCol = -1;
                } else {
                    // 移动棋子
                    board[row][col] = board[selectedRow][selectedCol];
                    board[selectedRow][selectedCol] = "";
                    selectedRow = -1;
                    selectedCol = -1;
                    
                    // 切换状态
                    String currentStatus = statusLabel.getText();
                    if (currentStatus.contains("White")) {
                        statusLabel.setText("Black to move");
                    } else {
                        statusLabel.setText("White to move");
                    }
                }
                repaint();
            }
        }
        
        public void resetBoard() {
            initializeBoard();
            selectedRow = -1;
            selectedCol = -1;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制棋盘格子
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    Color cellColor;
                    if ((row + col) % 2 == 0) {
                        cellColor = new Color(240, 217, 181); // 浅色格子
                    } else {
                        cellColor = new Color(181, 136, 99);  // 深色格子
                    }
                    
                    // 高亮选中的格子
                    if (row == selectedRow && col == selectedCol) {
                        cellColor = new Color(255, 255, 0, 128); // 黄色高亮
                    }
                    
                    g2d.setColor(cellColor);
                    g2d.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // 绘制格子边框
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
            
            // 绘制棋子
            drawPieces(g2d);
        }
        
        private void drawPieces(Graphics2D g2d) {
            g2d.setFont(new Font("Serif", Font.BOLD, 48));
            g2d.setColor(Color.BLACK);
            
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    String piece = board[row][col];
                    if (!piece.isEmpty()) {
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(piece);
                        int textHeight = fm.getAscent();
                        
                        int x = col * CELL_SIZE + (CELL_SIZE - textWidth) / 2;
                        int y = row * CELL_SIZE + (CELL_SIZE + textHeight) / 2;
                        
                        g2d.drawString(piece, x, y);
                    }
                }
            }
        }
    }
}
