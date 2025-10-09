package logic;

import java.util.ArrayDeque;
import java.util.Deque;
import objects.Piece;
import objects.PieceColor;

public class GameSession {
    private final Board board;
    private PieceColor currentTurn = PieceColor.WHITE;

    private final Deque<Board> undoStack = new ArrayDeque<>();
    private final Deque<Board> redoStack = new ArrayDeque<>();

    public GameSession(Board board) {
        this.board = board;
    }

    public void start() {
        board.resetToStandard();
        currentTurn = PieceColor.WHITE;
        undoStack.clear();
        redoStack.clear();
    }

        /**
         * 执行一次移动（用 Piece.canMove 检查合法性，并通过 setPieceAt 更新棋盘）
         */
        public boolean playMove(int sr, int sc, int er, int ec) {
            Piece moving = board.getPieceAt(sr, sc);
            if (moving == null) {
                System.out.println("⚠️ 起点没有棋子！");
                return false;
            }

            // 判断是否轮到该方
            if (moving.getColor() != currentTurn) {
                System.out.println("⚠️ 还没轮到 " + moving.getColor() + "！");
                return false;
            }

            // 判断能否移动
            if (!moving.canMove(board, sr, sc, er, ec)) {
                System.out.println("⚠️ 非法走法！");
                return false;
            }

            // ✅ 合法才执行
            undoStack.push(board.deepCopy());
            redoStack.clear();

            // 获取目标格的棋子（如果有就是被吃掉的）
            Piece target = board.getPieceAt(er, ec);

            // 清空起点格
            board.setPieceAt(sr, sc, null);

            // 放置到终点格（setPieceAt 会自动更新 moving 的坐标）
            board.setPieceAt(er, ec, moving);

            if (target != null) {
                System.out.println("✅ " + moving.getType() + " 吃掉了 " + target.getType());
            }

            // 切换行方
            currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

            return true;
        }


    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        redoStack.push(board.deepCopy());
        Board prev = undoStack.pop();
        board.copyFrom(prev);
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        undoStack.push(board.deepCopy());
        Board next = redoStack.pop();
        board.copyFrom(next);
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return true;
    }

    public PieceColor getCurrentTurn() { return currentTurn; }
    public Board getBoard() { return board; }
}
