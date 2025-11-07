package network;

import logic.Board;
import logic.GameSession;
import objects.PieceColor;

/**
 * multiplayer session class
 * Integrates network communication and game logic
 */
public class MultiplayerSession {
    private GameSession gameSession;
    private NetworkConnection network;
    private PieceColor myColor;
    private String myPlayerId;
    private String opponentId;
    private boolean isMyTurn;
    private volatile boolean waitingForOpponent = false;

    // Game state callback interface
    public interface GameStateCallback {
        void onGameStateChanged(String state);
        void onChatReceived(String message);
        void onOpponentMove(GameMove move);
        void onConnectionChanged(boolean connected);
        void onTurnChanged(boolean myTurn);
    }
    
    private GameStateCallback callback;
    
    public MultiplayerSession(Board board, String playerId) {
        this.gameSession = new GameSession(board);
        this.network = new NetworkConnection();
        this.myPlayerId = playerId;
        
        // set network message handler
        network.setMessageHandler(this::handleNetworkMessage);
    }
    
    /**
     * Set game state callback
     */
    public void setGameStateCallback(GameStateCallback callback) {
        this.callback = callback;
    }
    
    /**
     * host a game
     */
    public boolean hostGame(int port) {
        if (network.startAsHost(port)) {
            myColor = PieceColor.WHITE; // Host plays as white
            isMyTurn = true; // White goes first
            myPlayerId = "HOST_" + System.currentTimeMillis();
            
            // 等待连接建立
            new Thread(() -> {
                while (!network.isConnected() && network.isHost()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                if (network.isConnected()) {
                    startGame();
                    notifyConnectionChanged(true);
                }
            }).start();
            
            return true;
        }
        return false;
    }
    
    /**
     * join a game
     */
    public boolean joinGame(String hostAddress, int port) {
        if (network.connectToHost(hostAddress, port)) {
            myColor = PieceColor.BLACK; // Client plays as black
            isMyTurn = false; // Wait for white to move first
            myPlayerId = "CLIENT_" + System.currentTimeMillis();
            
            startGame();
            notifyConnectionChanged(true);
            return true;
        }
        return false;
    }
    
    /**
     * Start the game
     */
    private void startGame() {
        gameSession.start();
        waitingForOpponent = !isMyTurn;
        
        String state = "Game started! You are " + myColor + 
                      (isMyTurn ? " - Your turn!" : " - Waiting for opponent...");
        notifyGameStateChanged(state);
        notifyTurnChanged(isMyTurn);
    }
    
    /**
     * Execute a move
     */
    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
        if (!network.isConnected()) {
            notifyGameStateChanged("Not connected to opponent!");
            return false;
        }
        
        if (!isMyTurn) {
            notifyGameStateChanged("Not your turn!");
            return false;
        }

        // Execute local move
        boolean success = gameSession.playMove(startRow, startCol, endRow, endCol);
        if (success) {
            // Send move to opponent
            network.sendMove(startRow, startCol, endRow, endCol);
            isMyTurn = false;
            waitingForOpponent = true;
            
            notifyGameStateChanged("Move sent to opponent, waiting for their move...");
            notifyTurnChanged(false);

            // Check game over status
            if (gameSession.isGameOver()) {
                String result = myColor == PieceColor.WHITE ? "White wins!" : "Black wins!";
                notifyGameStateChanged("Game Over - " + result);
            }
        } else {
            notifyGameStateChanged("Invalid move!");
        }
        
        return success;
    }
    
    /**
     * Handle network messages
     */
    private void handleNetworkMessage(NetworkMessage message) {
        switch (message.getType()) {
            case CONNECTION_ACK:
                opponentId = message.getSenderId();
                notifyGameStateChanged("Connected to opponent: " + opponentId);
                break;
                
            case CONNECTION_REQUEST:
                opponentId = message.getSenderId();
                notifyGameStateChanged("Opponent connected: " + opponentId);
                break;
                
            case GAME_MOVE:
                handleOpponentMove((GameMove) message.getData());
                break;
                
            case CHAT:
                String chatMessage = (String) message.getData();
                notifyChatReceived("Opponent: " + chatMessage);
                break;
                
            case GAME_STATE:
                String state = (String) message.getData();
                notifyGameStateChanged("Opponent: " + state);
                break;
                
            case DISCONNECT:
                notifyGameStateChanged("Opponent disconnected!");
                notifyConnectionChanged(false);
                break;
                
            default:
                System.out.println("Unknown message type: " + message.getType());
        }
    }
    
    /**
     * Handle opponent move
     */
    private void handleOpponentMove(GameMove move) {
        if (isMyTurn) {
            System.out.println("Received move when it's my turn - ignoring");
            return;
        }
        
        if (!move.isValid()) {
            notifyGameStateChanged("Received invalid move from opponent!");
            return;
        }
        
        // execute opponent move
        boolean success = gameSession.playMove(move.getStartRow(), move.getStartCol(), 
                                             move.getEndRow(), move.getEndCol());
        if (success) {
            isMyTurn = true;
            waitingForOpponent = false;
            
            notifyGameStateChanged("Opponent moved: " + move.getNotation() + " - Your turn!");
            notifyTurnChanged(true);
            
            // Notify GUI of opponent move
            if (callback != null) {
                callback.onOpponentMove(move);
            }

            // Check game over status
            if (gameSession.isGameOver()) {
                String result = myColor == PieceColor.BLACK ? "Black wins!" : "White wins!";
                notifyGameStateChanged("Game Over - " + result);
            }
        } else {
            notifyGameStateChanged("Failed to execute opponent move: " + move.getNotation());
        }
    }
    
    /**
     * Send chat message
     */
    public boolean sendChat(String message) {
        return network.sendChat(message);
    }
    
    /**
     * Disconnect
     */
    public void disconnect() {
        if (network != null) {
            network.disconnect();
        }
        notifyConnectionChanged(false);
    }

    // Notify callback methods
    private void notifyGameStateChanged(String state) {
        if (callback != null) {
            callback.onGameStateChanged(state);
        }
    }
    
    private void notifyChatReceived(String message) {
        if (callback != null) {
            callback.onChatReceived(message);
        }
    }
    
    private void notifyConnectionChanged(boolean connected) {
        if (callback != null) {
            callback.onConnectionChanged(connected);
        }
    }
    
    private void notifyTurnChanged(boolean myTurn) {
        if (callback != null) {
            callback.onTurnChanged(myTurn);
        }
    }
    
    // Getters
    public GameSession getGameSession() { return gameSession; }
    public PieceColor getMyColor() { return myColor; }
    public String getMyPlayerId() { return myPlayerId; }
    public String getOpponentId() { return opponentId; }
    public boolean isMyTurn() { return isMyTurn; }
    public boolean isConnected() { return network != null && network.isConnected(); }
    public boolean isWaitingForOpponent() { return waitingForOpponent; }
    public NetworkConnection getNetwork() { return network; }
}