package versions.v5.negamax.betterMinMax;

import Game.ConnectFour;
import versions.v4.row_modifiers.betterMinMax.CacheEntry;

import java.util.Arrays;

public class NegamaxNode implements Comparable<NegamaxNode> {

    private float nodeScore;
    NegamaxNode[] childNodes = null;
    private final ConnectFour game;
    private final int currentDepth;
    private final EvaluationFunction evalFunction;
    public int moveIndex;
    private long maxTimeStamp;
    private DoubleHashMap<Long, CacheEntry> cache;
    public final int player;

    public NegamaxNode(ConnectFour game, EvaluationFunction evalFunction, int currentDepth, int move, DoubleHashMap<Long, CacheEntry> cache, int player) {
        this.player = player;
        this.game = game;
        this.currentDepth = currentDepth;
        this.evalFunction = evalFunction;
        this.moveIndex = move;
        this.cache = cache;
//        nodeScore = initScore();
    }

    public String debugInfo = "";
    public void search(int maxDepth, float alpha, float beta) throws InterruptedException {
        game.executePlay(moveIndex);
//        debugInfo+="\n"+currentDepth+"]1 Exploring move " + moveIndex + "\n";

//        CacheEntry savedEntry = cache.get(game.getRedBitBoard(), game.getYellowBitBoard());
//        if(savedEntry != null) {
//            if(checkCanUseCache(savedEntry, alpha, beta)) {
//                nodeScore = savedEntry.value;
//                game.undoMove(false);
//                return;
//            }
//        }

        boolean doReturn = maxDepth == currentDepth || game.gameState.gameDidEnd();

        if (currentDepth <= 3 && maxTimeStamp < System.currentTimeMillis()) {
            game.undoMove(false);
            return;
        }

        if (doReturn) {
            nodeScore = evalFunction.evaluate(game, currentDepth) * player;
//            debugInfo+="doReturn, end node. relative value " + nodeScore + "\n";
            game.undoMove(false);
            return;
        }
        byte nodeCacheType = CacheEntry.TYPE_UPPER_BOUND;

        nodeScore = initScore();

        if (childNodes == null) {
            childNodes = createNodes();
        } else {
            sortNodes(childNodes);
        }

        for (NegamaxNode node : childNodes) {
            node.search(maxDepth, -beta, -alpha);

            this.nodeScore = Math.max(-node.nodeScore, this.nodeScore);

//            if(evalFunction.scoreIsWinOrLoss(nodeScore, true)) {
//                game.undoMove(false);
//                return;
//            }
            alpha = Math.max(alpha, this.nodeScore);

            if (this.nodeScore >= beta) {
                // TODO: set correct one

//                nodeCacheType = CacheEntry.TYPE_LOWER_BOUND;
//                CacheEntry entry = new CacheEntry();
//                entry.value = nodeScore;
//                entry.type = nodeCacheType;
//                cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), entry);

                // cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), this.nodeScore);
                game.undoMove(false);
                return;
            }
//            if(foundNewBestMove(nodeScore, alpha, beta)) {
//                nodeCacheType = 0;
//            }
//            if (nodeScore > alpha && !isMaximizing()) {
//                nodeCacheType = 0;
//            }


        }

//        if(nodeCacheType == CacheEntry.TYPE_UPPER_BOUND || nodeCacheType == CacheEntry.TYPE_EXACT) {
//            CacheEntry entry = new CacheEntry();
//            entry.value = nodeScore;
//            entry.type = nodeCacheType;
//            cache.put(game.getRedBitBoard(), game.getYellowBitBoard(), entry);
//        }

        game.undoMove(false);
    }

    public float getReverseScore() {
        return -nodeScore;
    }

    private NegamaxNode[] createNodes() {
        int[] moves = game.getAvailableMoves();
        NegamaxNode[] nodes = new NegamaxNode[moves.length];
        int index = 0;
        for (int moveIndex : moves) {

            nodes[index] = new NegamaxNode(game, evalFunction, currentDepth + 1, moveIndex, cache, -player);
            if (currentDepth <= 2) {
                nodes[index].setMaxTimeStamp(maxTimeStamp);
            }
            index++;
        }
        return nodes;
    }

    static void sortNodes(NegamaxNode[] nodesToSort) {
        Arrays.sort(
                nodesToSort,
                (a, b) -> a.compareTo(b)
        );
    }

    public int compareTo(NegamaxNode other) {
        return Float.compare(other.nodeScore, nodeScore);
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMaxTimeStamp(long maxTimeStamp) {
        this.maxTimeStamp = maxTimeStamp;
    }

    float initScore() {
        return (float) Integer.MIN_VALUE;
    }

    public int getPlayer() {
        return player;
    }

    public NegamaxNode[] getChildNodes() {
        return childNodes;
    }

    public float getInnerScore() {
        return nodeScore;
    }
}
