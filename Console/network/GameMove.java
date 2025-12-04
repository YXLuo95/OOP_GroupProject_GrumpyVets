package network;

import java.io.Serializable;

/**
 * game move representation for a board game
 */
public class GameMove implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
    private long timestamp;
    private String playerId;
    
    public GameMove(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.timestamp = System.currentTimeMillis();
    }
    
    public GameMove(int startRow, int startCol, int endRow, int endCol, String playerId) {
        this(startRow, startCol, endRow, endCol);
        this.playerId = playerId;
    }
    
    // Getters and Setters
    public int getStartRow() { return startRow; }
    public void setStartRow(int startRow) { this.startRow = startRow; }
    
    public int getStartCol() { return startCol; }
    public void setStartCol(int startCol) { this.startCol = startCol; }
    
    public int getEndRow() { return endRow; }
    public void setEndRow(int endRow) { this.endRow = endRow; }
    
    public int getEndCol() { return endCol; }
    public void setEndCol(int endCol) { this.endCol = endCol; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    /**
     * acquire the algebraic notation of the move (e.g., "e2-e4")
     */
    public String getNotation() {
        char startFile = (char)('a' + startCol);
        char endFile = (char)('a' + endCol);
        int startRank = 8 - startRow;
        int endRank = 8 - endRow;
        return String.format("%c%d-%c%d", startFile, startRank, endFile, endRank);
    }
    
    /**
     * validate the move coordinates
     * duplicate check
     */
    public boolean isValid() {
        return startRow >= 0 && startRow < 8 && 
               startCol >= 0 && startCol < 8 &&
               endRow >= 0 && endRow < 8 && 
               endCol >= 0 && endCol < 8 &&
               !(startRow == endRow && startCol == endCol);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GameMove move = (GameMove) obj;
        return startRow == move.startRow && 
               startCol == move.startCol && 
               endRow == move.endRow && 
               endCol == move.endCol;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(startRow, startCol, endRow, endCol);
    }
    
    @Override
    public String toString() {
        return String.format("GameMove{%s, player=%s, time=%d}", 
                           getNotation(), playerId, timestamp);
    }
}