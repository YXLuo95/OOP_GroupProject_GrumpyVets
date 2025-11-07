# OOP_GroupProject_GrumpyVets - Enhanced Chess Game

A feature-rich Java Swing chess game with dual interaction methods, professional game over system, and multiplayer capabilities.

## Project Description

This project has evolved from a simple console-based chess application to a sophisticated GUI chess game with modern user interaction features. It demonstrates advanced object-oriented design principles and includes both command-line and enhanced graphical interfaces. 

**Latest Version Features**: Dual input methods (click-to-move + drag-and-drop), professional game over notifications, visual feedback system, and multiplayer framework with networking capabilities.

## Project Structure

```
OOP_GroupProject_GrumpyVets/
├── README.md
├── run.bat              # Automated build and run script
├── .gitignore           # Git ignore configuration
├── .vscode/             # VS Code project settings
├── Console/             # Game logic layer (complete chess engine)
│   ├── logic/           # Core game logic
│   │   ├── Board.java          # Chess board representation
│   │   ├── BoardPrinter.java   # Console board display
│   │   ├── GameSession.java    # Game state management
│   │   ├── GameSave.java       # Save/load game functionality
│   │   ├── Notation.java       # Chess notation handling
│   │   ├── Rules.java          # Chess rules validation
│   │   └── TestGameSession.java # Game logic testing
│   ├── objects/         # Chess piece definitions
│   │   ├── Piece.java          # Base piece class
│   │   ├── Pawn.java           # Pawn piece logic
│   │   ├── King.java           # King piece logic
│   │   ├── Queen.java          # Queen piece logic
│   │   ├── Rook.java           # Rook piece logic
│   │   ├── Bishop.java         # Bishop piece logic
│   │   ├── Knight.java         # Knight piece logic
│   │   ├── Moveable.java       # Movement interface
│   │   ├── PieceColor.java     # Piece color enumeration
│   │   ├── PieceType.java      # Piece type enumeration
│   │   └── PromotionChoice.java # Pawn promotion options
│   └── MainConsole.java # Console application entry point
├── GUI/                 # Graphical user interface layer
│   ├── MainMenuApp.java       # Main menu and application entry
│   ├── Singleplayer.java      # Single player game interface
│   └── MultiplayerFrame.java  # Multiplayer game interface with networking
└── saves/               # Game save files directory (auto-created)
    └── *.chess          # Saved game files
```

## Quick Start

### Method 1: Using Run Script (Recommended)
1. **Double-click** the `run.bat` file in the project root directory
2. The script will automatically:
   - Compile all Console classes (game logic)
   - Compile all GUI classes (user interface)
   - Display compilation status
3. **Choose option 1** when prompted to run the GUI application
4. The chess game will launch with a main menu

### Method 2: PowerShell/Command Prompt
```powershell
# Navigate to project directory
cd "path\to\OOP_GroupProject_GrumpyVets"

# Run the automated script
.\run.bat

# Follow the on-screen prompts
```

### Method 3: Manual Compilation (Advanced)
```batch
# Compile Console classes
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java

# Compile GUI classes
javac -cp ".;Console" GUI\*.java

# Run GUI application
java -cp ".;Console;GUI" MainMenuApp
```

## Features

### Complete Chess Engine (Console Package)
- **Full Rule Implementation**: All standard chess rules including special moves
- **Move Validation**: Prevents illegal moves and self-check situations
- **Game State Management**: Check, checkmate, and stalemate detection
- **Move History**: Complete game history with undo/redo functionality
- **Piece Logic**: Individual piece classes with proper movement rules
- **Board Representation**: Efficient 8×8 board with position tracking
- **Save System**: Serializable game state for persistence

### Graphical User Interface (GUI Package)
- **Dual Interaction Methods**: Both click-to-move and drag-and-drop piece movement
- **Visual Feedback System**: Real-time highlights, selection indicators, and drag previews
- **Unicode Pieces**: Clear piece representation using chess symbols
- **Game Over Notifications**: Professional popup dialogs declaring winners with options
- **Real-time Updates**: Instant board updates and status information
- **Intuitive Controls**: Easy-to-use toolbar with game management buttons
- **Modern Design**: Clean, centered board layout with minimal interface
- **Window Management**: Proper window switching between menu and game
- **Multiplayer Interface**: Network game support with chat functionality

