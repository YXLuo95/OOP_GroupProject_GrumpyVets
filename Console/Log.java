
// create a log class to undo and redo moves
// this class will store the moves in a stack
// so other methods can use the log to undo and redo moves
public class Log {
    
    private Move move;
    private Piece pieceMoved;
    private Piece pieceCaptured; // can be null if no piece was captured

    public Log(Move move, Piece pieceMoved, Piece pieceCaptured) {

        this.move = move;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
    }

    public Move getMove() {
        return move;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }

    public Piece getPieceCaptured() {
        return pieceCaptured;
    }   
    
}