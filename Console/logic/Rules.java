package logic;

import objects.*;

public final class Rules {
    private Rules() {}

    /* 某格是否被某方攻击 */
    public static boolean isSquareAttacked(Board board, int r, int c, PieceColor byColor) {
        for (Piece p : board.getPiecesByColor(byColor)) {
            if (p.attacks(board, p.getX(), p.getY(), r, c)) return true;
        }
        return false;
    }

    /* 是否被将军 */
    public static boolean isInCheck(Board board, PieceColor color) {
        int[] king = board.findKing(color);
        if (king == null) return false; // 容错
        PieceColor opp = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return isSquareAttacked(board, king[0], king[1], opp);
    }

    /* 是否尚有任何合法走子（能解将） */
    public static boolean hasAnyLegalMove(Board board, PieceColor color) {
        for (Piece p : board.getPiecesByColor(color)) {
            int sr = p.getX(), sc = p.getY();
            for (int er = 0; er < 8; er++) {
                for (int ec = 0; ec < 8; ec++) {
                    // 子类规则允许（路径/占子/吃法都在各子里）
                    if (!p.canMove(board, sr, sc, er, ec)) continue;

                    // 试走（你的 Board.movePiece 已处理升变）
                    Board copy = board.deepCopy();
                    copy.movePiece(sr, sc, er, ec);

                    // 试走后本方不在被将军 => 有合法解
                    if (!isInCheck(copy, p.getColor())) return true;
                }
            }
        }
        return false;
    }

    /* 组合判定 */
    public static boolean isCheckmate(Board board, PieceColor color) {
        return isInCheck(board, color) && !hasAnyLegalMove(board, color);
    }

    public static boolean isStalemate(Board board, PieceColor color) {
        return !isInCheck(board, color) && !hasAnyLegalMove(board, color);
    }
}
