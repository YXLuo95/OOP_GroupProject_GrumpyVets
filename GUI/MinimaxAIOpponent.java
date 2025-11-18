package GUI;

import logic.Board;
import logic.Rules;
import objects.*;

/**
 * Minimax/Negamax AI with alpha-beta pruning.
 * - Uses simple material evaluation with in-check penalty.
 * - Depth is configurable via constructor.
 */
public class MinimaxAIOpponent implements AIOpponent {
    private final int maxDepth;

    public MinimaxAIOpponent(int depth) {
        this.maxDepth = Math.max(1, depth);
    }

    @Override
    public int[] chooseMove(Board board, PieceColor aiColor) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        // Generate legal moves for aiColor
        for (Piece p : board.getPiecesByColor(aiColor)) {
            int sr = p.getX(), sc = p.getY();
            for (int er = 0; er < 8; er++) {
                for (int ec = 0; ec < 8; ec++) {
                    if (!p.canMove(board, sr, sc, er, ec)) continue;

                    Board trial = board.deepCopy();
                    trial.movePiece(sr, sc, er, ec);
                    // Illegal if leaves own king in check
                    if (Rules.isInCheck(trial, aiColor)) continue;

                    int score = -negamax(trial, flip(aiColor), maxDepth - 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{sr, sc, er, ec};
                    }
                }
            }
        }
        return bestMove; // can be null on mate/stalemate
    }

    private int negamax(Board b, PieceColor toMove, int depth, int alpha, int beta) {
        // Terminal or depth limit
        boolean anyMove = false;
        int best = Integer.MIN_VALUE;

        if (depth == 0) return evaluate(b, toMove);

        for (Piece p : b.getPiecesByColor(toMove)) {
            int sr = p.getX(), sc = p.getY();
            for (int er = 0; er < 8; er++) {
                for (int ec = 0; ec < 8; ec++) {
                    if (!p.canMove(b, sr, sc, er, ec)) continue;
                    Board t = b.deepCopy();
                    t.movePiece(sr, sc, er, ec);
                    if (Rules.isInCheck(t, toMove)) continue; // self-check invalid
                    anyMove = true;

                    int score = -negamax(t, flip(toMove), depth - 1, -beta, -alpha);
                    if (score > best) best = score;
                    if (score > alpha) alpha = score;
                    if (alpha >= beta) return alpha; // alpha-beta cut
                }
            }
        }

        if (!anyMove) {
            // No legal moves: checkmate or stalemate from toMove's perspective
            if (Rules.isCheckmate(b, toMove)) return -100000;
            if (Rules.isStalemate(b, toMove)) return 0;
            return 0;
        }
        return best;
    }

    private int evaluate(Board b, PieceColor perspective) {
        int material = 0;
        for (Piece p : b.getPiecesByColor(PieceColor.WHITE)) material += pieceVal(p);
        for (Piece p : b.getPiecesByColor(PieceColor.BLACK)) material -= pieceVal(p);

        int checkPenalty = Rules.isInCheck(b, perspective) ? 10 : 0;
        // Convert to perspective: positive good for perspective
        int score = (perspective == PieceColor.WHITE) ? material : -material;
        return score - checkPenalty;
    }

    private int pieceVal(Piece p) {
        return switch (p.getType()) {
            case QUEEN -> 900;
            case ROOK -> 500;
            case BISHOP, KNIGHT -> 300;
            case PAWN -> 100;
            case KING -> 0;
        };
    }

    private PieceColor flip(PieceColor c) {
        return (c == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }
}
