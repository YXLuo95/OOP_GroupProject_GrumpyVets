package logic;

import objects.*;

public final class BoardPrinter {
    private BoardPrinter() {}

    public static void print(Board b) {
        System.out.println("   a b c d e f g h");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r;
            System.out.print(rank + "  ");
            for (int c = 0; c < 8; c++) {
                Piece p = b.getPieceAt(r, c);
                System.out.print((p == null ? ". " : p.toString() + " "));
            }
            System.out.println(" " + rank);
        }
        System.out.println("   a b c d e f g h");
    }
}
