package objects;

import logic.Board;

public class Rook extends Piece {
    public Rook(PieceColor color, int x, int y) {
        super(color, PieceType.ROOK, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        if (sr != er && sc != ec) return false; // 必须直线

        int dr = Integer.compare(er, sr);
        int dc = Integer.compare(ec, sc);
        int r = sr + dr, c = sc + dc;

        while (r != er || c != ec) {
            if (board.getPieceAt(r, c) != null) return false;
            r += dr; c += dc;
        }

        Piece target = board.getPieceAt(er, ec);
        return target == null || target.getColor() != this.color;
    }
}
