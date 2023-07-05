package versions.v5.negamax.betterMinMax;

import Game.ConnectFour;
import versions.v4.row_modifiers.betterMinMax.CacheEntry;

public class MinNode extends Node {

    public MinNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, CacheEntry> cache) {
        super(game, evalFunction, currentDepth, move, cache);
    }

    @Override
    Node createOppositeNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, CacheEntry> cache) {
        return new MaxNode(game, evalFunction, currentDepth, move, cache);
    }

    @Override
    float compareScore(float score1, float score2) {
        return Math.min(score1, score2);
    }

    @Override
    int initScore() {
        return Integer.MAX_VALUE;
    }

    @Override
    boolean compareAlphaBeta(float score, float alpha, float beta) {
        return score < alpha;
    }

    @Override
    boolean foundNewBestMove(float score, float alpha, float beta) {
        return score <= beta;
    }
    @Override
    float setAlpha(float alpha, float score) {
        return alpha;
    }

    @Override
    float setBeta(float beta, float score) {
        return Math.min(beta, score);
    }

    @Override
    public int compareTo(Node other) {
        return super.compareTo(other, true);
    }

    @Override
    boolean isMaximizing() {
        return false;
    }
}
