# OOP_GroupProject_GrumpyVets - Chess Game

A Java Swing-based chess game project featuring complete game logic and graphical user interface.

## Project Description

This project has evolved from a simple console-based chess application to a full-featured GUI chess game. It demonstrates object-oriented design principles and includes both command-line and graphical interfaces for playing chess.

## Project Structure

```
OOP_GroupProject_GrumpyVets/
├── README.md
├── run.bat              # Quick run script
├── Console/             # Game logic layer
│   ├── logic/           # Game session, board, rules, etc.
│   │   ├── Board.java
│   │   ├── GameSession.java
│   │   ├── Rules.java
│   │   └── ...
│   └── objects/         # Chess piece class definitions
│       ├── Piece.java
│       ├── Pawn.java
│       ├── King.java
│       └── ...
└── GUI/                 # Graphical user interface layer
    ├── MainMenuApp.java     # Main menu application
    ├── Singleplayer.java   # Single player game interface
    ├── GameFacade.java      # Game facade interface
    ├── BoardSnapshot.java   # Board snapshot
    └── PieceUI.java         # Piece UI representation
```

## Quick Start

### Method 1: Using Script (Recommended)
Double-click the `run.bat` file to automatically compile and run the program.

### Method 2: Manual Compilation
```batch
# Compile Console classes
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java

# Compile GUI classes
javac -cp ".;Console" GUI\*.java

# Run GUI application
java -cp ".;Console;GUI" MainMenuApp
```

## Features

### GUI Version with Console Rules Integration
- **Full Chess Logic**: Uses the complete Console game engine for move validation
- **Rule Enforcement**: All standard chess rules including check, checkmate, and stalemate
- **Interactive Board**: 8x8 visual chess board with Unicode pieces (♔♕♖♗♘♙ for white, ♚♛♜♝♞♟ for black)
- **Smart Move Validation**: Prevents illegal moves and self-check situations
- **Game State Management**: Proper turn switching and game over detection
- **Undo/Redo**: Full move history with undo and redo functionality
- **Visual Feedback**: Yellow highlighting for selected pieces and clear status updates

### Main Menu
- Clean and intuitive main menu interface
- Three primary buttons: Single Player, Multiplayer, Saved Game
- ESC key to exit, Enter key to confirm
- Modern dark theme design

## How to Play

1. **Start Program**: Run `run.bat` and choose option 1 for GUI, or option 2 to test console rules
2. **Main Menu**: Click "Single Player" to start a chess game
3. **Game Controls**:
   - Click on a chess piece to select it (highlighted in yellow)
   - Click on the destination square to move the piece
   - Only legal moves are allowed - the game enforces all chess rules
   - Status bar shows whose turn it is and game state
4. **Game Features**:
   - "New Game": Reset the board for a new game
   - "Undo": Take back the last move
   - "Redo": Replay an undone move
   - "Back to Menu": Return to the main menu
5. **Debug Output**: Console window shows move validation and game logic information

## Technical Features

- **Object-Oriented Design**: Proper use of inheritance, polymorphism, and encapsulation
- **Design Patterns**: Facade pattern for GUI-logic separation
- **Modular Architecture**: Clear separation between game logic and presentation
- **Event-Driven Programming**: Swing-based event handling
- **Cross-Platform**: Runs on any system with Java 8+

## Development Environment

- **Java Version**: Java 8 or higher
- **GUI Framework**: Java Swing
- **Architecture**: MVC (Model-View-Controller)
- **Build Tool**: javac (JDK built-in)

## Educational Value

This project demonstrates:
- Object-oriented programming principles
- Game logic implementation
- GUI development with Swing
- Software architecture and design patterns
- Separation of concerns
- Event-driven programming

## Future Enhancements

- Network multiplayer support
- Save/load game functionality
- AI opponent with difficulty levels
- Enhanced graphics and animations
- Move history display
- Chess notation support

## Contributing

This is an educational project for learning OOP concepts. Feel free to explore the code and extend functionality as needed.