### Save & Load System
- **Game Persistence**: Save current game state at any time during play
- **Save Management**: Organized save files in dedicated `saves/` directory
- **Load from Menu**: Select and load saved games directly from main menu
- **State Restoration**: Complete restoration of board position, turn, and game status
- **Save Metadata**: Includes save date and custom save names for easy identification

### Integration Features
- **Seamless Logic Integration**: GUI uses Console engine for all game logic
- **Debug Console**: Real-time display of move validation and game events
- **Error Handling**: Graceful handling of invalid moves and game states
- **Performance**: Efficient rendering and responsive user interaction

## Enhanced User Experience

### Interactive Features
- **Dual Control Methods**: Choose between traditional click-to-move or modern drag-and-drop
- **Visual Feedback**: 
  - Selected pieces highlighted in yellow
  - Semi-transparent pieces during drag operations
  - Real-time cursor tracking with smooth movement
- **Professional Game Over**: 
  - Instant popup notifications for checkmate/stalemate
  - Winner announcements with clear action options
  - Seamless game continuation without application restart
- **Status Awareness**:
  - Real-time turn indicators
  - Check warnings with clear messaging
  - Move validation with helpful error messages

## How to Play

### Getting Started
1. **Launch**: Double-click `run.bat` and select option 1 for GUI application
2. **Main Menu**: The application opens with a dark-themed main menu
3. **Start Game**: Click "Single Player" to begin a chess game

### Game Interface
- **Chess Board**: 8×8 interactive board with alternating light/dark squares
- **Chess Pieces**: Unicode symbols for clear piece identification
  - White pieces: ♔♕♖♗♘♙ (King, Queen, Rook, Bishop, Knight, Pawn)
  - Black pieces: ♚♛♜♝♞♟ (King, Queen, Rook, Bishop, Knight, Pawn)
- **Status Bar**: Shows current player turn and game state
- **Toolbar**: Game control buttons at the top

### Game Controls
1. **Making Moves** (Two Methods Available):
   
   **Method 1 - Click and Move**:
   - **First Click**: Select a piece (highlights in yellow)
   - **Second Click**: Choose destination square
   - **Deselect**: Click the same piece again to cancel selection
   
   **Method 2 - Drag and Drop**:
   - **Press and Hold**: Click and hold on any piece
   - **Drag**: Move mouse to desired destination (semi-transparent piece follows cursor)
   - **Release**: Drop the piece on target square to complete move
   - **Visual Feedback**: Real-time highlighting of valid drop zones

2. **Game Rules & Status**:
   - Only legal moves are allowed (enforced by Console game engine)
   - Proper turn alternation (White moves first)
   - All standard chess rules apply (check, checkmate, castling, en passant)
   - **Check Warnings**: Clear status indicators when king is in check
   - **Game Over Detection**: Automatic checkmate and stalemate recognition
   - **Winner Declaration**: Professional popup dialogs announcing game results

3. **In-Game Controls**:
   - **Back to Menu**: Return to main menu
   - **New Game**: Reset board and start fresh
   - **Save Game**: Save current game state with custom name
   - **Undo**: Take back the last move
   - **Redo**: Replay an undone move

4. **Save & Load Operations**:
   - **Save During Play**: Click "Save Game" button and enter a save name
   - **Load from Menu**: Use "Saved Game" button on main menu
   - **Save Files**: Automatically stored in `saves/` directory as `.chess` files
   - **Continue Saved Games**: Loaded games maintain full functionality including save/undo/redo

### Menu Navigation
- **Single Player**: Start a new chess game with AI or practice mode
- **Saved Game**: Browse and load previously saved games  
- **Multiplayer**: Network chess with host/join capabilities and chat system (Some features are working, some are not)

### Game Over Experience
When a game ends (checkmate, stalemate), players receive:
- **Instant Notification**: Professional popup dialog declaring the result
- **Winner Declaration**: Clear announcement of "Checkmate! [Color] wins!" or "Stalemate - Draw!"
- **Action Options**: Choose to start a new game, return to menu, or exit
- **Seamless Continuation**: No need to restart the application after games end

