package versions.v4.row_modifiers.betterMinMax;

import Game.ConnectFour;

public class MaxNode extends Node {
    
    public MaxNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, Float> cache) {
        super(game, evalFunction, currentDepth, move, cache);
    }

    @Override
    Node createOppositeNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, Float> cache) {
        return new MinNode(game, evalFunction, currentDepth, move, cache);
    }

    @Override
    float compareScore(float score1, float score2) {
        return Math.max(score1, score2);
    }

    @Override
    int initScore() {
        return Integer.MIN_VALUE;
    }

    @Override
    boolean compareAlphaBeta(float score, float alpha, float beta) {
        return score > beta;
    }

    @Override
    float setAlpha(float alpha, float score) {
        return Math.max(alpha, score);
    }

    @Override
    float setBeta(float beta, float score) {
        return beta;
    }

    @Override
    public int compareTo(Node other) {
        return super.compareTo(other, false);
    }

    @Override
    boolean isMaximizing() {
        return true;
    }
}
