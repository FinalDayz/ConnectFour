package versions.column_reward.betterMinMax;

import Game.ConnectFour;

public class SimpleDepthEvaluationFunction implements EvaluationFunction {

    private boolean isRed;

    public SimpleDepthEvaluationFunction(boolean isRed) {
        this.isRed = isRed;
    }

    @Override
    public float evaluate(ConnectFour game, int depth) {
        if(!game.gameState.gameDidEnd()) {
            return 0;
        }

        if(game.gameState.gameDidDraw()) {
            return -1;
        }

        boolean won = game.gameState.redDidWon() == isRed;

        return 1000 / (won ? depth : -depth);
    }

    @Override
    public boolean scoreIsWinOrLoss(float score, boolean isMax) {
        return false;
    }

}
