package objects;

/**
 * 枚举：棋子类型（国际象棋六种）
 * 用于标识每个棋子的种类。
 */
public enum PieceType {
    PAWN,   // 兵（Pawn）
    ROOK,   // 车（Rook）
    KNIGHT, // 马（Knight）
    BISHOP, // 象（Bishop）
    QUEEN,  // 后（Queen）
    KING;   // 王（King）

    /**
     * 返回棋子对应的单字母代号（国际象棋常用）
     * 方便打印或调试。
     */
    public String symbol() {
        return switch (this) {
            case PAWN -> "P";
            case ROOK -> "R";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case QUEEN -> "Q";
            case KING -> "K";
        };
    }
}
