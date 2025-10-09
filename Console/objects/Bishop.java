package objects;

import logic.Board;

public class Bishop extends Piece {
    public Bishop(PieceColor color, int x, int y) {
        super(color, PieceType.BISHOP, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        if (Math.abs(er - sr) != Math.abs(ec - sc)) return false; // 必须对角

        int dr = Integer.compare(er, sr);
        int dc = Integer.compare(ec, sc);
        int r = sr + dr, c = sc + dc;

        while (r != er && c != ec) {
            if (board.getPieceAt(r, c) != null) return false;
            r += dr; c += dc;
        }

        Piece target = board.getPieceAt(er, ec);
        return target == null || target.getColor() != this.color;
    }
}
