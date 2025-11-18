# OOP_GroupProject_GrumpyVets — Chess Game

A feature-rich Java chess application built with a Console engine and a Swing-based GUI. It supports both click-to-move and drag-and-drop, polished game-over dialogs, save/load, and a basic peer-to-peer multiplayer UI with chat.

## Project Description

The project evolved from a console chess app to a GUI experience while keeping a clean separation: the Console package provides the complete chess engine (rules, validation, history, saves), and the GUI layer focuses on rendering and interaction. Multiplayer UI is available and under gradual improvement.

## Project Structure

```
OOP_GroupProject_GrumpyVets/
├── README.md
├── run.bat                      # One-click build and run (GUI or Console)
├── Console/
│   ├── MainConsole.java         # Console entry
│   ├── logic/
│   │   ├── Board.java
│   │   ├── BoardPrinter.java
│   │   ├── GameSave.java
│   │   ├── GameSession.java
│   │   ├── Notation.java
│   │   ├── Rules.java
│   │   └── TestGameSession.java
│   ├── network/
│   │   ├── GameMove.java
│   │   ├── MultiplayerSession.java
│   │   ├── NetworkConnection.java
│   │   └── NetworkMessage.java
│   └── objects/
│       ├── Bishop.java
│       ├── King.java
│       ├── Knight.java
│       ├── Moveable.java
│       ├── Pawn.java
│       ├── Piece.java
│       ├── PieceColor.java
│       ├── PieceType.java
│       ├── PromotionChoice.java
│       ├── Queen.java
│       └── Rook.java
├── GUI/
│   ├── AIOpponent.java
│   ├── BoardView.java
│   ├── MainMenuApp.java        # GUI entry (package GUI)
│   ├── MinimaxAIOpponent.java
│   ├── MultiplayerFrame.java
│   ├── Singleplayer.java
│   ├── SingleplayerAI.java
│   └── StatusText.java
└── saves/
    └── test1.chess             # Sample save (folder auto-created)
```

## Quick Start

### Method 1: Run Script (Recommended)
1. Double-click `run.bat` in the project root.
2. The script compiles Console + GUI into `out/` and shows a menu:
   - 1 Start Main Menu (GUI)
   - 2 Run Console Version
   - 3 Exit
3. Pick 1 for GUI or 2 to play in the terminal.
4. On Windows, allow Java through the firewall if prompted (for multiplayer).

### Method 2: PowerShell/Command Prompt
```powershell
cd "path\to\OOP_GroupProject_GrumpyVets"
.\run.bat
```

### Method 3: Manual Compilation (Advanced)
#### Windows (PowerShell/CMD)
```powershell
# Compile Console classes (logic, objects, network)
javac -cp ".;Console" Console\logic\*.java Console\objects\*.java Console\network\*.java

# Compile GUI classes (package: GUI)
javac -cp ".;Console" GUI\*.java

# Run GUI application (fully qualified main class)
java -cp ".;Console;." GUI.MainMenuApp

# (Optional) Run console version
javac -cp ".;Console" Console\MainConsole.java
java -cp ".;Console;." MainConsole
```

#### Linux/Mac (bash/zsh)
```bash
# Compile Console classes (logic, objects, network)
javac -cp ".:Console" Console/logic/*.java Console/objects/*.java Console/network/*.java

# Compile GUI classes (package: GUI)
javac -cp ".:Console" GUI/*.java

# Run GUI application (fully qualified main class)
java -cp ".:Console:." GUI.MainMenuApp

# (Optional) Run console version
javac -cp ".:Console" Console/MainConsole.java
java -cp ".:Console:." MainConsole
```

## Features

### Console Engine
- Full chess rules including castling, en passant, and promotion.
- Strong move validation preventing illegal or self-check moves.
- Check/checkmate/stalemate detection and complete move history (undo/redo).
- Serializable saves with restoration of full game state.