### Debug Information
- Console window displays move validation and game logic details
- Useful for understanding why certain moves are/aren't allowed

## Feature Showcase

### 🎮 **Dual Interaction System**
Try both control methods in the same game:
1. **Click Method**: Click piece → Click destination
2. **Drag Method**: Hold piece → Drag → Release

### 🏆 **Professional Game Over Experience**
When checkmate occurs:
- Instant popup: "Checkmate! White wins!" or "Checkmate! Black wins!"
- Choose: New Game | Back to Menu | Exit
- No application restart needed

### 🎯 **Visual Feedback System**
- **Selection Highlighting**: Yellow borders around selected pieces
- **Drag Preview**: Semi-transparent pieces follow your cursor
- **Status Updates**: Real-time turn and check notifications
- **Error Guidance**: Clear messages for invalid moves

### 🌐 **Multiplayer Ready**
- Network game interface with host/join options
- Built-in chat system for player communication
- Same enhanced controls in multiplayer mode

## Technical Features

- **Object-Oriented Design**: Comprehensive use of OOP principles
  - Inheritance: Piece hierarchy with base Piece class
  - Polymorphism: Different piece movement behaviors
  - Encapsulation: Protected game state and piece properties
  - Abstraction: Clear separation between interface and implementation

- **Design Patterns**: 
  - MVC Architecture: Separation of game logic, presentation, and control
  - Strategy Pattern: Different piece movement strategies
  - Observer Pattern: GUI updates based on game state changes

- **Modular Architecture**: 
  - **Console Package**: Complete, standalone chess engine
  - **GUI Package**: Pure presentation layer using Console logic
  - **Save System**: Serialization-based game persistence
  - **Clean Interfaces**: Well-defined boundaries between components

- **Event-Driven Programming**: Swing-based event handling with mouse interactions
- **File I/O Operations**: Java serialization for game state persistence
- **Cross-Platform Compatibility**: Runs on any system with Java 8+
- **Build Automation**: One-click compilation and execution via run.bat

## System Requirements

- **Java Version**: Java 8 (JDK 1.8) or higher
- **Operating System**: Windows (run.bat), Linux/Mac (manual compilation)
- **Memory**: Minimal requirements (< 50MB RAM)
- **Display**: Any resolution supporting 700×750 window size
- **Storage**: Minimal space for save files (< 1KB per save)

## Project Status

✅ **Completed Features:**
- Complete chess rule implementation with all standard moves
- **Dual Input System**: Both click-to-move and drag-and-drop interfaces
- **Enhanced Visual Feedback**: Real-time piece highlighting and drag previews
- **Game Over System**: Professional popup dialogs with winner declarations
- Move validation and game state management with check/checkmate detection
- Undo/redo functionality with complete move history
- Save/load game system with persistent storage
- **Multiplayer Interface**: Network game framework with chat system
- Clean, professional user interface with centered board layout
- Main menu with comprehensive game selection and save management
- **Seamless Game Flow**: No restarts needed between games

🚧 **Planned Enhancements:**
- Complete network multiplayer implementation (framework ready)
- AI opponent with difficulty levels
- Enhanced graphics and animations
- Chess notation display and export (PGN format)
- Game statistics and move analysis
- Tournament mode and time controls
- Sound effects for moves and game events
- Customizable themes and piece sets

## Troubleshooting

### Common Issues
1. **Compilation Errors**: Ensure Java JDK is installed and in PATH
2. **GUI Not Appearing**: Check if Java Swing is supported on your system
3. **Script Won't Run**: Right-click run.bat → "Run as Administrator" if needed
4. **Save Files Not Found**: The `saves/` directory is created automatically on first save

### Development Setup
- **IDE**: Works with any Java IDE (VS Code, IntelliJ, Eclipse)
- **Debugging**: Enable console output for detailed game logic information
- **Testing**: Use Console package independently for logic testing
- **Save System**: Saves are stored as serialized `.chess` files in `saves/` directory

## Contributing

This is an educational project for learning OOP concepts. Feel free to explore the code and extend functionality as needed.

---
*Created by Team GrumpyVets - Object-Oriented Programming Course Project*