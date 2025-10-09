package objects;

import logic.Board;

public abstract class Piece implements Moveable {
    protected PieceColor color;
    protected PieceType type;
    protected int x;  
    protected int y;  

    public Piece(PieceColor color, PieceType type, int x, int y) {
        this.color = color;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }

   
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    public abstract boolean canMove(Board board, int startRow, int startCol, int endRow, int endCol);

   @Override
    public String toString() {
        String s = type.symbol();
        return (color == PieceColor.WHITE) ? s : s.toLowerCase();
    }
}
