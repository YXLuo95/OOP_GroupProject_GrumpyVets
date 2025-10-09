package logic;

import objects.*;

/**
 * 棋盘类：负责保存所有棋子、执行移动、打印棋盘。
 * 不直接判断走法合法性（那由各个棋子类的 canMove() 决定）。
 */
public class Board {

    // 8x8 棋盘数组，squares[row][col]
    private final Piece[][] squares = new Piece[8][8];

    /** 构造函数：创建标准开局 */
    public Board() {
        resetToStandard();
    }

    /**
     * 重置棋盘为标准开局（黑方在上，白方在下）
     */
    public final void resetToStandard() {
        // 清空所有格子
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        // ===== 黑方（顶部）=====
        squares[0][0] = new Rook  (PieceColor.BLACK, 0, 0);
        squares[0][1] = new Knight(PieceColor.BLACK, 0, 1);
        squares[0][2] = new Bishop(PieceColor.BLACK, 0, 2);
        squares[0][3] = new Queen (PieceColor.BLACK, 0, 3);
        squares[0][4] = new King  (PieceColor.BLACK, 0, 4);
        squares[0][5] = new Bishop(PieceColor.BLACK, 0, 5);
        squares[0][6] = new Knight(PieceColor.BLACK, 0, 6);
        squares[0][7] = new Rook  (PieceColor.BLACK, 0, 7);

        for (int c = 0; c < 8; c++) {
            squares[1][c] = new Pawn(PieceColor.BLACK, 1, c);
        }

        // ===== 白方（底部）=====
        for (int c = 0; c < 8; c++) {
            squares[6][c] = new Pawn(PieceColor.WHITE, 6, c);
        }

        squares[7][0] = new Rook  (PieceColor.WHITE, 7, 0);
        squares[7][1] = new Knight(PieceColor.WHITE, 7, 1);
        squares[7][2] = new Bishop(PieceColor.WHITE, 7, 2);
        squares[7][3] = new Queen (PieceColor.WHITE, 7, 3);
        squares[7][4] = new King  (PieceColor.WHITE, 7, 4);
        squares[7][5] = new Bishop(PieceColor.WHITE, 7, 5);
        squares[7][6] = new Knight(PieceColor.WHITE, 7, 6);
        squares[7][7] = new Rook  (PieceColor.WHITE, 7, 7);
    }

    /* ---------- 工具方法 ---------- */

    /** 判断坐标是否在 8x8 范围内 */
    private boolean inBounds(int r, int c) {
        return 0 <= r && r < 8 && 0 <= c && c < 8;
    }

    /** 获取某格子的棋子 */
    public Piece getPieceAt(int r, int c) {
        return inBounds(r, c) ? squares[r][c] : null;
    }

    /** 设置某格子的棋子（并同步更新棋子的坐标） */
    public void setPieceAt(int r, int c, Piece p) {
        if (!inBounds(r, c)) return;
        squares[r][c] = p;
        if (p != null) p.setPosition(r, c); // 🔥 同步坐标
    }

    /** 清空某格子 */
    public void clearSquare(int r, int c) {
        if (inBounds(r, c)) squares[r][c] = null;
    }

    /**
     * 执行一次移动（不判断是否合法）
     * 同步棋子的坐标，并返回被吃掉的棋子（如果有）
     */
    public Piece movePiece(int sr, int sc, int er, int ec) {
        if (!inBounds(sr, sc) || !inBounds(er, ec)) return null;

        Piece moving = squares[sr][sc];
        Piece captured = squares[er][ec];

        squares[er][ec] = moving;
        squares[sr][sc] = null;

        if (moving != null) moving.setPosition(er, ec); // 🔥 更新棋子位置

        return captured;
    }

    /* ---------- 打印棋盘 ---------- */

    /** 打印棋盘（上 8 下 1，列 a..h） */
    public void printBoard() {
        System.out.println("   a b c d e f g h");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r;
            System.out.print(rank + "  ");
            for (int c = 0; c < 8; c++) {
                Piece p = squares[r][c];
                System.out.print((p == null ? ". " : p.toString() + " "));
            }
            System.out.println(" " + rank);
        }
        System.out.println("   a b c d e f g h");
    }

    /* ---------- 坐标工具 ---------- */

    /** 将 "e2" 转换为数组下标 (6,4)（白方在下） */
    public static int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) return null;
        char file = Character.toLowerCase(alg.charAt(0)); // a..h
        char rank = alg.charAt(1);                        // 1..8
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') return null;
        int col = file - 'a';
        int row = 8 - (rank - '0');
        return new int[]{row, col};
    }

    /** 将数组下标 (6,4) 转换为 "e2" */
    public static String toAlg(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return "??";
        char file = (char) ('a' + col);
        char rank = (char) ('0' + (8 - row));
        return "" + file + rank;
    }

    /** 复制棋盘（浅拷贝；GUI 时可扩展为深拷贝） */
    public Board copy() {
        Board b2 = new Board();
        // 清除新棋盘的默认开局
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) b2.squares[r][c] = null;
        // 拷贝当前局面
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = this.squares[r][c];
                b2.squares[r][c] = p;
            }
        }
        return b2;
    }
}
