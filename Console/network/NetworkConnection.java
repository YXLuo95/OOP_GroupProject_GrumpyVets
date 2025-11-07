package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * P2P网络连接管理器
 * 处理Socket连接、消息发送接收
 */
public class NetworkConnection {
    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ExecutorService executor;
    private Consumer<NetworkMessage> messageHandler;
    private volatile boolean connected = false;
    private volatile boolean isHost = false;
    private String connectionId;
    
    public NetworkConnection() {
        this.executor = Executors.newCachedThreadPool();
        this.connectionId = "CONN_" + System.currentTimeMillis();
    }
    
    /**
     * 设置消息处理器
     */
    public void setMessageHandler(Consumer<NetworkMessage> handler) {
        this.messageHandler = handler;
    }
    
    /**
     * 作为主机启动服务器
     */
    public boolean startAsHost(int port) {
        try {
            isHost = true;
            serverSocket = new ServerSocket(port);
            System.out.println("Host started on port " + port + ", waiting for connection...");
            
            // 异步等待连接
            executor.submit(() -> {
                try {
                    socket = serverSocket.accept();
                    InetAddress clientAddress = socket.getInetAddress();
                    System.out.println("Client connected: " + clientAddress.getHostAddress());
                    
                    setupStreams();
                    connected = true;
                    startMessageListener();
                    
                    // 发送连接确认消息
                    sendMessage(new NetworkMessage(NetworkMessage.Type.CONNECTION_ACK, 
                                                 "Host ready", connectionId));
                    
                } catch (IOException e) {
                    System.err.println("Host connection error: " + e.getMessage());
                    connected = false;
                }
            });
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to start host: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 作为客户端连接到主机
     */
    public boolean connectToHost(String hostAddress, int port) {
        try {
            isHost = false;
            System.out.println("Connecting to " + hostAddress + ":" + port + "...");
            
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostAddress, port), 5000); // 5秒超时
            
            System.out.println("Connected to host successfully!");
            
            setupStreams();
            connected = true;
            startMessageListener();
            
            // 发送连接请求
            sendMessage(new NetworkMessage(NetworkMessage.Type.CONNECTION_REQUEST, 
                                         "Client ready", connectionId));
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 设置输入输出流
     */
    private void setupStreams() throws IOException {
        // 先创建输出流，再创建输入流
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    /**
     * 启动消息监听线程
     */
    private void startMessageListener() {
        executor.submit(() -> {
            while (connected && socket != null && !socket.isClosed()) {
                try {
                    Object obj = in.readObject();
                    if (obj instanceof NetworkMessage message) {
                        handleReceivedMessage(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (connected) {
                        System.err.println("Message receive error: " + e.getMessage());
                        disconnect();
                    }
                    break;
                }
            }
            System.out.println("Message listener stopped.");
        });
    }
    
    /**
     * 处理接收到的消息
     */
    private void handleReceivedMessage(NetworkMessage message) {
        System.out.println("Received: " + message.getType() + " from " + message.getSenderId());
        
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }
    
    /**
     * 发送消息
     */
    public boolean sendMessage(NetworkMessage message) {
        if (!connected || out == null) {
            System.err.println("Cannot send message: not connected");
            return false;
        }
        
        try {
            message.setSenderId(connectionId);
            message.setTimestamp(System.currentTimeMillis());
            out.writeObject(message);
            out.flush();
            
            System.out.println("Sent: " + message.getType() + " to peer");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            disconnect();
            return false;
        }
    }
    
    /**
     * 发送游戏移动
     */
    public boolean sendMove(int startRow, int startCol, int endRow, int endCol) {
        GameMove move = new GameMove(startRow, startCol, endRow, endCol);
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.GAME_MOVE, move, connectionId);
        return sendMessage(message);
    }
    
    /**
     * 发送聊天消息
     */
    public boolean sendChat(String text) {
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.CHAT, text, connectionId);
        return sendMessage(message);
    }
    
    /**
     * 发送游戏状态
     */
    public boolean sendGameState(String state) {
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.GAME_STATE, state, connectionId);
        return sendMessage(message);
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        connected = false;
        
        try {
            if (out != null) {
                sendMessage(new NetworkMessage(NetworkMessage.Type.DISCONNECT, "Disconnecting", connectionId));
                out.close();
            }
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
        
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Network connection closed.");
    }
    
    // Getters
    public boolean isConnected() { return connected; }
    public boolean isHost() { return isHost; }
    public String getConnectionId() { return connectionId; }
    
    public String getConnectionInfo() {
        if (socket != null && connected) {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }
        return "Not connected";
    }
    
    public String getLocalInfo() {
        if (serverSocket != null) {
            return "Hosting on port " + serverSocket.getLocalPort();
        } else if (socket != null) {
            return "Local: " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
        }
        return "No connection";
    }
}