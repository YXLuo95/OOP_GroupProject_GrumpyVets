package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * P2P network connection manager
 * Handles Socket connections, message sending and receiving
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
     * Set message handler
     */
    public void setMessageHandler(Consumer<NetworkMessage> handler) {
        this.messageHandler = handler;
    }
    
    /**
     * Start server as host
     */
    public boolean startAsHost(int port) {
        try {
            isHost = true;
            serverSocket = new ServerSocket(port);
            System.out.println("Host started on port " + port + ", waiting for connection...");

            // Asynchronously wait for connection
            executor.submit(() -> {
                try {
                    socket = serverSocket.accept();
                    InetAddress clientAddress = socket.getInetAddress();
                    System.out.println("Client connected: " + clientAddress.getHostAddress());
                    
                    setupStreams();
                    connected = true;
                    startMessageListener();

                    // Send connection acknowledgment message
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
     * Connect to host as client
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
            
            // Send connection request
            sendMessage(new NetworkMessage(NetworkMessage.Type.CONNECTION_REQUEST, 
                                         "Client ready", connectionId));
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Set up input and output streams
     */
    private void setupStreams() throws IOException {
        // input and output streams
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    /**
     * Start message listener thread
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
     * Handle received messages
     */
    private void handleReceivedMessage(NetworkMessage message) {
        System.out.println("Received: " + message.getType() + " from " + message.getSenderId());
        
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }
    
    /**
     * Send message
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
     * send game move
     */
    public boolean sendMove(int startRow, int startCol, int endRow, int endCol) {
        GameMove move = new GameMove(startRow, startCol, endRow, endCol);
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.GAME_MOVE, move, connectionId);
        return sendMessage(message);
    }
    
    /**
     * Send chat message
     */
    public boolean sendChat(String text) {
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.CHAT, text, connectionId);
        return sendMessage(message);
    }
    
    /**
     * Send game state
     */
    public boolean sendGameState(String state) {
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.GAME_STATE, state, connectionId);
        return sendMessage(message);
    }
    
    /**
     * Disconnect
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