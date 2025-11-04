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

### GUI Version (Current)
- **Main Menu**: Clean interface with Single Player, Multiplayer, and Saved Game options
- **Chess Board**: Complete 8x8 board with Unicode chess pieces (♔♕♖♗♘♙)
- **Interactive Gameplay**: Click to select and move pieces
- **Visual Feedback**: Yellow highlighting for selected pieces
- **Turn Management**: Automatic turn switching between White and Black
- **Game Controls**: New Game and Back to Menu options

### Console Version (Legacy)
- Console-based chess board printing
- Input moves using chess notation (e.g., `e2e4`)
- Turn-based play (White / Black)
- Basic move validation (pawn, rook, knight, bishop, queen, king)
- Capturing pieces
- Check / Checkmate detection
- Special moves (castling, en passant, pawn promotion)

## How to Play

1. **Start Program**: Run `run.bat` or use manual compilation
2. **Main Menu**: Click "Single Player" to start a game
3. **Game Controls**:
   - Click on a chess piece to select it (highlighted in yellow)
   - Click on the destination square to move the piece
   - Status bar shows whose turn it is
4. **Game Options**:
   - "New Game": Reset the board for a new game
   - "Back to Menu": Return to the main menu

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