package network;

import java.io.Serializable;

/**
 * 网络消息类 - 用于P2P通信
 */
public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        CONNECTION_REQUEST,  // 连接请求
        CONNECTION_ACK,      // 连接确认
        DISCONNECT,          // 断开连接
        GAME_MOVE,          // 游戏移动
        CHAT,               // 聊天消息
        GAME_STATE,         // 游戏状态
        READY,              // 准备状态
        RESTART_REQUEST,    // 重新开始请求
        UNDO_REQUEST,       // 悔棋请求
        UNDO_RESPONSE       // 悔棋回应
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