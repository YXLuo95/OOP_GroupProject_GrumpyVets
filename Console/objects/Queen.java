package objects;

import logic.Board;

public class Queen extends Piece {
    public Queen(PieceColor color, int x, int y) {
        super(color, PieceType.QUEEN, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dr = Integer.compare(er, sr);
        int dc = Integer.compare(ec, sc);

        // 后走法 = 车 + 象
        boolean straight = (sr == er || sc == ec);
        boolean diagonal = (Math.abs(er - sr) == Math.abs(ec - sc));
        if (!straight && !diagonal) return false;

        int r = sr + dr, c = sc + dc;
        while (r != er || c != ec) {
            if (board.getPieceAt(r, c) != null) return false;
            r += dr; c += dc;
        }

        Piece target = board.getPieceAt(er, ec);
        return target == null || target.getColor() != this.color;
    }
}
