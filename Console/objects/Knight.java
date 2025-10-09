package objects;

import logic.Board;

public class Knight extends Piece {
    public Knight(PieceColor color, int x, int y) {
        super(color, PieceType.KNIGHT, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);
        if ((dr == 2 && dc == 1) || (dr == 1 && dc == 2)) {
            Piece target = board.getPieceAt(er, ec);
            return target == null || target.getColor() != this.color;
        }
        return false;
    }
}
