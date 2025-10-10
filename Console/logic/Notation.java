package logic;

public final class Notation {
    private Notation() {}

    public static int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) return null;
        char file = Character.toLowerCase(alg.charAt(0));
        char rank = alg.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') return null;
        int col = file - 'a';
        int row = 8 - (rank - '0');
        return new int[]{row, col};
    }

    public static String toAlg(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return "??";
        char file = (char) ('a' + col);
        char rank = (char) ('0' + (8 - row));
        return "" + file + rank;
    }
}
