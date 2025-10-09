package objects;

import logic.Board;

public class King extends Piece {
    public King(PieceColor color, int x, int y) {
        super(color, PieceType.KING, x, y);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);
        if (dr <= 1 && dc <= 1) {
            Piece target = board.getPieceAt(er, ec);
            return target == null || target.getColor() != this.color;
        }
        return false;
    }
}
