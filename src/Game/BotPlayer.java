package Game;

public interface BotPlayer extends ConnectFourPlayer {
    void setCanMakeRandomMove(boolean randMove);

    long getTotalMoveTimeTaken();

    void setMaxTimeToTake(long time);
    void setMaxDepth(int depth);

}
