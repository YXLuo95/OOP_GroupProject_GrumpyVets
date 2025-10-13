# GrumpyVets Chess (Java)
## Project Description

This is a simple, testable Java chess project built to practice object‑oriented design and core game logic. The first milestone is a console application with correct move validation (including checks and captures). Future work will add special moves (castling, en passant, promotion) and a JavaFX GUI.
## Features

* Console-based chess board printing
* Input moves using chess notation (e.g., `e2e4`)
* Turn-based play (White / Black)
* Basic move validation (pawn, rook, knight, bishop, queen, king)
* Capturing pieces
* Check / Checkmate detection
* Special moves (castling, en passant, pawn promotion)
* Graphical version (planned)

---

## Project Structure

```
OOP_GroupProject_GrumpyVets/
├─ README.md
└─ Console/
       ├─ logic/            # Rules, board state, check detection (e.g., Board.java, Rules.java)
       ├─ objects/          # Piece hierarchy & enums (e.g., Piece.java, Pawn.java, ...)
       └─ main              # CLI app/input parsing/printing (e.g., Main.java)
      
```