### GUI (Swing)
- Click-to-move and drag-and-drop piece interaction.
- Real-time visual feedback: selection highlights, drag previews, turn/check status.
- Unicode piece rendering for clear, cross-platform visuals.
- Professional game-over dialogs (New Game | Back to Menu | Exit).
- Toolbar controls: New Game, Undo, Redo, Save, Back to Menu.
- Multiplayer view with host/join controls and built-in chat (basic sync).

## How to Play

### Getting Started
1. Launch via `run.bat` and choose option 1 (GUI).
2. In the main menu, choose a mode: Single Player, Single Player (AI), Multiplayer, or Saved Game.

### Game Interface
- 8×8 board, alternating light/dark squares.
- Status bar shows current turn and game state.
- Toolbar provides common actions.

### Controls
1) Click-and-move: click a piece, then click a destination (click again to deselect).
2) Drag-and-drop: press and hold a piece, drag over a destination, release to drop.

### Menu Navigation
- Single Player: Start a new local game.
- Single Player (AI): Choose AI search depth (2/3/4) and AI side (default: Black); play vs AI.
- Saved Game: Browse and load saved games.
- Multiplayer (P2P): Host/Join with built-in chat (some features still in progress).

### Multiplayer Quick Guide
- Host:
  - Keep IP as `localhost` for same-PC testing.
  - Use port `8888` (default) or any open port; click "Host Game".
  - Allow through Windows Firewall if prompted.
- Join:
  - On another device or the same PC, enter the host IP and the same port.
  - Click "Join Game".
- Notes:
  - Ensure both devices are on the same network and the port is open.
  - For same-PC tests, use `localhost` on both sides.

## Save & Load
- Save during play from Single Player via "Save Game".
- Saves are stored as `.chess` files under `saves/`.
- Load from the main menu; game state (board, turn, status) is restored.

## Technical Notes
- OOP principles across the codebase (piece hierarchy, rule encapsulation).
- MVC-leaning separation: Console engine (model/rules), GUI (view/controller).
- Swing event-driven UI; Java serialization for saves.
- Cross-platform (Java 8+). Scripted build/run via `run.bat` on Windows.

## System Requirements
- Java 8 (JDK 1.8) or newer.
- Windows (run via `run.bat`) or Linux/Mac (manual compilation commands above).
- ~50MB RAM, 700×750 display or larger.

## Project Status

✔ Completed
- Full chess logic, validation, and game-over detection.
- Dual input (click + drag), visual feedback, undo/redo.
- Save/load with persistent storage.
- GUI main menu and single-player modes.
- Multiplayer UI with chat and basic move synchronization.

🚧 Planned
- Strengthen multiplayer: reconnection, resume, spectator, robustness.
- AI improvements and difficulty options.
- Notation display/export (PGN), statistics, analysis.
- Better graphics/animations, sound effects, themes/skins.

## Known Limitations
- Multiplayer is an early version: move sync and chat are available; reconnection, resume, and spectators are not yet implemented.
- GUI uses Unicode piece symbols by default; no image-based skins included yet.

## Architecture & Decisions
- Logic reuse: GUI builds on the stable Console engine to keep behavior consistent.
- Minimal extra abstraction: further layering is deferred until there’s a clear need (e.g., web/mobile frontends, richer spectators, robust reconnection/rollback). If needed later, lightweight interfaces like a GUI-facing `GameProvider` and simple DTOs can be introduced without breaking existing code.

## Troubleshooting
1) Script fails to run: ensure JDK is installed and on PATH; try running `run.bat` from a terminal.
2) GUI doesn’t appear: confirm Java Swing works on your system.
3) Firewall prompts: allow Java for hosting/joining multiplayer on Windows.
4) Manual run fails: use the fully qualified main class `GUI.MainMenuApp` and include project root `.` on the classpath.
5) Saves not found: the `saves/` folder is created automatically on first save.

## Contributing
This is an educational project for learning OOP concepts. Explore, learn, and extend as you like.

— Team GrumpyVets