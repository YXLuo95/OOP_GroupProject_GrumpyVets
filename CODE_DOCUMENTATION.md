# Chess Game Code Documentation

## Overview
This documentation explains the purpose and structure of each code block in the enhanced chess game implementation with dual interaction methods (click-and-move and drag-and-drop).

## File Structure

### GUI/Singleplayer.java
**Purpose**: Complete single-player chess game with enhanced user interaction

#### Class Structure:
```java
public class Singleplayer extends JFrame
```
**Purpose**: Main window for single-player chess game with integrated toolbar and status display

#### Core Components:

**1. Game Logic Components**
```java
private ChessBoard chessBoard;    // Custom interactive chess board component
private GameSession gameSession;  // Chess game logic controller and state manager
private JLabel statusLabel;       // Display area for game status and move information
```
**Purpose**: Manages chess game state, visual representation, and user feedback

**2. Initialization Methods**
- `initializeGame()`: Sets up chess game session with standard board layout
- `setupUI()`: Configures window layout and component arrangement
- `createToolbar()`: Creates game control buttons (New Game, Undo, Redo, Save, Back to Menu)

**3. Interactive Chess Board (Inner Class)**
```java
private class ChessBoard extends JPanel
```
**Purpose**: Provides dual interaction methods with visual feedback

**State Variables**:
- Click-and-Move: `selectedRow`, `selectedCol` track piece selection
- Drag-and-Drop: `isDragging`, `dragStartRow`, `dragStartCol`, `dragOffset`, `currentDragPosition`

**Mouse Event Handling**:
- `mouseClicked()`: Traditional click-to-select-and-move interface
- `mousePressed()`: Initiates drag operation for valid pieces
- `mouseDragged()`: Updates piece position during drag
- `mouseReleased()`: Completes move attempt and resets drag state

**Rendering Methods**:
- `paintComponent()`: Main drawing method with anti-aliasing
- `drawBoard()`: Renders 8x8 chess board with alternating colors and highlights
- `drawPieces()`: Draws all chess pieces, skipping dragged pieces at original position
- `drawPiece()`: Renders individual pieces with Unicode symbols and outlines
- `drawDraggedPiece()`: Renders semi-transparent piece at cursor position during drag
- `getPieceSymbol()`: Maps piece types to Unicode chess symbols

### GUI/MultiplayerFrame.java
**Purpose**: Multiplayer chess game with network connectivity and chat functionality

#### Class Structure:
```java
public class MultiplayerFrame extends JFrame
```
**Purpose**: Complete multiplayer interface with network controls and communication

#### Core Components:

**1. Game Logic Components**
```java
private GameSession gameSession;  // Handles chess rules, moves, and game state
private ChessBoard chessBoard;    // Custom panel for interactive chess board display
```
**Purpose**: Maintains local chess game state even without network connection

**2. Network Components**
```java
private JButton hostButton;       // Button to start hosting a game
private JButton joinButton;       // Button to join an existing game
private JButton disconnectButton; // Button to disconnect from network game
private JTextField hostIpField;   // Input field for target IP address
private JTextField portField;     // Input field for network port number
private JLabel connectionLabel;   // Shows current connection status
```
**Purpose**: Enable multiplayer game setup and connection management

**3. Communication Components**
```java
private JTextArea chatArea;       // Display area for chat messages
private JTextField chatInput;     // Input field for typing chat messages
private JButton sendChatButton;   // Button to send chat messages
```
**Purpose**: Real-time communication between players

**4. Layout Organization**
- **Top Section**: Menu bar and network connection controls
- **Center Section**: Interactive chess board with dual interaction support
- **Right Section**: Chat panel with message history and input
- **Bottom Section**: Status bar showing game state and move information

**5. Network Methods**
- `hostGame()`: Starts hosting a multiplayer game (UI updates implemented, network logic placeholder)
- `joinGame()`: Connects to existing multiplayer game (UI updates implemented, network logic placeholder)
- `disconnect()`: Terminates network connection and resets UI state
- `sendChatMessage()`: Handles chat message input and display
- `backToMainMenu()`: Provides navigation back to main menu with confirmation

## Enhanced Interaction Features

### Click-and-Move Interface
**Purpose**: Traditional chess interface familiar to most users
**Implementation**:
1. Click on piece to select (yellow highlight appears)
2. Click on destination square to move
3. Move validation through existing chess logic
4. Visual feedback through square highlighting

### Drag-and-Drop Interface  
**Purpose**: Modern, intuitive piece movement
**Implementation**:
1. Press and hold on piece to start drag (piece becomes semi-transparent)
2. Drag piece to desired location (green highlight shows drop target)
3. Release to complete move
4. Real-time visual feedback during entire operation

### Visual Feedback System
**Highlights**:
- **Yellow**: Selected piece (click-and-move mode)
- **Green**: Valid drop target (drag-and-drop mode)  
- **Semi-transparent**: Piece being dragged

**Rendering Features**:
- Unicode chess piece symbols for clear identification
- Contrasting outlines for better visibility on different square colors
- Smooth visual transitions during piece manipulation
- Real-time status updates showing current player and move results

## Integration Points

### Chess Logic Integration
Both GUI classes integrate with existing console chess logic:
- `GameSession`: Handles move validation, turn management, game state
- `Board`: Maintains piece positions and board state
- `Piece` objects: Represent individual chess pieces with type and color
- Move validation ensures all chess rules are enforced

### UI Consistency
Both single-player and multiplayer modes provide:
- Identical chess board interaction methods
- Consistent visual feedback and highlighting
- Same keyboard shortcuts and mouse behaviors
- Unified status display and move notation

### Error Handling
- Invalid move attempts are caught and reported to user
- Network connection errors display appropriate error messages
- Game state corruption is prevented through proper validation
- UI state is properly reset after errors or disconnections

## Testing and Validation

### Compilation
- Both files compile successfully with no errors or warnings
- All dependencies properly imported and resolved
- Class path configuration supports console logic integration

### Runtime Testing
- Single-player mode launches and functions correctly
- Multiplayer mode initializes with proper UI layout
- Both interaction methods (click-and-move and drag-and-drop) work simultaneously
- Visual feedback displays correctly during piece manipulation
- Menu navigation and window management function properly

This enhanced chess implementation provides a modern, user-friendly interface while maintaining full compatibility with the existing chess logic system.