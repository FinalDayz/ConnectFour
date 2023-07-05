package versions.v3.row_pattern_reward.betterMinMax;

import Game.ConnectFour;

public interface EvaluationFunction {
    public float evaluate(ConnectFour game, int depth);

    public boolean scoreIsWinOrLoss(float score, boolean isMax);
}
