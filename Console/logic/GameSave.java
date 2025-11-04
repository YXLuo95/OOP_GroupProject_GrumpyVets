package logic;

import java.io.*;
import java.util.Date;
import objects.*;

/**
 * GameSave handles saving and loading game states to/from files.
 * Stores board state, current turn, game over status, and metadata.
 */
public class GameSave implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Game state data
    private String[][] boardState;
    private String currentTurn;
    private boolean gameOver;
    private Date saveDate;
    private String saveName;
    
    // Constructor for saving current game state
    public GameSave(GameSession gameSession, String saveName) {
        this.saveName = saveName;
        this.saveDate = new Date();
        this.currentTurn = gameSession.getCurrentTurn().name();
        this.gameOver = gameSession.isGameOver();
        this.boardState = new String[8][8];
        
        // Convert board to serializable format
        Board board = gameSession.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece == null) {
                    boardState[row][col] = null;
                } else {
                    // Format: "COLOR_TYPE" (e.g., "WHITE_KING", "BLACK_PAWN")
                    boardState[row][col] = piece.getColor().name() + "_" + piece.getType().name();
                }
            }
        }
    }
    
    // Save game to file
    public static boolean saveGame(GameSession gameSession, String fileName) {
        try {
            // Create saves directory if it doesn't exist
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            
            String filePath = "saves/" + fileName + ".chess";
            GameSave gameSave = new GameSave(gameSession, fileName);
            
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(gameSave);
            oos.close();
            fos.close();
            
            System.out.println("Game saved as: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return false;
        }
    }
    
    // Load game from file
    public static GameSave loadGame(String fileName) {
        try {
            String filePath = "saves/" + fileName + ".chess";
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            GameSave gameSave = (GameSave) ois.readObject();
            ois.close();
            fis.close();
            
            System.out.println("Game loaded from: " + filePath);
            return gameSave;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }
    
    // Apply loaded save to a GameSession
    public void applyToGameSession(GameSession gameSession) {
        Board board = gameSession.getBoard();
        
        // Clear the board first
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.setPieceAt(row, col, null);
            }
        }
        
        // Restore pieces from save data
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (boardState[row][col] != null) {
                    String[] parts = boardState[row][col].split("_");
                    PieceColor color = PieceColor.valueOf(parts[0]);
                    PieceType type = PieceType.valueOf(parts[1]);
                    
                    Piece piece = createPiece(color, type, row, col);
                    board.setPieceAt(row, col, piece);
                }
            }
        }
        
        // Restore game state using setter methods
        gameSession.setCurrentTurn(PieceColor.valueOf(currentTurn));
        gameSession.setGameOver(gameOver);
        gameSession.clearHistory(); // Clear undo/redo history for loaded game
    }
    
    // Helper method to create piece instances
    private Piece createPiece(PieceColor color, PieceType type, int row, int col) {
        switch (type) {
            case KING: return new King(color, row, col);
            case QUEEN: return new Queen(color, row, col);
            case ROOK: return new Rook(color, row, col);
            case BISHOP: return new Bishop(color, row, col);
            case KNIGHT: return new Knight(color, row, col);
            case PAWN: return new Pawn(color, row, col);
            default: return null;
        }
    }
    
    // List available save files
    public static String[] listSaveFiles() {
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            return new String[0];
        }
        
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".chess"));
        if (files == null) {
            return new String[0];
        }
        
        String[] saveNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            saveNames[i] = fileName.substring(0, fileName.lastIndexOf(".chess"));
        }
        return saveNames;
    }
    
    // Getters for metadata
    public String getSaveName() { return saveName; }
    public Date getSaveDate() { return saveDate; }
    public String getCurrentTurn() { return currentTurn; }
    public boolean isGameOver() { return gameOver; }
}