package network;

import java.io.Serializable;

/**
 * 网络消息类 - 用于P2P通信
 */
public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        CONNECTION_REQUEST,  // Connection request
        CONNECTION_ACK,      // Connection acknowledgment
        DISCONNECT,          // Disconnect
        GAME_MOVE,          // Game move
        CHAT,               // Chat message
        GAME_STATE,         // Game state
        READY,              // Ready state
        RESTART_REQUEST,    // Restart request
        UNDO_REQUEST,       // Undo request
        UNDO_RESPONSE       // Undo response
    }
    
    private Type type;
    private Object data;
    private String senderId;
    private long timestamp;
    
    public NetworkMessage(Type type, Object data, String senderId) {
        this.type = type;
        this.data = data;
        this.senderId = senderId;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return String.format("NetworkMessage{type=%s, senderId=%s, timestamp=%d}", 
                           type, senderId, timestamp);
    }
}