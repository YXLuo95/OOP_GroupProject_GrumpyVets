package network;

import logic.Board;
import logic.GameSession;
import objects.PieceColor;

/**
 * 多人游戏会话管理器
 * 整合网络连接和游戏逻辑
 */
public class MultiplayerSession {
    private GameSession gameSession;
    private NetworkConnection network;
    private PieceColor myColor;
    private String myPlayerId;
    private String opponentId;
    private boolean isMyTurn;
    private volatile boolean waitingForOpponent = false;
    
    // 状态回调接口
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
        
        // 设置网络消息处理器
        network.setMessageHandler(this::handleNetworkMessage);
    }
    
    /**
     * 设置状态回调
     */
    public void setGameStateCallback(GameStateCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 作为主机启动游戏
     */
    public boolean hostGame(int port) {
        if (network.startAsHost(port)) {
            myColor = PieceColor.WHITE; // 主机为白方
            isMyTurn = true; // 白方先走
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
     * 作为客户端加入游戏
     */
    public boolean joinGame(String hostAddress, int port) {
        if (network.connectToHost(hostAddress, port)) {
            myColor = PieceColor.BLACK; // 客户端为黑方
            isMyTurn = false; // 等待白方先走
            myPlayerId = "CLIENT_" + System.currentTimeMillis();
            
            startGame();
            notifyConnectionChanged(true);
            return true;
        }
        return false;
    }
    
    /**
     * 开始游戏
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
     * 执行移动
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
        
        // 执行本地移动
        boolean success = gameSession.playMove(startRow, startCol, endRow, endCol);
        if (success) {
            // 发送移动给对手
            network.sendMove(startRow, startCol, endRow, endCol);
            isMyTurn = false;
            waitingForOpponent = true;
            
            notifyGameStateChanged("Move sent to opponent, waiting for their move...");
            notifyTurnChanged(false);
            
            // 检查游戏结束状态
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
     * 处理网络消息
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
     * 处理对手移动
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
        
        // 执行对手移动
        boolean success = gameSession.playMove(move.getStartRow(), move.getStartCol(), 
                                             move.getEndRow(), move.getEndCol());
        if (success) {
            isMyTurn = true;
            waitingForOpponent = false;
            
            notifyGameStateChanged("Opponent moved: " + move.getNotation() + " - Your turn!");
            notifyTurnChanged(true);
            
            // 通知GUI对手移动
            if (callback != null) {
                callback.onOpponentMove(move);
            }
            
            // 检查游戏结束状态
            if (gameSession.isGameOver()) {
                String result = myColor == PieceColor.BLACK ? "Black wins!" : "White wins!";
                notifyGameStateChanged("Game Over - " + result);
            }
        } else {
            notifyGameStateChanged("Failed to execute opponent move: " + move.getNotation());
        }
    }
    
    /**
     * 发送聊天消息
     */
    public boolean sendChat(String message) {
        return network.sendChat(message);
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        if (network != null) {
            network.disconnect();
        }
        notifyConnectionChanged(false);
    }
    
    // 通知回调方法
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