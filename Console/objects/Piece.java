package objects;
public class Piece{
    
    private PieceType type;
    private PieceColor color;

    private int id; // Unique identifier for each piece
    

    public Piece(PieceType type, PieceColor color, int id) {
        this.type = type;
        this.color = color;
        this.id = id;
    }

    public PieceType getType() {
        return type;
    }
    public PieceColor getColor() {
        return color;
    }
    public int getId() {
        return id;
    }
}