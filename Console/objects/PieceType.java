package objects;

/** Enum for piece types. */    
public enum PieceType {
    PAWN,   
    ROOK,   
    KNIGHT, 
    BISHOP, 
    QUEEN,  
    KING;   

    /* Returns the symbol for the piece type. */
    public String symbol() {
        return switch (this) {
            case PAWN -> "P";
            case ROOK -> "R";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case QUEEN -> "Q";
            case KING -> "K";
        };
    }
}
