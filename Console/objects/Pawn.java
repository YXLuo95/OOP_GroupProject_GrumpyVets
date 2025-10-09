package objects;

import logic.Board;

public class Pawn extends Piece {
    public Pawn(PieceColor color, int x, int y) {
        super(color, PieceType.PAWN, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dir = (color == PieceColor.WHITE) ? -1 : 1;  // 白上黑下
        Piece target = board.getPieceAt(er, ec);

        // 前进一步
        if (sc == ec && target == null && er == sr + dir) return true;

        // 起始位置可以走两步
        if (sc == ec && target == null &&
            sr == (color == PieceColor.WHITE ? 6 : 1) &&
            er == sr + 2 * dir &&
            board.getPieceAt(sr + dir, sc) == null)
            return true;

        // 斜吃
        if (Math.abs(ec - sc) == 1 && er == sr + dir && target != null &&
            target.getColor() != this.color)
            return true;

        return false;
    }
}
